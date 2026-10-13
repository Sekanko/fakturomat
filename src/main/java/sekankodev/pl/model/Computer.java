package sekankodev.pl.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Computer {
    private final String name;
    private final LocalDate postDate;
    private final BigDecimal pricePln;
    private final BigDecimal priceUsd;

    public Computer(String name, LocalDate postDate, BigDecimal pricePln, BigDecimal priceUsd) {
        this.name = name;
        this.postDate = postDate;
        this.pricePln = pricePln;
        this.priceUsd = priceUsd;
    }

    public String getName() {
        return name;
    }

    public LocalDate getPostDate() {
        return postDate;
    }

    public BigDecimal getPricePln() {
        return pricePln;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    @Override
    public String toString() {
        return "Computer: " +
                "nazwa = " + name +
                ", data_księgowania = " + postDate +
                ", cena PLN = " + pricePln +
                ", cena USD = " + priceUsd;
    }
}
