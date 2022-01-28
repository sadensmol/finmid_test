package me.sadensmol.finmid.config

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ApplicationConfig {

    @Bean
    fun objectMapper(): ObjectMapper =
        ObjectMapper()
            .registerModule(kotlinModule())
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
            .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)


    @Bean
    fun kotlinModule() = KotlinModule.Builder()
        .build()
}