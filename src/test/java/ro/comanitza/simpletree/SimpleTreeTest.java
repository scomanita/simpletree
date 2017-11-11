package ro.comanitza.simpletree;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

public class SimpleTreeTest {

    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private final List<String> EXPECTED_RESULTS_AS_STRINGS = Arrays.asList(("Transport{gender='Male', hasCar='0', costPerKm='Cheap', income='Low', transportation='null'} -> Bus\n" +
            "Transport{gender='Male', hasCar='1', costPerKm='Cheap', income='Low', transportation='null'} -> Bus\n" +
            "Transport{gender='Male', hasCar='1', costPerKm='Cheap', income='Medium', transportation='null'} -> Bus\n" +
            "Transport{gender='Female', hasCar='0', costPerKm='Cheap', income='Low', transportation='null'} -> Bus\n" +
            "Transport{gender='Male', hasCar='1', costPerKm='Cheap', income='Medium', transportation='null'} -> Bus\n" +
            "Transport{gender='Male', hasCar='0', costPerKm='Standard', income='Medium', transportation='null'} -> Train\n" +
            "Transport{gender='Female', hasCar='1', costPerKm='Standard', income='Medium', transportation='null'} -> Train\n" +
            "Transport{gender='Female', hasCar='1', costPerKm='Expensive', income='High', transportation='null'} -> Car\n" +
            "Transport{gender='Male', hasCar='2', costPerKm='Expensive', income='Medium', transportation='null'} -> Car\n" +
            "Transport{gender='Female', hasCar='2', costPerKm='Expensive', income='Medium', transportation='null'} -> Car\n" +
            "Transport{gender='Male', hasCar='0', costPerKm='Cheap', income='Low', transportation='null'} -> Bus\n" +
            "Transport{gender='Male', hasCar='1', costPerKm='Cheap', income='Medium', transportation='null'} -> Bus\n" +
            "Transport{gender='Female', hasCar='1', costPerKm='Cheap', income='Medium', transportation='null'} -> Train\n" +
            "Transport{gender='Male', hasCar='1', costPerKm='Cheap', income='Low', transportation='null'} -> Bus\n" +
            "Transport{gender='Male', hasCar='1', costPerKm='Cheap', income='Medium', transportation='null'} -> Bus\n" +
            "Transport{gender='Male', hasCar='0', costPerKm='Standard', income='Medium', transportation='null'} -> Train\n" +
            "Transport{gender='Female', hasCar='1', costPerKm='Standard', income='Medium', transportation='null'} -> Train\n" +
            "Transport{gender='Female', hasCar='1', costPerKm='Expensive', income='High', transportation='null'} -> Car\n" +
            "Transport{gender='Male', hasCar='2', costPerKm='Expensive', income='Medium', transportation='null'} -> Car\n" +
            "Transport{gender='Female', hasCar='2', costPerKm='Expensive', income='Medium', transportation='null'} -> Car").split("\\n"));

    @Test
    public void testUsage () throws IOException {
        DecisionTree<Transport> decider = new DecisionTreeImpl<>();
        decider.learn(TestUtil.readFromResource("transport.txt"));

        TestUtil.readFromResource("testTransport.txt").forEach((e) -> {
            Assert.assertTrue("Expected value not found", EXPECTED_RESULTS_AS_STRINGS.contains(e + " -> " + decider.outcome(e).orElse(null))); });

    }
}