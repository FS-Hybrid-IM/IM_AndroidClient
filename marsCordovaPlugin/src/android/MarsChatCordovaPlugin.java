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
import com.accenture.hybrid.utils.print.BaseConstants;
import com.tencent.mars.app.AppLogic;
import com.tencent.mars.xlog.Xlog;

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

    public static String covertHashMapToString(HashMap hashmap) {

        ObjectMapper mapper = new ObjectMapper();
        try
        {
            //Convert Map to JSON
            String json = mapper.writeValueAsString(hashmap);

            return json;
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

//    class MessageHandler extends BusinessHandler {
//
//        private String JSCallback;
//        private CordovaWebView webView;
//
//        public MessageHandler(String jsCallback) {
//
//            this.JSCallback = jsCallback;
//
//        }
//
//        @Override
//        public boolean handleRecvMessage(HashMap<String, Object> message) {
//
//            Integer cmdId;
//
//            if (message.containsKey("cmdId")) {
//                cmdId = Integer.parseInt((String)message.get("cmdId"));
//            } else {
//                return false;
//            }
//            String jsonString = MarsChatCordovaPlugin.covertHashMapToString(message);
//
//            if (jsonString == null) {
//                return false;
//            }
//
//            switch (cmdId) {
//                case BaseConstants.PUSHMSG_CMDID:
//                    webView.loadUrl("javascript:" + this.JSCallback + "(" + jsonString + ");");
//                    return true;
//                default:
//                    break;
//            }
//
//            return false;
//        }
//    }
//
//    private void onMessageReceive(final CallbackContext callbackContext) {
//        MarsChatCordovaPlugin.messageReceiveCallbackContext = callbackContext;
//        if (MarsChatCordovaPlugin.messageStack != null) {
//            for (Bundle bundle : MarsChatCordovaPlugin.messageStack) {
//                MarsChatCordovaPlugin.sendReceiveMessage(bundle);
//            }
//            MarsChatCordovaPlugin.messageStack.clear();
//        }
//    }
//
//    public static void sendReceiveMessage(Bundle bundle) {
//        if (MarsChatCordovaPlugin.messageReceiveCallbackContext == null) {
//            if (MarsChatCordovaPlugin.messageStack == null) {
//                MarsChatCordovaPlugin.messageStack = new ArrayList<Bundle>();
//            }
//            notificationStack.add(bundle);
//            return;
//        }
//        final CallbackContext callbackContext = MarsChatCordovaPlugin.messageReceiveCallbackContext;
//        if (callbackContext != null && bundle != null) {
//            JSONObject json = new JSONObject();
//            Set<String> keys = bundle.keySet();
//            for (String key : keys) {
//                try {
//                    json.put(key, bundle.get(key));
//                } catch (JSONException e) {
//                    callbackContext.error(e.getMessage());
//                    return;
//                }
//            }
//
//            PluginResult pluginresult = new PluginResult(PluginResult.Status.OK, json);
//            pluginresult.setKeepCallback(true);
//            callbackContext.sendPluginResult(pluginresult);
//        }
//    }

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

            callbackContext.success();
            return true;

        } else if (action.equals("onMessageReceive")) {

//            JSONObject msgObject = data.getJSONObject(0);
//
//            String JsCallback = msgObject.getString("JsCallback");
//
//            MessageHandleService handlerService = new MessageHandleService();
//            handlerService.registerBusinessHandler("message", new MessageHandler(JsCallback));
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.CGIHISTORY_CMDID, handlerService);
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.CONNSTATUS_CMDID, handlerService);
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.FLOW_CMDID, handlerService);
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.PUSHMSG_CMDID, handlerService);
//            MarsServiceProxy.setOnPushMessageListener(BaseConstants.SDTRESULT_CMDID, handlerService);
//
//            this.callbackContext.success();
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
