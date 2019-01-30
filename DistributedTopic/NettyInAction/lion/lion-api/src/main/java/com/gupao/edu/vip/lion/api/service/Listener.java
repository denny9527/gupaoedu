package com.gupao.edu.vip.lion.api.service;

public interface Listener {
    void onSuccess(Object... args);

    void onFailure(Throwable cause);
}