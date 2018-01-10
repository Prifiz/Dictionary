package controller.filesystem;

import java.io.File;

public class AbstractFileOperation {
    protected String fileContent;
    protected File file;

    public String getFileContent() {
        return fileContent;
    }

    public AbstractFileOperation(String filePath) {
        this.file = new File(filePath);
    }
}
