package controller;

import controller.updater.Updater;
import controller.updater.UpdaterDistributor;
import model.ConvertParseException;
import model.ConvertRangeException;
import model.Converter;
import view.AppFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by alutman on 24/02/14.
 *
 * Handles user input and calls Converter to produce appropriate outputs
 *
 */
public class ModelController implements KeyListener, ActionListener {

    private AppFrame appFrame;
    private Updater updater;

    public ModelController(AppFrame af) {
        appFrame = af;
        appFrame.setActionListeners(this);
        appFrame.setInputKeyListener(this);
        UpdaterDistributor.init(this);
    }

    public void setInput(String input) {
        appFrame.setInputText(input);
    }

    public void startTimer(long startPoint) {
        updater = UpdaterDistributor.getNewUpdater(startPoint);
        updater.start();
    }
    public void stopTimer() {
        updater.stopUpdater();
    }

    /**
     * Read the input field and calculate the appropriate outputs
     */
    public void updateGUI() {
        String in = appFrame.getInputText();
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
                date = Converter.epochToDateString(epoch);
                appFrame.setOutputText(date);
                appFrame.setSpanOutputText(Converter.msToHumanSpan(epoch));
            }
        }
        else {
            try {
                epoch = Converter.dateStringToEpoch(in);
                appFrame.setOutputText(epoch+"");
                appFrame.setSpanOutputText(Converter.msToHumanSpan(epoch));
            } catch (ConvertParseException e) {
                appFrame.setOutputText("Date must be in "+Converter.DATE_FORMAT+" format");
                appFrame.setSpanOutputText(null);
            } catch (ConvertRangeException e) {
                appFrame.setOutputText("Date is outside epoch time range");
                appFrame.setSpanOutputText(null);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        updateGUI();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        switch(ae.getActionCommand()){
            case "timer":
                long start = 0L;
                try {
                    start = Long.parseLong(appFrame.getInputText());
                } catch (NumberFormatException nfe) {
                    start = 0L;
                }
                startTimer(start);
                appFrame.setNonThreadButtons(false);
                break;
            case "stop":
                stopTimer();
                appFrame.setNonThreadButtons(true);
                break;
            case "max":
                appFrame.setInputText(Long.MAX_VALUE + "");
                updateGUI();
                break;
            case "min":
                appFrame.setInputText(0+"");
                updateGUI();
                break;
            case "clear":
                appFrame.setInputText(null);
                appFrame.setOutputText(null);
                appFrame.setSpanOutputText(null);
                break;
            case "swap":
                String temp = appFrame.getInputText();
                appFrame.setInputText(appFrame.getOutputText());
                appFrame.setOutputText(temp);
                break;
            case "today":
                appFrame.setInputText(Converter.getEpoch() + "");
                updateGUI();
                break;
        }
    }
}
