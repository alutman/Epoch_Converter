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
    private Converter converter;
    private boolean doRun = false;
    private long startTime;

    protected Updater(ModelController modelController, Converter converter, long startTime) {
        this.modelController = modelController;
        this.converter = converter;
        this.startTime = startTime;
    }
    public void stopUpdater() {
        doRun = false;
    }

    public void timerMode(long offset) {
        long start = System.currentTimeMillis();
        while(doRun) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException: updater.java, line 49. Exiting...");
                System.exit(1);
            }
            modelController.updateGUI(((converter.getEpoch()-start)+offset) + "");
        }
    }

    public void run() {
        doRun = true;
        timerMode(startTime);

    }
}
