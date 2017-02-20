package gui.swingui.record;

import com.sun.beans.editors.IntegerEditor;
import controller.Controller;
import controller.SwingApplicationController;
import controller.search.SimpleSearch;
import datamodel.Language;
import datamodel.Word;
import gui.swingui.MainWindow;
import org.apache.commons.lang3.StringUtils;
import utils.Constants;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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


    protected abstract java.util.List<Word> initWords();
    protected abstract String getWindowTitle();
    protected abstract String getDescription();

    public RecordWindow(MainWindow parentForm) throws HeadlessException {
        this.mainWindow = parentForm;
        initForm();
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

        wordsTable.setSurrendersFocusOnKeystroke(true);

        wordsTable.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_V) {
                        if (wordsTable.getCellEditor() != null) {
                            wordsTable.getCellEditor().cancelCellEditing();
                        }

                        try {
                            Toolkit toolkit = Toolkit.getDefaultToolkit();
                            Clipboard clipboard = toolkit.getSystemClipboard();
                            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
                            int selectedRow = wordsTable.getSelectedRow();
                            int selectedColumn = wordsTable.getSelectedColumn();
                            System.out.println(result);

                            if (wordsTable.isCellEditable(selectedRow, selectedColumn)
                                    && isLineContainsUmlaut(result)) {
                                JOptionPane.showMessageDialog(null, "Cannot paste umlaut!");
                            }

//
//                            if (wordsTable.isCellEditable(selectedRow, selectedColumn)
//                                    && !isGermanSelected(selectedRow, selectedColumn)
//                                    && isLineContainsUmlaut(result)) {
//                                JOptionPane.showMessageDialog(null, "Cannot paste umlaut!");
//                            }
                        } catch (IOException ex) {
                            // TODO
                        } catch (UnsupportedFlavorException ex) {
                            // TODO
                        }
                    }
                }
            }
        });
    }

    protected abstract void setSelectedTopic();

    protected boolean isLineContainsUmlaut(String line) {
        boolean result = false;
        for(JButton umlaut : umlauts) {
            if(line.contains(umlaut.getText())) {
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

        for(JButton umlaut : umlauts) {
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

            if(wordsTable.getCellEditor() != null) {
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

            if(description.isEditable() && description.hasFocus()) {
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
