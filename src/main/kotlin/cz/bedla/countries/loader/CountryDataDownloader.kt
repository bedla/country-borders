package cz.bedla.countries.loader

import cz.bedla.countries.ApplicationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
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

        val response = restTemplate.getForEntity(url, Resource::class.java)
        if (response.statusCode == HttpStatus.OK) {
            return response.body ?: error("Invalid body from $url and response $response")
        } else {
            error("Unable to load data from $url with response $response")
        }
    }
}
