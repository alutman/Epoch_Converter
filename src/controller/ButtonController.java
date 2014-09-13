package controller;

import view.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by alutman on 24/02/14.
 *
 * Listens to buttons, passes input along for calculation and controls button status (enabled/disabled)
 */
public class ButtonController implements ActionListener {
    private AppFrame appFrame;
    private ModelController modelController;

    public ButtonController(AppFrame af, ModelController guiC) {
        appFrame = af;
        modelController = guiC;
        appFrame.setActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
        switch(ae.getActionCommand()){
            case "timer":
                long start = 0L;
                try {
                    start = Long.parseLong(appFrame.getInputText());
                } catch (NumberFormatException nfe) {
                    start = 0L;
                }
                modelController.startTimer(start);
                appFrame.setNonThreadButtons(false);
                break;
            case "stop":
                modelController.stopTimer();
                appFrame.setNonThreadButtons(true);
                break;
            case "max":
                modelController.updateGUI(Long.MAX_VALUE + "");
                break;
            case "min":
                modelController.updateGUI(0+"");
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
                modelController.updateGUI(modelController.getEpoch() + "");
                break;
            case "input": //When enter is pressed inside the input field, do calculate
            case "calculate":
                modelController.updateGUI(appFrame.getInputText());
                break;
        }
    }
}
