package controller.filesystem;

import controller.Command;

public abstract class AbstractFileCommand implements Command {
    protected String filePath;
    protected String fileContent;

    public AbstractFileCommand(String filePath) {
        this.filePath = filePath;
    }
}
