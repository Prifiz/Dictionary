package controller.record;

import controller.Command;
import datamodel.Dictionary;

public class RemoveCommand implements Command {

    private int recordId;

    public RemoveCommand(int recordId) {
        this.recordId = recordId;
    }

    public void execute(Dictionary dictionary) {
        dictionary.removeRecord(recordId);
    }
}
