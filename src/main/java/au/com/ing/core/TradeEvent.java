package au.com.ing.core;

import java.util.Objects;

public class TradeEvent extends AbstractTradeEvent {
    private int version;
    private TradeEventType tradeEventType;
    private String currencyPair;
    private Direction direction;
    private double amount;
    private double fxRate;

    public TradeEvent(long tradeId, int version, TradeEventType tradeEventType, String currencyPair, Direction direction, double amount, double fxRate) {
        super(tradeId);
        this.version = version;
        this.tradeEventType = tradeEventType;
        this.currencyPair = currencyPair;
        this.direction = direction;
        this.amount = amount;
        this.fxRate = fxRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeEvent)) return false;
        if (!super.equals(o)) return false;
        TradeEvent trade = (TradeEvent) o;
        return version == trade.version &&
                Double.compare(trade.amount, amount) == 0 &&
                Double.compare(trade.fxRate, fxRate) == 0 &&
                tradeEventType == trade.tradeEventType &&
                currencyPair.equals(trade.currencyPair) &&
                direction == trade.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), version, tradeEventType, currencyPair, direction, amount, fxRate);
    }

    public int getVersion() {
        return version;
    }

    public TradeEventType getTradeEventType() {
        return tradeEventType;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public Direction getDirection() {
        return direction;
    }

    public double getAmount() {
        return amount;
    }

    public double getFxRate() {
        return fxRate;
    }
}
