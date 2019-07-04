package com.neueda.etiqet.core.message;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;

public class CdrItemBuilder {
    private CdrItem cdrItem;

    private CdrItemBuilder(CdrItem.CdrItemType itemType) {
        cdrItem = new CdrItem(itemType);
    }

    public static CdrItemBuilder aCdrItem(CdrItem.CdrItemType itemType) {
        return new CdrItemBuilder(itemType);
    }

    public CdrItemBuilder addCdr(Cdr cdr) {
        cdrItem.addCdrToList(cdr);
        return this;
    }

    public CdrItem build() {
        return cdrItem;
    }
}
