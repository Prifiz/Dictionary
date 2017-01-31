package controller;

import controller.filesystem.LoadFromXmlCommand;
import controller.filesystem.SaveToXmlCommand;
import controller.record.AddCommand;
import controller.record.EditCommand;
import controller.record.RemoveCommand;
import controller.record.RemoveTopicCommand;
import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;
import gui.swingui.MainWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class SwingApplicationController implements Controller {

    private Dictionary dictionary;
    private static final Logger LOGGER = LogManager.getLogger(SwingApplicationController.class);

    private static final SwingApplicationController INSTANCE = new SwingApplicationController();

    private SwingApplicationController() {
        this.dictionary = new Dictionary();
    }

    public static SwingApplicationController getInstance() {
        return INSTANCE;
    }

    public void startApplication() {
        LOGGER.info("Starting application...");
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }

    @Override
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void loadDictionary() throws IOException {
        Command loadCommand = new LoadFromXmlCommand("Dictionary.xml");
        loadCommand.execute(dictionary);
    }

    @Override
    public void saveDictionary() throws IOException {
        Command saveCommand = new SaveToXmlCommand("Dictionary.xml");
        saveCommand.execute(dictionary);
    }

    @Override
    public void removeTopic(String topicName) throws IOException {
        Command removeTopicCommand = new RemoveTopicCommand(topicName);
        removeTopicCommand.execute(dictionary);
    }

    @Override
    public void addRecord(List<Word> words, String pictureName) throws IOException {
        Command addCommand = new AddCommand(words, pictureName);
        addCommand.execute(dictionary);
    }

    @Override
    public void editRecord(Record recordToEdit, List<Word> editedWords, String editedPictureName) throws IOException {
        Command editCommand = new EditCommand(recordToEdit, editedWords, editedPictureName);
        editCommand.execute(dictionary);
    }

    @Override
    public void removeRecord(int recordId) throws IOException {
        Command removeCommand = new RemoveCommand(recordId);
        removeCommand.execute(dictionary);
    }

}
