package cz.bedla.countries.service

import cz.bedla.countries.roads.CountriesDatabase
import cz.bedla.countries.roads.SearchAlgorithm
import org.apache.commons.lang3.Validate.validState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch

@Service
class SearchService(
    private val database: CountriesDatabase,
    private val algorithmBeans: List<SearchAlgorithm>
) : InitializingBean {
    private val algorithms = mutableMapOf<String, SearchAlgorithm>()

    fun searchRoute(fromCountry: String, toCountry: String, algorithmId: String): List<String> {
        validate(fromCountry, toCountry, algorithmId)

        if (fromCountry == toCountry) {
            logger.info("Found empty route from $fromCountry to $toCountry")
            return emptyList()
        }

        val stopWatch = StopWatch().also { it.start() }
        val searchAlgorithm = algorithms[algorithmId.asKey()]!!
        return searchAlgorithm.findRoute(fromCountry, toCountry, database.graph)
            .also {
                stopWatch.stop()
                logger.info("Found route from $fromCountry to $toCountry using algorithm $algorithmId with " +
                        "result $it and time ${stopWatch.totalTimeMillis}ms")
            }
    }

    override fun afterPropertiesSet() {
        algorithmBeans.forEach {
            val key = it.getIdentifier().asKey()
            val previousAlgorithm = algorithms.put(key, it)
            validState(
                previousAlgorithm == null,
                "Duplicate algorithm key $key of algorithms $it and $previousAlgorithm"
            )
        }
    }

    private fun validate(fromCountry: String, toCountry: String, algorithmId: String) {
        if (database.notContainsCountry(fromCountry)) {
            error("Invalid from-country identifier: $fromCountry")
        }

        if (database.notContainsCountry(toCountry)) {
            error("Invalid to-country identifier: $toCountry")
        }

        if (!algorithms.containsKey(algorithmId.asKey())) {
            error("Invalid algorithm-id: $algorithmId")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SearchService::class.java)!!

        fun String.asKey() = this.toLowerCase()
    }
}
