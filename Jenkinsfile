podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true)],
    workspaceVolume: dynamicPVC(accessModes: 'ReadWriteMany', requestsSize: "10Gi")  
    ) {
        node (POD_LABEL) {
            stage ('test') {
                checkout scm
                container('maven') {
                    sh 'mvn compile'
            }
        }
    }
}