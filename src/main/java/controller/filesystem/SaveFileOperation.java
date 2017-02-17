package controller.filesystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by vaba1010 on 17.02.2017.
 */
public class SaveFileOperation extends AbstractFileOperation implements FileOperation {

    // FIXME where it comes from???
    private String fileContent;
    private File saveFile;

    public SaveFileOperation(String filePath, String fileContent) {
        super(filePath);
        saveFile = new File(filePath);
        this.fileContent = fileContent;
    }

    @Override
    public void doFileOperation() throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(saveFile), "UTF-8"));
        try {
            out.write(fileContent);
        } catch (IOException ex) {
            // TODO
        } finally {
            out.close();
        }
    }
}
