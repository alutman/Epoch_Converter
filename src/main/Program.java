package main;

import controller.ModelController;
import view.AppFrame;
import view.DPIController;

/**
 * Created by alutman on 19/02/14.
 *
 * Assembles the various parts of the program and links them together.
 *
 */
public class Program {

    public static final String VERSION = "1.3";

    public static void main(String args[]) {
        DPIController.scaleFontsToDPI();
        AppFrame af = new AppFrame();
        new ModelController(af);

    }
}
