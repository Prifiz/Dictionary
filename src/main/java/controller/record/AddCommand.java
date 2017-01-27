package controller.record;

import controller.Command;
import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;

import java.util.List;

public class AddCommand implements Command {

    private Record record;

    public AddCommand(List<Word> words, String pictureName) {
        record = new Record(words, pictureName);
    }

    public void execute(Dictionary dictionary) {
        dictionary.addRecord(record);
    }
}
