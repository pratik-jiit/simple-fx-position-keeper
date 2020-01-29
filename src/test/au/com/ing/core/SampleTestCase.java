package au.com.ing.core;

import au.com.ing.challenge.PositionKeeperImpl;
import au.com.ing.testingUtils.EventReader;
import au.com.ing.testingUtils.TradeEventReceiver;
import com.opencsv.CSVReader;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

public class SampleTestCase {
    private final byte WAIT_TIME = 100;

    //update the initialization of you PositionKeeperImpl
    private final PositionKeeperImpl positionKeeper = new PositionKeeperImpl();

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private final EventReader reader = new EventReader("/sample.csv");

    @Test
    public void when_reading_events_from_the_sample_file_the_expected_positions_are_achieved() throws InterruptedException {
        TradeEventReceiver receiver = new TradeEventReceiver(positionKeeper, reader);
        String expectedResult = getExpectedString();
        Thread thread = new Thread(receiver);

        executorService.execute(thread);
        Thread.sleep(WAIT_TIME);
        receiver.stop();
        executorService.shutdown();
        executorService.awaitTermination(WAIT_TIME, TimeUnit.MILLISECONDS);

        assertEquals(expectedResult.trim(), positionKeeper.printPositions().trim());
    }

    private String getExpectedString() {
        List<String[]> expected = new ArrayList<>();
        try (Reader expectedResultReader = new BufferedReader(new InputStreamReader(PositionKeeperImpl.class.getResourceAsStream("/sampleExpected.csv")))) {
            CSVReader csvReader = new CSVReader(expectedResultReader);
            expected = csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();
        for (String[] line : expected) {
            List<String> row = Arrays.stream(line).map(String::trim).collect(Collectors.toList());
            builder.append(row.toString().replaceAll("[\\[\\]]", "").trim()).append("\n");
        }
        return builder.toString();
    }
}
