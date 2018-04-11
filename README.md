JS/TS内使用说明：
1. 初始化系统（注意this.platform.ready()后使用）
marsChat.initPlatform({
          'userName': 'Marven',
          'host': '10.204.13.145'
}, function () {
          console.log("Init Success ! ==> ");
});

2. Send Text （注意Mars系统初始化后使用）
 marsChat.sendTextMessage({
     'to': 'ghsd',
     'text': 'Hello Marven!',
     'topic': '0'
 }, function () {
     console.log("sendTextMessage Success ! ==> ");
 }, function () {
     console.log("sendTextMessage Failed ! ==> ");
 });

3.Get Conversation List
  marsChat.getConversationList(function (data) {
    console.log(" ==> getConversationList Success !");
    for(var i = 0; i < data.length; i++) {
      console.log("Entity " + " Name:" + data[i].name
        + " topic:" + data[i].topic + " notice:" + data[i].notice);
    }
  }, function () {
     console.log(" ==> getConversationList Failed !");
  });


4. Receive Message

  public msgList = [];

  public successCallback = (data: any) => {

    console.log("onMessageReceive Success ! ==> Content:" + data.msgcontent
      + " Topic:" + data.msgtopic + " From:" + data.msgfrom);

    if (data.msgcontent == undefined) {
      return;
    }

    this.msgList = this.msgList.concat([{
      "msgfrom": data.msgfrom,
      "msgtopic": data.msgtopic,
      "msgcontent": data.msgcontent
    }]);

    this.cd.detectChanges();

  };
  
  ...
  
  marsChat.onMessageReceive(this.successCallback);