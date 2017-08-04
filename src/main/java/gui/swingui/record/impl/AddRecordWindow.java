package gui.swingui.record.impl;

import datamodel.EmptyTheme;
import datamodel.Theme;
import datamodel.Word;
import datamodel.language.LanguageInfo;
import gui.swingui.MainWindow;
import gui.swingui.record.RecordWindow;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

public class AddRecordWindow extends RecordWindow {

    private static final Logger LOGGER = LogManager.getLogger(AddRecordWindow.class);

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
                        LOGGER.error(ex.getMessage());
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

        saveButton.addActionListener(e -> {

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

            String pictureName;
            if(copiedPictureFile == null || !copiedPictureFile.exists()) {
                pictureName = "";
            } else {
                pictureName = copiedPictureFile.getName();
            }

            try {
                appController.addRecord(words, pictureName, description.getText());
                mainWindow.updateFormData();
                dispose();
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

        });
    }

    public AddRecordWindow(MainWindow main) throws HeadlessException {
        super(main);
        initUmlauts();
        initOperations();
        initActions();
        initLayout();
        this.pack();
    }

    @Override
    protected void setSelectedTopic() {

    }

    @Override
    protected List<Word> initWords(Set<LanguageInfo> supportedLanguages) {
        final java.util.List<Word> words = new ArrayList<>();
        supportedLanguages.forEach(languageInfo ->
                words.add(
                        new Word(
                                StringUtils.EMPTY,
                                languageInfo.getLanguage(),
                                new EmptyTheme(),
                                false)));
        return words;
    }

    @Override
    protected String getWindowTitle() {
        return "Dictionary - Add New Record";
    }

    @Override
    protected String getDescription() {
        return StringUtils.EMPTY;
    }
}
