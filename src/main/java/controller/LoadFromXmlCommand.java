package controller;

import datamodel.Dictionary;

import java.io.IOException;

/**
 * Created by vaba1010 on 10.01.2017.
 */
public class LoadFromXmlCommand extends AbstractLoadCommand {

    public LoadFromXmlCommand(String filePath) throws IOException {
        super(filePath);
    }

    public void execute(Dictionary dictionary) throws IllegalArgumentException {
        dictionary.resetWithNewData(DictionaryFileReader.readFile(fileContent));
    }
}
