package com.neueda.etiqet.core.message.cdr;

import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CdrItem {

    private CdrItemType type;
    private Long intval;
    private String strval;
    private Double doubleval;
    private Boolean boolVal;
    private List<Cdr> cdrs;
    public CdrItem(CdrItemType t) {
        type = t;
    }

    public CdrItem() {
        type = CdrItemType.CDR_STRING;
    }

    public CdrItemType getType() {
        return type;
    }

    public void setType(CdrItemType type) {
        this.type = type;
    }

    public Long getIntval() {
        return intval;
    }

    public void setIntval(Long intval) {
        this.intval = intval;
        this.type = CdrItemType.CDR_INTEGER;
    }

    public void setIntval(Integer intval) {
        this.intval = intval.longValue();
        this.type = CdrItemType.CDR_INTEGER;
    }

    public String getStrval() {
        return strval;
    }

    public void setStrval(String strval) {
        this.strval = strval;
        this.type = CdrItemType.CDR_STRING;
    }

    public Double getDoubleval() {
        return doubleval;
    }

    public void setDoubleval(Double doubleval) {
        this.doubleval = doubleval;
        this.type = CdrItemType.CDR_DOUBLE;
    }

    public Boolean getBoolVal() {
        return boolVal;
    }

    public void setBoolVal(Boolean boolVal) {
        this.boolVal = boolVal;
        this.type = CdrItemType.CDR_BOOLEAN;
    }

    public List<Cdr> getCdrs() {
        return cdrs;
    }

    public void setCdrs(List<Cdr> cdrs) {
        this.cdrs = cdrs;
        this.type = CdrItemType.CDR_ARRAY;
    }

    public void addCdrToList(Cdr cdr) {
        if(cdrs == null) {
            this.cdrs = new ArrayList<>();
        }
        cdrs.add(cdr);
        this.type = CdrItemType.CDR_ARRAY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CdrItem cdrItem = (CdrItem) o;
        return type == cdrItem.type &&
            Objects.equals(intval, cdrItem.intval) &&
            Objects.equals(strval, cdrItem.strval) &&
            Objects.equals(doubleval, cdrItem.doubleval) &&
            Objects.equals(boolVal, cdrItem.boolVal) &&
            Objects.equals(cdrs, cdrItem.cdrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, intval, strval, doubleval, boolVal, cdrs);
    }

    @Override
    public String toString() {
        switch (type) {
            case CDR_INTEGER:
                return intval.toString();
            case CDR_DOUBLE:
                return doubleval.toString();
            case CDR_BOOLEAN:
                return boolVal.toString();
            case CDR_STRING:
                return strval;
            case CDR_ARRAY:
                return cdrs.toString();
            case CDR_NULL:
                return String.valueOf(null);
            default:
                throw new EtiqetRuntimeException(String.format("Unsupported type '%s' for getAsString", type));
        }
    }

    public enum CdrItemType {
        CDR_STRING, CDR_INTEGER, CDR_DOUBLE, CDR_BOOLEAN, CDR_ARRAY, CDR_NULL
    }
}
