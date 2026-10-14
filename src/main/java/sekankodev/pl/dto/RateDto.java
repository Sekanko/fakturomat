package sekankodev.pl.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RateDto(
        String no,
        LocalDate effectiveDate,
        BigDecimal bid,
        BigDecimal ask
) {
}
