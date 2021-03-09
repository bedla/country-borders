package cz.bedla.countries.loader

import com.fasterxml.jackson.annotation.JsonProperty

data class JsonCountry(
    @JsonProperty("cca3")
    var id: String,
    @JsonProperty("borders")
    var borders: List<String>
)
