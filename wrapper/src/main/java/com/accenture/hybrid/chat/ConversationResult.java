package com.accenture.hybrid.chat;

import com.accenture.hybrid.chat.ConversationEntity;

import java.util.List;

/**
 * Created by bo.e.liu on 17/03/2018.
 */

public interface ConversationResult {

    void process(List<ConversationEntity> list);

}
