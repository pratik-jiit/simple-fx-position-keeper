package au.com.ing.core;

import au.com.ing.challenge.CancelTradeService;
import au.com.ing.challenge.PositionService;
import au.com.ing.challenge.ProcessTradeService;
import au.com.ing.challenge.RateTradeService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static junit.framework.TestCase.assertEquals;

public class RateTradeServiceTest {

    private PositionService positionService;
    private ProcessTradeService processTradeService;
    private CancelTradeService cancelTradeService;
    private RateTradeService rateTradeService;

    @Before
    public void init() {
        this.positionService = new PositionService();
        this.processTradeService = new ProcessTradeService(positionService);
        this.cancelTradeService = new CancelTradeService(this.positionService);
        this.processTradeService.setCancelTradeService(this.cancelTradeService);
        this.cancelTradeService.setProcessTradeService(this.processTradeService);
        this.rateTradeService = new RateTradeService(this.positionService);
        this.rateTradeService.setProcessTradeService(this.processTradeService);
    }

    @Test
    public void mustChangeTheFXRateForEveryTradeInAPosition() {
        TradeEvent event1 = new TradeEvent(1l, 0, TradeEventType.NEW,
                "AUDUSD", Direction.BUY, 8000000, 0.7131);
        TradeEvent event2 = new TradeEvent(2l, 0, TradeEventType.NEW,
                "AUDUSD", Direction.SELL, 5000000, 0.7671);
        FxRate fxRateEvent = new FxRate("AUDUSD", 0.85);

        processTradeService.processTradeEvent(event1);
        processTradeService.processTradeEvent(event2);
        assertEquals(processTradeService.getTradeEvents().size(), 2);
        assertEquals(positionService.getPositions().get(0).getCurrencyPair(), event1.getCurrencyPair());
        assertEquals(positionService.getPositions().get(0).getAmountBaseCurrency(),
                BigDecimal.valueOf(event1.getAmount() - event2.getAmount()).setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getAmountTermCurrency(),
                BigDecimal.ZERO
                        .subtract(BigDecimal.valueOf(event1.getAmount() * event1.getFxRate()))
                        .add(BigDecimal.valueOf(event2.getAmount() * event2.getFxRate()))
                        .setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getMarketRate(), event2.getFxRate());

        rateTradeService.processRateTradeEvent(fxRateEvent);

        assertEquals(processTradeService.getTradeEvents().size(), 2);
        assertEquals(positionService.getPositions().get(0).getCurrencyPair(), event1.getCurrencyPair());
        assertEquals(positionService.getPositions().get(0).getAmountBaseCurrency(),
                BigDecimal.valueOf(event1.getAmount() - event2.getAmount()).setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getAmountTermCurrency(),
                BigDecimal.ZERO
                        .subtract(BigDecimal.valueOf(event1.getAmount() * fxRateEvent.getRate()))
                        .add(BigDecimal.valueOf(event2.getAmount() * fxRateEvent.getRate()))
                        .setScale(4, RoundingMode.HALF_UP));
        assertEquals(positionService.getPositions().get(0).getMarketRate(), fxRateEvent.getRate());
    }
}
