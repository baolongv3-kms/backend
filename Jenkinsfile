pipeline{
    agent {
        kubernetes{
            yamlFile 'kubernetes-pod.yaml'
        }

    }
    stages{
        stage('checkout project'){
            steps{
                checkout
                container('maven'){
                    sh "mvn compile"
                }
            }
        }
    }
}