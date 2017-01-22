package controller;

import datamodel.Dictionary;

import java.io.IOException;

/**
 * Created by Prifiz on 03.01.2017.
 */
public interface Command {
    void execute(Dictionary dictionary) throws IOException;
}
