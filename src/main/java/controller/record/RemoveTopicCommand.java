package controller.record;

import controller.Command;
import datamodel.Dictionary;

public class RemoveTopicCommand implements Command {

    private String topicName;

    public RemoveTopicCommand(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public void execute(Dictionary dictionary) {
        dictionary.removeAllTopicOccurences(topicName);
    }
}
