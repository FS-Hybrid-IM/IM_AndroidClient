package com.accenture.mars.chat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.accenture.hybrid.chat.ChatMsgRequest;
import com.accenture.hybrid.chat.ConversationEntity;
import com.accenture.hybrid.chat.ConversationListTask;
import com.accenture.hybrid.chat.ConversationResult;
import com.accenture.hybrid.chat.TextMessageTask;
import com.accenture.hybrid.core.BusinessHandler;
import com.accenture.hybrid.core.MessageHandleService;
import com.accenture.hybrid.proto.Main;
import com.accenture.hybrid.wrapper.remote.MarsServiceProxy;
import com.accenture.hybrid.wrapper.remote.MarsTaskProperty;
import com.accenture.hybrid.wrapper.service.DefaultMarsServiceProfile;
import com.accenture.hybrid.wrapper.service.MarsServiceNative;
import com.accenture.hybrid.wrapper.service.MarsServiceProfile;
import com.accenture.hybrid.wrapper.service.MarsServiceProfileFactory;
import com.accenture.hybrid.wrapper.remote.PushMessage;
import com.accenture.hybrid.utils.print.BaseConstants;
import com.tencent.mars.app.AppLogic;
import com.tencent.mars.xlog.Xlog;

import com.accenture.hybrid.chat.proto.Messagepush;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static android.content.Context.MODE_WORLD_WRITEABLE;

public class MarsChatCordovaPlugin extends CordovaPlugin {

    protected final static String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";

    private static ArrayList<Bundle> messageStack = null;

    private UserServerProfile userProfile = new UserServerProfile();

    private static CallbackContext messageReceiveCallbackContext;
    private static CallbackContext messageSendCallbackContext;
    private static CallbackContext conversationListCallbackContext;


    class MessageHandler extends BusinessHandler {

        public MessageHandler() {

            if (MarsChatCordovaPlugin.messageStack == null) {
                MarsChatCordovaPlugin.messageStack = new ArrayList<Bundle>();
            }

        }

        @Override
        public boolean handleRecvMessage(PushMessage pushMessage) {

            switch (pushMessage.cmdId) {
                case BaseConstants.PUSHMSG_CMDID:
                    try {
                        Messagepush.MessagePush message = Messagepush.MessagePush.parseFrom(pushMessage.buffer);
                        Bundle bundle = new Bundle();
                        bundle.putString("cmdId", pushMessage.cmdId);
                        bundle.putString("msgfrom", message.from);
                        bundle.putString("msgcontent", message.content);
                        bundle.putString("msgtopic", message.topic);

                        Log.d("handleRecvMessage",  "PushMessage from:" + message.from
                        + " contetnt:" + message.content
                        + " topic:" + message.topic);
                        MarsChatCordovaPlugin.messageStack.add(bundle);
                        sendReceiveMessage(bundle);
                    } catch (InvalidProtocolBufferNanoException e) {
                        Log.e(TAG, "%s", e.toString());
                    }
                    return true;
                default:
                    break;
            }

            return false;
        }
    }

    private void onMessageReceive(final CallbackContext callbackContext) {
        MarsChatCordovaPlugin.messageReceiveCallbackContext = callbackContext;
        Log.d("onMessageReceive",  "Set keep Callback!!");
        PluginResult pluginresult = new PluginResult(PluginResult.Status.OK, "");
        pluginresult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginresult);
    }

    private void sendReceiveMessage(Bundle bundle) {
        Log.d("sendReceiveMessage",  "Enter");
        final CallbackContext callbackContext = MarsChatCordovaPlugin.messageReceiveCallbackContext;
        if (callbackContext != null && bundle != null) {
            JSONObject json = new JSONObject();
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                try {
                    json.put(key, bundle.get(key));
                } catch (JSONException e) {
                    callbackContext.error(e.getMessage());
                    return;
                }
            }

            Log.d("sendReceiveMessage",  "Json:" + json.toString());

            PluginResult pluginresult = new PluginResult(PluginResult.Status.OK, json);
            pluginresult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginresult);
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        String response = "success";

        if (action.equals("initPlatform")) {

            String serverHost = "";
            String userName = "";

            try {
                serverHost = data.getJSONObject(0).getString("host");
                userName = data.getJSONObject(0).getString("userName");

            } catch (JSONException e) {
                // TODO
            }

            this.userProfile.setUserName(userName);
            this.userProfile.setLongLinkHost(serverHost);
            this.setPreferenceProfile(userName, serverHost);

            AppLogic.AccountInfo accountInfo = new AppLogic.AccountInfo(
                    new Random(System.currentTimeMillis() / 1000).nextInt(), userName);

            System.loadLibrary("stlport_shared");
            System.loadLibrary("marsxlog");

            MarsServiceProxy.init(this.cordova.getActivity().getApplicationContext(), null, null);
            MarsServiceProxy.inst.accountInfo = accountInfo;

            MessageHandleService handlerService = new MessageHandleService();
            handlerService.registerBusinessHandler("message", new MessageHandler());
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.CGIHISTORY_CMDID, handlerService);
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.CONNSTATUS_CMDID, handlerService);
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.FLOW_CMDID, handlerService);
            MarsServiceProxy.setOnPushMessageListener(BaseConstants.PUSHMSG_CMDID, handlerService);
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.SDTRESULT_CMDID, handlerService);

            callbackContext.success();
            return true;

        } else if (action.equals("onMessageReceive")) {
            onMessageReceive(callbackContext);
            return true;

        } else if (action.equals("sendTextMessage")) {

            this.messageSendCallbackContext = callbackContext;

            if (data == null || data.length() == 0 || data.length() > 1) {
                JSONObject returnObj = new JSONObject();
                addProperty(returnObj, KEY_ERROR, action);
                addProperty(returnObj, KEY_MESSAGE, "Input Data is Null or larger than 1.");
                callbackContext.error(returnObj);
            }

            JSONObject msgObject = data.getJSONObject(0);

            ChatMsgRequest msgRequest = new ChatMsgRequest();
            msgRequest.setAccessToken(this.userProfile.getAccessToken());
            msgRequest.setFrom(this.userProfile.getUserName());
            msgRequest.setTo(msgObject.getString("to"));
            msgRequest.setText(msgObject.getString("text"));
            msgRequest.setTopic(msgObject.getString("topic"));

            MarsTaskProperty property = new MarsTaskProperty();
            property.setHost(this.userProfile.getLongLinkHost());
            property.setCgiPach("/mars/sendmessage");
            property.setCmdId(Main.CMD_ID_SEND_MESSAGE);
            property.setLongSupport(true);
            property.setShortSupport(false);

            MarsServiceProxy.send(new TextMessageTask(msgRequest, property)
                            .onOK(new Runnable() {

                                @Override
                                public void run() {
                                    MarsChatCordovaPlugin.this.messageSendCallbackContext.success();
                                }

                            }).onError(new Runnable() {

                                @Override
                                public void run() {
                                    MarsChatCordovaPlugin.this.messageSendCallbackContext.error("Send Message Failed!");
                                }

                            }));

            return true;

        } else if (action.equals("getConversationList")) {

            this.conversationListCallbackContext = callbackContext;

            MarsTaskProperty property = new MarsTaskProperty();
            property.setCgiPach("/mars/getconvlist");
            property.setHost(this.userProfile.getLongLinkHost());

            class MessageHandler implements ConversationResult {
                @Override
                public void process(List<ConversationEntity> list) {

                    JSONArray returnList = new JSONArray();

                    for (ConversationEntity m : list) {

                        Log.d("ConversationEntity",  "Name:" + m.getName()
                        + " topic:" + m.getTopic() + " notice:" + m.getNotice());
                        JSONObject json = new JSONObject();
                        addProperty(json, "name", m.getName());
                        addProperty(json, "topic",m.getTopic());
                        addProperty(json, "notice", m.getNotice());
                        returnList.put(json);
                    }

                    MarsChatCordovaPlugin.this.conversationListCallbackContext.success(returnList);

                }
            }

            MarsServiceProxy.send(new ConversationListTask(property, new MessageHandler()));
            return true;


        } else {
            return false;
        }

    }

    private void setPreferenceProfile(String userName, String serverHost) {

        String PREFS_NAME = "chat.user.profile";
        SharedPreferences userProfile = cordova.getActivity().getApplicationContext()
                .getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = userProfile.edit();
        editor.putString("chatUserName", userName);
        editor.putString("chatServerHost", serverHost);
        editor.commit();
    }

    private void addProperty(JSONObject obj, String key, Object value) {
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException ignored) {
            //Believe exception only occurs when adding duplicate keys, so just ignore it
        }
    }

}
