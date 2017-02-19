package controller.filesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import java.io.*;

public class LoadFileOperation extends AbstractFileOperation implements FileOperation {

    private static final Logger LOGGER = LogManager.getLogger(LoadFileOperation.class);

    public LoadFileOperation(String filePath) {
        super(filePath);
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
            }
            fileContent = stringBuilder.toString();
            in.close();
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}
