package pl.sekankodev.dto;

import java.util.List;

public record NbpRateResponseDto(
        char table,
        String currency,
        String code,
        List<RateDto> rates
) {
}
