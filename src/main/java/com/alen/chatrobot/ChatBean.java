package com.alen.chatrobot;

/**
 * Created by Alen on 2016/1/12.
 */
public class ChatBean {

    private String content;
    private boolean isAsk;
    private int imageId;

    public ChatBean(String content, boolean isAsk, int imageId) {
        this.content = content;
        this.isAsk = isAsk;
        this.imageId = imageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAsk() {
        return isAsk;
    }

    public void setIsAsk(boolean isAsk) {
        this.isAsk = isAsk;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
