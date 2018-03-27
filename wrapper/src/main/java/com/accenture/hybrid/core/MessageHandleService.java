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

package com.accenture.hybrid.core;

import com.accenture.hybrid.chat.proto.Messagepush;
import com.accenture.hybrid.utils.print.BaseConstants;
import com.accenture.hybrid.wrapper.remote.PushMessage;
import com.accenture.hybrid.wrapper.remote.PushMessageHandler;
import com.google.gson.Gson;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.tencent.mars.sdt.SignalDetectResult;
import com.tencent.mars.stn.TaskProfile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import com.tencent.mars.xlog.Log;

public class MessageHandleService implements PushMessageHandler {

    public static String TAG = "Mars.MessageHandleService";
    public static volatile long wifiRecvFlow = 0;
    public static volatile long wifiSendFlow = 0;
    public static volatile long mobileRecvFlow = 0;
    public static volatile long mobileSendFlow = 0;

    private Thread recvThread;

    private LinkedBlockingQueue<PushMessage> pushMessages = new LinkedBlockingQueue<>();

    private  Map<String, BusinessHandler> handlers = new HashMap<String, BusinessHandler>();

    public MessageHandleService() {
        this.start();
    }

    public void start() {
        if (recvThread == null) {
            recvThread = new Thread(pushReceiver, "PUSH-RECEIVER");
        }

        recvThread.start();
    }

    public void registerBusinessHandler(String key, BusinessHandler handler) {

        handlers.put(key, handler);

    }

    public void unRegisterBusinessHandler(String key) {

        handlers.remove(key);

    }

    public void unRegisterAllBusinessHandler() {

        handlers.clear();
        recvThread.interrupted();

    }

    private HashMap<String, Object> formatPushMessageToHashMap(PushMessage pushMessage) {

        HashMap<String, Object> returnMap = new HashMap<String, Object>();

        switch (pushMessage.cmdId) {
            case BaseConstants.PUSHMSG_CMDID:
                try {
                    Messagepush.MessagePush message = Messagepush.MessagePush.parseFrom(pushMessage.buffer);
                    returnMap.put("cmdId", pushMessage.cmdId);
                    returnMap.put("msgfrom", message.from);
                    returnMap.put("msgcontent", message.content);
                    returnMap.put("msgtopic", message.topic);
                } catch (InvalidProtocolBufferNanoException e) {
                    Log.e(TAG, "%s", e.toString());
                }
                break;
            case BaseConstants.CGIHISTORY_CMDID:
                Gson gson = new Gson();
                TaskProfile profile = gson.fromJson(new String(pushMessage.buffer,
                        Charset.forName("UTF-8")), TaskProfile.class);
                returnMap.put("cmdId", pushMessage.cmdId);
                returnMap.put("history", profile);
                break;
            case BaseConstants.CONNSTATUS_CMDID:
                returnMap.put("cmdId", pushMessage.cmdId);
                break;
            case BaseConstants.FLOW_CMDID:
                String flowStr = new String(pushMessage.buffer, Charset.forName("UTF-8"));
                String[] flowsizes = flowStr.split(",");
                wifiRecvFlow += Integer.valueOf(flowsizes[0]);
                wifiSendFlow += Integer.valueOf(flowsizes[1]);
                mobileRecvFlow += Integer.valueOf(flowsizes[2]);
                mobileSendFlow += Integer.valueOf(flowsizes[3]);
                returnMap.put("cmdId", pushMessage.cmdId);
                returnMap.put("wifiRecvFlow", wifiRecvFlow);
                returnMap.put("wifiSendFlow", wifiSendFlow);
                returnMap.put("mobileRecvFlow", mobileRecvFlow);
                returnMap.put("mobileSendFlow", mobileSendFlow);
                break;
            case BaseConstants.SDTRESULT_CMDID:
                Gson sdtGson = new Gson();
                SignalDetectResult sdtProfile = sdtGson.fromJson(new String(pushMessage.buffer,
                        Charset.forName("UTF-8")), SignalDetectResult.class);
                returnMap.put("cmdId", pushMessage.cmdId);
                returnMap.put("sdtResult", sdtProfile);
                break;
            default:
                break;
        }

        return returnMap;
    }


    private final Runnable pushReceiver = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    PushMessage pushMessage = pushMessages.take();
                    if (pushMessage != null) {
                        for (BusinessHandler handler : handlers.values()) {
                            HashMap map = formatPushMessageToHashMap(pushMessage);
                            if (handler.handleRecvMessage(map)) {
                                break;
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        //
                    }
                }
            }
        }
    };

    @Override
    public void process(PushMessage message) {
        pushMessages.offer(message);
    }
}
