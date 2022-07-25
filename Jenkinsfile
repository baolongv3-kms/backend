def yaml = """
spec:
  securityContext:
      fsGroup: 1000
  containers:
  - name: jnlp
    env:
      - name: HOME
        value: /home/jenkins
    securityContext:
      fsGroup: 1000
      runAsGroup: 1000
      runAsUser: 1000
"""
podTemplate(containers: [containerTemplate(name: 'maven', image: 'maven' , command: 'cat', ttyEnabled: true),
                         containerTemplate(name: 'kaniko', image:'gcr.io/kaniko-project/executor:debug-539ddefcae3fd6b411a95982a830d987f4214251', command: 'cat', ttyEnabled: true)],
    volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false),
        configMapVolume(configMapName: 'docker-config', mountPath: '/kaniko/.docker/'),
        secretVolume(secretName: 'aws-secret', mountPath: '/root/.aws')
    ],
    workspaceVolume: persistentVolumeClaimWorkspaceVolume(mountPath:'/home/jenkins/agent', claimName: 'pv-workspace'),
    runAsUser:"1000",
    runAsGroup:"1000",
    yaml: yaml)
    {
        node (POD_LABEL) {
            stage ('checkout') {
                checkout scm
            }


            stage('SonarQube'){
                container('maven'){
                    withSonarQubeEnv() {
                        sh "mvn clean verify sonar:sonar -Dsonar.projectKey=Project-Analysis"
                    }
                }
            }

            if(env.BRANCH_NAME == 'dev'){
                stage('test'){
                    container('maven'){
                        when(env.BRANCH_NAME == 'dev'){
                            sh "mvn test"
                        }                   
                    }
                }
            }

            if(env.BRANCH_NAME == 'release'){
                    stage('Build Artifact'){
                        container('maven'){
                            sh 'mvn clean package'
                        }
                    }
                    stage('Build Docker Image and publish to ECR'){
                        container('kaniko'){
                        
                            sh "/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --destination=553061678476.dkr.ecr.ap-southeast-1.amazonaws.com/backend:${env.BUILD_ID}"
                        }
                        
                    }
                }
            

           
        }
    }
