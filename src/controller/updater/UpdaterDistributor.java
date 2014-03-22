package controller.updater;

import controller.ModelController;
import model.Converter;

/**
 * Created by alutman on 24/02/14.
 *
 * Hands out new instances of updater classes.
 * updater instances can only be created and accessed through here
 *
 */
public class UpdaterDistributor {

    private static ModelController modelController;
    private static Converter converter;
    public static int POLLING_RATE = 10; /* How often should the updater thread update, Thread.sleep(POLLING_RATE)*/

    public static void init(ModelController modelController, Converter converter) {
        UpdaterDistributor.modelController = modelController;
        UpdaterDistributor.converter = converter;
    }

    public static Updater getNewUpdater(Updater.MODE mode) {
        return new Updater(modelController,converter, mode);
    }



}
