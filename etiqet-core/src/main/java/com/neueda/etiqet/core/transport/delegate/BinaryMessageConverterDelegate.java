package com.neueda.etiqet.core.transport.delegate;

public interface BinaryMessageConverterDelegate<T> {

    T fromByteArray(byte[] binaryMessage);

    byte[] toByteArray(T message);

}
