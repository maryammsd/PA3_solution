package hk.ust.comp3021.utils;

import hk.ust.comp3021.resource.Paper;
import java.util.*;
import java.io.*;

public class BibParser {
	private final String bibfilePath;
	private boolean isErr;
	private final HashMap<String, Paper> result;

	public BibParser(String bibfilePath) {
		this.bibfilePath = bibfilePath;
		this.isErr = false;
		this.result = new HashMap<>();
	}

	public String getBibfilePath() {
		return bibfilePath;
	}

	public boolean isErr() {
		return isErr;
	}

	public HashMap<String, Paper> getResult() {
		return result;
	}

	public void parse() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(bibfilePath));
			Paper currentPaper = null;
			String paperID = null;
			String line = reader.readLine();

			while (line != null) {
				if (line.startsWith("@")) {
					paperID = processPaperIDLine(line);
					currentPaper = new Paper(paperID);
				} else if (line.startsWith("}")) {
					result.put(paperID, currentPaper);
					currentPaper = null;
					paperID = null;
				} else {
					assert (currentPaper != null);
					line = line.trim();
					if (line.startsWith("author")) {
						currentPaper.setAuthors(new ArrayList<>(Arrays.asList(processAuthorLine(line))));
					} else if (line.startsWith("keywords")) {
						currentPaper.setKeywords(new ArrayList<>(Arrays.asList(processKeywordLine(line))));
					} else {
						String s = processOtherLine(line);
						if (line.startsWith("title")) {
							currentPaper.setTitle(s);
						} else if (line.startsWith("doi")) {
							currentPaper.setDoi(s);
						} else if (line.startsWith("journal")) {
							currentPaper.setJournal(s);
						} else if (line.startsWith("year")) {
							currentPaper.setYear(Integer.parseInt(s));
						} else if (line.startsWith("url")) {
							currentPaper.setUrl(s);
						} else if (line.startsWith("abstract")) {
							currentPaper.setAbsContent(s);
						}
					}
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			isErr = true;
		}
	}

	public String processPaperIDLine(String line) {
		assert (line.contains("@"));
		int index1 = line.indexOf("{");
		int index2 = line.indexOf(",");
		assert (index1 != -1 && index2 != -1);
		return line.substring(index1 + 1, index2);
	}

	public String[] processAuthorLine(String line) {
		assert (line.contains("author = "));
		int index1 = line.indexOf("{");
		int index2 = line.indexOf("}");
		assert (index1 != -1 && index2 != -1);
		String authorStr = line.substring(index1 + 1, index2);
		return authorStr.split(" and ");
	}

	public String[] processKeywordLine(String line) {
		assert (line.contains("keywords"));
		int index1 = line.indexOf("{");
		int index2 = line.indexOf("}");
		assert (index1 != -1 && index2 != -1);
		String keywordsStr = line.substring(index1 + 1, index2);
		return keywordsStr.split(",");
	}

	public String processOtherLine(String line) {
		int index1 = line.indexOf("{");
		int index2 = line.indexOf("}");
		assert (index1 != -1 && index2 != -1);
		return line.substring(index1 + 1, index2);
	}

	public Paper parseLineToPaper(String line, String id) {
		Paper paper;
		String[] component = line.split(",");
		if (id != null) {
			paper = new Paper(id);
		} else {
			paper = new Paper(component[0]);
		}
		for (int i = 0; i < component.length; i++) {
			String content = component[i];
			if (content.contains("author = ")) {
				paper.setAuthors(new ArrayList<>(Arrays.asList(processAuthorLine(content))));
			} else if (content.contains("journal = ")) {
				paper.setJournal(processOtherLine(content));
			} else if (content.contains("year = ")) {
				paper.setYear(Integer.parseInt(processOtherLine(content)));
			} else if (content.contains("keywords = ")) {
				paper.setKeywords(new ArrayList<>(Arrays.asList(processKeywordLine(content))));
			} else if (content.contains("title = ")) {
				paper.setTitle(processOtherLine(content));
			}
		}
		return paper;
	}
}
