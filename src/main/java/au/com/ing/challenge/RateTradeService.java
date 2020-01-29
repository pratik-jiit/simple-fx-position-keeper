package au.com.ing.challenge;

import au.com.ing.core.CancelTradeEvent;
import au.com.ing.core.FxRate;
import au.com.ing.core.TradeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RateTradeService extends PositionService{

    private PositionService positionService;
    private ProcessTradeService processTradeService;

    public RateTradeService(PositionService positionService) {
        this.positionService = positionService;
    }

    public void setProcessTradeService(ProcessTradeService processTradeService) {
        this.processTradeService = processTradeService;
    }

    synchronized
    public void processRateTradeEvent(FxRate fxRate) {
        List<TradeEvent> tradeEvents = this.processTradeService.getTradeEvents()
                .stream()
                .filter(trade -> trade.getCurrencyPair().equalsIgnoreCase(fxRate.getCurrencyPair())).collect(Collectors.toList());
        tradeEvents
                .forEach(trade -> {
                    processTradeService.rollbackTrade(trade);
                    TradeEvent newTradeEvent = new TradeEvent(trade.getTradeId(),
                            trade.getVersion(),
                            trade.getTradeEventType(),
                            trade.getCurrencyPair(),
                            trade.getDirection(),
                            trade.getAmount(),
                            fxRate.getRate());
                    processTradeService.applyTrade(newTradeEvent);
                });
    }
}
