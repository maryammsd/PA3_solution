package hk.ust.comp3021.resource;

import hk.ust.comp3021.person.User;
import java.util.*;

public class Comment {
    public enum CommentType {
        COMMENT_OF_COMMENT,
        COMMENT_OF_PAPER;
    }

    private final String commentID;

    private Date creationTime;

    private String content;

    private User creator;

    private CommentType type;

    private String commentObjId;

    private final ArrayList<Comment> attachedComments = new ArrayList<>();

    public Comment(String commentID, Date creationTime, String content, User creator, CommentType type, String commentObjId) {
        this.commentID = commentID;
        this.creationTime = creationTime;
        this.content = content;
        this.creator = creator;
        this.type = type;
        this.commentObjId = commentObjId;
    }

    public String getCommentID() {
        return commentID;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(String creatorID) {
        this.creator = creator;
    }

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public String getCommentObjId() {
        return commentObjId;
    }

    public void setCommentObjId(String commentObjId) {
        this.commentObjId = commentObjId;
    }

    public ArrayList<Comment> getAttachedComments() {
        return attachedComments;
    }

    public void appendComment(Comment attachedComment) {
        this.attachedComments.add(attachedComment);
    }

    public boolean removeComment(Comment comment) {
        return this.attachedComments.remove(comment);
    }
}
