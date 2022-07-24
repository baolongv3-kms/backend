podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true)],
    volumes: [
  persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false)
  ])
    {
        node (POD_LABEL) {
            stage ('checkout') {
                checkout scm
            }
            // stage('test'){
            //     container('maven'){
            //         sh 'mvn test'
            //     }
            // }

            stage('build'){
                container('maven'){
                    sh 'mvn clean package'
                }
            }
        }
    }