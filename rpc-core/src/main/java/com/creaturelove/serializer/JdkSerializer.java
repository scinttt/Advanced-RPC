package com.creaturelove.serializer;

import java.io.*;

public class JdkSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        // Initialize a object used to output byte stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Initialize a object used to change object to byte stream

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            // change the object from object to byte stream
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            // return the stream as Byte array type
            return outputStream.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException{
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

