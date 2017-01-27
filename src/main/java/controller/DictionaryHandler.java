package controller;

public class DictionaryHandler {

    private Command addCommand;
    private Command editCommand;
    private Command removeCommand;

    public DictionaryHandler(Command addCommand, Command editCommand, Command removeCommand) {
        this.addCommand = addCommand;
        this.editCommand = editCommand;
        this.removeCommand = removeCommand;
    }

    public void add() {
        addCommand.execute();
    }

    public void edit() {
        editCommand.execute();
    }

    public void remove() {
        removeCommand.execute();
    }
}
