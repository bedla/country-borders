package cz.bedla.countries.loader

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class CountryDataParser {
    private val objectMapper = ObjectMapper()
        .registerModule(KotlinModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun parseData(resource: Resource): List<JsonCountry> = resource.inputStream.use {
        return@use objectMapper.readValue(it, object : TypeReference<List<JsonCountry>>() {})
    }
}
