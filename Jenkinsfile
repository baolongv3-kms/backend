podTemplate(yaml: readTrusted('kubernetes-pod.yaml')) {
        node (POD_LABEL) {
            stage ('test') {
                checkout scm
                container('maven') {
                    sh 'mvn compile'
            }
        }
    }
}