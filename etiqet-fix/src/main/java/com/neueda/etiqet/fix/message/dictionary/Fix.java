package com.neueda.etiqet.fix.message.dictionary;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Fix {
	private Trailer trailer;

    private String minor;

    private Components components;

    private String type;

    private String major;

    private Header header;

    private Messages messages;

    private String servicepack;

    private Fields fields;

    public Trailer getTrailer ()
    {
        return trailer;
    }

    public void setTrailer (Trailer trailer)
    {
        this.trailer = trailer;
    }

    public String getMinor ()
    {
        return minor;
    }

    public void setMinor (String minor)
    {
        this.minor = minor;
    }

    public Components getComponents ()
    {
        return components;
    }

    public void setComponents (Components components)
    {
        this.components = components;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getMajor ()
    {
        return major;
    }

    public void setMajor (String major)
    {
        this.major = major;
    }

    public Header getHeader ()
    {
        return header;
    }

    public void setHeader (Header header)
    {
        this.header = header;
    }

    public Messages getMessages ()
    {
        return messages;
    }

    public void setMessages (Messages messages)
    {
        this.messages = messages;
    }

    public String getServicepack ()
    {
        return servicepack;
    }

    public void setServicepack (String servicepack)
    {
        this.servicepack = servicepack;
    }

    public Fields getFields ()
    {
        return fields;
    }

    public void setFields (Fields fields)
    {
        this.fields = fields;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [trailer = "+trailer+", minor = "+minor+", components = "+components+", type = "+type+", major = "+major+", header = "+header+", messages = "+messages+", servicepack = "+servicepack+", fields = "+fields+"]";
    }
}
