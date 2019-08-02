buildscript {
    val artifactory_user: String by settings
    val artifactory_contextUrl: String by settings
    val artifactory_password: String by settings

    repositories {
        maven {
            url = uri("$artifactory_contextUrl/gradle-virtual")
            credentials {
                username = artifactory_user
                password = artifactory_password
            }
        }
    }

    dependencies {
        classpath("ru.adk:application-gradle-plugin:1.+")
    }
}
apply(plugin = "adkSettings")

include("application-config")
include("application-config-extensions")
include("application-config-groovy")
include("application-config-remote")
include("application-config-remote-api")
include("application-config-typesafe")
include("application-config-yaml")
include("application-configurator")
include("application-configurator-api")
include("application-core")
include("application-entity")
include("application-example")
include("application-example-api")
include("application-generator")
include("application-grpc")
include("application-grpc-client")
include("application-grpc-server")
include("application-http")
include("application-http-client")
include("application-http-json")
include("application-http-server")
include("application-http-xml")
include("application-json")
include("application-kafka-consumer")
include("application-kafka-producer")
include("application-logging")
include("application-metrics")
include("application-metrics-http")
include("application-module-executor")
include("application-network-manager")
include("application-platform")
include("application-platform-api")
include("application-protobuf")
include("application-protobuf-generated")
include("application-reactive-service")
include("application-remote-scheduler")
include("application-remote-scheduler-api")
include("application-rocks-db")
include("application-rsocket")
include("application-scheduler")
include("application-scheduler-db-adapter-api")
include("application-service")
include("application-soap")
include("application-soap-client")
include("application-soap-server")
include("application-sql")
include("application-state")
include("application-state-api")
include("application-tarantool")
include("application-xml")