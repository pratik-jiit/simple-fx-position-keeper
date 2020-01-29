package au.com.ing.core;

public class CancelTradeEvent extends AbstractTradeEvent {

    public CancelTradeEvent(long tradeId) {
        super(tradeId);
    }
}
