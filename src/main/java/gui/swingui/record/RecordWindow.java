package gui.swingui.record;

import controller.Controller;
import controller.SwingApplicationController;
import controller.search.SimpleSearch;
import datamodel.language.Language;
import datamodel.Word;
import datamodel.language.LanguageInfo;
import gui.swingui.MainWindow;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.LanguageUtils;

import javax.swing.*;

import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class RecordWindow extends JFrame {

    protected Controller appController = SwingApplicationController.getInstance();

    private JLabel topicLabel, newTopicLabel;
    private JTextField newTopicField;
    private JButton addTopicButton;
    private JScrollPane scrollPane;
    private JLabel descriptionLabel;
    private JScrollPane descriptionPane;

    protected JTable wordsTable;
    protected JComboBox<String> existingTopicsCombo;
    protected JButton saveButton;
    protected JButton choosePicture;
    protected JLabel pictureLabel;
    protected JTextArea description;
    protected File pictureFile;
    protected File copiedPictureFile;
    protected MainWindow mainWindow;
    protected java.util.List<Word> words;

    private java.util.List<JButton> umlauts = new ArrayList<>();
    private JButton umlaut_a;
    private JButton umlaut_A;
    private JButton umlaut_o;
    private JButton umlaut_O;
    private JButton umlaut_u;
    private JButton umlaut_U;
    private JButton umlaut_B;


    private static final Logger LOGGER = LogManager.getLogger(RecordWindow.class);

    protected abstract java.util.List<Word> initWords(Set<LanguageInfo> supportedLanguages);

    protected abstract String getWindowTitle();

    protected abstract String getDescription();

    protected abstract void setSelectedTopic();

    protected abstract void initPictureChooser();

    protected abstract void initSaveOperation(final java.util.List<Word> words);

    public RecordWindow(MainWindow parentForm) throws HeadlessException {
        this.mainWindow = parentForm;
        initForm();
    }

    private void initUmlautsPasteAction() {
        JTextField cell = new JTextField();
        final TableCellEditor cellEditor = new DefaultCellEditor(cell);
        wordsTable.getColumnModel().getColumn(0).setCellEditor(cellEditor);
        InputMap iMap = cell.getInputMap(JComponent.WHEN_FOCUSED);
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), KeyEvent.getKeyText(KeyEvent.VK_V));
        ActionMap aMap = cell.getActionMap();
        aMap.put(KeyEvent.getKeyText(KeyEvent.VK_V), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Clipboard clipboard = toolkit.getSystemClipboard();
                    String clipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);
                    int selectedRow = wordsTable.getSelectedRow();
                    int selectedColumn = wordsTable.getSelectedColumn();

                    if (wordsTable.isCellEditable(selectedRow, selectedColumn)
                            && isLineContainsUmlaut(clipboardContent)
                            && !isGermanSelected(selectedRow)) {
                        LOGGER.info("Attempt to paste umlaut: " + clipboardContent);
                        cellEditor.cancelCellEditing();
                    } else {
                        if (wordsTable.getCellEditor() != null) {
                            wordsTable.getCellEditor().stopCellEditing();
                        }
                        String currentValue = wordsTable.getValueAt(selectedRow, selectedColumn).toString();
                        String updatedValue = currentValue + clipboardContent;
                        wordsTable.setValueAt(updatedValue, selectedRow, selectedColumn);
                        wordsTable.updateUI();

                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    LOGGER.error("Failed to perform paste action");
                }
            }
        });
    }

    protected void initActions() {

        description.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                wordsTable.clearSelection();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        initUmlautsPasteAction();
    }

    private boolean isLineContainsUmlaut(String line) {
        for (JButton umlaut : umlauts) {
            if (line.contains(umlaut.getText())) {
                return true;
            }
        }
        return false;
    }

    protected void initUmlauts() {
        umlaut_a = new JButton("\u00e4"); // ä
        umlauts.add(umlaut_a);
        umlaut_A = new JButton("\u00c4"); // Ä
        umlauts.add(umlaut_A);
        umlaut_o = new JButton("\u00f6"); // ö
        umlauts.add(umlaut_o);
        umlaut_O = new JButton("\u00d6"); // Ö
        umlauts.add(umlaut_O);
        umlaut_u = new JButton("\u00fc"); // ü
        umlauts.add(umlaut_u);
        umlaut_U = new JButton("\u00dc"); // Ü
        umlauts.add(umlaut_U);
        umlaut_B = new JButton("\u00df"); // ß
        umlauts.add(umlaut_B);

        for (JButton umlaut : umlauts) {
            umlaut.setFocusable(false);
            umlaut.addActionListener(new UmlautButtonActionListener());
        }
    }

    private void initTopics() {

        newTopicLabel = new JLabel("New Topic");
        newTopicField = new JTextField();
        topicLabel = new JLabel("Topic");
        addTopicButton = new JButton("Add");

        final Set<String> existingTopics = new TreeSet<>();
        existingTopics.add(Constants.NO_TOPIC);
        existingTopics.addAll(new SimpleSearch().findAllTopics(appController.getDictionary()));
        final ComboBoxModel<String> existingTopicsModel = new DefaultComboBoxModel<>(
                existingTopics.toArray(new String[existingTopics.size()]));
        existingTopicsCombo = new JComboBox<>(existingTopicsModel);
        setSelectedTopic();
        addTopicButton.addActionListener(e -> {
            String newTopicValue = newTopicField.getText();
            if (newTopicValue != null
                    && !StringUtils.EMPTY.equals(newTopicValue.trim())
                    && !existingTopics.contains(newTopicValue)) {
                existingTopics.add(newTopicValue);
                existingTopicsCombo.addItem(newTopicValue);
                existingTopicsCombo.setSelectedItem(newTopicValue);
            }
        });
    }

    private void initForm() {
        setTitle(getWindowTitle());
        setSize(640, 480);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void changeUmlautsVisibility(boolean visible) {
        for (JButton umlaut : umlauts) {
            umlaut.setVisible(visible);
            umlaut.updateUI();
        }
    }

    private void showUmlauts() {
        changeUmlautsVisibility(true);
    }

    private void hideUmlauts() {
        changeUmlautsVisibility(false);
    }

    private boolean isGermanSelected(int selectedRow) {
        return (Language.GERMAN.name().equalsIgnoreCase(
                ((WordsTableModel) wordsTable.getModel()).getWords().get(selectedRow).getLanguage()));
    }

    protected class UmlautButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (wordsTable.getCellEditor() != null) {
                wordsTable.getCellEditor().stopCellEditing();
            }
            int selectedRow = wordsTable.getSelectedRow();
            int selectedColumn = wordsTable.getSelectedColumn();

            if (selectedRow >= 0 && selectedColumn >= 0) {
                if (isGermanSelected(selectedRow)) {
                    if (wordsTable.isCellEditable(selectedRow, selectedColumn)) {
                        String value = wordsTable.getValueAt(selectedRow, selectedColumn).toString();
                        value += ((JButton) e.getSource()).getText();
                        wordsTable.setValueAt(value, selectedRow, selectedColumn);
                        wordsTable.updateUI();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Umlauts supported only for GERMAN language");
                }
            }

            if (description.isEditable() && description.hasFocus()) {
                description.insert(((JButton) e.getSource()).getText(), description.getCaretPosition());
                description.grabFocus();
            }

        }

    }

    protected void initOperations() {
        words = initWords(appController.getSupportedLanguages());

        saveButton = new JButton("Save");
        Set<LanguageInfo> supportedLanguages = appController.getSupportedLanguages();

        if(LanguageUtils.isSupportedLanguage("german", supportedLanguages)) {
            showUmlauts();
        } else {
            hideUmlauts();
        }

        Map<String, TableCellEditor> partsOfSpeechMapping = new HashMap<>();
        Map<String, TableCellEditor> gendersMapping = new HashMap<>();
        supportedLanguages.forEach( supportedLanguage -> {
            String currentLang = supportedLanguage.getLanguage().toLowerCase();
            JComboBox<String> partOfSpeechCombo = new JComboBox<>();
            partOfSpeechCombo.addItem(Constants.NOT_SET);
            supportedLanguage.getPartsOfSpeech().forEach(
                    partOfSpeech -> partOfSpeechCombo.addItem(partOfSpeech.getValue()));
            partsOfSpeechMapping.put(currentLang, new DefaultCellEditor(partOfSpeechCombo));
            JComboBox<String> gendersCombo = new JComboBox<>();
            gendersCombo.addItem(Constants.NOT_SET);
            supportedLanguage.getGenders().forEach(gender -> gendersCombo.addItem(gender.getValue()));
            gendersMapping.put(currentLang, new DefaultCellEditor(gendersCombo));
        });

        wordsTable = new JTable(new WordsTableModel(words)) {
            public TableCellEditor getCellEditor(int row, int column) {
                int modelColumn = convertColumnIndexToModel(column);

                if("Part Of Speech".equals(getModel().getColumnName(modelColumn))) {
                    return partsOfSpeechMapping.get(getValueAt(row, 1).toString().toLowerCase());
                } else if("Gender".equals(getModel().getColumnName(modelColumn))) {
                    return gendersMapping.get(getValueAt(row, 1).toString().toLowerCase());
                } else {
                    return super.getCellEditor(row, column);
                }
            }
        };

        scrollPane = new JScrollPane(wordsTable);
        initTopics();
        pictureLabel = new JLabel();
        pictureLabel.setSize(300, 300);
        choosePicture = new JButton("Choose Picture");
        description = new JTextArea(getDescription());
        description.setLineWrap(true);
        descriptionPane = new JScrollPane(description);
        descriptionLabel = new JLabel("Description:");
        initPictureChooser();

        initSaveOperation(words);
    }

    protected void initLayout() {
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup horizontalGroup = gl.createSequentialGroup();

        horizontalGroup.addGroup(gl.createParallelGroup()
                .addComponent(scrollPane)
                .addGroup(gl.createSequentialGroup()
                        .addComponent(umlaut_a)
                        .addComponent(umlaut_A)
                        .addComponent(umlaut_o)
                        .addComponent(umlaut_O)
                        .addComponent(umlaut_u)
                        .addComponent(umlaut_U)
                        .addComponent(umlaut_B)));
        horizontalGroup.addGroup(gl.createParallelGroup()
                .addComponent(topicLabel)
                .addComponent(existingTopicsCombo)
                .addComponent(newTopicLabel)
                .addGroup(gl.createSequentialGroup()
                        .addComponent(newTopicField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(addTopicButton))
                .addComponent(choosePicture)
                .addComponent(pictureLabel)
                .addComponent(descriptionLabel)
                .addComponent(descriptionPane)
                .addComponent(saveButton)
        );


        gl.setHorizontalGroup(horizontalGroup);

        GroupLayout.SequentialGroup verticalGroup = gl.createSequentialGroup();

        verticalGroup.addGroup(gl.createParallelGroup()

                .addGroup(gl.createSequentialGroup()
                        .addComponent(scrollPane)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(umlaut_a)
                                .addComponent(umlaut_A)
                                .addComponent(umlaut_o)
                                .addComponent(umlaut_O)
                                .addComponent(umlaut_u)
                                .addComponent(umlaut_U)
                                .addComponent(umlaut_B)))
                .addGroup(gl.createSequentialGroup()
                        .addComponent(topicLabel)
                        .addComponent(existingTopicsCombo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(newTopicLabel)
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(newTopicField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(addTopicButton))
                        .addComponent(choosePicture)
                        .addComponent(pictureLabel)
                        .addComponent(descriptionLabel)
                        .addComponent(descriptionPane)
                        .addComponent(saveButton)));

        gl.setVerticalGroup(verticalGroup);
    }


}
