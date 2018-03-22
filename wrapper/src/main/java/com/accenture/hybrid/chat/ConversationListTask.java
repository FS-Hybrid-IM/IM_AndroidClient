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

import com.accenture.hybrid.proto.Main;
import com.accenture.hybrid.wrapper.remote.MarsTaskProperty;
import com.accenture.hybrid.wrapper.remote.NanoMarsTaskWrapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Get Conversation List task
 */
public class ConversationListTask extends NanoMarsTaskWrapper<Main.ConversationListRequest, Main.ConversationListResponse> {

    private List<ConversationEntity> dataList = new LinkedList<>();
    private Bundle properties = new Bundle();

    private ConversationResult result;

    public ConversationListTask(MarsTaskProperty property, ConversationResult callbackResult) {
        super(new Main.ConversationListRequest(), new Main.ConversationListResponse());
        properties = MarsTaskProperty.getPropertyBundle(property);
        result = callbackResult;

    }

    @Override
    public Bundle getProperties() {
        return properties;
    }

    @Override
    public void onPreEncode(Main.ConversationListRequest req) {
        req.type = Main.ConversationListRequest.DEFAULT;
    }

    @Override
    public void onPostDecode(Main.ConversationListResponse response) {

    }

    @Override
    public void onTaskEnd(int errType, int errCode) {
        if (response != null) {
            for (Main.Conversation conv : response.list) {
                dataList.add(new ConversationEntity(conv.name, conv.topic, conv.notice));
            }
        }
        result.process(dataList);
    }
}
