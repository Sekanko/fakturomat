package sekankodev.pl.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Computer(String name, LocalDate postDate, BigDecimal pricePln, BigDecimal priceUsd) {
    @Override
    public String toString() {
        return "Computer: " +
                "nazwa = " + name +
                ", data_księgowania = " + postDate +
                ", cena PLN = " + pricePln +
                ", cena USD = " + priceUsd;
    }
}
