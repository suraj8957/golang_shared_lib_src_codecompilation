import org.mygurukulam.ci.GolangPipeline

def call(Map config=[:]) {

    node {

        def defaultConfig = [

            GIT_REPO_URL:
            'https://github.com/suraj8957/employee-api.git',

            GIT_BRANCH:
            'main',

            GO_TOOL_NAME:
            'go',

            SLACK_CHANNEL:
            '#ci-operation-notifications'

        ]


        config = defaultConfig + config


        def pipeline =
            new GolangPipeline(this)

        pipeline.execute(config)

    }

}
