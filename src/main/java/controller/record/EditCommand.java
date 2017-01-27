package controller.record;

import controller.Command;
import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;

import java.io.IOException;
import java.util.List;

/**
 * Created by vaba1010 on 12.01.2017.
 */
public class EditCommand implements Command {

    private Record recordToEdit;
    private Record edited;

    public EditCommand(Record recordToEdit, List<Word> editedWords, String editedPictureName) {
        this.recordToEdit = recordToEdit;
        this.edited = new Record(editedWords, editedPictureName);
    }

    @Override
    public void execute(Dictionary dictionary) throws IOException {
        dictionary.replaceRecord(recordToEdit, edited);
    }
}
