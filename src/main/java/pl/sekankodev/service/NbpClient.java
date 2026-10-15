package pl.sekankodev.service;

import pl.sekankodev.dto.NbpRateResponseDto;
import pl.sekankodev.dto.RateDto;
import pl.sekankodev.exception.NbpApiException;
import pl.sekankodev.exception.RateNotFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Comparator;

public class NbpClient {
    private final String defaultApiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NbpClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this("https://api.nbp.pl/api/", httpClient, objectMapper);
    }

    // testing purpose only
    public NbpClient(String defaultApiUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.defaultApiUrl = defaultApiUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public RateDto getDollarPlnRate(LocalDate maxDate, LocalDate minDate) {
        String maxDateString = maxDate.toString();
        String minDateString = minDate.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(defaultApiUrl + "/exchangerates/rates/c/usd/" + minDateString + "/" + maxDateString + "/"))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new RateNotFoundException("Exchange rate not found: " + response.statusCode());
            }

            if (response.statusCode() != 200) {
                throw new NbpApiException("Api responded with code: " + response.statusCode());
            }

            NbpRateResponseDto result = objectMapper.readValue(response.body(), NbpRateResponseDto.class);

            if (result.rates() == null || result.rates().isEmpty()) {
                throw new NbpApiException("Api responded with no rates: " + response.body());
            }

            return result.rates()
                    .stream()
                    .max(Comparator.comparing(RateDto::effectiveDate))
                    .orElseThrow(() -> new RateNotFoundException("Exchange rate not found beetween dates: " + minDateString + " and " + maxDateString));

        } catch (IOException | InterruptedException e){
            throw new NbpApiException("Request failed:" + e.getMessage(), e);
        } catch (JacksonException e) {
            throw new NbpApiException("Map JSON to DTO failed:" + e.getMessage(), e);
        }
    }

}
