package sekankodev.pl.service;

import sekankodev.pl.dto.InvoiceToXmlDto;
import sekankodev.pl.exception.InvoiceNotCreatedException;
import tools.jackson.dataformat.xml.XmlMapper;

import java.nio.file.Path;

public class XmlComputerExporter {
    private final XmlMapper xmlMapper;

    public XmlComputerExporter(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    public void createInvoice(InvoiceToXmlDto computers, Path path) {
        try {
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), computers);
        } catch (Exception e) {
            throw new InvoiceNotCreatedException("Invoice not created: " + e.getMessage());
        }
    }
}
