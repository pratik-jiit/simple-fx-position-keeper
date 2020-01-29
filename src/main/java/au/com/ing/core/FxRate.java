package au.com.ing.core;

import java.util.Objects;

public class FxRate implements Event {
    private String currencyPair;
    private double rate;

    public FxRate(String currencyPair, double rate) {
        this.currencyPair = currencyPair;
        this.rate = rate;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FxRate fxRate = (FxRate) o;
        return Double.compare(fxRate.rate, rate) == 0 &&
                Objects.equals(currencyPair, fxRate.currencyPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyPair, rate);
    }
}
