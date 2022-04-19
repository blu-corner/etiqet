package com.neueda.etiqet.orderbook.etiqetorderbook.entity;


public class Tag {
    private String key;
    private String value;
    private Boolean used;

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
        this.used = false;
    }

    public Tag(){
        this.used = false;
    }

    public void put(String key, String value, Boolean used){
        this.key = key;
        this.value = value;
        this.used = used;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

    public boolean isUsed(){
        return this.used;
    }

    public void setUsed(){
        this.used = true;
    }

}
