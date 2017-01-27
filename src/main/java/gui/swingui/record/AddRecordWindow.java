package gui.swingui.record;

import datamodel.EmptyTheme;
import datamodel.Language;
import datamodel.Theme;
import datamodel.Word;
import gui.swingui.MainWindow;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class AddRecordWindow extends RecordWindow {

    protected void initPictureChooser() {
        final String LAST_USED_FOLDER = ".";
        choosePicture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences prefs = Preferences.userRoot().node(getClass().getName());
                JFileChooser pictureChooser = new JFileChooser(prefs.get(LAST_USED_FOLDER,
                        new File(".").getAbsolutePath()));
                pictureChooser.setMultiSelectionEnabled(false);
                int returnVal = pictureChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    pictureFile = pictureChooser.getSelectedFile();
                    copiedPictureFile = new File("pictures", pictureFile.getName());
                    try {
                        FileUtils.copyFile(pictureFile, copiedPictureFile);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }

                    pictureLabel.setIcon(
                            new ImageIcon(
                                    new ImageIcon(
                                            copiedPictureFile.getAbsolutePath()).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
                    prefs.put(LAST_USED_FOLDER, pictureChooser.getSelectedFile().getParent());
                }
            }
        });
    }

    protected void initSaveOperation(final java.util.List<Word> words) {

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (wordsTable.hasFocus() || wordsTable.isEditing()) {
                    wordsTable.getCellEditor().stopCellEditing();
                }
                for (Word word : words) {
                    if (existingTopicsCombo.getModel().getSize() == 0) {
                        word.setTheme(new EmptyTheme());
                    } else {
                        word.setTheme(new Theme(existingTopicsCombo.getSelectedItem().toString(), "empty description"));
                    }
                }
                if(copiedPictureFile == null || !copiedPictureFile.exists()) {
                    JOptionPane.showMessageDialog(null, "Please, select the picture");
                } else {
                    try {
                        appController.addRecord(words, copiedPictureFile.getName());
                        mainWindow.updateFormData();
                        dispose();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            }
        });
    }

    public AddRecordWindow(MainWindow main) throws HeadlessException {
        super(main);
        initUmlauts();
        initOperations();
        initLayout();
        this.pack();
        this.setVisible(true);
    }

    @Override
    protected void setSelectedTopic() {

    }

    @Override
    protected List<Word> initWords() {
        final java.util.List<Word> words = new ArrayList<>();
        for(Language language : Language.values()) {
            words.add(new Word("", language, new EmptyTheme()));
        }
        return words;
    }

    @Override
    protected String getWindowTitle() {
        return "Dictionary - Add New Record";
    }
}
