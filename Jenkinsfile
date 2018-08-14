pipeline {
    agent { label 'linux1' }
    
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
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package'
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
            mail to:"vstefan02@gmail.com",
                 subject:"Build \"${currentBuild.fullDisplayName}\" has status ${currentBuild.currentResult}",
                 body:"Build ${currentBuild.fullDisplayName} is OK."
               
            }
            
        failure {
            mail to:"vstefan02@gmail.com",
                 subject:"Build ${currentBuild.fullDisplayName} has status ${currentBuild.currentResult}",
                 body:"Something went wrong with ${currentBuild.fullDisplayName}, please check it at ${env.BUILD_URL}"
        }
    }
}
