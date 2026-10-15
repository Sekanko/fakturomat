package pl.sekankodev.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RateDto(
        String no,
        LocalDate effectiveDate,
        BigDecimal bid,
        BigDecimal ask
) {
}
