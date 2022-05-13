package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

/**
 * Tag used to encode/decode FIX messages
 */
public class Tag {
    private String key;
    private String field;
    private String value;
    private String meaning;
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

    public Tag() {
        this.used = false;
        this.key = "0";
    }

    public void put(String field, String value, Boolean used) {
        this.field = field;
        this.value = value;
        this.used = used;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isUsed() {
        return this.used;
    }

    public void setUsed() {
        this.used = true;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public String toString() {
        return "Tag{" +
            "key='" + key + '\'' +
            ", field='" + field + '\'' +
            ", value='" + value + '\'' +
            ", meaning='" + meaning + '\'' +
            ", used=" + used +
            '}';
    }
}
