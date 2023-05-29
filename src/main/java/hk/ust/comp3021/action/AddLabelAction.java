package hk.ust.comp3021.action;

import hk.ust.comp3021.person.User;
import java.util.Date;

public class AddLabelAction extends Action {
    private String labelStr;

    private String paperID;

    private boolean actionResult;


    public AddLabelAction(String id, User user, Date time, String labelStr, String paperID) {
        super(id, user, time, ActionType.ADD_LABEL);
        this.labelStr = labelStr;
        this.paperID = paperID;
        this.actionResult = false;
    }

    public String getLabelStr() {
        return labelStr;
    }

    public void setLabelStr(String labelStr) {
        this.labelStr = labelStr;
    }

    public String getPaperID() {
        return paperID;
    }

    public void setPaperID(String paperID) {
        this.paperID = paperID;
    }

    public boolean getActionResult() {
        return actionResult;
    }

    public void setActionResult(boolean actionResult) {
        this.actionResult = actionResult;
    }
}
