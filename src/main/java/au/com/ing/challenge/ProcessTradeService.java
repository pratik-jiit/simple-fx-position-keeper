package au.com.ing.challenge;

import au.com.ing.core.Direction;
import au.com.ing.core.TradeEvent;
import au.com.ing.core.TradeEventType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcessTradeService{

    private List<TradeEvent> tradeEvents;
    private PositionService positionService;
    private CancelTradeService cancelTradeService;

    public ProcessTradeService(PositionService positionService) {
        this.tradeEvents = new ArrayList<>();
        this.positionService = positionService;
    }

    public List<TradeEvent> getTradeEvents() {
        return tradeEvents;
    }

    public void setCancelTradeService(CancelTradeService cancelTradeService) {
        this.cancelTradeService = cancelTradeService;
    }

    public void processTradeEvent(TradeEvent event) {
        // Verify that the Trade is not already cancelled
        if (this.cancelTradeService.isTradeCancelled(event)) {
            System.out.println("This trade is already cancelled. Ignoring");
            return;
        }
        long tradeId = event.getTradeId();
        // Check if this trade already exists
        Optional<TradeEvent> existingTrade = this.tradeEvents.stream()
                .filter(tradeEvent -> tradeEvent.getTradeId() == tradeId).findFirst();

        if (existingTrade.isPresent()) {
            // Rule - Ignore if the event is NEW and there already exists a trade with the same trade ID
            // OR if the version ID of the event is less than that of the existing trade.
            if (event.getTradeEventType() == TradeEventType.NEW ||
                    event.getVersion() < existingTrade.get().getVersion()) {
                return;
            }
            rollbackTrade(existingTrade.get());
        }
        applyTrade(event);
    }

    synchronized
    protected void rollbackTrade(TradeEvent event) {
        Optional<Position> position = this.positionService.getPositions().stream()
                .filter(p -> p.getCurrencyPair().equalsIgnoreCase(event.getCurrencyPair())).findFirst();
        if (!position.isPresent()) {
            throw new RuntimeException("Position does not exist and cannot be rolled back.");
        }
        if (event.getDirection() == Direction.BUY) {
            sellTrade(position.get(), event);
        } else {
            buyTrade(position.get(), event);
        }
        this.tradeEvents.remove(event);
    }

    synchronized
    protected void applyTrade(TradeEvent event) {
        Optional<Position> position = this.positionService.getPositions().stream()
                .filter(p -> p.getCurrencyPair().equalsIgnoreCase(event.getCurrencyPair())).findFirst();
        if (!position.isPresent()) {
            Position newPosition = new Position(event.getCurrencyPair(), BigDecimal.ZERO, BigDecimal.ZERO, event.getFxRate());
            this.positionService.getPositions().add(newPosition);
            position = Optional.of(newPosition);
        }
        if (event.getDirection() == Direction.BUY) {
            buyTrade(position.get(), event);
        } else {
            sellTrade(position.get(), event);
        }
        this.tradeEvents.add(event);
    }

    private void buyTrade(Position position, TradeEvent event) {
        position.setAmountBaseCurrency(position.getAmountBaseCurrency()
                .add(BigDecimal.valueOf(event.getAmount())));
        position.setAmountTermCurrency(position.getAmountTermCurrency()
                .subtract(BigDecimal.valueOf(event.getAmount() * event.getFxRate())));
        position.setMarketRate(event.getFxRate());
    }

    private void sellTrade(Position position, TradeEvent event) {
        position.setAmountBaseCurrency(position.getAmountBaseCurrency()
                .subtract(BigDecimal.valueOf(event.getAmount())));
        position.setAmountTermCurrency(position.getAmountTermCurrency()
                .add(BigDecimal.valueOf(event.getAmount() * event.getFxRate())));
        position.setMarketRate(event.getFxRate());
    }
}
