podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'kaniko', image:'gcr.io/kaniko-project/executor:debug-539ddefcae3fd6b411a95982a830d987f4214251', command: 'cat', ttyEnabled: true)],
    volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false),
        configMapVolume(configMapName: 'docker-config', mountPath: '/kaniko/.docker')
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

            stage('Build Docker'){
                container('kaniko'){
                    sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=public.ecr.aws/z0e0c2y1/kms-probation-public/backend:${env.BUILD_ID}"
                }
            }
        }
    }