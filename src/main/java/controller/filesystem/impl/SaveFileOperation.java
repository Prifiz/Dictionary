package controller.filesystem.impl;

import controller.filesystem.AbstractFileOperation;
import controller.filesystem.FileOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class SaveFileOperation extends AbstractFileOperation implements FileOperation {

    private static final Logger LOGGER = LogManager.getLogger(SaveFileOperation.class);

    public SaveFileOperation(String filePath, String fileContent) {
        super(filePath);
        this.fileContent = fileContent;
    }

    @Override
    public void doFileOperation() throws IOException {
        File parent = file.getParentFile();
        if(parent != null) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IOException("Couldn't create directory structure for file");
            }
        }
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            out.write(fileContent);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}
