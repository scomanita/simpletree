package ro.comanitza.simpletree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    private TestUtil () {}

    public static List<Transport> readFromResource (final String resourceName) throws IOException {

        List<Transport> transports = new ArrayList<>();

        InputStream is = TestUtil.class.getClassLoader().getResourceAsStream(resourceName);
        BufferedReader buff = new BufferedReader(new InputStreamReader(is));

        while (true) {

            String line = buff.readLine();

            if (line == null) {
                return transports;
            }

            String[] arr = line.split(",");

            Transport t = new Transport();
            t.setGender(arr[0]);
            t.setHasCar(arr[1]);
            t.setCostPerKm(arr[2]);
            t.setIncome(arr[3]);

            if (arr.length == 5) {
                t.setTransportation(arr[4]);
            }

            transports.add(t);
        }
    }
}
