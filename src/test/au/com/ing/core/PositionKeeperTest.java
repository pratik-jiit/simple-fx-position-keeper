package au.com.ing.core;

import au.com.ing.challenge.Position;
import au.com.ing.challenge.PositionKeeperImpl;
import au.com.ing.challenge.PositionService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertEquals;

public class PositionKeeperTest {

    private PositionService positionService;
    private PositionKeeperImpl positionKeeper;

    @Before
    public void init() {
        this.positionService = new PositionService();
        this.positionKeeper = new PositionKeeperImpl(this.positionService);
    }

    @Test
    public void positionsMustBePrintedCorrectly() {
        positionService.addPosition(new Position("AUDUSD", BigDecimal.valueOf(8000000),
                BigDecimal.valueOf(-5646400), 0.7058));
        positionService.addPosition(new Position("EURUSD", BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(-5676000), 1.1352));

        String positionsResult = positionKeeper.printPositions().trim();
        String expectedResult = getExpectedString().trim();
        assertEquals(expectedResult, positionsResult);
    }

    private String getExpectedString() {
        return "CurrencyPair, AmountBaseCurrency, AmountTermCurrency, MarketRate\n" +
                "AUDUSD, 8000000.00, -5646400.00, 0.7058\n" +
                "EURUSD, 5000000.00, -5676000.00, 1.1352\n";
    }
}
