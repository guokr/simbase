package com.guokr.simbase.handler;

import clojure.lang.IFn;
import clojure.lang.ISeq;

import com.guokr.simbase.server.ServerCallback;

public class Ping implements IFn {

    @Override
    public Object call() throws Exception {
        return invoke();
    }

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object applyTo(ISeq arg0) {
        return invoke();
    }

    @Override
    public Object invoke() {
        return "pong";
    }

    @Override
    public Object invoke(Object arg0) {
        return invoke();
    }

    @Override
    public Object invoke(Object engine, Object callback) {
        ServerCallback cb = (ServerCallback) callback;
        cb.stringValue("pong");
        cb.flip();
        cb.response.run(cb.buffer);
        return true;
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        // TODO Auto-generated method stub
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13, Object arg14) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13, Object arg14, Object arg15) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13, Object arg14, Object arg15, Object arg16) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13, Object arg14, Object arg15, Object arg16, Object arg17) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13, Object arg14, Object arg15, Object arg16, Object arg17, Object arg18) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13, Object arg14, Object arg15, Object arg16, Object arg17, Object arg18, Object arg19) {
        return invoke();
    }

    @Override
    public Object invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
            Object arg10, Object arg11, Object arg12, Object arg13, Object arg14, Object arg15, Object arg16, Object arg17, Object arg18, Object arg19,
            Object... arg20) {
        return invoke();
    }

}
