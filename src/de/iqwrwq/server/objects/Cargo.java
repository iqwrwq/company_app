package de.iqwrwq.server.objects;

import de.iqwrwq.ui.Command;

import java.util.regex.Pattern;

public class Cargo {
    public int id;
    public Harbour source;
    public Harbour destination;
    public int value;

    public Cargo(String cargo){
        int id = 1;
        int source_harbour = 2;
        int dest_harbour = 3;
        int value = 4;

        String[] cargo_parts = cargo.split(Pattern.quote("|"));
        this.id = Integer.parseInt(cargo_parts[id]);
        this.source = new Harbour(cargo_parts[source_harbour]);
        this.destination = new Harbour(cargo_parts[dest_harbour]);
        this.value =  Integer.parseInt(cargo_parts[value]);
    }
}
