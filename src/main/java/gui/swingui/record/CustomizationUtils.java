package gui.swingui.record;

import controller.SwingApplicationController;
import gui.swingui.ViewCustomizationRecord;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class CustomizationUtils {
    public static java.util.List<ViewCustomizationRecord> loadViewCustomization() {
        try {
            return SwingApplicationController.getInstance().loadCustomization();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return new ArrayList<>();
        }
    }
}
