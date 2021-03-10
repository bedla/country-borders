package cz.bedla.countries.loader

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component

@Component
class CountryDataParser(
    objectMapperBuilder: Jackson2ObjectMapperBuilder
) {
    private val objectMapper = objectMapperBuilder.build<ObjectMapper>()

    fun parseData(resource: Resource): List<JsonCountry> = resource.inputStream.use { stream ->
        logger.info("Parsing countries data")
        return@use objectMapper.readValue(stream, object : TypeReference<List<JsonCountry>>() {})
            .also {
                logger.info("Loaded ${it.size} countries")
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CountryDataParser::class.java)!!
    }
}
