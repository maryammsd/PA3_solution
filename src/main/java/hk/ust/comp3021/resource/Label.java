package hk.ust.comp3021.resource;

import hk.ust.comp3021.person.User;
import java.util.*;

public class Label {

    private final String labelID;

    private String paperID;

    private Date creationTime;

    private String content;

    private User creator;

    public Label(String labelID, String paperID, Date creationTime, String content, User creator) {
        this.labelID = labelID;
        this.paperID = paperID;
        this.creationTime = creationTime;
        this.content = content;
        this.creator = creator;
    }

    public String getLabelID() {
        return labelID;
    }

    public String getPaperID() {
        return paperID;
    }

    public void setPaperID(String paperID) {
        this.paperID = paperID;
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

    public void setCreator(User creator) {
        this.creator = creator;
    }

}
