package cz.bedla.countries

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class CountryBordersApplication

fun main(args: Array<String>) {
    runApplication<CountryBordersApplication>(*args)
}
