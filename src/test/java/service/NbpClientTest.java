package service;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sekankodev.dto.RateDto;
import pl.sekankodev.exception.NbpApiException;
import pl.sekankodev.service.NbpClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@WireMockTest
public class NbpClientTest {
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private NbpClient nbpClient;

    @BeforeEach
    public void setUp(WireMockRuntimeInfo wm) {
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        nbpClient = new NbpClient(wm.getHttpBaseUrl(),httpClient, objectMapper);
    }

    @Test
    public void getDollarPlnRate_validInput_returnsRateDto(){
        stubFor(get("/exchangerates/rates/c/usd/2000-01-01/2000-01-08/").willReturn(okJson(
                """
            {
              "table": "C",
              "currency": "Dolar Amerykański",
              "code": "USD",
              "rates": [
                {
                  "no": "C/C/C",
                  "effectiveDate": "2000-01-01",
                  "bid": 1.2345,
                  "ask": 1.2346
                },
                {
                  "no": "B/B/B",
                  "effectiveDate": "2000-01-08",
                  "bid": 1.5432,
                  "ask": 1.6432
                },
                {
                  "no": "A/A/A",
                  "effectiveDate": "2000-01-02",
                  "bid": 1.2356,
                  "ask": 1.2367
                }
              ]
            }
            """
        )));

        RateDto expected = new RateDto(
                "B/B/B",
                LocalDate.of(2000,1,8),
                BigDecimal.valueOf(1.5432),
                BigDecimal.valueOf(1.6432)
        );

        RateDto result = nbpClient.getDollarPlnRate(LocalDate.of(2000,1,8), LocalDate.of(2000,1,1));

        assertEquals(expected, result);
    }

    @Test
    public void getDollarPlnRate_invalidInput_throwsNbpApiException(){
         stubFor(get("/exchangerates/rates/c/usd/2000-01-01/2000-01-08/").willReturn(notFound()));
         assertThrows(NbpApiException.class, () -> nbpClient.getDollarPlnRate(LocalDate.of(2000,1,8), LocalDate.of(2000,1,1)));
    }

    @Test
    public void getDollarPlnRate_serverError_throwsNbpApiException(){
        stubFor(get("/exchangerates/rates/c/usd/2000-01-01/2000-01-08/").willReturn(serverError()));
        assertThrows(NbpApiException.class, () -> nbpClient.getDollarPlnRate(LocalDate.of(2000,1,8), LocalDate.of(2000,1,1)));
    }

    @Test
    public void getDollarPlnRate_invalidBody_throwsNbpApiException(){
        stubFor(get("/exchangerates/rates/c/usd/2000-01-01/2000-01-08/").willReturn(okJson(
                """
              {
                "idk": "test"
              }
              """
        )));
        assertThrows(NbpApiException.class, () -> nbpClient.getDollarPlnRate(LocalDate.of(2000,1,8), LocalDate.of(2000,1,1)));
    }

    @Test
    public void getDollarPlnRate_invalidJson_throwsNbpApiException(){
        stubFor(get("/exchangerates/rates/c/usd/2000-01-01/2000-01-08/").willReturn(okJson("{ this is not json")));
        assertThrows(NbpApiException.class, () -> nbpClient.getDollarPlnRate(LocalDate.of(2000,1,8), LocalDate.of(2000,1,1)));
    }

    @Test
    public void getDollarPlnRate_invalidResponse_throwsNbpApiException() throws IOException, InterruptedException {
        stubFor(get("/exchangerates/rates/c/usd/2000-01-01/2000-01-08/").willReturn(okJson("""
                {
                  "key": "value"
                }
                """)));

        Thread.currentThread().interrupt();
        try {
            assertThrows(NbpApiException.class, () -> nbpClient.getDollarPlnRate(LocalDate.of(2000,1,8), LocalDate.of(2000,1,1)));
        } finally {
            Thread.interrupted();
        }

    }


}
