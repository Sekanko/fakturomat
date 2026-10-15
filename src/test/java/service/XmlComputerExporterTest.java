package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sekankodev.pl.dto.ComputerXmlDto;
import sekankodev.pl.dto.InvoiceToXmlDto;
import sekankodev.pl.exception.InvoiceNotCreatedException;
import sekankodev.pl.service.XmlComputerExporter;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlComputerExporterTest {
    XmlMapper xmlMapper;
    XmlComputerExporter exporter;

    InvoiceToXmlDto invoiceToXmlDto;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        xmlMapper = new XmlMapper();
        exporter = new XmlComputerExporter(xmlMapper);

        List<ComputerXmlDto> computers = List.of(
                        new ComputerXmlDto("test", LocalDate.now(), BigDecimal.valueOf(100), BigDecimal.valueOf(120)),
                        new ComputerXmlDto("test2", LocalDate.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(220))
                );

        invoiceToXmlDto = new InvoiceToXmlDto(computers);
    }

    @Test
    void createInvoice_catalogDoesNotExist_throwsInvoiceNotCreatedException(){
        Path file = tempDir.resolve("nie/ma/katalogu/items.xml");
        assertThrows(InvoiceNotCreatedException.class, () -> exporter.createInvoice(invoiceToXmlDto, file));
    }

    @Test
    void createInvoice_pathIsNull_throwsInvoiceNotCreatedException(){
        assertThrows(InvoiceNotCreatedException.class, () -> exporter.createInvoice(invoiceToXmlDto, null));
    }

    @Test
    void createInvoice_pathIsEmpty_throwsInvoiceNotCreatedException(){
        assertThrows(InvoiceNotCreatedException.class, () -> exporter.createInvoice(invoiceToXmlDto, Paths.get("")));
    }

    @Test
    void createInvoice_validInput_invoiceIsCreated() throws IOException {
        Path file = tempDir.resolve("items.xml");
        exporter.createInvoice(invoiceToXmlDto, file);

        assertTrue(Files.exists(file));

        String xml = Files.readString(file);

        assertTrue(xml.contains("<faktura>"));
        assertTrue(xml.contains("<nazwa>test</nazwa>"));
        assertTrue(xml.contains("<koszt_USD>100</koszt_USD>"));
        assertTrue(xml.contains("<koszt_PLN>120</koszt_PLN>"));
        assertTrue(xml.contains("<data_ksiegowania>"));
        assertTrue(xml.contains("<nazwa>test2</nazwa>"));
    }

}
