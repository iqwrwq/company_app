package de.iqwrwq.server.objects;

public class Ship {

    public int id;
    public String name;
    private Cargo cargo;

    public Ship(int id,String name){
        this.id = id;
        this.name = name;
    }
}
