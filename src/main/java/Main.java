import controller.Controller;
import controller.SwingApplicationController;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Initializing controller...");
        Controller appController = SwingApplicationController.getInstance();
        appController.startApplication();
    }
}
