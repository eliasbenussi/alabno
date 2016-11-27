package alabno.msfeedback.haskellupdater;

import alabno.utils.NumericUtils;

/**
 * Refreshes the training suite at period intervals
 *
 */
public class HaskellMarkerRunner implements Runnable {

    private static final int INTERVAL_MINUTES = 5;
    
    private final HaskellMarkerUpdater updater;

    // package visibility
    HaskellMarkerRunner(HaskellMarkerUpdater updater) {
        this.updater = updater;
    }
    
    @Override
    public void run() {
        System.out.println("HaskellMarkerRunner: starting...");
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
