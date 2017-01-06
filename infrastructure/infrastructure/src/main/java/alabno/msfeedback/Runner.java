package alabno.msfeedback;

import alabno.utils.NumericUtils;

public class Runner implements Runnable {

    private final MicroServiceUpdater updater;
    private String name;
    private static final int INTERVAL_MINUTES = 5;

    public Runner(MicroServiceUpdater updater, String name) {
        this.updater = updater;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Runner starting for: " + updater.getClass());
        try {
            while (true) {
                updater.updateTraining();
                Thread.sleep(NumericUtils.minutesToMillis(INTERVAL_MINUTES));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Runner for "+name+" interrupted. Thread will be terminated");
        }
    }
}
