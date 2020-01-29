package au.com.ing.challenge;

import java.util.ArrayList;
import java.util.List;

public class PositionService {
    public static final String HEADER = "CurrencyPair, AmountBaseCurrency, AmountTermCurrency, MarketRate";
    protected List<Position> positions;

    public PositionService() {
        this.positions = new ArrayList<>();
    }

    synchronized
    public void addPosition(Position position) {
        getPositions().add(position);
    }

    public List<Position> getPositions() {
        return positions;
    }
}
