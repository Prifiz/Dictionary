package controller;


import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by vaba1010 on 10.01.2017.
 */
public abstract class AbstractLoadCommand extends AbstractFileCommand {

    public AbstractLoadCommand(String filePath) throws IOException {
        super(filePath);

        //fileContent = IOUtils.toString(new FileInputStream(loadFile));


        try {
            File loadFile = new File(filePath);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(loadFile), "UTF-8"));

            String str;
            StringBuilder stringBuilder = new StringBuilder();
            while ((str = in.readLine()) != null) {
                stringBuilder.append(str);
            }
            fileContent = stringBuilder.toString();
            in.close();
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}
