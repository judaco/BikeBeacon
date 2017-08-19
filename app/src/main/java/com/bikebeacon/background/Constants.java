package com.bikebeacon.background;

/**
 * Created by Alon on 8/18/2017.
 */

public final class Constants {
    public static final String JSON_ACTION = "action";
    public static final String JSON_OWNER = "owner";
    public static final String JSON_CELLTOWERS = "towers";
    public static final String JSON_GPS = "coords";
    public static final String JSON_PREVIOUS_ALERT = "alert";
    public static final String JSON_IS_CLOSED = "closed";
    public static final String JSON_ID = "id";

    public static final String ALERT_NEW = "newAlert";
    public static final String ALERT_CONVERSATION_RECEIVED = "conversationStarted";
    public static final String ALERT_DELTE = "deleteAlert";
    public static final String ALERT_UPDATE = "updateAlert";

    public enum AlertAction {
        ALERT_NEW("newAlert"),
        ALERT_CONVERSATION_RECEIVED("conversationStarted"),
        ALERT_DELETE("deleteAlert"),
        ALERT_UPDATE("updateAlert");

        private String mAction;

        AlertAction(String action) {
            mAction = action;
        }

        @Override
        public String toString() {
            return mAction;
        }
    }

}
