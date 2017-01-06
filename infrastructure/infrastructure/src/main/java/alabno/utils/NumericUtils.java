package alabno.utils;

public class NumericUtils {

    public static long minutesToMillis(int minutes) {
        long seconds = minutes * 60;
        long millis = seconds * 1000;
        return millis;
    }
    
}
