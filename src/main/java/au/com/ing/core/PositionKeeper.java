package au.com.ing.core;

public interface PositionKeeper {
    void processEvent(Event event);
    String printPositions();
}
