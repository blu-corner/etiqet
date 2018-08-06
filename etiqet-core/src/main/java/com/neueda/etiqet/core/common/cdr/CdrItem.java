package com.neueda.etiqet.core.common.cdr;

import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;

import java.util.List;

public class CdrItem {

    public enum CdrItemType {
        CDR_STRING, CDR_INTEGER, CDR_DOUBLE, CDR_BOOLEAN, CDR_ARRAY
    }

    private CdrItemType type;
    private Integer intval;
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

    public Integer getIntval() {
        return intval;
    }

    public void setIntval(Integer intval) {
        this.intval = intval;
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
            default:
                throw new EtiqetRuntimeException(String.format("Unsupported type '%s' for getAsString", type));
        }
    }
}