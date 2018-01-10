package controller.filesystem.impl;

import controller.filesystem.AbstractFileOperation;
import controller.filesystem.FileOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import java.io.*;

public class LoadFileOperation extends AbstractFileOperation implements FileOperation {

    private static final Logger LOGGER = LogManager.getLogger(LoadFileOperation.class);

    private boolean appendLineBreak;

    public LoadFileOperation(String filePath) {
        super(filePath);
        appendLineBreak = false;
    }

    public LoadFileOperation(String filePath, boolean appendLineBreak) {
        super(filePath);
        this.appendLineBreak = appendLineBreak;
    }

    @Override
    public void doFileOperation() throws IOException {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), Constants.UTF_8));

            String str;
            StringBuilder stringBuilder = new StringBuilder();
            while ((str = in.readLine()) != null) {
                stringBuilder.append(str);
                if(appendLineBreak) {
                    stringBuilder.append(Constants.EOL);
                }
            }
            fileContent = stringBuilder.toString();
            in.close();
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}
