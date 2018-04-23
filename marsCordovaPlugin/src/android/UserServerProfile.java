package com.accenture.mars.chat;

/**
 * User Set profile.
 */

public class UserServerProfile {

    private short produceId = 200;
    private String longLinkHost = "localhost";
    private int[] longLinkPorts = new int[]{8081};
    private int shortLinkPort = 8080;
    private String userName = "Mars";
    private String accessToken = "test_token";

    private String deviceId = "12345678901234567890";

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLongLinkHost(String longLinkHost) {
        this.longLinkHost = longLinkHost;
    }

    public void setLongLinkPorts(int longLinkPorts) {
        this.longLinkPorts = new int[]{longLinkPorts};
    }

    public void setShortLinkPort(int shortLinkPort) {
        this.shortLinkPort = shortLinkPort;
    }

    public short getProduceId() {
        return produceId;
    }

    public String getLongLinkHost() {
        return longLinkHost;
    }

    public int[] getLongLinkPorts() {
        return longLinkPorts;
    }

    public int getShortLinkPort() {
        return shortLinkPort;
    }

    public void setProduceId(short produceId) {
        this.produceId = produceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


}
