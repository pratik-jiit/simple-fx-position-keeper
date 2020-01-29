package au.com.ing.core;

import java.util.Objects;

public abstract class AbstractTradeEvent implements Event {

    private long tradeId;

    protected AbstractTradeEvent(long tradeId) {
        this.tradeId = tradeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractTradeEvent)) return false;
        AbstractTradeEvent that = (AbstractTradeEvent) o;
        return tradeId == that.tradeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId);
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }
}
