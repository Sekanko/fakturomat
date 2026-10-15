package pl.sekankodev.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonRootName(value = "faktura")
public record InvoiceToXmlDto(
        @JacksonXmlProperty(localName = "komputer")
        @JacksonXmlElementWrapper(useWrapping = false)
        List<ComputerXmlDto> computers
) {

}
