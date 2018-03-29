/*global cordova, module*/

module.exports = {
    initPlatform: function(data, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "initPlatform", [data]);
    },

    setForeground: function(data, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "setForeground", [data]);
    },

    onMessageReceive: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "registerMessageHandler", []);
    },

    sendTextMessage: function(data, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "sendTextMessage", [data]);
    },

    getConversationList: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "getConversationList", []);
    }
};
