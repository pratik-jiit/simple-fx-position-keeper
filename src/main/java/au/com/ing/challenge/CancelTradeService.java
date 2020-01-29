package au.com.ing.challenge;

import au.com.ing.core.CancelTradeEvent;
import au.com.ing.core.Direction;
import au.com.ing.core.TradeEvent;
import au.com.ing.core.TradeEventType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CancelTradeService extends PositionService{

    private List<CancelTradeEvent> cancelTradeEvents;
    private PositionService positionService;
    private ProcessTradeService processTradeService;

    public CancelTradeService(PositionService positionService) {
        this.cancelTradeEvents = new ArrayList<>();
        this.positionService = positionService;
    }

    public List<CancelTradeEvent> getCancelTradeEvents() {
        return cancelTradeEvents;
    }

    public void setProcessTradeService(ProcessTradeService processTradeService) {
        this.processTradeService = processTradeService;
    }

    public boolean isTradeCancelled(TradeEvent event) {
        return getCancelTradeEvents().stream().filter(x -> x.equals(event)).findFirst().isPresent();
    }

    synchronized
    public void cancelTrade(CancelTradeEvent cancelEvent) {
        if (cancelTradeEvents.contains(cancelEvent)) {
            return;
        }
        Optional<TradeEvent> tradeEvent = processTradeService.getTradeEvents()
                .stream().filter(x -> x.getTradeId() == cancelEvent.getTradeId()).findFirst();
        if(tradeEvent.isPresent()) {
            this.processTradeService.rollbackTrade(tradeEvent.get());
            this.cancelTradeEvents.add(cancelEvent);
            this.processTradeService.getTradeEvents().removeIf(x -> x.getTradeId() == cancelEvent.getTradeId());
        }
    }
}
