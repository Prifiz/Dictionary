package gui.swingui.record;

import datamodel.EmptyTheme;
import datamodel.Record;
import datamodel.Theme;
import datamodel.Word;
import gui.swingui.MainWindow;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

public class EditRecordWindow extends RecordWindow {

    private static final Logger LOGGER = LogManager.getLogger(EditRecordWindow.class);

    private Record recordToEdit;

    public EditRecordWindow(MainWindow main, Record record) throws HeadlessException {
        super(main);
        this.recordToEdit = record;
        initUmlauts();
        initOperations();
        initLayout();
        this.pack();
    }

    @Override
    protected List<Word> initWords() {
        return recordToEdit.getWords();
    }

    @Override
    protected String getWindowTitle() {
        return "Dictionary - Edit Record";
    }

    @Override
    protected void setSelectedTopic() {
        if(recordToEdit.getWords().isEmpty()) {
            existingTopicsCombo.setSelectedItem(Constants.NO_TOPIC);
        } else {
            String firstWordTopic = recordToEdit.getWords().get(0).getTheme().getName();
            if(StringUtils.isBlank(firstWordTopic)) {
                existingTopicsCombo.setSelectedItem(Constants.NO_TOPIC);
            } else {
                existingTopicsCombo.setSelectedItem(firstWordTopic);
            }

        }
    }

    @Override
    protected void initPictureChooser() {
        pictureLabel.setIcon(
                new ImageIcon(
                        new ImageIcon(
                                new File("pictures", recordToEdit.getPictureName()).getAbsolutePath())
                                .getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        copiedPictureFile = new File(recordToEdit.getPictureName());
        final String LAST_USED_FOLDER = new File("pictures", recordToEdit.getPictureName()).getAbsolutePath();
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

    protected void initSaveOperation(final List<Word> words) {
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

                try {
                    appController.editRecord(recordToEdit, words, copiedPictureFile.getName());
                    mainWindow.updateFormData();
                    dispose();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
    }
}
