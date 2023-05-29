package hk.ust.comp3021.action;

import hk.ust.comp3021.person.User;
import java.util.*;

public class DownloadPaperAction extends Action {
    private String downloadPath;

    private final ArrayList<String> papers = new ArrayList<>();

    private boolean actionResult;


    public DownloadPaperAction(String id, User user, Date time, String downloadPath) {
        super(id, user, time, ActionType.DOWNLOAD_PAPER);
        this.downloadPath = downloadPath;
        this.actionResult = false;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public ArrayList<String> getPaper() {
        return papers;
    }

    public void appendPapers(String paperID) {
        this.papers.add(paperID);
    }

    public boolean getActionResult() {
        return actionResult;
    }

    public void setActionResult(boolean actionResult) {
        this.actionResult = actionResult;
    }
}
