package au.com.ing.challenge;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

public class Position {
    private String currencyPair;
    private BigDecimal amountBaseCurrency;
    private BigDecimal amountTermCurrency;
    private double marketRate;

    public Position(String currencyPair, BigDecimal amountBaseCurrency, BigDecimal amountTermCurrency, double marketRate) {
        this.currencyPair = currencyPair;
        this.amountBaseCurrency = amountBaseCurrency.setScale(4, RoundingMode.HALF_UP);
        this.amountTermCurrency = amountTermCurrency.setScale(4, RoundingMode.HALF_UP);
        this.marketRate = marketRate;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public BigDecimal getAmountBaseCurrency() {
        return amountBaseCurrency;
    }

    public void setAmountBaseCurrency(BigDecimal amountBaseCurrency) {
        this.amountBaseCurrency = amountBaseCurrency.setScale(4, RoundingMode.HALF_UP);
    }

    public BigDecimal getAmountTermCurrency() {
        return amountTermCurrency;
    }

    public void setAmountTermCurrency(BigDecimal amountTermCurrency) {
        this.amountTermCurrency = amountTermCurrency.setScale(4, RoundingMode.HALF_UP);
    }

    public double getMarketRate() {
        return marketRate;
    }

    public void setMarketRate(double marketRate) {
        this.marketRate = marketRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.marketRate, marketRate) == 0 &&
                Objects.equals(currencyPair, position.currencyPair) &&
                Objects.equals(amountBaseCurrency, position.amountBaseCurrency) &&
                Objects.equals(amountTermCurrency, position.amountTermCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyPair, amountBaseCurrency, amountTermCurrency, marketRate);
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        return currencyPair + ", " +
                df.format(amountBaseCurrency) + ", " +
                df.format(amountTermCurrency) + ", " +
                marketRate;
    }
}
