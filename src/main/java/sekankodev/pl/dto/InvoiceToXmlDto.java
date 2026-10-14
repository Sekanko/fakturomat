package sekankodev.pl.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JsonRootName(value = "faktura")
public record InvoiceToXmlDto(
        @JacksonXmlProperty(localName = "komputer")
        @JacksonXmlElementWrapper(useWrapping = false)
        List<ComputerXmlDto> computers
) {

}
