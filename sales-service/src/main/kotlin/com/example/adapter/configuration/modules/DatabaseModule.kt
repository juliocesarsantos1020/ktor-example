package com.example.adapter.configuration.modules

import com.example.adapter.configuration.property.DatasourceJDBCProperties
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import javax.sql.DataSource

class DatabaseConst {
    companion object {
        const val dataSourceNameRW = "applicationRW"
        const val databasePropertiesRW = "${dataSourceNameRW}DatabaseProperties"
        const val dataSourceRW = "${dataSourceNameRW}Datasource"
        const val databaseRW = "${dataSourceNameRW}Database"

        const val dataSourceNameRO = "applicationRO"
        const val databasePropertiesRO = "${dataSourceNameRO}DatabaseProperties"
        const val dataSourceRO = "${dataSourceNameRO}Datasource"
        const val databaseRO = "${dataSourceNameRO}Database"
    }
}

val database = DI.Module("database") {
    importOnce(configuration)

    bind(DatabaseConst.databasePropertiesRW) from singleton {
        datasourceJDBCProperties(
            instance(),
            DatabaseConst.dataSourceNameRW,
        )
    }

    bind(DatabaseConst.dataSourceRW) from singleton {
        datasourceJDBC(
            instance(DatabaseConst.databasePropertiesRW),
        )
    }

    bind(DatabaseConst.databaseRW) from singleton {
        Database.connect(instance(DatabaseConst.dataSourceRW) as DataSource)
    }

    bind(DatabaseConst.databasePropertiesRO) from singleton {
        datasourceJDBCProperties(
            instance(),
            DatabaseConst.dataSourceNameRO,
        )
    }

    bind(DatabaseConst.dataSourceRO) from singleton {
        datasourceJDBC(
            instance(DatabaseConst.databasePropertiesRO),
        )
    }

    bind(DatabaseConst.databaseRO) from singleton {
        Database.connect(instance(DatabaseConst.dataSourceRO) as DataSource)
    }
}

private fun datasourceJDBCProperties(config: ApplicationConfig, datasource: String): DatasourceJDBCProperties {
    val builder = DatasourceJDBCProperties.Builder()
    config.getString("datasource.$datasource.jdbc.url")?.let { builder.jdbcUrl = it }
    config.getString("datasource.$datasource.jdbc.username")?.let { builder.username = it }
    config.getString("datasource.$datasource.jdbc.password")?.let { builder.password = it }
    config.getInt("datasource.$datasource.jdbc.minimumIdle")?.let { builder.minimumIdle = it }
    config.getInt("datasource.$datasource.jdbc.maximumPoolSize")?.let { builder.maximumPoolSize = it }
    config.getLong("datasource.$datasource.jdbc.idleTimeoutMs")?.let { builder.idleTimeoutMs = it }
    config.getLong("datasource.$datasource.jdbc.maxLifetimeMs")?.let { builder.maxLifetimeMs = it }
    config.getLong("datasource.$datasource.jdbc.connectionTimeout")?.let { builder.connectionTimeout = it }
    config.getString("datasource.$datasource.jdbc.connectionTestQuery")?.let { builder.connectionTestQuery = it }
    config.getLong("datasource.$datasource.jdbc.leakDetectionThreshold")?.let { builder.leakDetectionThreshold = it }
    config.getString("datasource.$datasource.jdbc.driver")?.let { builder.driver = it }

    return builder.build()
}

private fun datasourceJDBC(
    properties: DatasourceJDBCProperties,
): DataSource {
    val dataSource = HikariDataSource()

    dataSource.driverClassName = properties.driver
    dataSource.jdbcUrl = properties.jdbcUrl
    dataSource.username = properties.username
    dataSource.password = properties.password
    dataSource.minimumIdle = properties.minimumIdle
    dataSource.maximumPoolSize = properties.maximumPoolSize
    dataSource.idleTimeout = properties.idleTimeoutMs
    dataSource.maxLifetime = properties.maxLifetimeMs
    dataSource.connectionTestQuery = properties.connectionTestQuery
    dataSource.leakDetectionThreshold = properties.leakDetectionThreshold
    dataSource.connectionInitSql = properties.initQuery
    dataSource.isRegisterMbeans = true
    dataSource.connectionTimeout = properties.connectionTimeout
    return dataSource
}
