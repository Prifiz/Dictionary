package controller;

import java.io.*;

public abstract class AbstractSaveCommand implements Command {

    private File saveFile;

    public AbstractSaveCommand(String filePath) {
        saveFile = new File(filePath);
    }

    protected void save(String fileContent) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(saveFile), "UTF-8"));
        try {
            out.write(fileContent);
        } finally {
            out.close();
        }
    }
}
