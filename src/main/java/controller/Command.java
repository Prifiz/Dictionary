package controller;

import datamodel.Dictionary;
import gui.swingui.ViewCustomizationRecord;

import java.io.IOException;
import java.util.List;

public interface Command {
    void execute(Dictionary dictionary) throws IOException;
}
