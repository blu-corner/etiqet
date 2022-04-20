package com.neueda.etiqet.orderbook.etiqetorderbook.entity;


public class Tag {
    private String key;
    private String field;
    private String value;
    private Boolean used;

    public Tag(String field, String value) {
        this.field = field;
        this.value = value;
        this.used = false;
    }

    public Tag(String key, String field, String value) {
        this.key = key;
        this.field = field;
        this.value = value;
        this.used = false;
    }

    public Tag(){
        this.used = false;
        this.key = "0";
    }

    public void put(String field, String value, Boolean used){
        this.field = field;
        this.value = value;
        this.used = used;
    }

    public String getField(){
        return this.field;
    }

    public String getValue(){
        return this.value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public boolean isUsed(){
        return this.used;
    }

    public void setUsed(){
        this.used = true;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "Tag{" +
            "key='" + key + '\'' +
            ", field='" + field + '\'' +
            ", value='" + value + '\'' +
            ", used=" + used +
            '}';
    }
}
