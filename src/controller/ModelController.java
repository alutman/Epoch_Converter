package controller;

import controller.updater.Updater;
import controller.updater.UpdaterDistributor;
import model.ConvertError;
import model.Converter;
import view.AppFrame;

/**
 * Created by alutman on 24/02/14.
 *
 * Uses converter to create results and updates the AppFrame with them. Most of the
 * program's functionality is contained here.
 *
 */
public class ModelController {

    private AppFrame appFrame;
    private Converter converter;
    private Updater updater;
    private long resumeTime = 0;

    public ModelController(AppFrame af, Converter c) {
        appFrame = af;
        converter = c;
        UpdaterDistributor.init(this, converter);
    }
    public long getEpoch() {
        return converter.getEpoch();
    }
    public void startTimer() {
        updater = UpdaterDistributor.getNewUpdater(Updater.MODE.TIMER);
        updater.start();
    }
    public void startRealTime() {
        updater = UpdaterDistributor.getNewUpdater(Updater.MODE.REAL_TIME);
        updater.start();
    }
    public void pauseTimer(long resumeTime) {
        updater.stopUpdater();
        this.resumeTime = resumeTime;
    }
    public void resumeTimer() {
        updater = UpdaterDistributor.getNewUpdater(Updater.MODE.RESUME);
        updater.setResumeTime(resumeTime);
        updater.start();
    }
    public void stopTimer() {
        updater.stopUpdater();
    }
    public void updateGUI(String in) {
        //String in = appFrame.getInputText();
        appFrame.setInputText(in);
        long epoch = -1;
        String date;
        boolean isInt = true;

        try {
            epoch = Long.parseLong(in);
        }
        catch (NumberFormatException e){
            //Not a valid long
            if(in.matches("^\\d+$")){
                //is still a number, just too large
                appFrame.setOutputText("Epoch cannot be greater than 2^(63)-1");
                appFrame.setSpanOutputText(null);
                return;
            }
            //Has alphabetical characters, probably a date
            isInt = false;
        }
        if(isInt) {
            if(epoch <  0) {
                appFrame.setOutputText("Epoch must be positive");
                appFrame.setSpanOutputText(null);
            }
            else {
                date = converter.toHuman(epoch);
                appFrame.setOutputText(date);
                appFrame.setSpanOutputText(converter.toHumanSpan(epoch));
            }
        }
        else {
            epoch = converter.toEpoch(in);
            if(epoch == ConvertError.PARSE_ERROR.getValue()) {

                appFrame.setOutputText("Date must be in "+Converter.DATE_FORMAT+" format");
                appFrame.setSpanOutputText(null);
            }
            else if(epoch == ConvertError.RANGE_ERROR.getValue()) {
                appFrame.setOutputText("Date is outside epoch time range");
                appFrame.setSpanOutputText(null);
            }
            else appFrame.setOutputText(epoch+"");
            appFrame.setSpanOutputText(converter.toHumanSpan(epoch));
        }
    }
}
