package controller.filesystem;

import java.io.IOException;

/**
 * Created by vaba1010 on 17.02.2017.
 */
public class XmlFileOperationDecorator extends FileOperationDecorator {

    public XmlFileOperationDecorator(FileOperation fileOperation) {
        super(fileOperation);
    }

    public void setContent(FileOperation fileOperation) {

    }

    @Override
    public void doFileOperation() throws IOException {

    }
}
