package au.com.ing.testingUtils;

import au.com.ing.challenge.PositionKeeperImpl;
import au.com.ing.core.Event;


public class TradeEventReceiver implements Runnable {

    private static int numProcessed = 0;
    private final PositionKeeperImpl positionKeeper;
    private final EventReader eventReader;
    private boolean running = true;

    /**
     * An event receiver. Acts as a socket that receives events. Events are received from the specified EventReader.
     *
     * @param positionKeeper Applicant's {@code PositionKeeperImpl}
     * @param eventReader    An {@code EventReader} containing the events.
     */
    public TradeEventReceiver(PositionKeeperImpl positionKeeper, EventReader eventReader) {
        this.positionKeeper = positionKeeper;
        this.eventReader = eventReader;
    }

    @Override
    public void run() {
        while (running) {
            Event newEvent = eventReader.read();
            if (newEvent != null) {
                numProcessed++;
                positionKeeper.processEvent(newEvent);
            }
        }
    }

    public void stop() {
        running = false;
    }

    public static int getNumProcessed() {
        return numProcessed;
    }
}
