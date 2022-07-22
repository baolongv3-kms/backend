pipeline{
    agent {
        kubernetes{
            yamlFile 'kubernetes-pod.yaml'
        }

    }
    stages{
        stage('checkout project'){
            steps{
                checkout scm
                container('maven'){
                    sh "mvn compile"
                }
            }
        }
    }
}