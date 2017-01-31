package gui.swingui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class CustomizeViewWindow extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger(CustomizeViewWindow.class);
    private MainWindow mainWindow;

    private TableModel model;
    private JTable customizationTable;
    private JScrollPane scrollPane;
    private JButton submitButton;
    private JButton previewButton;

    public CustomizeViewWindow(MainWindow parentForm) throws HeadlessException {
        this.mainWindow = parentForm;
        initForm();
        initControls();
        initCustomizationTable();
        initButtonsActions();
        initLayout();
    }

    private void initButtonsActions() {
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.updateTableView(
                        ((CustomizeViewTableModel)customizationTable.getModel()).getCustomizationRecords());
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.updateTableView(
                        ((CustomizeViewTableModel)customizationTable.getModel()).getCustomizationRecords());
                dispose();
            }
        });
    }

    private void initForm() {
        setTitle("View Customization");
        setSize(640, 480);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        LOGGER.info("View customization initialization complete");

        Set<ViewCustomizationRecord> customizationData = new HashSet<>();
        customizationData.clear();
        customizationData.add(new ViewCustomizationRecord(new Boolean(true), "English", ""));

    }

    private void initControls() {
        model = new CustomizeViewTableModel(mainWindow.getMainTable().getModel());
        customizationTable = new JTable(model);
        scrollPane = new JScrollPane(customizationTable);
        customizationTable.updateUI();
        submitButton = new JButton("Submit View");
        previewButton = new JButton("PreView");
    }

    private void initCustomizationTable() {

    }

    // FIXME
    private void initLayout() {
        Container pane = getContentPane();
        GroupLayout groupLayout = new GroupLayout(pane);
        pane.setLayout(groupLayout);

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup horizontalGroup = groupLayout.createSequentialGroup();
        horizontalGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(scrollPane)
                .addGroup(groupLayout.createSequentialGroup())
                    .addComponent(submitButton)
                    .addComponent(previewButton));

        groupLayout.setHorizontalGroup(horizontalGroup);

        GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
        vGroup.addGroup(groupLayout.createSequentialGroup()
                .addComponent(scrollPane).addGroup(groupLayout.createParallelGroup()
                .addComponent(submitButton).addComponent(previewButton)));

        groupLayout.setVerticalGroup(vGroup);
    }
}
