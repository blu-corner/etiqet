package com.neueda.etiqet.fix.message.dictionary;

public class Components
{
    private Component[] component;

    public Component[] getComponent ()
    {
        return component;
    }

    public void setComponent (Component[] component)
    {
        this.component = component;
    }

    @Override
    public String toString()
    {
        return "Components [component = "+component+"]";
    }
}
