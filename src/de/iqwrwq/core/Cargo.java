package de.iqwrwq.core;

import java.util.regex.Pattern;

public class Cargo {
    public String toString;
    public int id;
    public String source;
    public String destination;
    public int value;

    public Cargo(String information){
        this.toString = information;
        String[] information_arr = information.split(Pattern.quote("|"));
        this.id = Integer.parseInt(information_arr[1]);
        this.source = information_arr[2];
        this.destination = information_arr[3];
        this.value =  Integer.parseInt(information_arr[4]);
    }
}
