package hk.ust.comp3021.action;

import hk.ust.comp3021.person.User;
import java.util.*;

public class Action {

    public enum ActionType {
        ADD_COMMENT,
        ADD_LABEL,
        DOWNLOAD_PAPER,
        UPLOAD_PAPER,
        SEARCH_PAPER,
        SORT_PAPER,
        STATISTICAL_INFO,
        UPLOAD_PARALLEL,
        SEARCH_SMART,
        UPDATE_LABELS,
        DELETE_LABELS,
        PROCESS_QUERY,
    };

    private String id;

    private Date time;

    private User user;

    private ActionType actionType;

    public Action(String id, User user, Date time, ActionType actionType) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.actionType = actionType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}
