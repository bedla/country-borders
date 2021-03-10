package cz.bedla.countries.service

import cz.bedla.countries.loader.CountryDataDownloader
import cz.bedla.countries.loader.CountryDataParser
import cz.bedla.countries.roads.CountriesDatabase
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch

@Service
class InitializationService(
    private val database: CountriesDatabase,
    private val downloader: CountryDataDownloader,
    private val parser: CountryDataParser
) : InitializingBean {
    override fun afterPropertiesSet() {
        logger.info("Initializing countries database")
        val stopWatch = StopWatch().also { it.start() }

        val resource = downloader.downloadData()
        val data = parser.parseData(resource)
        database.load(data)
        stopWatch.stop()
        logger.info("Countries loaded in ${stopWatch.totalTimeMillis}ms")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(InitializationService::class.java)!!
    }
}
