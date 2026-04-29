package org.mygurukulam.ci

class GolangPipeline implements Serializable {

    def script

    GolangPipeline(script){
        this.script = script
    }


    def initialize(){

        script.stage('Initialize') {

            script.cleanWs()

        }

    }



    def checkoutCode(config){

        script.stage('Checkout Code') {

            script.git(
                url: config.GIT_REPO_URL,
                branch: config.GIT_BRANCH
            )

        }

    }



    def setupGo(config){

        script.stage('Setup Go Environment') {

            def goHome = script.tool config.GO_TOOL_NAME

            script.env.PATH =
                    "${goHome}/bin:${script.env.PATH}"

        }

    }



    def compile(){

        script.stage('Code Compilation') {

            script.sh '''
            set -e
            go mod tidy
            go build ./...
            '''

        }

    }



    def notifySlack(config,status){

        script.stage('Slack Notification') {

            script.slackSend(
                channel: config.SLACK_CHANNEL,

                message:
                "*${status}* - ${script.env.JOB_NAME} #${script.env.BUILD_NUMBER}\n${script.env.BUILD_URL}",

                color:
                status == 'SUCCESS' ?
                'good' :
                'danger'
            )

        }

    }



    def cleanup(){

        script.cleanWs()

    }



    def execute(config){

        try {

            initialize()

            checkoutCode(config)

            setupGo(config)

            compile()

            script.currentBuild.result='SUCCESS'
        }

        catch(Exception e){

            script.currentBuild.result='FAILURE'

            script.echo "Build failed: ${e}"

            script.error("Pipeline Failed")

        }

        finally {

            def status =
                script.currentBuild.result ?: 'SUCCESS'

            notifySlack(config,status)

            cleanup()

        }

    }

}
