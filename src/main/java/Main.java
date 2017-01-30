import controller.Controller;
import controller.SwingApplicationController;

public class Main {

    public static void main(String[] args) {
        Controller appController = SwingApplicationController.getInstance();
        appController.startApplication();
    }
}
