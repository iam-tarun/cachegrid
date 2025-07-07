pipeline {
    agent {
        label 'docker-agent'
    }

    environment {
        APP_NAME = 'CacheGrid'
        BUILD_ENV = "dev"

        BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d_%H%M%S', returnStdout: true).trim()

        IMAGE_TAG = "${BUILD_NUMBER}-${BUILD_TIMESTAMP}"
        FULL_IMAGE_NAME = "${APP_NAME}:${IMAGE_TAG}"
    }

    options {

        buildDiscarder(logRotator(numToKeepStr: '10'))

        timeout(time: 30, unit: 'MINUTES')

        disableConcurrentBuilds()

        timestamps()

        retry(1)
    }

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'production'],
            description: 'Target environment'
        )
        string(
            name: 'BRANCH_NAME',
            defaultValue: 'develop',
            description: 'Git branch to build'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip running tests'
        )
        text(
            name: 'RELEASE_NOTES',
            defaultValue: '',
            description: 'Release notes for this build'
        )
    }

    stages {
        stage('Initialize') {
            steps {
                script {

                    def currentDate = new Date().format('yyyy-MM-dd')
                    def gitCommit = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()

                    echo "Starting build for ${APP_NAME}"
                    echo "Build Date: ${currentDate}"
                    echo "Build Number: ${BUILD_NUMBER}"
                    echo "ENVIRONMENT: ${params.ENVIRONMENT}"
                    echo "Branch: ${params.BRANCH_NAME}"
                    echo "Git Commit: ${gitCommit}"

                    env.GIT_COMMIT_SHORT = gitCommit.take(7)
                    env.CURRENT_DATE = currentDate
                }
            }
        }
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/iam-tarun/cachegrid'
                    ]],
                    extensions: [
                        [$class: 'CloneOption', depth: 1, shallow: true],
                        [$class: 'CheckoutOption', timeout: 10]
                    ]
                ])

                sh '''
                    echo "Git Information:"
                    echo "Repository: $(git config --get remote.origin.url)"
                    echo "Branch: $(git rev-parse --abbrev-ref HEAD)"
                    echo "Commit: $(git rev-parse HEAD)"
                    echo "Author: $(git log -1 --pretty=format:'%an <%ae>')"
                    echo "Message: $(git log -1 --pretty=format:'%s')"
                '''
            }
        }
        stage('Build') {
            environment {
                NODE_ENV = 'dev'
                STAGE_NAME = 'build'
            }

            steps {
                echo " Building ${APP_NAME} version ${IMAGE_TAG} "

                sh '''
                    echo "current directory: $(pwd)"
                    echo "Files in directory:"
                    ls -la
                    echo "Building application"
                    docker build .
                    docker-compose -d docker-compose.dev.yml
                    echo "Build completed successfully"
                '''

                sh "echo 'Building for environment: ${params.ENVIRONMENT}'"
                sh "echo 'Full image name will be: ${FULL_IMAGE_NAME}'"
            }
        }
    }


}