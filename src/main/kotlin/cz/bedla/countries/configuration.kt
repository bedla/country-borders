package cz.bedla.countries

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.net.URI

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {
    @Bean
    fun jacksonCustomizer(): Jackson2ObjectMapperBuilderCustomizer? {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder.modules(KotlinModule())
        }
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class ApplicationProperties(
    val dataUrl: URI,
    val defaultAlgorithm: String
)
