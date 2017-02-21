package gui.swingui.record;

import controller.Controller;
import controller.SwingApplicationController;
import controller.search.SimpleSearch;
import datamodel.Language;
import datamodel.Word;
import gui.swingui.MainWindow;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

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
import java.util.Set;
import java.util.TreeSet;

public abstract class RecordWindow extends JFrame {

    protected Controller appController = SwingApplicationController.getInstance();

    protected JTable wordsTable;
    protected JLabel topicLabel, newTopicLabel;
    protected JComboBox existingTopicsCombo;
    protected JTextField newTopicField;
    protected JButton addTopicButton;
    protected JButton saveButton;
    protected JScrollPane scrollPane;
    protected JButton choosePicture;
    protected JLabel pictureLabel;
    protected JLabel descriptionLabel;
    protected JTextArea description;
    protected JScrollPane descriptionPane;

    protected File pictureFile;
    protected File copiedPictureFile;

    protected MainWindow mainWindow;

    protected JButton umlaut_a;
    protected JButton umlaut_A;
    protected JButton umlaut_o;
    protected JButton umlaut_O;
    protected JButton umlaut_u;
    protected JButton umlaut_U;
    protected JButton umlaut_B;

    protected java.util.List<Word> words;
    protected int descriptionCaretPos;

    java.util.List<JButton> umlauts = new ArrayList<>();

    private static final Logger LOGGER = LogManager.getLogger(RecordWindow.class);

    protected abstract java.util.List<Word> initWords();

    protected abstract String getWindowTitle();

    protected abstract String getDescription();

    public RecordWindow(MainWindow parentForm) throws HeadlessException {
        this.mainWindow = parentForm;
        initForm();
    }

    protected void initUmlautsPasteAction() {
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
                    String result = (String) clipboard.getData(DataFlavor.stringFlavor);
                    int selectedRow = wordsTable.getSelectedRow();
                    int selectedColumn = wordsTable.getSelectedColumn();
                    System.out.println(result);

                    if (wordsTable.isCellEditable(selectedRow, selectedColumn)
                            && isLineContainsUmlaut(result)
                            && !isGermanSelected(selectedRow)) {
                        System.out.println("Attempt to paste umlaut");
                        cellEditor.cancelCellEditing();
                    } else {
                        if (wordsTable.getCellEditor() != null) {
                            wordsTable.getCellEditor().stopCellEditing();
                        }
                        String currentValue = wordsTable.getValueAt(selectedRow, selectedColumn).toString();
                        String updatedValue = currentValue + result;
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

    protected abstract void setSelectedTopic();

    protected boolean isLineContainsUmlaut(String line) {
        boolean result = false;
        for (JButton umlaut : umlauts) {
            if (line.contains(umlaut.getText())) {
                return true;
            }
        }
        return result;
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

    protected void initTopics() {

        newTopicLabel = new JLabel("New Topic");
        newTopicField = new JTextField();
        topicLabel = new JLabel("Topic");
        addTopicButton = new JButton("Add");

        final Set<String> existingTopics = new TreeSet<>();
        existingTopics.add(Constants.NO_TOPIC);
        existingTopics.addAll(new SimpleSearch().findAllTopics(appController.getDictionary()));
        final ComboBoxModel existingTopicsModel = new DefaultComboBoxModel(existingTopics.toArray());
        existingTopicsCombo = new JComboBox(existingTopicsModel);
        setSelectedTopic();
        addTopicButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newTopicValue = newTopicField.getText();
                if (newTopicValue != null
                        && !StringUtils.EMPTY.equals(newTopicValue.trim())
                        && !existingTopics.contains(newTopicValue)) {
                    existingTopics.add(newTopicValue);
                    existingTopicsCombo.addItem(newTopicValue);
                    existingTopicsCombo.setSelectedItem(newTopicValue);
                }
            }
        });
    }

    protected abstract void initPictureChooser();

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
        return (Language.GERMAN.name().equals(
                ((WordsTableModel) wordsTable.getModel()).getWords().get(selectedRow).getLanguage().name()));
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
        words = initWords();

        saveButton = new JButton("Save");
        wordsTable = new JTable(new WordsTableModel(words));
        scrollPane = new JScrollPane(wordsTable);

        initTopics();

        pictureLabel = new JLabel();
        pictureLabel.setSize(300, 300);
        choosePicture = new JButton("Choose Picture");
        description = new JTextArea(getDescription());
        descriptionPane = new JScrollPane(description);
        descriptionLabel = new JLabel("Description:");
        descriptionCaretPos = description.getText().length();
        initPictureChooser();

        initSaveOperation(words);
    }

    protected abstract void initSaveOperation(final java.util.List<Word> words);

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
