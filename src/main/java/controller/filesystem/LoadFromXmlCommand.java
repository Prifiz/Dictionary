package controller.filesystem;

import controller.DictionaryFileReader;
import datamodel.Dictionary;

import java.io.IOException;

public class LoadFromXmlCommand extends AbstractLoadCommand {

    public LoadFromXmlCommand(String filePath) throws IOException {
        super(filePath);
    }

    public void execute() throws IllegalArgumentException {
        dictionary.resetWithNewData(DictionaryFileReader.readFile(fileContent));
    }
}
