package com.creaturelove.protocol;

import cn.hutool.http.Header;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    // Message header
    private Header header;

    // message body
    private T body;

    @Data
    public static class Header{
        // ensure security with magic number
        private byte magic;

        // version number
        private byte version;

        // serializer
        private byte serializer;

        // message type
        private byte type;

        // status
        private byte status;

        // request id
        private long requestId;

        // message body length
        private int bodyLength;
    }
}
