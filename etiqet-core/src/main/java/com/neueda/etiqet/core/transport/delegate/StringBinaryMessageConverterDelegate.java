package com.neueda.etiqet.core.transport.delegate;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StringBinaryMessageConverterDelegate implements BinaryMessageConverterDelegate<String> {

    @Override
    public String fromByteArray(byte[] binaryMessage) {
        return new String(binaryMessage);
    }

    @Override
    public byte[] toByteArray(String message) {
        return message.getBytes(UTF_8);
    }
}
