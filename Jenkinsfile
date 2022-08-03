
podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'kaniko', image:'gcr.io/kaniko-project/executor:debug-539ddefcae3fd6b411a95982a830d987f4214251', command: 'cat', ttyEnabled: true)],
    volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false),
        configMapVolume(configMapName: 'docker-config', mountPath: '/kaniko/.docker/'),
        secretVolume(secretName: 'aws-secret', mountPath: '/root/.aws')
    ])
    {
        withCredentials([string(credentialsId: 'teethcare-password',variable: 'DB_PASSWORD')]){
            node (POD_LABEL) {
                
                stage ('checkout') {
                    checkout scm
                }


                stage('SonarQube'){
                    container('maven'){
                        withSonarQubeEnv() {
                            env.DB_TYPE = "teethcare-qa"
                            sh "mvn clean verify sonar:sonar -Dsonar.projectKey=Project-Analysis"
                        }
                    }
                }

                if(env.CHANGE_TARGET == 'dev'){
                    stage('test'){
                        container('maven'){   
                            env.DB_TYPE = "teethcare-qa"                   
                            sh "mvn test"                                     
                        }
                    }
                }

                if(env.CHANGE_TARGET == 'release'){
                        env.DB_TYPE = "teethcare-staging"
                        stage('Build Artifact'){
                            container('maven'){
                                sh 'mvn clean package'
                            }
                        }
                        stage('Build Docker Image and publish to ECR'){
                            container('kaniko'){
                                sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${GIT_COMMIT}"
                            }
                                
                        }
                        
                }
                

            
            }
        }
    }
