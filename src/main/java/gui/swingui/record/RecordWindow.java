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

    private JButton e_left;
    private JButton e_right;
    private JButton e_up;
    private JButton e_double;
    private JButton a_left;
    private JButton a_up;
    private JButton u_left;
    private JButton u_up;
    private JButton o_up;
    private JButton i_double;
    private JButton i_up;
    private JButton c;
    private JButton oe;
    private JButton apo_top;
    private JButton apo_bottom;

//
//    private JButton umlaut_a;
//    private JButton umlaut_A;
//    private JButton umlaut_o;
//    private JButton umlaut_O;
//    private JButton umlaut_u;
//    private JButton umlaut_U;
//    private JButton umlaut_B;


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

        e_left = new JButton("\u00e8"); // è
        umlauts.add(e_left);
        e_right = new JButton("\u00e9"); // é
        umlauts.add(e_right);
        e_up = new JButton("\u00ea"); // ê
        umlauts.add(e_up);
        e_double = new JButton("\u00eb"); // ë
        umlauts.add(e_double);
        a_left = new JButton("\u00e0"); // à
        umlauts.add(a_left);
        a_up = new JButton("\u00e2"); // â
        umlauts.add(a_up);
        u_left = new JButton("\u00f9"); // ù
        umlauts.add(u_left);
        u_up = new JButton("\u00fb"); // û
        umlauts.add(u_up);
        o_up = new JButton("\u00f4"); // ô
        umlauts.add(o_up);
        i_double = new JButton("\u00ef"); // ï
        umlauts.add(i_double);
        i_up = new JButton("\u00ee"); // î
        umlauts.add(i_up);
        c = new JButton("\u00e7"); // ç
        umlauts.add(c);
        oe = new JButton("\u0153"); // œ
        umlauts.add(oe);
        apo_top = new JButton("\u02CA"); // ˊ
        umlauts.add(apo_top);
        apo_bottom = new JButton("\u02cf"); // ˏ
        umlauts.add(apo_bottom);

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
        return (Language.FRENCH.name().equalsIgnoreCase(
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

        if(LanguageUtils.isSupportedLanguage("french", supportedLanguages)) {
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
                        .addComponent(e_left)
                        .addComponent(e_right)
                        .addComponent(e_up)
                        .addComponent(e_double)
                        .addComponent(a_left)
                        .addComponent(a_up)
                        .addComponent(u_left)
                        .addComponent(u_up)
                        .addComponent(o_up)
                        .addComponent(i_double)
                        .addComponent(i_up)
                        .addComponent(c)
                        .addComponent(oe)
                        .addComponent(apo_top)
                        .addComponent(apo_bottom)));
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
                                .addComponent(e_left)
                                .addComponent(e_right)
                                .addComponent(e_up)
                                .addComponent(e_double)
                                .addComponent(a_left)
                                .addComponent(a_up)
                                .addComponent(u_left)
                                .addComponent(u_up)
                                .addComponent(o_up)
                                .addComponent(i_double)
                                .addComponent(i_up)
                                .addComponent(c)
                                .addComponent(oe)
                                .addComponent(apo_top)
                                .addComponent(apo_bottom)))
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
