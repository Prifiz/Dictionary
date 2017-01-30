package controller.filesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public abstract class AbstractLoadCommand extends AbstractFileCommand {

    private static final Logger LOGGER = LogManager.getLogger(AbstractLoadCommand.class);

    public AbstractLoadCommand(String filePath) throws IOException {
        super(filePath);

        try {
            File loadFile = new File(filePath);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(loadFile), Constants.UTF_8));

            String str;
            StringBuilder stringBuilder = new StringBuilder();
            while ((str = in.readLine()) != null) {
                stringBuilder.append(str);
            }
            fileContent = stringBuilder.toString();
            in.close();
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

}
