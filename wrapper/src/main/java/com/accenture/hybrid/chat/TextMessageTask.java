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

package com.accenture.hybrid.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.accenture.hybrid.chat.proto.Chat;
import com.accenture.hybrid.wrapper.remote.MarsTaskProperty;
import com.accenture.hybrid.wrapper.remote.NanoMarsTaskWrapper;
import android.util.Log;

/**
 * Send Text messaging task
 */
public class TextMessageTask extends NanoMarsTaskWrapper<Chat.SendMessageRequest, Chat.SendMessageResponse> {

    private Runnable callback = null;

    private Runnable onOK = null;
    private Runnable onError = null;

    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private Bundle properties = new Bundle();

    public TextMessageTask(ChatMsgRequest msgRequest, MarsTaskProperty property) {
        super(new Chat.SendMessageRequest(), new Chat.SendMessageResponse());

        request.accessToken = msgRequest.getAccessToken();
        request.from = msgRequest.getFrom();
        request.to = msgRequest.getTo();
        request.text = msgRequest.getText();
        request.topic = msgRequest.getTopic();
        request.deviceId = msgRequest.getDeviceId();
        properties = MarsTaskProperty.getPropertyBundle(property);

        Log.d("TextMessageTask", " request:" + request.toString()
            + " properties:" + properties.toString());

    }

    @Override
    public void onPreEncode(Chat.SendMessageRequest request) {

    }

    @Override
    public Bundle getProperties() {
        return properties;
    }

    @Override
    public void onPostDecode(Chat.SendMessageResponse response) {
        if (response.errCode == Chat.SendMessageResponse.ERR_OK) {

            Log.d("TextMessageTask", " callback = onOK");
            callback = onOK;

        } else {
            Log.d("TextMessageTask", "  callback = onError");
            callback = onError;
        }
    }

    @Override
    public void onTaskEnd(int errType, int errCode) {
        if (callback == null) {
            callback = onError;
        }
        Log.d("TextMessageTask", "onTaskEnd post callback");
        uiHandler.post(callback);
    }

    public TextMessageTask onOK(Runnable onOK) {

        Log.d("TextMessageTask", "onOK");
        this.onOK = onOK;
        return this;
    }

    public TextMessageTask onError(Runnable onError) {
        Log.d("TextMessageTask", "onError");
        this.onError = onError;
        return this;
    }
}
