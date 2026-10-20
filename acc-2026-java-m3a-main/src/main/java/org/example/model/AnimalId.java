package org.example.model;

public final class AnimalId {
    private static int nextId = 1;
    private final String value;
    public AnimalId(){
        this.value = String.valueOf(nextId++);
    }

    public AnimalId(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
