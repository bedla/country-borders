package cz.bedla.countries

import io.restassured.RestAssured
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
    fun fromCzechiaToItaly() {
        RestAssured.get("/routing/{from}/{to}", "CZE", "ITA")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(3))
            .body("route", contains("CZE", "AUT", "ITA"))
    }

    @Test
    fun fromCzechiaToNorway() {
        RestAssured.get("/routing/{from}/{to}", "CZE", "NOR")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(4))
            .body("route", contains("CZE", "POL", "RUS", "NOR"))
    }

    @Test
    fun fromCzechiaToAustralia() {
        RestAssured.get("/routing/{from}/{to}", "CZE", "AUS")
            .then()
            .statusCode(400)
            .body(emptyString())
    }

    @Test
    fun fromUruguayToCanada() {
        RestAssured.get("/routing/{from}/{to}", "URY", "CAN")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(11))
            .body("route", contains("URY", "BRA", "COL", "PAN", "CRI", "NIC", "HND", "GTM", "MEX", "USA", "CAN"))
    }

    @Test
    fun fromPortugalToJar() {
        RestAssured.get("/routing/{from}/{to}", "PRT", "ZAF")
            .then()
            .statusCode(200)
            .body("route", hasSize<Int>(11))
            .body("route", contains("PRT", "ESP", "MAR", "DZA", "LBY", "SDN", "SSD", "COD", "ZMB", "ZWE", "ZAF"))
    }

    @Test
    fun fromMalaysiaToPortugal() {
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
    fun customAlgorithm() {
        RestAssured.get("/routing/{from}/{to}?algorithm=my-custom", "AUS", "CZE")
            .then()
            .statusCode(500)
            .body("message", equalTo("I am not implemented"))
    }

    @Test
    fun contextLoads() {
    }
}
