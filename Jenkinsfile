
podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'kaniko', image:'gcr.io/kaniko-project/executor:debug-539ddefcae3fd6b411a95982a830d987f4214251', command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'tools', image: 'argoproj/argo-cd-ci-builder', command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'argocd-cli', image: 'kenshin17/argocd-cli', command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'git', image: 'alpine/git:latest', command: '/bin/sh', ttyEnabled: true)],
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
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                    stage('Build Docker Image and publish to ECR'){
                        container('kaniko'){
                            sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}-${env.CHANGE_BRANCH}"
                        }
                    }
                    stage('Deploy to QA'){
                        container('tools'){
                            branchName = "${env.VERSION_NUMBER}-${env.CHANGE_BRANCH}".toLowerCase()
                            sh "git clone https://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@github.com/baolongv3-kms/backend-deploy"
                            sh "git config --global user.email 'ci@ci.com'"
                            sh "chmod -R 777 ./backend-deploy"
                            dir('backend-deploy'){
                                gitBranchExist = sh(returnStatus: true, script: "git show-ref --verify refs/remotes/origin/${env.VERSION_NUMBER}-${env.CHANGE_BRANCH}")
                                if(gitBranchExist == 0){
                                    sh "git checkout ${branchName}"
                                } else{
                                    sh "git checkout -b ${branchName}"
                                }
                            }
                            dir('backend-deploy/overlays/qa'){
                                sh "kustomize edit set image 553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${branchName}"
                                sh "kustomize edit set namesuffix ${branchName}"
                            }                    
                            dir('backend-deploy'){
                                sh "git commit --allow-empty -am 'Publish new version ${env.VERSION_NUMBER} to QA'"
                                sh "git push origin HEAD"
                            }
                        }

                        container('argocd-cli'){
                            withCredentials([string(credentialsId: 'argocd-token',variable: 'ARGOCD_AUTH_TOKEN')]){
                                env.ARGOCD_SERVER = "a6e044b5ce8b84442a276c9a2ca3a6a3-1501782069.ap-southeast-1.elb.amazonaws.com"
                                if(gitBranchExist == 0){
                                    sh "argocd --insecure --grpc-web app sync backend-qa-${branchName} --resource apps:Deployment:qa-teethcare-backend --prune --replace --force"
                                }else{
                                    sh "argocd --insecure --grpc-web app create backend-qa-${branchName} --repo git@github.com:baolongv3-kms/backend-deploy --path overlays/qa --revision ${branchName} --sync-policy automated --dest-server https://kubernetes.default.svc"
                                }
                            }
                        }

                    } 
                }

                // if(env.CHANGE_TARGET == 'release'){
                //     env.DB_TYPE = "teethcare-qa"
                //     stage('test'){
                //         container('maven'){   
                //             env.DB_TYPE = "teethcare-qa"                   
                //             sh "mvn test"                                     
                //         }
                //     }
                //     stage('Build Artifact'){
                //         container('maven'){
                //             sh 'mvn clean package'
                //         }
                //     }
                //     stage('Build Docker Image and publish to ECR'){
                //         container('kaniko'){
                //             sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}"
                //         }
                            
                //     }
                //     stage('Deploy to Staging'){
                //         container('kustomize'){
                //             sh "git clone https://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@github.com/baolongv3-kms/backend-deploy"
                //             sh "git config --global user.email 'ci@ci.com'"
                //             dir("backend-deploy"){
                //                 sh "cd ./backend-deploy/overlays/qa && kustomize edit set image 553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.VERSION_NUMBER}"
                                
                //             }
                //         }
                //         sh "git commit -am 'Publish new version ${env.VERSION_NUMBER} to staging' && git push || echo 'no changes'"
            
                //     }
                // }          
            }
        }
    }
