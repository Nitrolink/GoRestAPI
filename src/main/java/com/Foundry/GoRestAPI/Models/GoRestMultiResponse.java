package com.Foundry.GoRestAPI.Models;

public class GoRestMultiResponse {
    private GoRestMeta meta;
    private GoRestUser[] data;

    public GoRestMeta getMeta() {
        return meta;
    }

    public void setMeta(GoRestMeta meta) {
        this.meta = meta;
    }

    public GoRestUser[] getData() {
        return data;
    }

    public void setData(GoRestUser[] data) {
        this.data = data;
    }
}

