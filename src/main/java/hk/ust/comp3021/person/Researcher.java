

package hk.ust.comp3021.person;

import hk.ust.comp3021.resource.Paper;
import java.util.*;

public class Researcher extends Person {

    private final ArrayList<Paper> papers;

    public Researcher(String id, String pName) {
        super(id, pName);
        papers = new ArrayList<>();

    }

    public void appendNewPaper(Paper paper) {
        papers.add(paper);
    }

    public Paper searchPaperByID(String id) {
        for (Paper p : papers) {
            if (p.getPaperID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Paper> getPapers() {
        return papers;
    }
}
