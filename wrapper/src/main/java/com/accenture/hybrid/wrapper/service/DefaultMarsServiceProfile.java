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

package com.accenture.hybrid.wrapper.service;

/**
 * Default profile.
 * <p></p>
 * Created by zhaoyuan on 2016/11/16.
 */

public class DefaultMarsServiceProfile implements MarsServiceProfile {

    private short MAGIC = 0x0110;
    private short PRODUCT_ID = 200;
    private String LONG_LINK_HOST = "www.tencent.com";
    private int[] LONG_LINK_PORTS = new int[]{8081};
    private int SHORT_LINK_PORT = 8080;

    @Override
    public short magic() {
        return MAGIC;
    }

    @Override
    public short productID() {
        return PRODUCT_ID;
    }

    @Override
    public String longLinkHost() {
        return LONG_LINK_HOST;
    }

    @Override
    public int[] longLinkPorts() {
        return LONG_LINK_PORTS;
    }

    @Override
    public int shortLinkPort() {
        return SHORT_LINK_PORT;
    }


    public void setMagic(short magic) {
        this.MAGIC = magic;
    }

    public void setProductId(short id) {
        this.PRODUCT_ID = id;
    }

    public void setLongLinkHost(String host) {
        this.LONG_LINK_HOST = host;
    }

    public void setLongLinkPorts(int port) {
        this.LONG_LINK_PORTS = new int[]{port};
    }

    public void setShortLinkPort(int port) {
        this.SHORT_LINK_PORT = port;
    }

}
