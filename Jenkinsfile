pipeline { 
    agent { label 'linux1' }

    environment {
        EMAIL_RECIPIENTS = 'vstefan02@gmail.com'
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
        
        stage('Package') {
            steps {
                sh 'mvn clean package'
                archiveArtifacts '**/target/*.jar'
                fingerprint '**/target/*.jar'
            }
        }
        
        stage('Deploy') {
            agent { label 'master' }
            steps {
                echo "${BUILD_NUMBER}"
                sh 'echo Shell $PWD'
                sh "cp /var/lib/jenkins/jobs/docker/jobs/wms-pipeline-deploy/builds/${BUILD_NUMBER}/archive/target/wms-1.0.jar /home/stefan/wms-deploy/target"
                sh "cd /home/stefan/wms-deploy && docker build -t wms/app -f Dockerfile-app ."
                sh "cd /home/stefan/wms-deploy && docker-compose up -d"
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

def sendEmail(status) {
    mail(
            to: "$EMAIL_RECIPIENTS",
            subject: "Build $BUILD_NUMBER of ${currentBuild.fullDisplayName} has status " + status,
            body: "Changes:\n " + getChangeString() + "\n\n Check console output at: $BUILD_URL/console" + "\n")
}

