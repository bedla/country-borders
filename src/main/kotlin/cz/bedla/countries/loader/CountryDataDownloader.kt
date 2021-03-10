package cz.bedla.countries.loader

import cz.bedla.countries.ApplicationProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.io.FileUrlResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class CountryDataDownloader(
    private val applicationProperties: ApplicationProperties,
    restTemplateBuilder: RestTemplateBuilder
) {
    private val restTemplate: RestTemplate = restTemplateBuilder.build()
    fun downloadData(): Resource {
        val url = applicationProperties.dataUrl
        logger.info("Downloading countries data from $url")

        return when (url.scheme) {
            "file" -> {
                FileUrlResource(url.toURL())
            }
            else -> {
                val response = restTemplate.getForEntity(url, Resource::class.java)
                if (response.statusCode == HttpStatus.OK) {
                    response.body ?: error("Invalid body from $url and response $response")
                } else {
                    error("Unable to load data from $url with response $response")
                }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CountryDataDownloader::class.java)!!
    }
}
