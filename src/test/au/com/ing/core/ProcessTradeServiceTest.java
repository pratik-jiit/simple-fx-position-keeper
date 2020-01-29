package au.com.ing.core;

import au.com.ing.challenge.CancelTradeService;
import au.com.ing.challenge.Position;
import au.com.ing.challenge.PositionService;
import au.com.ing.challenge.ProcessTradeService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static junit.framework.TestCase.*;

public class ProcessTradeServiceTest {

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
    public void mustProcessNewTrade() {
        TradeEvent event = new TradeEvent(1l, 0, TradeEventType.NEW,
                "AUDUSD", Direction.BUY, 8000000, 0.7131);
        processTradeService.processTradeEvent(event);
        assertFalse(positionService.getPositions().isEmpty());
        assertEquals(processTradeService.getTradeEvents().size(), 1);
        assertEquals(positionService.getPositions().get(0).getCurrencyPair(), event.getCurrencyPair());
        assertEquals(positionService.getPositions().get(0).getAmountBaseCurrency(),
                BigDecimal.valueOf(event.getAmount()).setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getAmountTermCurrency(),
                BigDecimal.ZERO.subtract(BigDecimal.valueOf(event.getAmount() * event.getFxRate()))
                        .setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getMarketRate(), event.getFxRate());
    }

    @Test
    public void mustIgnoreNewTradeIfTradeAlreadyExists() {
        TradeEvent existingTradeEvent = new TradeEvent(1l, 0, TradeEventType.NEW,
                "AUDUSD", Direction.BUY, 8000000, 0.7131);
        processTradeService.getTradeEvents().add(existingTradeEvent);

        TradeEvent newTradeEvent = new TradeEvent(1l, 1, TradeEventType.NEW,
                "AUDUSD", Direction.SELL, 7000000, 0.7131);

        processTradeService.processTradeEvent(newTradeEvent);
        assertEquals(positionService.getPositions().size(), 0);
        assertEquals(processTradeService.getTradeEvents().size(), 1);
    }

    @Test
    public void mustIgnoreTradeEventIfVersionIsLowerThanExisting() {
        TradeEvent existingTradeEvent = new TradeEvent(1l, 4, TradeEventType.AMEND,
                "AUDUSD", Direction.BUY, 8000000, 0.7131);
        processTradeService.getTradeEvents().add(existingTradeEvent);

        TradeEvent newTradeEvent = new TradeEvent(1l, 1, TradeEventType.AMEND,
                "AUDUSD", Direction.SELL, 7000000, 0.7131);

        processTradeService.processTradeEvent(newTradeEvent);
        assertEquals(positionService.getPositions().size(), 0);
        assertEquals(processTradeService.getTradeEvents().size(), 1);
    }

    @Test
    public void mustApplyAmendedTradeEventIfVersionIsHigherThanExisting() {
        TradeEvent existingTradeEvent = new TradeEvent(1l, 4, TradeEventType.AMEND,
                "AUDUSD", Direction.BUY, 8000000, 0.7131);
        processTradeService.getTradeEvents().add(existingTradeEvent);
        positionService.addPosition(new Position("AUDUSD",
                BigDecimal.valueOf(existingTradeEvent.getAmount()),
                BigDecimal.ZERO.subtract(BigDecimal.valueOf(existingTradeEvent.getAmount() * existingTradeEvent.getFxRate())),
                existingTradeEvent.getFxRate()));

        TradeEvent newTradeEvent = new TradeEvent(1l, 5, TradeEventType.AMEND,
                "AUDUSD", Direction.BUY, 7000000, 0.7678);

        processTradeService.processTradeEvent(newTradeEvent);
        assertEquals(positionService.getPositions().size(), 1);
        assertEquals(processTradeService.getTradeEvents().size(), 1);

        assertEquals(positionService.getPositions().get(0).getCurrencyPair(), newTradeEvent.getCurrencyPair());
        assertEquals(positionService.getPositions().get(0).getAmountBaseCurrency(),
                BigDecimal.valueOf(newTradeEvent.getAmount()).setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getAmountTermCurrency(),
                BigDecimal.valueOf(0 - newTradeEvent.getAmount() * newTradeEvent.getFxRate())
                        .setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getMarketRate(), newTradeEvent.getFxRate());
    }

    @Test
    public void mustNotProcessCancelledTrade() {
        this.cancelTradeService.getCancelTradeEvents().add(new CancelTradeEvent(1l));
        TradeEvent event = new TradeEvent(1l, 0, TradeEventType.NEW,
                "AUDUSD", Direction.BUY, 8000000, 0.7131);
        processTradeService.processTradeEvent(event);
        assertTrue(positionService.getPositions().isEmpty());
    }

}
