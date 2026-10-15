package pl.sekankodev.exception;

public class RateNotFoundException extends NbpApiException {
    public RateNotFoundException(String message) {
        super(message);
    }
}
