package com.neueda.etiqet.fix.message.dictionary;

import javax.xml.bind.annotation.XmlAttribute;

public class Message
{
    private Field[] field;

    private String msgcat;

    private Component[] component;

    private String msgtype;

    private String name;

    public Field[] getField ()
    {
        return field;
    }

    public void setField (Field[] field)
    {
        this.field = field;
    }

    @XmlAttribute
    public String getMsgcat ()
    {
        return msgcat;
    }

    public void setMsgcat (String msgcat)
    {
        this.msgcat = msgcat;
    }

    public Component[] getComponent ()
    {
        return component;
    }

    public void setComponent (Component[] component)
    {
        this.component = component;
    }

    @XmlAttribute
    public String getMsgtype ()
    {
        return msgtype;
    }

    public void setMsgtype (String msgtype)
    {
        this.msgtype = msgtype;
    }

    @XmlAttribute
    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Message [field = "+field+", msgcat = "+msgcat+", component = "+component+", msgtype = "+msgtype+", name = "+name+"]";
    }
}
