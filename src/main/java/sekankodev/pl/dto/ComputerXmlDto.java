package sekankodev.pl.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import sekankodev.pl.model.Computer;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

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
                return new ComputerXmlDto(computer.getName(), computer.getPostDate(), computer.getPricePln(), computer.getPriceUsd());
        }
}
