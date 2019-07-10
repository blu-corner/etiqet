package com.neueda.etiqet.core.transport.delegate;

public class ByteArrayConverterDelegate implements BinaryMessageConverterDelegate<byte[]> {
    @Override
    public byte[] fromByteArray(byte[] binaryMessage) {
        return binaryMessage;
    }

    @Override
    public byte[] toByteArray(byte[] message) {
        return message;
    }
}
