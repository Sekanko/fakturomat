package sekankodev.pl.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComputerRequestDto(
        String name,
        LocalDate post_date,
        BigDecimal usdPrice
) {
}
