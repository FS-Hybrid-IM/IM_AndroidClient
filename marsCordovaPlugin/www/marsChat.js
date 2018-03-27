/*global cordova, module*/

module.exports = {
    initPlatform: function(data, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "initPlatform", [JSON.stringify(data)]);
    },

    setForeground: function(data, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "setForeground", [JSON.stringify(data)]);
    },

    registerMessageHandler: function(data, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "registerMessageHandler", [JSON.stringify(data)]);
    },

    sendTextMessage: function(data, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "sendTextMessage", [JSON.stringify(data)]);
    },

    getConversationList: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "marsChat", "getConversationList", []);
    }
};
