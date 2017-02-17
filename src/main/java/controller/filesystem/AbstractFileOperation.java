package controller.filesystem;

/**
 * Created by vaba1010 on 17.02.2017.
 */
public class AbstractFileOperation {
    protected String filePath;
    protected String fileContent;

    public AbstractFileOperation(String filePath) {
        this.filePath = filePath;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
