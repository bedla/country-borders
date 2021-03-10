package cz.bedla.countries.service

import cz.bedla.countries.loader.CountryDataLoader
import cz.bedla.countries.loader.CountryDataParser
import cz.bedla.countries.roads.CountriesDatabase
import cz.bedla.countries.utils.measureTimeMillis
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

@Service
class InitializationService(
    private val database: CountriesDatabase,
    private val loader: CountryDataLoader,
    private val parser: CountryDataParser
) : InitializingBean {
    override fun afterPropertiesSet() {
        logger.info("Initializing countries database")

        measureTimeMillis({ millis, _ -> logger.info("Countries loaded in ${millis}ms") }) {
            val resource = loader.downloadData()
            val data = parser.parseData(resource)
            database.load(data)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(InitializationService::class.java)!!
    }
}
