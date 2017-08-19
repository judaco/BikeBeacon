package com.bikebeacon.pojo;

/**
 * Created by Alon on 8/18/2017.
 */

public abstract class Action {

    private String mAction;

    public Action(String action) {
        mAction = action;
    }

    public String getAction() {
        return mAction;
    }

    public abstract String toJSON();

}
