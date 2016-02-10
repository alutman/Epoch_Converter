package controller.updater;

import controller.ModelController;
import model.Converter;

/**
 * Created by alutman on 24/02/14.
 *
 * Constantly updates with new times. Used for real time output and timers.
 * Each instance of updater is used once and cannot be switched to another mode.
 *
 */
public class Updater extends Thread {
    private ModelController modelController;
    private boolean doRun = false;
    private long startTime;

    protected Updater(ModelController modelController, long startTime) {
        this.modelController = modelController;
        this.startTime = startTime;
    }
    public void stopUpdater() {
        doRun = false;
    }

    public void timerMode(long offset) {
        long start = System.currentTimeMillis();
        while(doRun) {
            try {
                Thread.sleep(UpdaterDistributor.POLLING_RATE);
            } catch (InterruptedException e) {
                stopUpdater();
            }
            modelController.setInput(((Converter.getEpoch()-start)+offset) + "");
            modelController.updateGUI();
        }
    }

    public void run() {
        doRun = true;
        timerMode(startTime);

    }
}
