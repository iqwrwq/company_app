package de.iqwrwq.server.objects;

import de.iqwrwq.ui.req;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Cargo {
    public int id;
    public Harbour source;
    public Harbour destination;
    public int value;

    public Cargo(@NotNull String cargo) {
        int id = 1;
        int source_harbour = 2;
        int dest_harbour = 3;
        int value = 4;

        System.out.println(cargo);
        String[] cargo_parts = cargo.split(Pattern.quote("|"));
        this.id = Integer.parseInt(cargo_parts[id]);
        this.source = new Harbour(cargo_parts[source_harbour]);
        this.destination = new Harbour(cargo_parts[dest_harbour]);
        this.value = Integer.parseInt(cargo_parts[value]);
    }

    public String[] getInfo() {
        return new String[]{String.valueOf(id), source.name, destination.name, String.valueOf(value)};
    }

    public String getObjString() {
        return "{ " + id + req.DIVIDER + source.name + req.SEPARATOR + destination.name + req.DIVIDER + value + " }";
    }
}
