package cz.bedla.countries.loader

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.UrlResource
import java.util.stream.Stream

class CountryDataParserTest {
    private var parser = CountryDataParser()

    @Test
    fun empty() {
        val list = parser.parseData(ByteArrayResource("""[]""".toByteArray()))
        assertThat(list)
            .isEmpty()
    }

    @Test
    @Disabled("For local run only, test is dependent on state of GitHub data (for local debugging only)")
    fun largeJsonFromGithub() {
        val list =
            parser.parseData(UrlResource("https://raw.githubusercontent.com/mledoze/countries/master/countries.json"))
        assertThat(list)
            .hasSize(250)
            .contains(JsonCountry("CZE", listOf("AUT", "DEU", "POL", "SVK")))
    }

    @Test
    fun countryWithEmptyBorders() {
        val list = parser.parseData(
            ByteArrayResource(
                """
                [
                    {
                        "foo": 123,
                        "cca3": "DE",
                        "borders": []
                    }
                ]
            """.trimIndent().toByteArray()
            )
        )
        assertThat(list)
            .hasSize(1)
            .containsExactly(JsonCountry("DE", emptyList<String>()))
    }

    @Test
    fun validCountry() {
        val list = parser.parseData(
            ByteArrayResource(
                """
                [
                    {
                        "foo": 123,
                        "cca3": "CZ",
                        "borders": ["SK"]
                    }
                ]
            """.trimIndent().toByteArray()
            )
        )
        assertThat(list)
            .hasSize(1)
            .containsExactly(JsonCountry("CZ", listOf("SK")))
    }

    @Test
    fun validCountries() {
        val list = parser.parseData(
            ByteArrayResource(
                """
                [
                    {
                        "foo": 123,
                        "name": {
                            "common": "xxx",
                            "official": "bbb"
                        },
                        "tld": [".cz"],
                        "cca3": "CZ",
                        "borders": ["SK"]
                    },
                    {
                        "foo": 456,
                        "cca3": "SK",
                        "borders": ["CZ", "DE"]
                    },
                    {
                        "foo": 789,
                        "cca3": "FOO",
                        "borders": []
                    }
                ]
            """.trimIndent().toByteArray()
            )
        )
        assertThat(list)
            .hasSize(3)
            .containsExactly(
                JsonCountry("CZ", listOf("SK")),
                JsonCountry("SK", listOf("CZ", "DE")),
                JsonCountry("FOO", emptyList())
            )
    }

    @ParameterizedTest
    @MethodSource("invalidDataProvider")
    fun invalidData(json: String, expectedErrorMessage: String, expectedExceptionType: Class<*>) {
        assertThatThrownBy { parser.parseData(ByteArrayResource(json.toByteArray())) }
            .hasMessageContaining(expectedErrorMessage)
            .isInstanceOf(expectedExceptionType)
    }

    companion object {
        @JvmStatic
        fun invalidDataProvider(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    """{}""",
                    "Cannot deserialize",
                    MismatchedInputException::class.java
                ),
                arguments(
                    """
                    [
                        {
                            "foo": 123
                        }
                    ]
                    """.trimIndent(),
                    "value failed for JSON property cca3 due to missing (therefore NULL) value",
                    MissingKotlinParameterException::class.java
                ),
                arguments(
                    """
                    [
                        {
                            "foo": 123,
                            "cca3": "CZ"
                        }
                    ]
                    """.trimIndent(),
                    "value failed for JSON property borders due to missing (therefore NULL) value",
                    MissingKotlinParameterException::class.java
                )
            )
        }
    }
}
