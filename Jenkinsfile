pipeline{
    agent {
        kubernetes{
            yamlFile 'kubernetes-pod.yaml'
        }

    }
    stages{
        stage('checkout project'){
            checkout
            container('maven'){
                stage('Build a Maven project') {
                    sh 'mvn -B -ntp clean install'
                }
            }
        }
    }
}