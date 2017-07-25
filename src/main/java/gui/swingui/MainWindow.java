package gui.swingui;

import controller.Controller;
import controller.SwingApplicationController;
import controller.search.SimpleSearch;
import datamodel.Record;
import gui.swingui.record.AddRecordWindow;
import gui.swingui.record.EditRecordWindow;
import gui.swingui.record.RecordWindow;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.ImageUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeSet;
import java.util.prefs.Preferences;

public class MainWindow extends JFrame implements Customizable {

    private static final Logger LOGGER = LogManager.getLogger(MainWindow.class);

    private Controller appController = SwingApplicationController.getInstance();

    private TableModel model;
    private TableRowSorter<MainTableModel> sorter;
    private JTable mainTable;
    private JLabel byTopicLabel;

    private JScrollPane scrollPane;
    private JButton newButton;
    private JButton removeButton;
    private JButton removeTopicButton;


    private JButton resetButton;
    private JComboBox byTopicCombo;
    private String byTopicComboCurrentlySelected = Constants.NO_TOPIC;
    private java.util.Set<String> topics = new TreeSet<>();

    private ComboBoxModel comboBoxModel = new DefaultComboBoxModel<>(topics.toArray());

    public MainWindow() throws HeadlessException {
        initMainForm();
        initControls();
        initMainTable();
        initButtonsActions();
        initMenu();
        initLayout();
        loadDictionaryData();
    }


    private void configureViewCustomization() {
        CustomizeViewWindow customizeViewWindow = new CustomizeViewWindow(this);
        customizeViewWindow.setVisible(true);
    }

    @Override
    public void customize(java.util.List<ViewCustomizationRecord> customizationData) {
        for (ViewCustomizationRecord record : customizationData) {
            for (Map.Entry<Integer, String> entry : ((MainTableModel) mainTable.getModel()).getHeaders().entrySet()) {
                if (entry.getValue().equals(record.getColumnName())) {
                    Integer columnIdx = entry.getKey();
                    if (record.isVisible()) {
                        unhideColumn(columnIdx);
                    } else {
                        hideColumn(columnIdx);
                    }
                }
            }
        }
    }

    private void initMainForm() {
        setTitle("Dictionary - Main Page");
        setSize(1024, 768);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        LOGGER.info("Common settings initialization complete");
    }

    private void initControls() {
        model = new MainTableModel(appController.getDictionary());
        sorter = new TableRowSorter<>((MainTableModel) model);
        mainTable = new JTable(model);
        scrollPane = new JScrollPane(mainTable);
        byTopicLabel = new JLabel("By Topic");
        newButton = new JButton("New...");
        removeButton = new JButton("Remove");
        resetButton = new JButton("Show All");
        byTopicCombo = new JComboBox(comboBoxModel);
        removeTopicButton = new JButton("Remove Topic");
        LOGGER.info("Controls initialization complete");
    }

    public void updateFormData() {
        updateMainTable();
        topics.clear();
        topics.add(Constants.NO_TOPIC);
        topics.addAll(new SimpleSearch().findAllTopics(appController.getDictionary()));
        byTopicCombo.setModel(new DefaultComboBoxModel<>(topics.toArray()));
        if (topics.contains(byTopicComboCurrentlySelected)) {
            byTopicCombo.setSelectedItem(byTopicComboCurrentlySelected);
        } else {
            byTopicCombo.setSelectedItem(Constants.NO_TOPIC);
        }
        byTopicCombo.updateUI();
    }

    public JTable getMainTable() {
        return mainTable;
    }

    private void createNewRecord() {
        RecordWindow addRecordWindow = new AddRecordWindow(this);
        addRecordWindow.setVisible(true);
    }

    private void editRecord(int recordIdx) {
        Record record = appController.getDictionary().getAllRecordsAsList().get(recordIdx);
        final EditRecordWindow editRecordWindow = new EditRecordWindow(this, record);
        editRecordWindow.setVisible(true);
    }


    private void initButtonsActions() {
        newButton.addActionListener(e -> createNewRecord());

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainTableModel model = (MainTableModel) mainTable.getModel();
                int[] rows = mainTable.getSelectedRows();
                for(int i=0;i<rows.length;i++){
                    model.removeRow(rows[i]-i);
                }
                updateFormData();
                for (ActionListener a : byTopicCombo.getActionListeners()) {
                    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {
                    });
                }
            }
        });

        resetButton.addActionListener(e -> {
            sorter.setRowFilter(null);
            byTopicCombo.setSelectedItem(Constants.NO_TOPIC);
            mainTable.setRowSorter(sorter);
        });

        removeTopicButton.addActionListener(e -> {
            if (!topics.isEmpty()) {
                String topic = byTopicCombo.getSelectedItem().toString();
                if (!Constants.NO_TOPIC.equals(topic)) {
                    try {
                        appController.removeTopic(topic);
                        updateFormData();
                    } catch (IOException ex) {
                        LOGGER.error(ex.getMessage());
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            }
        });

        byTopicCombo.addActionListener((e) -> {
            sorter.setModel((MainTableModel) model);
            RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
                public boolean include(Entry entry) {
                    if (Constants.NO_TOPIC.equals(byTopicCombo.getSelectedItem())) {
                        return true;
                    } else {
                        String topic = (String) entry.getValue(4);
                        return topic.equals(byTopicCombo.getSelectedItem());
                    }
                }
            };
            sorter.setRowFilter(filter);
            mainTable.setRowSorter(sorter);
            byTopicComboCurrentlySelected = byTopicCombo.getSelectedItem().toString();
        });
        LOGGER.info("Controls actions initialization complete");
    }

    private void updateMainTable() {
        ((MainTableModel) mainTable.getModel()).setDictionary(appController.getDictionary());
        mainTable.updateUI();
    }

    private void loadDictionaryData() {
        LOGGER.info("Loading dictionary data...");
        try {
            appController.loadDictionary();
            updateFormData();
            LOGGER.info("Done!");
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void configureExportToExcel() {
        final String LAST_USED_FOLDER = ".";
        LOGGER.info("Starting export to Excel sheet...");

        Preferences prefs = Preferences.userRoot().node(getClass().getName());
        final java.util.List<String> EXCEL_EXTENSIONS = Arrays.asList(Constants.XLS, Constants.XLSX);

        JFileChooser outPathChooser = new JFileChooser(prefs.get(LAST_USED_FOLDER,
                new File(".").getAbsolutePath())) {
            public static final String DEFAULT_EXCEL_EXT = Constants.XLSX;

            @Override
            public void approveSelection() {
                File selectedFile = getSelectedFile();

                if (selectedFile.exists() && getDialogType() == SAVE_DIALOG) {

                    String extension = FilenameUtils.getExtension(selectedFile.getName());
                    if (EXCEL_EXTENSIONS.contains(extension.toLowerCase())) {
                        int result = JOptionPane.showConfirmDialog(
                                this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                super.approveSelection();
                                return;
                            case JOptionPane.NO_OPTION:
                                return;
                            case JOptionPane.CLOSED_OPTION:
                                return;
                            case JOptionPane.CANCEL_OPTION:
                                cancelSelection();
                                return;
                        }
                    } else {
                        int result = JOptionPane.showConfirmDialog(
                                this, "Non-excel file chosen. Would you like to export to excel file with the chosen file name?",
                                "Existing non-excel file", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                String chosenFileNameWithoutExt = FilenameUtils.removeExtension(selectedFile.getAbsolutePath());
                                setSelectedFile(new File(chosenFileNameWithoutExt + "." + DEFAULT_EXCEL_EXT));
                                super.approveSelection();
                                return;
                            case JOptionPane.NO_OPTION:
                                return;
                            case JOptionPane.CLOSED_OPTION:
                                return;
                            case JOptionPane.CANCEL_OPTION:
                                cancelSelection();
                                return;
                        }
                    }
                }
                super.approveSelection();
            }
        };

        outPathChooser.setMultiSelectionEnabled(false);

        int returnVal = outPathChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = outPathChooser.getSelectedFile();
            appController.exportToExcel(selectedFile.getAbsolutePath(), mainTable);
            prefs.put(LAST_USED_FOLDER, outPathChooser.getSelectedFile().getParent());
            JOptionPane.showMessageDialog(null, "Successfully saved");
        }
    }

    private void configureImportFromExcel() {
        final String LAST_USED_FOLDER = ".";
        LOGGER.info("Starting import from Excel document...");

        Preferences prefs = Preferences.userRoot().node(getClass().getName());
        final java.util.List<String> EXCEL_EXTENSIONS = Arrays.asList(Constants.XLS);


        JFileChooser inputPathChooser = new JFileChooser(prefs.get(LAST_USED_FOLDER,
                new File(".").getAbsolutePath())) {

            @Override
            public void approveSelection() {
                File selectedFile = getSelectedFile();

                if (selectedFile.exists() && getDialogType() == OPEN_DIALOG) {

                    String extension = FilenameUtils.getExtension(selectedFile.getName());
                    if (EXCEL_EXTENSIONS.contains(extension.toLowerCase())) {
                        super.approveSelection();
                        return;
                    } else {
                        JOptionPane.showMessageDialog(this, "Chosen file is not an excel file! Please, choose another one");
                        super.cancelSelection();
                        return;
                    }
                }
                super.approveSelection();
            }
        };

        inputPathChooser.setMultiSelectionEnabled(false);

        int returnVal = inputPathChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = inputPathChooser.getSelectedFile();
            try {
                appController.importFromExcel(selectedFile.getAbsolutePath(), (MainTableModel) mainTable.getModel());
                prefs.put(LAST_USED_FOLDER, inputPathChooser.getSelectedFile().getParent());
                updateFormData();
                JOptionPane.showMessageDialog(null, "Successfully loaded");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void saveDictionaryData() {
        LOGGER.info("Saving dictionary data...");
        try {
            appController.saveDictionary();
            JOptionPane.showMessageDialog(null, "Successfully saved!");
            LOGGER.info("Done!");
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private java.util.List<Integer> getVisibleColumns() {
        java.util.List<Integer> result = new ArrayList<>();
        for (Integer idx = 0; idx < mainTable.getColumnModel().getColumnCount(); idx++) {
            if (mainTable.getColumnModel().getColumn(idx).getMaxWidth() > 0) {
                result.add(idx);
            }
        }
        return result;
    }

    private void updateColumnsWidth() {
        java.util.List<Integer> visibleColumnsIndices = getVisibleColumns();
        int columnNewWidth;
        if (visibleColumnsIndices.isEmpty()) {
            columnNewWidth = (int) mainTable.getSize().getWidth();
        } else {
            columnNewWidth = (int) mainTable.getSize().getWidth() / visibleColumnsIndices.size();
        }
        for (Integer visibleColumnIdx : visibleColumnsIndices) {
            mainTable.getColumnModel().getColumn(visibleColumnIdx).setMinWidth(columnNewWidth);
            mainTable.getColumnModel().getColumn(visibleColumnIdx).setMaxWidth(columnNewWidth);
        }
    }

    private void hideColumn(int columnIdx) {
        final int ZERO_WIDTH = 0;
        mainTable.getColumnModel().getColumn(columnIdx).setMinWidth(ZERO_WIDTH);
        mainTable.getColumnModel().getColumn(columnIdx).setMaxWidth(ZERO_WIDTH);
        updateColumnsWidth();
        mainTable.updateUI();
    }

    private void unhideColumn(int columnIdx) {
        final int MINIMUM_NON_ZERO_WIDTH = 1;
        mainTable.getColumnModel().getColumn(columnIdx).setMinWidth(MINIMUM_NON_ZERO_WIDTH);
        mainTable.getColumnModel().getColumn(columnIdx).setMaxWidth(MINIMUM_NON_ZERO_WIDTH);
        updateColumnsWidth();
        mainTable.updateUI();
    }

    private void initMainTable() {
        mainTable.setRowHeight(160);
        hideColumn(4);
        mainTable.setAutoCreateRowSorter(true);
        mainTable.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
        TextTableRenderer textRenderer = new TextTableRenderer();
        mainTable.setDefaultRenderer(String.class, textRenderer);

        TableCellRenderer renderer = (table, value, isSelected, hasFocus, row, column) -> {
            final int IMAGE_WIDTH = 150;
            final int IMAGE_HEIGHT = IMAGE_WIDTH;
            try {
                JLabel label = new JLabel();

                Image fullSize = new ImageIcon(((File) value).getCanonicalPath()).getImage();
                Image resized = ImageUtils.resize(
                        ImageUtils.toBufferedImage(fullSize), IMAGE_WIDTH, IMAGE_HEIGHT);
                if (resized != null) {
                    ImageIcon imageIcon = new ImageIcon(
                            resized.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH));
                    label.setIcon(imageIcon);
                }
                return label;

            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
            return new JLabel("Error: " + value);
        };

        mainTable.setDefaultRenderer(File.class, renderer);

        mainTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getClickCount() == 2 && me.getButton() == 1) {
                    JTable table = (JTable) me.getSource();
                    Point p = me.getPoint();
                    int row = table.rowAtPoint(p);
                    if (row != -1) {
                        int modelIndex = mainTable.convertRowIndexToModel(row);
                        MainTableModel model = (MainTableModel) mainTable.getModel();
                        editRecord(modelIndex);
                        model.setDictionary(appController.getDictionary());
                    }
                }
            }
        });
        LOGGER.info("Main Table initialization complete");
    }

    private void initLayout() {
        Container pane = getContentPane();
        GroupLayout groupLayout = new GroupLayout(pane);
        pane.setLayout(groupLayout);

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup horizontalGroup = groupLayout.createSequentialGroup();
        horizontalGroup.addGroup(groupLayout.createParallelGroup()
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(newButton)
                        .addComponent(removeButton))
                .addComponent(scrollPane));
        horizontalGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(byTopicLabel)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(byTopicCombo)
                        .addComponent(removeTopicButton))
                .addComponent(resetButton));

        groupLayout.setHorizontalGroup(horizontalGroup);

        GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
        vGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(newButton)
                .addComponent(removeButton)
                .addComponent(byTopicLabel));
        vGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(scrollPane)
                .addGroup(groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup()
                                .addComponent(byTopicCombo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(removeTopicButton))
                        .addComponent(resetButton)));

        groupLayout.setVerticalGroup(vGroup);
        LOGGER.info("MainWindow layout initialization complete");
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu dictionaryActionsMenu = new JMenu("Dictionary Actions");
        menuBar.add(dictionaryActionsMenu);
        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);

        JMenuItem saveItem = new JMenuItem("Save");
        dictionaryActionsMenu.add(saveItem);
        JMenuItem excelExport = new JMenuItem("Export to Excel");
        dictionaryActionsMenu.add(excelExport);
        JMenuItem excelImport = new JMenuItem("Import from Excel");
        dictionaryActionsMenu.add(excelImport);

        JMenuItem customizeViewItem = new JMenuItem("Customize View");
        viewMenu.add(customizeViewItem);

        saveItem.addActionListener((e) -> saveDictionaryData());

        customizeViewItem.addActionListener((e) -> configureViewCustomization());

        excelExport.addActionListener((e) -> configureExportToExcel());

        excelImport.addActionListener((e) -> configureImportFromExcel());

        this.setJMenuBar(menuBar);
        LOGGER.info("Main menu initialization complete");
    }

}
