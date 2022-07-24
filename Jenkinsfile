podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true)],
    volumes: [
  persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false)
  ])
     {
        node (POD_LABEL) {
            stage ('test') {
                checkout scm
                container('maven') {
                    sh 'mvn compile'
            }
        }
    }
}