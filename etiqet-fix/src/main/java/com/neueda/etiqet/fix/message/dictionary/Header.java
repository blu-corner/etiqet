package com.neueda.etiqet.fix.message.dictionary;

public class Header
{
    private Field[] field;

    private Component component;

    public Field[] getField ()
    {
        return field;
    }

    public void setField (Field[] field)
    {
        this.field = field;
    }

    public Component getComponent ()
    {
        return component;
    }

    public void setComponent (Component component)
    {
        this.component = component;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [field = "+field+", component = "+component+"]";
    }
}