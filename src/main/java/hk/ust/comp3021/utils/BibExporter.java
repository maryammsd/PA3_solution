package hk.ust.comp3021.utils;

import hk.ust.comp3021.resource.Paper;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class BibExporter {
    private final HashMap<String, Paper> papers;

    private String savePath;

    private boolean isErr = false;

    public BibExporter(HashMap<String, Paper> papers, String savePath) {
        this.papers = papers;
        this.savePath = savePath;
    }

    public HashMap<String, Paper> getPapers() {
        return papers;
    }

    public String getSavePath() {
        return savePath;
    }

    public boolean isErr() {
        return isErr;
    }

    public String generate(){
        String s = "";
        for (Map.Entry<String, Paper> p : papers.entrySet()) {
            s += p.getValue().toString();
        }
        return s;
    }

    public void export() {
        assert(savePath != null);
        String result = generate();
        try {
            File f = new File(savePath);
            FileOutputStream fop = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
            System.out.println(result);
            writer.write(result);
            writer.close();
            fop.close();
        } catch (IOException e) {
            isErr = true;
        }
    }
}
