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
                sh 'mvn -B -ntp clean install'
            }
        }
    }
}