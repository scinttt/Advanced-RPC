package com.creaturelove.server.tcp;

import com.creaturelove.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

// Enhance the buffer processor through decorator pattern
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer){
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler){
        // Construct Parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // initialization
            int size = -1;

            // get (header + body) at once
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if( -1 == size){
                    // read message body length
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    //write header info to the result
                    resultBuffer.appendBuffer(buffer);
                }else{
                    // write body info to the result
                    resultBuffer.appendBuffer(buffer);
                    // make it complete buffer, execute processing
                    bufferHandler.handle(resultBuffer);
                    // do it again
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }

            }
        });

        return parser;
    }
}
