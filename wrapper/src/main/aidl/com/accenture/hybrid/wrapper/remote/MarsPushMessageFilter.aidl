// MarsRecvCallBack.aidl
package com.accenture.hybrid.wrapper.remote;

// Declare any non-default types here with import statements

interface MarsPushMessageFilter {

    // returns processed ?
    boolean onRecv(int cmdId, inout byte[] buffer);

}
