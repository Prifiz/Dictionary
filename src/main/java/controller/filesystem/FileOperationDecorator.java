package controller.filesystem;


import java.io.IOException;

/**
 * Created by vaba1010 on 17.02.2017.
 */
public abstract class FileOperationDecorator implements FileOperation {

    protected FileOperation fileOperation;

    public FileOperationDecorator(FileOperation fileOperation) {
        this.fileOperation = fileOperation;
    }

    @Override
    public void doFileOperation() throws IOException {
        fileOperation.doFileOperation();
    }

}
