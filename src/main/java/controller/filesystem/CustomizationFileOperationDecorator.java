package controller.filesystem;

import gui.swingui.ViewCustomizationRecord;

import java.util.List;

/**
 * Created by vaba1010 on 17.02.2017.
 */
public class CustomizationFileOperationDecorator extends FileOperationDecorator {

    List<ViewCustomizationRecord> customizationData;

    public CustomizationFileOperationDecorator(FileOperation fileOperation, List<ViewCustomizationRecord> customizationData) {
        super(fileOperation);
        this.customizationData = customizationData;
    }
}
