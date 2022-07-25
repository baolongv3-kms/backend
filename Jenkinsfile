podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'kaniko', image:'gcr.io/kaniko-project/executor:debug-539ddefcae3fd6b411a95982a830d987f4214251', command: 'cat', ttyEnabled: true)],
    volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false),
        configMapVolume(configMapName: 'docker-config', mountPath: '/kaniko/.docker/'),
        secretVolume(secretName: 'aws-secret', mountPath: '/root/.aws')
    ])
    {
        node (POD_LABEL) {
            stage ('checkout') {
                checkout scm
            }

            stage('SonarQube Analysis') {
                
            }

            stage('SonarQube'){
                container('maven'){
                    withSonarQubeEnv() {
                        sh "mvn clean verify sonar:sonar -Dsonar.projectKey=Project-Analysis"
                    }
                }
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
                    sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.BUILD_ID}"
                }
            }
        }
    }