art {
    providedModules {
        applicationCore()
        applicationEntity()
        applicationLogging()
    }
}

dependencies {
    with(art.externalDependencyVersionsConfiguration) {
        embedded("io.github.resilience4j", "resilience4j-circuitbreaker", resilience4jVersion)
        embedded("io.github.resilience4j", "resilience4j-ratelimiter", resilience4jVersion)
        embedded("io.github.resilience4j", "resilience4j-retry", resilience4jVersion)
        embedded("io.github.resilience4j", "resilience4j-metrics", resilience4jVersion)
        embedded("io.github.resilience4j", "resilience4j-bulkhead", resilience4jVersion)
        embedded("io.github.resilience4j", "resilience4j-timelimiter", resilience4jVersion)
        embedded("io.dropwizard.metrics", "metrics-json", metricsDropwizVersion)
    }
}