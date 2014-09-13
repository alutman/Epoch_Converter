package main;

import controller.ButtonController;
import controller.ModelController;
import model.Converter;
import view.AppFrame;

/**
 * Created by alutman on 19/02/14.
 *
 * Assembles the various parts of the program and links them together.
 *
 */
public class Program {

    public static final String VERSION = "1.2";
    //Program flow:
    //AppFrame creates GUI Buttons/input/output
    //ButtonController listens to button click then passes input to...
    //ModelController which performs calculations on the input which are executed by...
    //Converter. ModelController then updates AppFrame with the results.
    //UpdaterDistributer hands out single use updater instances.
    //updater is a threaded class which latches on to ModelController. It just frequently asks ModelController
    //to update AppFrame with constantly updating values.

    public static void main(String args[]) {
        Converter c = new Converter();
        AppFrame af = new AppFrame();
        //ModelController requires AppFrame to post results and Converter to determine said results
        ModelController mc = new ModelController(af, c);
        //ButtonController requires AppFrame to link buttons and for button enable/disable. ModelController is
        //needed so ButtonController can pass inputs to ModelController and tell it what to do.
        new ButtonController(af, mc);

    }
}
