package sekankodev.pl.service;

import sekankodev.pl.dto.ComputerRequestDto;
import sekankodev.pl.dto.ComputerXmlDto;
import sekankodev.pl.dto.InvoiceToXmlDto;
import sekankodev.pl.dto.RateDto;
import sekankodev.pl.enums.SortDirection;
import sekankodev.pl.enums.SortOption;
import sekankodev.pl.exception.DataAccessException;
import sekankodev.pl.exception.InvoiceNotCreatedException;
import sekankodev.pl.model.Computer;
import sekankodev.pl.repository.ComputerRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ComputerService {
    private final ComputerRepository computerRepository;
    private final NbpClient nbpClient;
    private final XmlComputerExporter exporter;
    private static final int MAX_DAYS_BACK = 7;

    public ComputerService(ComputerRepository computerRepository, NbpClient nbpClient, XmlComputerExporter exporter) {
        this.computerRepository = computerRepository;
        this.nbpClient = nbpClient;
        this.exporter = exporter;
    }

    public void processOrder(List<ComputerRequestDto> computerDtos, String strPath){
        if (computerDtos.isEmpty()) return;

        if (strPath == null || strPath.isBlank()) throw new InvoiceNotCreatedException("Invoice path cannot be empty");

        List<Computer> computers = computerDtos.stream()
                .map(c -> {
                    LocalDate postDate = c.post_date();
                    BigDecimal usdPrice = c.usdPrice();
                    BigDecimal plnPrice = calculatePlnPrice(postDate, usdPrice);

                    return new Computer(c.name(), postDate, plnPrice, usdPrice);
                }).toList();

        Path path = Path.of(strPath);

        try {
            InvoiceToXmlDto invoice = new InvoiceToXmlDto(
                    computers.stream().map(ComputerXmlDto::fromEntity).toList()
            );

            exporter.createInvoice(invoice, path);
            computerRepository.insert(computers);
        } catch (DataAccessException e){
            saveFailedProcedure(path);
            throw new InvoiceNotCreatedException("Invoice not created: " + e.getMessage());
        }
    }

    public List<Computer> findComputers(SortOption sort, SortDirection direction, String searchQuery){
        return computerRepository.findAll(sort, direction, searchQuery);
    }

    private BigDecimal calculatePlnPrice(LocalDate date, BigDecimal usdPrice){
        RateDto rateDto = nbpClient.getDollarPlnRate(date, date.minusDays(MAX_DAYS_BACK));
        return usdPrice.multiply(rateDto.bid()).setScale(2, RoundingMode.HALF_UP);
    }

    private void saveFailedProcedure(Path path){
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored){}
    }
}
