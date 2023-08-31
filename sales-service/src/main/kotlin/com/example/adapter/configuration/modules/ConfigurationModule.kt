package com.example.adapter.configuration.modules

import com.example.adapter.configuration.plugins.JacksonExtension
import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import mu.KotlinLogging
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val logger = KotlinLogging.logger { }

val serialization = DI.Module("serialization") {
    bind() from singleton { objectMapper() }
}

fun objectMapper(): ObjectMapper {
    return JacksonExtension.jacksonObjectMapper
}

val configuration = DI.Module("configuration") {
    logger.info { "Current profile: default" }

    bind<ApplicationConfig>() with singleton { HoconApplicationConfig(ConfigFactory.defaultApplication()) }
}

val injector = DI {
    import(configuration)
    import(serialization)
    /*    import(services)
        import(health)
        import(migration)
        import(mappers)*/
}

fun ApplicationConfig.getString(fieldName: String): String? {
    return propertyOrNull(fieldName)?.getString()
}

fun ApplicationConfig.getInt(fieldName: String): Int? {
    return getString(fieldName)?.toInt()
}

fun ApplicationConfig.getLong(fieldName: String): Long? {
    return getString(fieldName)?.toLong()
}

fun ApplicationConfig.getBoolean(fieldName: String): Boolean? {
    return getString(fieldName)?.toBoolean()
}
