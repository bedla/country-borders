package cz.bedla.countries

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.URI

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class ApplicationProperties(
    val dataUrl: URI
)
