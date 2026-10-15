package pl.sekankodev.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import pl.sekankodev.model.Computer;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonRootName(value = "komputer")
public record ComputerXmlDto(
        @JacksonXmlProperty(localName = "nazwa")
        String name,
        @JacksonXmlProperty(localName = "data_ksiegowania")
        LocalDate postDate,
        @JacksonXmlProperty(localName = "koszt_USD")
        BigDecimal pricePln,
        @JacksonXmlProperty(localName = "koszt_PLN")
        BigDecimal priceUsd
) {
        public static ComputerXmlDto fromEntity(Computer computer){
                return new ComputerXmlDto(computer.name(), computer.postDate(), computer.pricePln(), computer.priceUsd());
        }
}
