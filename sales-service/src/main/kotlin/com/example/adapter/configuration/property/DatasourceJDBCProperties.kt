package com.example.adapter.configuration.property

data class DatasourceJDBCProperties(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val driver: String,
    val minimumIdle: Int,
    val maximumPoolSize: Int,
    val idleTimeoutMs: Long,
    val maxLifetimeMs: Long,
    val connectionTestQuery: String,
    val leakDetectionThreshold: Long,
    val initQuery: String,
    var connectionTimeout: Long,
) {

    data class Builder(
        var jdbcUrl: String = "",
        var username: String = "",
        var password: String = "",
        var driver: String = "org.postgresql.Driver",
        var minimumIdle: Int = 1,
        var maximumPoolSize: Int = 10,
        var idleTimeoutMs: Long = 600000,
        var maxLifetimeMs: Long = 1800000,
        var connectionTestQuery: String = "SELECT 1",
        var leakDetectionThreshold: Long = 6000,
        var initQuery: String = "",
        var connectionTimeout: Long = 5000,
    ) {
        fun build(): DatasourceJDBCProperties {
            return DatasourceJDBCProperties(
                jdbcUrl = jdbcUrl,
                username = username,
                password = password,
                driver = driver,
                minimumIdle = minimumIdle,
                maximumPoolSize = maximumPoolSize,
                idleTimeoutMs = idleTimeoutMs,
                maxLifetimeMs = maxLifetimeMs,
                connectionTestQuery = connectionTestQuery,
                leakDetectionThreshold = leakDetectionThreshold,
                initQuery = initQuery,
                connectionTimeout = connectionTimeout,
            )
        }
    }
}
