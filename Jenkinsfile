pipeline {
    agent { label 'linux1' }
    
    environment {
        EMAIL_RECIPIENTS = 'vstefan02@gmail.com'
        IMAGE_NAME= 'wms/app'
        CONTAINER_NAME= 'app'
    }
    
    stages {
        
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, \
                         extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/VStefan01/wms']]])
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
                parallel {
                    stage('IntegrationTest') {
                        steps {
                            sh "mvn test"
                        }
                    }
                    stage('SonarCheck') {
                        steps {
                            script {
                                try {
                                     sh "mvn sonar:sonar \
                                         -Dsonar.host.url=http://192.168.152.162:9000 \
                                         -Dsonar.login=c9e14f5a71c67420abcf94e5fcc305bb298a3d3a"
                                } catch(error) {
                                   echo "The sonar server could not be reached ${error}"
                                }
                            }
                        }
                    }
                }
        }
        
        /*stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                        script {
                            def qg = waitForQualityGate()
                            if (qg.status != 'OK') {
                                error "Pipeline aborted due to quality gate failure: ${qg.status}"
                            }
                        }
                }
            }
        }*/

        stage('Package') {
            steps {
                sh 'mvn -DskipTests clean package'
                archiveArtifacts '**/target/*.jar'
                fingerprint '**/target/*.jar'
                stash includes: '**/target/*.jar', name: 'appPackage'
                stash includes: 'docker/**/*' , name: 'dockerConfig'
            }
        }
        
        stage('Image Prune') {
            //agent { label 'master'}
            steps {
                imagePrune(CONTAINER_NAME)
            }
        }
        
        stage('Image Build') {
            //agent { label 'master'}
            steps {
                dir('/opt/wms_app/wms') {
                    unstash 'dockerConfig'
                }
                dir('/opt/wms_app/wms/docker/app') {
                    unstash 'appPackage'
                    sh "docker build -t $IMAGE_NAME -f Dockerfile-app ."
                }
            }
        }
        
        stage('Runn App') {
            //agent { label 'master'}
            steps {
                dir('/opt/wms_app/wms/docker') {
                    sh "docker-compose up -d --force-recreate"
                }
            }
        }
    }
    
    post {
        success {
            sendEmail("Successful");
        }
        unstable {
            sendEmail("Unstable");
        }
        failure {
            sendEmail("Failed");
        }
    }
}

def imagePrune(containerName){
    try {
        sh "docker image rm -f $IMAGE_NAME"
        sh "docker container rm -f $containerName"
    } catch(error){}
}

def sendEmail(status) {
    mail(
            to: "$EMAIL_RECIPIENTS",
            subject: "Build $BUILD_NUMBER of ${currentBuild.fullDisplayName} has status " + status + "",
            body: "Changes:\n $currentBuild.changeSets" + "\n\n You can see details at: $BUILD_URL " + "\n")
}
