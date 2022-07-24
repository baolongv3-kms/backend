podTemplate(containers: [
    containerTemplate(name: 'maven', image: 'maven', command: 'cat', ttyEnabled: true)]) {
        node (POD_LABEL) {
            stage ('test') {
                checkout scm
                container('maven') {
                while (true) {
                    sh "mvn compile"  
                }
            }
        }
    }
}