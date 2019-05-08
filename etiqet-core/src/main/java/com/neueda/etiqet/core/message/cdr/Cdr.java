package com.neueda.etiqet.core.message.cdr;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Cdr {
    protected String msgType;
    private final Map<String, CdrItem> items = new LinkedHashMap<>();

    public Cdr (String typeId) {
        this.msgType = typeId;
    }

    public String getType() {
        return msgType;
    }

    public void set(String key, Long val) {
        CdrItem i = new CdrItem(CdrItem.CdrItemType.CDR_INTEGER);
        i.setIntval(val);
        setItem(key, i);
    }

    public void set(String key, Integer val) {
        CdrItem i = new CdrItem(CdrItem.CdrItemType.CDR_INTEGER);
        i.setIntval(val.longValue());
        setItem(key, i);
    }

    public void set(String key, String val) {
        CdrItem i = new CdrItem(CdrItem.CdrItemType.CDR_STRING);
        i.setStrval(val);
        setItem(key, i);
    }

    public void set(String key, Double val) {
        CdrItem i = new CdrItem(CdrItem.CdrItemType.CDR_DOUBLE);
        i.setDoubleval(val);
        setItem(key, i);
    }

    public void set(String key, Boolean val) {
        CdrItem i = new CdrItem(CdrItem.CdrItemType.CDR_BOOLEAN);
        i.setBoolVal(val);
        setItem(key, i);
    }

    public Boolean containsKey(String key) {
        return items.containsKey(key);
    }

    public String getAsString(String key) {
        CdrItem i = getItem(key);
        return (i != null)? i.toString (): null;
    }
    
    public CdrItem getItem(String key) {
        return items.get(key);
    }

    public void setItem(String key, CdrItem val) {
        items.put(key, val);
    }

    public Cdr update(Cdr d) {
        d.items.forEach(this::setItem);
        return this;
    }

    public Cdr replace(Cdr d) {
        items.clear();
        msgType = d.msgType;
        return update(d);
    }

    public void clear() {
        items.clear();
    }
    
    public Map<String, CdrItem> getItems() {
		return items;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cdr cdr = (Cdr) o;
        return Objects.equals(msgType, cdr.msgType) &&
            items.keySet().containsAll(cdr.items.keySet()) &&
            items.values().containsAll(cdr.items.values()) &&
            cdr.items.keySet().containsAll(items.keySet()) &&
            cdr.items.values().containsAll(items.values());
    }

    @Override
    public int hashCode() {
        return Objects.hash(msgType, items);
    }

	@Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	
    	for (Map.Entry<String, CdrItem> entry : items.entrySet()) {
            builder.append("[key=")
                   .append(entry.getKey())
                   .append(", value=")
                   .append(entry.getValue().toString())
                   .append("]|");
        }
    	
    	return builder.toString();
    }
}
