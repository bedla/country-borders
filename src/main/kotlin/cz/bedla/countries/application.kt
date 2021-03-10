package cz.bedla.countries

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CountryBordersApplication

fun main(args: Array<String>) {
    runApplication<CountryBordersApplication>(*args)
}
