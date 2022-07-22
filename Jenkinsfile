pipeline{
    agent any
    podTemplate(containers: [
        containerTemplate(name: 'maven', image: 'maven:3.8.1-jdk-8', command: 'sleep', args: '99d',volumes: [
  persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false)
  ]),
  ])
    node(POD_LABEL){
        stage('checkout project'){
            checkout
            container('maven'){
                stage('Build a Maven project') {
                    sh 'mvn -B -ntp clean install'
                }
            }
        }
    }
}