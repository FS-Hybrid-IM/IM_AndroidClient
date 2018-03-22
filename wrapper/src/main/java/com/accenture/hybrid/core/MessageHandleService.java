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

import com.accenture.hybrid.core.BusinessHandler;
import com.accenture.hybrid.wrapper.remote.PushMessage;
import com.accenture.hybrid.wrapper.remote.PushMessageHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandleService implements PushMessageHandler {

    public static String TAG = "Mars.MessageHandleService";

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


    private final Runnable pushReceiver = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    PushMessage pushMessage = pushMessages.take();
                    if (pushMessage != null) {
                        for (BusinessHandler handler : handlers.values()) {
                            if (handler.handleRecvMessage(pushMessage)) {
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
