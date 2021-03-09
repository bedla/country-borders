package cz.bedla.countries.loader

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.common.Slf4jNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import cz.bedla.countries.ApplicationProperties
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import java.net.URI

class CountryDataDownloaderTest {

    private lateinit var properties: ApplicationProperties

    @BeforeEach
    fun setUp() {
        WireMock.resetAllRequests()

        properties = ApplicationProperties(URI("http://localhost:${wireMockServer.port()}/hello.json"))
    }

    @Test
    fun downloadContent() {
        stubFor(
            get(urlEqualTo("/hello.json"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/plain; charset=utf-8")
                        .withBody("hello")
                )
        )

        val resource = CountryDataDownloader(properties, RestTemplateBuilder()).downloadData()
        assertThat(IOUtils.toString(resource.inputStream))
            .isEqualTo("hello")

        assertThat(findUnmatchedRequests())
            .isEmpty()
    }

    @Test
    fun emptyBody() {
        stubFor(
            get(urlEqualTo("/hello.json"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/plain; charset=utf-8")
                        .withBody("")
                )
        )

        assertThatThrownBy {
            CountryDataDownloader(properties, RestTemplateBuilder()).downloadData()
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Invalid body from http://localhost:")
            .hasMessageContaining("/hello.json and response <200,")
    }

    @Test
    fun invalidResponseCode() {
        stubFor(
            get(urlEqualTo("/hello.json"))
                .willReturn(
                    aResponse()
                        .withStatus(201)
                        .withBody("xxx")
                )
        )

        assertThatThrownBy {
            CountryDataDownloader(properties, RestTemplateBuilder()).downloadData()
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Unable to load data from http://localhost:")
            .hasMessageContaining("/hello.json with response <201,")
    }

    companion object {
        @JvmStatic
        private lateinit var wireMockServer: WireMockServer

        @JvmStatic
        @BeforeAll
        fun setupAll() {
            wireMockServer = WireMockServer(
                options()
                    .notifier(Slf4jNotifier(true))
                    .stubRequestLoggingDisabled(false)
                    .dynamicPort()
            )
            wireMockServer.start()
            WireMock.configureFor(wireMockServer.port())
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}
