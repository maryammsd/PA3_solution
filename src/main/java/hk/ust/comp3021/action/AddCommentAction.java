package hk.ust.comp3021.action;

import hk.ust.comp3021.person.User;
import hk.ust.comp3021.resource.Comment.*;

import java.util.Date;

public class AddCommentAction extends Action {
    private String commentStr;
    private CommentType commentType;
    private String objectId;
    private boolean actionResult;

    public AddCommentAction(String id, User user, Date time, String commentStr, CommentType commentType, String objectId) {
        super(id, user, time, ActionType.ADD_COMMENT);
        this.commentStr = commentStr;
        this.commentType = commentType;
        this.objectId = objectId;
        this.actionResult = false;
    }

    public String getCommentStr() {
        return commentStr;
    }

    public void setCommentStr(String commentStr) {
        this.commentStr = commentStr;
    }

    public CommentType getCommentType() {
        return commentType;
    }

    public void setCommentType(CommentType commentType) {
        this.commentType = commentType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean getActionResult() {
        return actionResult;
    }

    public void setActionResult(boolean actionResult) {
        this.actionResult = actionResult;
    }
}
