package com.guokr.simbase;

import static org.junit.Assert.*;
import java.nio.ByteBuffer;

public abstract class TestableCallback extends SimCallback {

    public abstract void validator();
    
    
    public static TestableCallback noop(){
        
        return new TestableCallback(){
            @Override
            public void validator(){
            }
        };
    }
 public static TestableCallback print(){
        
        return new TestableCallback(){
            @Override
            public void validator(){
                byte[] dst = new byte[buffer.length];
                buffer.get(dst);
                System.out.println(dst);
            }
        };
    }
    @Override
    public void response() {
        validator();
    }

    public void assertArrayEquals(byte[] source, byte[] target) {
        assert(source.length==target.length);
        for(int index=0;index<source.length;index++){
            assert(source[index]==target[index]);
        }
    }

    public void assertBytes(byte[] expect) {
        byte[] dst = new byte[expect.length];
        buffer.get(dst);
        assertEquals(dst,expect);
    }

    public void assertByte(byte expect) {
        assert (buffer.get() == expect);
    }

    public void isOk() {
        ByteBuffer expect = ByteBuffer.allocate(5);
        expect.put(SimUtils.PLUS);
        expect.put(SimUtils.OK);
        expect.put(SimUtils.CRLF);
        assertBytes(expect.array());
    }
    
}
