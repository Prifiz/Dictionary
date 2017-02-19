package controller.filesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class SaveFileOperation extends AbstractFileOperation implements FileOperation {

    private static final Logger LOGGER = LogManager.getLogger(SaveFileOperation.class);

    public SaveFileOperation(String filePath, String fileContent) {
        super(filePath);
        this.fileContent = fileContent;
    }

    @Override
    public void doFileOperation() throws IOException {
        if(!file.getParentFile().mkdirs()) {
            throw new IOException("Couldn't create directory structure for file");
        }
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            out.write(fileContent);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}
