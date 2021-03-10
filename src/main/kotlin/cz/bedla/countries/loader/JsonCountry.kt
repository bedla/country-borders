package cz.bedla.countries.loader

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonCountry(
    @JsonProperty("cca3")
    var id: String,
    @JsonProperty("borders")
    var borders: List<String>
)
