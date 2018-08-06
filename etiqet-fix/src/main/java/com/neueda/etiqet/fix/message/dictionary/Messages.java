package com.neueda.etiqet.fix.message.dictionary;

public class Messages
{
    private Message[] message;

    public Message[] getMessage ()
    {
        return message;
    }

    public void setMessage (Message[] message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+"]";
    }
}
