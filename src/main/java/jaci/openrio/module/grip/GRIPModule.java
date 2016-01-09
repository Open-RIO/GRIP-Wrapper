package jaci.openrio.module.grip;

import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ToastModule;

public class GRIPModule extends ToastModule {

    public static Logger logger;
    public static boolean started = false;

    @Override
    public String getModuleName() {
        return "GRIP";
    }

    @Override
    public String getModuleVersion() {
        return "0.1.0";
    }

    @Override
    public void prestart() {
        logger = new Logger("GRIP", Logger.ATTR_DEFAULT);
        logger.info("Starting GRIP...");
        try {
            GRIPAttacher.startGRIP();
            started = true;
            logger.info("GRIP Started!");
        } catch (GRIPAttacher.GRIPLoadException e) {
            logger.error("GRIP Could not be loaded!");
            logger.exception(e);
        }
    }

    @Override
    public void start() { }
}
