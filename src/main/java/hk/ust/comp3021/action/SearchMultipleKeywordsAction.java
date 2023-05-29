package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.Date;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.resource.Paper;

public class SearchMultipleKeywordsAction extends Action {

	public static int numThreads = 5;
	private ArrayList<String> words = new ArrayList<>();
	private ArrayList<Paper> results = new ArrayList<>();
	private int foundResult = 0;
	private boolean isFound = false;

	public SearchMultipleKeywordsAction(String id, User user, Date time) {
		super(id, user, time, ActionType.SEARCH_SMART);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Paper> getResults() {
		return results;
	}

	public ArrayList<String> getWords() {
		return words;
	}

	public boolean isFound() {
		return isFound;
	}

	public void setWords(ArrayList<String> words) {
		for (String word : words)
			if (!this.words.contains(word))
				this.words.add(word);
	}

	public void setResults(ArrayList<Paper> results) {
		this.results = results;
	}

	public void setFoundResult(int foundResult) {
		this.foundResult = foundResult;
	}

	public void addFoundResult(Paper paper) {
		if (!results.contains(paper)) {
			this.results.add(paper);
		}
	}

	public void increaseFound() {
		foundResult++;
	}

	public void setFound(boolean isFound) {
		this.isFound = isFound;
	}

	public static int getNumThreads() {
		return numThreads;
	}

	public int getFoundResult() {
		return foundResult;
	}
}
