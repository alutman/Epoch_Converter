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
    private MODE modeI;
    private long resumeTime;

    public static enum MODE {
        REAL_TIME,
        TIMER,
        RESUME
    }
    protected Updater(ModelController modelController, Converter converter, Updater.MODE mode) {
        this.modelController = modelController;
        this.converter = converter;
        modeI = mode;
    }
    public void setResumeTime(long resumeTime) {
        this.resumeTime = resumeTime;
    }
    public void stopUpdater() {
        doRun = false;
    }
    public void realTimeMode() {
        while(doRun) {
            try {
                Thread.sleep(UpdaterDistributor.POLLING_RATE);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException: updater.java, line 37. Exiting...");
                System.exit(1);
            }
            modelController.updateGUI(converter.getEpoch()+"");
        }
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
        if(modeI == MODE.REAL_TIME) {
            realTimeMode();

        }
        else if(modeI == MODE.TIMER) {
            timerMode(0);
        }
        else if(modeI == MODE.RESUME) {
            timerMode(resumeTime);
        }

    }
}
