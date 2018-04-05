/*
* Tencent is pleased to support the open source community by making Mars available.
* Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
*
* Licensed under the MIT License (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://opensource.org/licenses/MIT
*
* Unless required by applicable law or agreed to in writing, software distributed under the License is
* distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
* either express or implied. See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.accenture.hybrid.wrapper.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.tencent.mars.Mars;
import com.tencent.mars.app.AppLogic;
import com.accenture.hybrid.wrapper.remote.MarsPushMessageFilter;
import com.accenture.hybrid.wrapper.remote.MarsService;
import com.accenture.hybrid.wrapper.remote.MarsTaskWrapper;
import com.tencent.mars.sdt.SdtLogic;
import com.tencent.mars.stn.StnLogic;
import com.tencent.mars.xlog.Log;

/**
 * Actually Mars Service running in main app
 * <p></p>
 * Created by zhaoyuan on 16/2/29.
 */
public class MarsServiceNative extends Service implements MarsService {

    private static final String TAG = "Mars.Sample.MarsServiceNative";

    private MarsServiceStub stub;

    @Override
    public int send(MarsTaskWrapper taskWrapper, Bundle taskProperties) throws RemoteException {
        return stub.send(taskWrapper, taskProperties);
    }

    @Override
    public void cancel(int taskID) throws RemoteException {
        stub.cancel(taskID);
    }

    @Override
    public void registerPushMessageFilter(MarsPushMessageFilter filter) throws RemoteException {
        stub.registerPushMessageFilter(filter);
    }

    @Override
    public void unregisterPushMessageFilter(MarsPushMessageFilter filter) throws RemoteException {
        stub.unregisterPushMessageFilter(filter);
    }

    @Override
    public void setAccountInfo(long uin, String userName) {
        stub.setAccountInfo(uin, userName);
    }

    @Override
    public void setForeground(int isForeground) {
        stub.setForeground(isForeground);
    }

    @Override
    public IBinder asBinder() {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String PREFS_NAME = "chat.user.profile";
        SharedPreferences userProfile = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
        String userName = userProfile.getString("chatUserName", "");
        String serverHost = userProfile.getString("chatServerHost", "");

        DefaultMarsServiceProfile profile = new DefaultMarsServiceProfile();
        profile.setLongLinkHost(serverHost);

        stub = new MarsServiceStub(this, profile);

        android.util.Log.e("MarsServiceNative",
                "onCreate profile LongHost:" + profile.longLinkHost()
                        + " LongPorts:" + profile.longLinkPorts().toString()
                        + " shortPort:" + profile.shortLinkPort()
        );


        // set callback
        AppLogic.setCallBack(stub);
        StnLogic.setCallBack(stub);
        SdtLogic.setCallBack(stub);

        // Initialize the Mars PlatformComm
        Mars.init(getApplicationContext(), new Handler(Looper.getMainLooper()));

        // Initialize the Mars
        StnLogic.setLonglinkSvrAddr(profile.longLinkHost(), profile.longLinkPorts());
        StnLogic.setShortlinkSvrAddr(profile.shortLinkPort());
        StnLogic.setClientVersion(profile.productID());
        Mars.onCreate(true);

        StnLogic.makesureLongLinkConnected();

        //
        Log.d(TAG, "mars service native created");
        android.util.Log.e("MarsServiceNative",
                "onCreate: mars service native create");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "mars service native destroying");

        // Leave Mars
        Mars.onDestroy();
        // ContentDistributionNetwork.onDestroy();

        Log.d(TAG, "mars service native destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }
}
