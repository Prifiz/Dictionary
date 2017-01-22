package controller;

/**
 * Created by vaba1010 on 10.01.2017.
 */
public abstract class AbstractFileCommand implements Command {
    protected String filePath;
    protected String fileContent;

    public AbstractFileCommand(String filePath) {
        this.filePath = filePath;
    }
}
