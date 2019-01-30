
package com.gupao.edu.vip.lion.api.push;

import com.gupao.edu.vip.lion.api.router.ClientLocation;

import java.util.Arrays;


public class PushResult {
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_FAILURE = 2;
    public static final int CODE_OFFLINE = 3;
    public static final int CODE_TIMEOUT = 4;
    public int resultCode;
    public String userId;
    public Object[] timeLine;
    public ClientLocation location;

    public PushResult(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public PushResult setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public PushResult setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Object[] getTimeLine() {
        return timeLine;
    }

    public PushResult setTimeLine(Object[] timeLine) {
        this.timeLine = timeLine;
        return this;
    }

    public ClientLocation getLocation() {
        return location;
    }

    public PushResult setLocation(ClientLocation location) {
        this.location = location;
        return this;
    }

    public String getResultDesc() {
        switch (resultCode) {
            case CODE_SUCCESS:
                return "success";
            case CODE_FAILURE:
                return "failure";
            case CODE_OFFLINE:
                return "offline";
            case CODE_TIMEOUT:
                return "timeout";
        }
        return Integer.toString(CODE_TIMEOUT);
    }

    @Override
    public String toString() {
        return "PushResult{" +
                "resultCode=" + getResultDesc() +
                ", userId='" + userId + '\'' +
                ", timeLine=" + Arrays.toString(timeLine) +
                ", " + location +
                '}';
    }
}
