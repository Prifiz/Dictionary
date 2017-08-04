package controller;

import datamodel.Dictionary;

import java.io.IOException;

public interface Command {
    void execute(Dictionary dictionary) throws IOException;
}
