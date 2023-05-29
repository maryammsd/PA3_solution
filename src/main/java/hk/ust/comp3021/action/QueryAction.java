package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.Date;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.utils.Query;

public class QueryAction extends Action{
    private ArrayList<Query> queries = new ArrayList<>();
    private String filePath = "";
    private boolean completed = false;
    private int numOfProcessed = 0;

    /**
     * TODO
     * Implement suitable code in the constructor to achieve the goal of performing multiple queries in parallel.
     * You can add more functions to this class.
     * @param id
     * @param user
     * @param time
     * @param actionType
     */
    public QueryAction(String id, User user, Date time, ActionType actionType) {
        super(id, user, time, actionType);
        // TODO Implement appropriate code for processing the queries
    }

    public void setQueries(ArrayList<Query> queries) {
        this.queries = queries;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public ArrayList<Query> getQueries() {
        return queries;
    }

    public String getFilePath() {
        return filePath;
    }

    public void addQuery(Query query) {
        queries.add(query);
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getNumOfProcessed() {
        return numOfProcessed;
    }

    public void setNumOfProcessed(int numOfProcessed) {
        this.numOfProcessed = numOfProcessed;
    }

    public void increaseNumOfProcessed() {
        numOfProcessed++;
    }
}
