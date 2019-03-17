package com.feixiang.Common;

/**
 * @Author: lidaofei
 * @Date: 2019/3/17 20:58
 */
public class View {
    private String url;
    private String dispatchAction;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
