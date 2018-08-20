pipeline {
    agent { label 'linux1' }
    
    environment {
        EMAIL_RECIPIENTS = 'vstefan02@gmail.com'
        IMAGE_NAME= 'wms/app'
        CONTAINER_NAME= 'app'
        GIT_COMMIT= ''
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
                script {
                    GIT_COMMIT = getGitCommit()
                    sh 'mvn clean compile'
                }
                
                
            }
        }
        
        stage('Test') {
                parallel {
                    stage('UnitTest') {
                        steps {
                            sh "mvn test"
                        }
                    }
                    stage('SonarCheck') {
                        steps {
                            script {
                                try {
                                     withSonarQubeEnv('sonar') {
                                         sh 'mvn sonar:sonar'
                                     }
                                } catch(error) {
                                   echo "The sonar server could not be reached ${error}"
                                }
                            }
                        }
                    }
                }
        }
        
        stage('Quality Gate') {
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
        }

        stage('Package') {
            steps {
                sh 'mvn -Dmaven.test.skip=true clean package'
                archiveArtifacts '**/target/*.jar'
                fingerprint '**/target/*.jar'
                stash includes: '**/target/*.jar', name: 'appPackage'
                stash includes: 'app/**/* database/**/* nginx/**/* docker-compose.yml' , name: 'dockerConfig'
            }
        }
        
        stage('Container and Image Prune') {
            agent { label 'master'}
            steps {
                containerPrune()
                imagePrune()
            }
        }
        
        stage('Image Build') {
            agent { label 'master'}
            steps {
                dir('/opt/wms_app/wms') {
                    unstash 'dockerConfig'
                }
                dir('/opt/wms_app/wms/app') {
                        unstash 'appPackage'
                        sh "docker build -t $IMAGE_NAME -f Dockerfile-app ."
                        sh "docker build -t $IMAGE_NAME:$GIT_COMMIT -f Dockerfile-app ."
                        echo "Pushing to docker registry $IMAGE_NAME:$GIT_COMMIT"                        
                }
            }
        }
        
        stage('Deploy') {
            agent { label 'master'}
            steps {
                dir('/opt/wms_app/wms') {
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

def getGitCommit() {
    def gitCommit = sh(script: 'git rev-parse HEAD', returnStdout: true)
    def versionNumber;
    if (gitCommit == null) {
        versionNumber = env.BUILD_NUMBER;
    } else {
        versionNumber = gitCommit.take(8);
    }
    return versionNumber
}

def containerPrune(){
    try {
        sh "docker container rm -f $CONTAINER_NAME"
    } catch(error){}
}

def imagePrune() {
    try {
        sh "docker image rm -f $IMAGE_NAME"
        sh "docker image rm -f $IMAGE_NAME:$GIT_COMMIT"
    } catch(error){}
}

def sendEmail(status) {
    mail(
            to: "$EMAIL_RECIPIENTS",
            subject: "Build $BUILD_NUMBER of ${currentBuild.fullDisplayName} has status " + status + "",
            body: "Changes:\n $currentBuild.changeSets" + "\n\n You can see details at: $BUILD_URL \n")
}
