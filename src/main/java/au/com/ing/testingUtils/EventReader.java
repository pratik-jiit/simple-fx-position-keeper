package au.com.ing.testingUtils;

import au.com.ing.challenge.PositionKeeperImpl;
import au.com.ing.core.CancelTradeEvent;
import au.com.ing.core.Direction;
import au.com.ing.core.Event;
import au.com.ing.core.FxRate;
import au.com.ing.core.TradeEvent;
import au.com.ing.core.TradeEventType;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventReader {

    private static int numRead = 0;
    private List<Event> events;
    private static Logger LOG = Logger.getLogger(EventReader.class);

    /**
     * Create a reader that reads events from the specified csv file.
     *
     * @param pathToFile {@code String} Path to the csv file.
     */
    public EventReader(String pathToFile) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(PositionKeeperImpl.class.getResourceAsStream(pathToFile)));
                Stream<String> stream = reader.lines()
        ) {
            events = stream.map(line -> {
                Event event;
                String[] strings = line.split(",");
                if (strings.length > 2) {
                    event = new TradeEvent(Long.parseLong(strings[0].trim()), Integer.parseInt(strings[1].trim()),
                            TradeEventType.valueOf(strings[2].trim()), strings[3].trim(), Direction.valueOf(strings[4].trim()),
                            Double.parseDouble(strings[5].trim()), Double.parseDouble(strings[6].trim()));

                } else if (strings.length > 1) {
                    event = new FxRate(strings[0].trim(), Double.parseDouble(strings[1].trim()));
                } else {
                    event = new CancelTradeEvent(Long.parseLong(strings[0].trim()));
                }
                return event;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            LOG.fatal("Unable to read events!", e);
        }
    }

    public Event read() {
        if (!events.isEmpty()) {
            Event event = events.get(0);
            events.remove(0);
            numRead++;
            return event;
        } else {
            return null;
        }
    }

    public static int getNumRead() {
        return numRead;
    }
}
