package hk.ust.comp3021.resource;

import java.util.*;

public class Paper {
    private final String paperID;

    private String doi = null;

    private ArrayList<String> authors = new ArrayList<>();

    private String title = null;

    private String journal = null;

    private ArrayList<String> keywords = new ArrayList<>();

    private int year = 1900;

    private String url = null;

    private String absContent = null;

    private final ArrayList<Comment> comments = new ArrayList<>();

    private final ArrayList<Label> labels = new ArrayList<>();

    public Paper(String paperID) {
        this.paperID = paperID;
    }

    public String getPaperID() {
        return this.paperID;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAbsContent() {
        return absContent;
    }

    public void setAbsContent(String absContent) {
        this.absContent = absContent;
    }

    public ArrayList<Comment> getComments() {
        return this.comments;
    }

    public void appendComment(Comment comment) {
        this.comments.add(comment);
    }

    public boolean removeComment(Comment comment) {
        return this.comments.remove(comment);
    }

    public ArrayList<Label> getLabels() {
        return this.labels;
    }

    public void appendLabelContent(Label label) {
        this.labels.add(label);
    }

    public String toString() {
        String res = "@article{" + this.paperID + ",\n";
        if (this.absContent != null) {
            res += "   abstract = {" + this.absContent + "},\n";
        }
        if (this.authors.size() > 0) {
            res += "   author = {" + authors.get(0);
            for (int i = 1; i < authors.size(); i++) {
                res += " and ";
                res += authors.get(i);
            }
            res += "},\n";
        }
        if (this.doi != null) {
            res += "   doi = {" + this.doi + "},\n";
        }
        if (this.journal != null) {
            res += "   journal = {" + this.journal + "},\n";
        }
        if (this.keywords.size() > 0) {
            res += "   keywords = {" + keywords.get(0);
            for (int i = 1; i < keywords.size(); i++) {
                res += ",";
                res += keywords.get(i);
            }
            res += "},\n";
        }
        if (this.title != null) {
            res += "   title = {" + title + "},\n";
        }
        res += "   year = {" + year + "},\n";
        if (this.url != null) {
            res += "   url = {" + url + "},\n";
        }
        res += "}\n";

        return res;
    }
}
