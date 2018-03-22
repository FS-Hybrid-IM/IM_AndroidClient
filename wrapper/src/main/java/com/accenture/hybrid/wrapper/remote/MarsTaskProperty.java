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

package com.accenture.hybrid.wrapper.remote;

import android.os.Bundle;

/**
 * Constants for Mars Task properties
 * <p></p>
 * Created by zhaoyuan on 16/2/29.
 */
public class MarsTaskProperty {

    public static final String OPTIONS_HOST = "host";
    public static final String OPTIONS_CGI_PATH = "cgi_path";
    public static final String OPTIONS_CMD_ID = "cmd_id";
    public static final String OPTIONS_CHANNEL_SHORT_SUPPORT = "short_support";
    public static final String OPTIONS_CHANNEL_LONG_SUPPORT = "long_support";
    public static final String OPTIONS_TASK_ID = "task_id";

    private String host;
    private String cgiPach;
    private int cmdId;
    private Boolean shortSupport = true;
    private Boolean longSupport = false;
    private int taskId;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCgiPach() {
        return cgiPach;
    }

    public void setCgiPach(String cgiPach) {
        this.cgiPach = cgiPach;
    }

    public int getCmdId() {
        return cmdId;
    }

    public void setCmdId(int cmdId) {
        this.cmdId = cmdId;
    }

    public Boolean getShortSupport() {
        return shortSupport;
    }

    public void setShortSupport(Boolean shortSupport) {
        this.shortSupport = shortSupport;
    }

    public Boolean getLongSupport() {
        return longSupport;
    }

    public void setLongSupport(Boolean longSupport) {
        this.longSupport = longSupport;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public MarsTaskProperty() {

    }

    public MarsTaskProperty(String host, String cgiPach, int cmdId, Boolean shortSupport,
                             Boolean longSupport, int taskId) {
        this.host = host;
        this.cgiPach = cgiPach;
        this.cmdId = cmdId;
        this.shortSupport = shortSupport;
        this.longSupport = longSupport;
        this.taskId = taskId;

    }

    public static Bundle getPropertyBundle(MarsTaskProperty property) {

        Bundle properties = new Bundle();
        properties.putString(MarsTaskProperty.OPTIONS_HOST,
                ("".equals(property.getHost()) ? null : property.getHost()));
        properties.putString(MarsTaskProperty.OPTIONS_CGI_PATH, property.getCgiPach());
        properties.putBoolean(MarsTaskProperty.OPTIONS_CHANNEL_SHORT_SUPPORT, property.getShortSupport());
        properties.putBoolean(MarsTaskProperty.OPTIONS_CHANNEL_LONG_SUPPORT, property.getLongSupport());
        properties.putInt(MarsTaskProperty.OPTIONS_CMD_ID, property.getCmdId());

        return properties;

    }





}
