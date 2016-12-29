package alabno.msfeedback;

import alabno.utils.NumericUtils;

public class Runner implements Runnable {

    private final MicroServiceUpdater updater;
    private static final int INTERVAL_MINUTES = 5;

    public Runner(MicroServiceUpdater updater) {
        this.updater = updater;
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
            System.out.println("HaskellMarkerRunner interrupted. Thread will be terminated");
        }
    }
}
