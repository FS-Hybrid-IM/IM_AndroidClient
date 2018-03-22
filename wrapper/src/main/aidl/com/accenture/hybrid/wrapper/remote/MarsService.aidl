// IMarsService.aidl
package com.accenture.hybrid.wrapper.remote;

// Declare any non-default types here with import statements

import com.accenture.hybrid.wrapper.remote.MarsTaskWrapper;
import com.accenture.hybrid.wrapper.remote.MarsPushMessageFilter;

interface MarsService {

    int send(MarsTaskWrapper taskWrapper, in Bundle taskProperties);

    void cancel(int taskID);

    void registerPushMessageFilter(MarsPushMessageFilter filter);

    void unregisterPushMessageFilter(MarsPushMessageFilter filter);

    void setAccountInfo(in long uin, in String userName);

    void setForeground(in int isForeground);
}
