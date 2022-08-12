
podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'kaniko', image:'gcr.io/kaniko-project/executor:debug-539ddefcae3fd6b411a95982a830d987f4214251', command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'kustomize', image: 'line/kubectl-kustomize:latest', command: 'cat', ttyEnabled: true)],
                         containerTemplate(name: 'git', image: 'alpine/git:latest', command: 'cat', ttyEnabled: true)]
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


                stage('SonarQube Analysis'){
                    container('maven'){
                        withSonarQubeEnv() {
                            env.DB_TYPE = "teethcare-qa"
                            env.VERSION_NUMBER = sh script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
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
                    stage('Build Artifact'){
                        container('maven'){
                            sh 'mvn clean package'
                        }
                    }
                    stage('Build Docker Image and publish to ECR'){
                        container('kaniko'){
                            sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}"
                        }
                    }
                    stage('Deploy to QA'){
                        container('kustomize'){
                            git url: "https://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@github.com/baolongv3-kms/backend-deploy"
                            sh "git config --global user.email 'ci@ci.com'"
                            dir("backend-deploy/overlays/qa"){
                                sh "kustomize edit set image 553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}-${env.CHANGE_BRANCH}"
                            }   
                        }
                        container('git'){
                            sh "git commit -am 'Publish new version ${env.VERSION_NUMBER} to staging' && git push || echo 'no changes'"
                        }

                    }
            
                }

                if(env.CHANGE_TARGET == 'release'){
                        env.DB_TYPE = "teethcare-qa"
                        stage('test'){
                            container('maven'){   
                                env.DB_TYPE = "teethcare-qa"                   
                                sh "mvn test"                                     
                            }
                        }
                        stage('Build Artifact'){
                            container('maven'){
                                sh 'mvn clean package'
                            }
                        }
                        stage('Build Docker Image and publish to ECR'){
                            container('kaniko'){
                                sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}"
                            }
                                
                        }
                        stage('Deploy to QA'){
                            container('kustomize'){
                                sh "git clone https://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@github.com/baolongv3-kms/backend-deploy"
                                sh "git config --global user.email 'ci@ci.com'"
                                dir("backend-deploy"){
                                    sh "cd ./backend-deploy/overlays/qa && kustomize edit set image 553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}"
                                    
                                }
                            }
                            sh "git commit -am 'Publish new version ${env.VERSION_NUMBER} to staging' && git push || echo 'no changes'"
                
                        }
                }            
                

                if(env.BRANCH_NAME == 'release'){
                    stage('Deploy to Staging'){
                        container('argocd-tools'){
                            sh "git clone https://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@github.com/baolongv3-kms/backend-deploy"
                            sh "git config --global user.email 'ci@ci.com'"
                            dir("backend-deploy"){
                                sh "cd ./backend-deploy && kustomize edit set image 553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}"
                                sh "git commit -am 'Publish new version ${env.VERSION_NUMBER} to staging' && git push || echo 'no changes'"
                            }
                        }
                
                    }
                }




            
            }
        }
    }
