package com.feixiang.Common;

public class View {
    private String url;
    private String dispatchAction;

    public View(){
    }

    public View(String url, String dispatchAction) {
        this.url = url;
        this.dispatchAction = dispatchAction;
    }

    public String getDispatchAction() {
        return dispatchAction;
    }

    public void setDispatchAction(String dispatchAction) {
        this.dispatchAction = dispatchAction;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
