package de.iqwrwq.ui;

import java.util.ArrayList;
import java.util.Arrays;

public class Command {
    public String header;
    public ArrayList<String> arguments;

    public Command(String header, String ... args){
        this.header = header;
        this.arguments = new ArrayList<>(Arrays.asList(args));
    }
}
