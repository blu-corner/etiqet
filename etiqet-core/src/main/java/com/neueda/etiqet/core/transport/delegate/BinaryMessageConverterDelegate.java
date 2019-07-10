package com.neueda.etiqet.core.transport.delegate;

import com.neueda.etiqet.core.message.config.AbstractDictionary;

public interface BinaryMessageConverterDelegate<T> {

    T fromByteArray(byte[] binaryMessage);

    byte[] toByteArray(T message);

    default void setDictionary(AbstractDictionary dictionary){}
}
