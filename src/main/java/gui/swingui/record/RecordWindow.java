package gui.swingui.record;

import controller.Controller;
import controller.SwingApplicationController;
import controller.search.SimpleSearch;
import datamodel.Word;
import gui.swingui.MainWindow;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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


    protected abstract java.util.List<Word> initWords();
    protected abstract String getWindowTitle();

    public RecordWindow(MainWindow parentForm) throws HeadlessException {
        this.mainWindow = parentForm;
        initForm();
    }

    protected abstract void setSelectedTopic();

    protected void initUmlauts() {
        java.util.List<JButton> umlauts = new ArrayList<>();

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
                if (newTopicValue != null && !"".equals(newTopicValue.trim()) && !existingTopics.contains(newTopicValue)) {
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

    protected class UmlautButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(wordsTable.getCellEditor() != null) {
                wordsTable.getCellEditor().stopCellEditing();
            }
            int selectedRow = wordsTable.getSelectedRow();
            int selectedColumn = wordsTable.getSelectedColumn();

            if (selectedRow < 0 || selectedColumn < 0) {
                return;
            }
            if (wordsTable.isCellEditable(selectedRow, selectedColumn)) {
                String value = wordsTable.getValueAt(selectedRow, selectedColumn).toString();
                value += ((JButton)e.getSource()).getText();
                wordsTable.setValueAt(value, selectedRow, selectedColumn);
                wordsTable.updateUI();
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
                        .addComponent(saveButton)));

        gl.setVerticalGroup(verticalGroup);
    }


}
