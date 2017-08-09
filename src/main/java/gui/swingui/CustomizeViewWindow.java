package gui.swingui;

import controller.Controller;
import controller.SwingApplicationController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;

public class CustomizeViewWindow extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger(CustomizeViewWindow.class);
    private MainWindow mainWindow;

    private TableModel model;
    private JTable customizationTable;
    private JScrollPane scrollPane;
    private JButton submitButton;
    private JButton previewButton;

    private Controller appController = SwingApplicationController.getInstance();

    public CustomizeViewWindow(MainWindow parentForm) throws HeadlessException {
        this.mainWindow = parentForm;
        initForm();
        initControls();
        initButtonsActions();
        initLayout();
    }

    private void initButtonsActions() {
        previewButton.addActionListener(e -> {
            mainWindow.customize(
                    ((CustomizeViewTableModel)customizationTable.getModel()).getCustomizationRecords());
            //mainWindow.toFront();
        });

        submitButton.addActionListener(e -> {
            mainWindow.customize(
                    ((CustomizeViewTableModel)customizationTable.getModel()).getCustomizationRecords());

            try {
                appController.saveCustomization(
                        ((CustomizeViewTableModel)customizationTable.getModel()).getCustomizationRecords());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
            dispose();
        });
    }

    private void initForm() {
        setTitle("View Customization");
        setSize(640, 480);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        LOGGER.info("View customization initialization complete");
    }

    private void initControls() {
        model = new CustomizeViewTableModel(mainWindow.getMainTable());
        customizationTable = new JTable(model);
        scrollPane = new JScrollPane(customizationTable);
        customizationTable.updateUI();
        previewButton = new JButton("Preview");
        submitButton = new JButton("Submit View");
    }

    private void initLayout() {
        Container pane = getContentPane();
        GroupLayout groupLayout = new GroupLayout(pane);
        pane.setLayout(groupLayout);

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup horizontalGroup = groupLayout.createSequentialGroup();
        horizontalGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(scrollPane)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(previewButton)
                        .addComponent(submitButton)));

        groupLayout.setHorizontalGroup(horizontalGroup);

        GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
        vGroup.addGroup(groupLayout.createSequentialGroup()
                .addComponent(scrollPane)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(previewButton)
                        .addComponent(submitButton)));

        groupLayout.setVerticalGroup(vGroup);
    }
}
