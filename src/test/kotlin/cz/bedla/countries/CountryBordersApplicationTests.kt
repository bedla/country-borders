package cz.bedla.countries

import io.restassured.RestAssured
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    properties = [
        "app.data-url=file:./data/countries.json",
        "app.default-algorithm=jgrapht-dijkstra"
    ]
)
class CountryBordersApplicationTests {
    @LocalServerPort
    private var port: Int? = null

    @BeforeEach
    fun setUp() {
        RestAssured.port = port ?: error("Server port not found")
    }

    @Test
    fun routeWithDefaultAlgorithm() {
        RestAssured.get("/routing/{from}/{to}", "CZE", "ITA")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(3))
            .body("route", contains("CZE", "AUT", "ITA"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["my-custom", "jgrapht-dijkstra"])
    fun fromCzechiaToCanada(algorithm: String) {
        RestAssured.get("/routing/{from}/{to}?$algorithm", "CZE", "CAN")
            .then()
            .statusCode(400)
            .body(emptyString())
    }

    @ParameterizedTest
    @ValueSource(strings = ["my-custom", "jgrapht-dijkstra"])
    fun fromCzechiaToItaly(algorithm: String) {
        RestAssured.get("/routing/{from}/{to}?$algorithm", "CZE", "ITA")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(3))
            .body("route", contains("CZE", "AUT", "ITA"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["my-custom", "jgrapht-dijkstra"])
    fun fromCzechiaToNorway(algorithm: String) {
        RestAssured.get("/routing/{from}/{to}", "CZE", "NOR")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(4))
            .body("route", contains("CZE", "POL", "RUS", "NOR"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["my-custom", "jgrapht-dijkstra"])
    fun fromCzechiaToAustralia(algorithm: String) {
        RestAssured.get("/routing/{from}/{to}", "CZE", "AUS")
            .then()
            .statusCode(400)
            .body(emptyString())
    }

    @ParameterizedTest
    @ValueSource(strings = ["my-custom", "jgrapht-dijkstra"])
    fun fromUruguayToCanada(algorithm: String) {
        RestAssured.get("/routing/{from}/{to}", "URY", "CAN")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(11))
            .body("route", contains("URY", "BRA", "COL", "PAN", "CRI", "NIC", "HND", "GTM", "MEX", "USA", "CAN"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["my-custom", "jgrapht-dijkstra"])
    fun fromPortugalToJar(algorithm: String) {
        RestAssured.get("/routing/{from}/{to}", "PRT", "ZAF")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(11))
            .body("route", contains("PRT", "ESP", "MAR", "DZA", "LBY", "SDN", "SSD", "COD", "ZMB", "ZWE", "ZAF"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["my-custom", "jgrapht-dijkstra"])
    fun fromMalaysiaToPortugal(algorithm: String) {
        RestAssured.get("/routing/{from}/{to}", "MYS", "PRT")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(10))
            .body("route", contains("MYS", "THA", "MMR", "CHN", "RUS", "POL", "DEU", "FRA", "ESP", "PRT"))
    }

    @Test
    fun invalidFromCountry() {
        RestAssured.get("/routing/{from}/{to}", "invalid-country", "AUS")
            .then()
            .statusCode(400)
            .body("message", equalTo("findRoute.from: Invalid country identifier"))
            .body("constraintErrors", hasSize<Int>(1))
            .body("constraintErrors[0].path", equalTo("findRoute.from"))
            .body("constraintErrors[0].value", equalTo("invalid-country"))
    }

    @Test
    fun invalidToCountry() {
        RestAssured.get("/routing/{from}/{to}", "AUS", "invalid-country")
            .then()
            .statusCode(400)
            .body("message", equalTo("findRoute.to: Invalid country identifier"))
            .body("constraintErrors", hasSize<Int>(1))
            .body("constraintErrors[0].path", equalTo("findRoute.to"))
            .body("constraintErrors[0].value", equalTo("invalid-country"))
    }

    @Test
    fun invalidAlgorithmId() {
        RestAssured.get("/routing/{from}/{to}?algorithm=invalid-algo", "AUS", "CZE")
            .then()
            .statusCode(400)
            .body("message", equalTo("findRoute.algorithm: Invalid algorithm identifier"))
            .body("constraintErrors", hasSize<Int>(1))
            .body("constraintErrors[0].path", equalTo("findRoute.algorithm"))
            .body("constraintErrors[0].value", equalTo("invalid-algo"))
    }

    @Test
    fun contextLoads() {
    }
}
