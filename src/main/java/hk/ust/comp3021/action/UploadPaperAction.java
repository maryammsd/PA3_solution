package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.Date;
import java.util.HashMap;

public class UploadPaperAction extends Action {
    private String bibfilePath;

    private HashMap<String, Paper> uploadedPapers = new HashMap<>();
    private boolean actionResult = false;

    public UploadPaperAction(String id, User user, Date time, String bibfilePath) {
        super(id, user, time, ActionType.UPLOAD_PAPER);
        this.bibfilePath = bibfilePath;
    }

    public String getBibfilePath() {
        return bibfilePath;
    }

    public void setBibfilePath(String bibfilePath) {
        this.bibfilePath = bibfilePath;
    }

    public HashMap<String, Paper> getUploadedPapers() {
        return uploadedPapers;
    }

    public void setUploadedPapers(HashMap<String, Paper> uploadedPapers) {
        this.uploadedPapers = uploadedPapers;
    }
    public boolean getActionResult() {
        return actionResult;
    }

    public void setActionResult(boolean actionResult) {
        this.actionResult = actionResult;
    }
}
