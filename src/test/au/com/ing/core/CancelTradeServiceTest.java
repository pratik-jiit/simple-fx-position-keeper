package au.com.ing.core;

import au.com.ing.challenge.CancelTradeService;
import au.com.ing.challenge.PositionService;
import au.com.ing.challenge.ProcessTradeService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertEquals;

public class CancelTradeServiceTest {

    private PositionService positionService;
    private ProcessTradeService processTradeService;
    private CancelTradeService cancelTradeService;

    @Before
    public void init() {
        this.positionService = new PositionService();
        this.processTradeService =  new ProcessTradeService(positionService);
        this.cancelTradeService = new CancelTradeService(positionService);
        this.processTradeService.setCancelTradeService(this.cancelTradeService);
        this.cancelTradeService.setProcessTradeService(processTradeService);
    }

    @Test
    public void mustCancelAnExistingTrade() {
        TradeEvent event1 = new TradeEvent(1l, 0, TradeEventType.NEW,
                "AUDUSD", Direction.BUY, 8000000, 0.7131);
        TradeEvent event2 = new TradeEvent(1l, 0, TradeEventType.NEW,
                "AUDUSD", Direction.SELL, 5000000, 0.7671);
        CancelTradeEvent cancelEvent = new CancelTradeEvent(1l);

        processTradeService.processTradeEvent(event1);
        processTradeService.processTradeEvent(event2);
        assertEquals(processTradeService.getTradeEvents().size(), 1);

        cancelTradeService.cancelTrade(cancelEvent);

        assertEquals(processTradeService.getTradeEvents().size(), 0);
        assertEquals(positionService.getPositions().get(0).getCurrencyPair(), event1.getCurrencyPair());
        assertEquals(positionService.getPositions().get(0).getAmountBaseCurrency(), BigDecimal.ZERO.setScale(4));
        assertEquals(positionService.getPositions().get(0).getAmountTermCurrency(), BigDecimal.ZERO.setScale(4));
    }

}
