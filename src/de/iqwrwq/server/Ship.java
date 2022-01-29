package de.iqwrwq.server;

import de.iqwrwq.client.Cargo;

public class Ship {

    public int id;
    public String name;
    private Cargo cargo;

    public Ship(int id,String name){
        this.id = id;
        this.name = name;
    }

    public void setCargo(Cargo cargo){
        this.cargo = cargo;
    }

    public Cargo getCargo(){
        return cargo;
    }


}
