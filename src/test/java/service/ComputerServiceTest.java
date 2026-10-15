package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sekankodev.dto.ComputerRequestDto;
import pl.sekankodev.dto.RateDto;
import pl.sekankodev.enums.SortDirection;
import pl.sekankodev.enums.SortOption;
import pl.sekankodev.exception.DataAccessException;
import pl.sekankodev.exception.InvoiceNotCreatedException;
import pl.sekankodev.model.Computer;
import pl.sekankodev.repository.ComputerRepository;
import pl.sekankodev.service.ComputerService;
import pl.sekankodev.service.NbpClient;
import pl.sekankodev.service.XmlComputerExporter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComputerServiceTest {
    @Mock
    private NbpClient nbpClient;

    @Mock
    private ComputerRepository computerRepository;

    @Mock
    private XmlComputerExporter exporter;

    @InjectMocks
    private ComputerService computerService;

    @BeforeEach
     void setUp() {
        reset(nbpClient, computerRepository, exporter);
    }

    @Nested
    class ProcessOrderTest {
        List<ComputerRequestDto> dtoList;

        @BeforeEach
        void setUp() {
            reset(nbpClient, computerRepository, exporter);
            dtoList = List.of(
                    new ComputerRequestDto("test", LocalDate.of(2026, 1, 1), BigDecimal.valueOf(100)),
                    new ComputerRequestDto("test2", LocalDate.of(2026, 1, 1), BigDecimal.valueOf(150))
            );
        }


        @Test
        public
        void emptyList_nothingHappens() {
            computerService.processOrder(List.of(), "./faktura.xml");
            verifyNoInteractions(computerRepository);
            verifyNoInteractions(exporter);
            verifyNoInteractions(nbpClient);
        }

        @Test
        public
        void pathIsBlank_throwsInvoiceNotCreatedException(){
            assertThrows(InvoiceNotCreatedException.class ,()->computerService.processOrder(dtoList, "    "));
        }

        @Test
        public
        void pathIsNull_throwsInvoiceNotCreatedException(){
            assertThrows(InvoiceNotCreatedException.class ,()->computerService.processOrder(dtoList, null));
        }

        @Test
        public
        void exporterFailed_throwsInvoiceNotCreatedException(){
            doThrow(new DataAccessException("message", new Exception())).when(exporter).createInvoice(any(), any());
            when(nbpClient.getDollarPlnRate(any(), any())).thenReturn(
                    new RateDto("test", LocalDate.of(2000,1,1), BigDecimal.valueOf(2.35), BigDecimal.valueOf(2.5))
            );
            assertThrows(InvoiceNotCreatedException.class ,()->computerService.processOrder(dtoList, "./faktura.xml"));
        }

        @Test
        void validInput_invoiceIsCreated(){
            when(nbpClient.getDollarPlnRate(any(), any())).thenReturn(
                    new RateDto("test", LocalDate.of(2000,1,1), BigDecimal.valueOf(2.35), BigDecimal.valueOf(2.5))
            );
            computerService.processOrder(dtoList, "./faktura.xml");

            verify(exporter, times(1)).createInvoice(any(), any());
            verify(computerRepository, times(1)).insert(anyList());
        }
    }

    @Nested
    class FindComputersTest {
         @Test
         void findComputers_validInput_returnsComputers(){
            List<Computer> computers = List.of(
                    new Computer("test", LocalDate.of(2000,1,1), BigDecimal.valueOf(100), BigDecimal.valueOf(120)),
                    new Computer("test2", LocalDate.of(2000,2,2), BigDecimal.valueOf(200), BigDecimal.valueOf(220))
            );
            when(computerRepository.findAll(any(), any(), any())).thenReturn(computers);
            List<Computer> result = computerService.findComputers(SortOption.NOTHING, SortDirection.ASC,"");

            verify(computerRepository, times(1)).findAll(SortOption.NOTHING, SortDirection.ASC,"");
            assertEquals(computers, result);
         }
    }
}
