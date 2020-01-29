package au.com.ing.challenge;

import au.com.ing.core.*;

import javax.swing.event.CaretEvent;

public class PositionKeeperImpl implements PositionKeeper {

    private PositionService positionService;
    private ProcessTradeService processTradeService;
    private CancelTradeService cancelTradeService;
    private RateTradeService rateTradeService;

    public PositionKeeperImpl() {
        this.positionService = new PositionService();
        this.processTradeService = new ProcessTradeService(this.positionService);
        this.cancelTradeService = new CancelTradeService(this.positionService);
        this.processTradeService.setCancelTradeService(this.cancelTradeService);
        this.cancelTradeService.setProcessTradeService(this.processTradeService);
        this.rateTradeService = new RateTradeService(this.positionService);
        this.rateTradeService.setProcessTradeService(this.processTradeService);
    }

    public PositionKeeperImpl(PositionService positionService) {
        this.positionService = positionService;
    }

    @Override
    public void processEvent(Event event) {
        //Your implementation
        if(event instanceof TradeEvent) {
            processTradeService.processTradeEvent((TradeEvent) event);
        } else if (event instanceof CancelTradeEvent) {
            cancelTradeService.cancelTrade((CancelTradeEvent) event);
        } else if (event instanceof FxRate) {
            rateTradeService.processRateTradeEvent((FxRate) event);
        }
    }

    @Override
    public String printPositions() {
        //Your implementation
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PositionService.HEADER).append("\n");
        positionService.getPositions().stream().forEach(position -> stringBuilder.append(position).append("\n"));
        return stringBuilder.toString();
    }
}
