package hk.ust.comp3021.actions;

import hk.ust.comp3021.action.Action;
import hk.ust.comp3021.action.QueryAction;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.utils.Query;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.MiniMendeleyEngine;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentQueryTest {

	@Tag(TestKind.PUBLIC)
	@Test
	void QueryProcessing_ActionSize() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		int original = engine.getActions().size();
		QueryAction action = new QueryAction("Action_1", user, new Date(), Action.ActionType.PROCESS_QUERY);
		action.setFilePath("resources/query.txt");
		engine.processConcurrentQuery(user, action);

		int current = engine.getActions().size();

		assertEquals(original + 1, current);
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void QueryProcessing_CheckResult() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		QueryAction action = new QueryAction("Action_1", user, new Date(), Action.ActionType.PROCESS_QUERY);
		action.setFilePath("resources/query.txt");
		engine.processConcurrentQuery(user, action);

		int completed = 0;
		for (Query query : action.getQueries())
			if (query.isCompleted())
				completed++;

		assertEquals(completed, 18);
	}

	@Tag(TestKind.HIDDEN)
	@Test
	void QueryProcessing_Performance() throws InterruptedException, IOException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		long start = System.nanoTime();
		QueryAction action = new QueryAction("Action_1", user, new Date(), Action.ActionType.PROCESS_QUERY);
		action.setFilePath("resources/query.txt");
		engine.processConcurrentQuery(user, action);
		long finish = System.nanoTime();
		long paralleltimeElapsed = finish - start;

		start = System.nanoTime();
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new FileReader(action.getFilePath()));

		Query query = null;
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (line != null) {
			query = new Query(line);
			if (query.getValidity()) {
				System.out.println("**** Processing Query " + query.getQuery());
				switch (query.getType()) {
				case ADD:
					break;
				case REMOVE:
					switch (query.getObject()) {
					case PAPER:
						for (String paper : engine.getPaperBase().keySet()) {
							if (paper.equals(query.getValue())) {
								engine.getPaperBase().remove(paper);
								query.setCompleted(true);
							}
						}
						break;
					case AUTHOR:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getAuthors().contains(query.getValue())) {
								engine.getPaperBase().get(paper).getAuthors().remove(query.getValue());
								query.setCompleted(true);
							}
						}
						break;
					case JOURNAL:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getJournal() != null) {
								if (engine.getPaperBase().get(paper).getJournal().contains(query.getValue())) {
									engine.getPaperBase().get(paper).setJournal("");
									query.setCompleted(true);
								}
							}
						}
						break;
					case YEAR:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getYear() == Integer.valueOf(query.getValue())) {
								engine.getPaperBase().get(paper).setYear(0);
								query.setCompleted(true);
							}
						}
						break;
					case KEYWORDS:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getKeywords().size() > 0) {
								if (engine.getPaperBase().get(paper).getKeywords().contains(query.getCondition())) {
									engine.getPaperBase().get(paper).getKeywords().remove(query.getValue());
									query.setCompleted(true);
								}
							}
						}
						break;
					case TITLE:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getKeywords().size() > 0) {
								if (engine.getPaperBase().get(paper).getTitle().equals(query.getValue())) {
									engine.getPaperBase().get(paper).setTitle("");
									query.setCompleted(true);
								}
							}
						}
						break;
					default:
						break;
					}
					break;
				case UPDATE:
					switch (query.getObject()) {
					case AUTHOR:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getAuthors().contains(query.getCondition())) {
								engine.getPaperBase().get(paper).getAuthors().remove(query.getCondition());
								engine.getPaperBase().get(paper).getAuthors().add(query.getValue());
								query.setCompleted(true);
							}
						}
						break;
					case JOURNAL:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getJournal() != null) {
								if (engine.getPaperBase().get(paper).getJournal().contains(query.getCondition())) {
									engine.getPaperBase().get(paper).setJournal(query.getValue());
									query.setCompleted(true);
								}
							}
						}
						break;
					case YEAR:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getYear() == Integer.valueOf(query.getCondition())) {
								engine.getPaperBase().get(paper).setYear(Integer.valueOf(query.getValue()));
								query.setCompleted(true);
							}
						}
						break;
					case KEYWORDS:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getKeywords().size() > 0) {
								if (engine.getPaperBase().get(paper).getKeywords().contains(query.getCondition())) {
									int index = engine.getPaperBase().get(paper).getKeywords()
											.indexOf(query.getCondition());
									engine.getPaperBase().get(paper).getKeywords().set(index, query.getValue());
									query.setCompleted(true);
								}
							}
						}
						break;
					case TITLE:
						for (String paper : engine.getPaperBase().keySet()) {
							if (engine.getPaperBase().get(paper).getKeywords().size() > 0) {
								if (engine.getPaperBase().get(paper).getTitle().equals(query.getCondition())) {
									engine.getPaperBase().get(paper).setTitle(query.getValue());
									query.setCompleted(true);
								}
							}
						}
						break;
					default:
						break;
					}
					break;
				default:
					break;

				}
			} else {
				// query is not valid, let's go to the next line
				System.out.println(" -- Query is not valid: " + line);
			}
			line = reader.readLine();
		}
		reader.close();
		finish = System.nanoTime();

		long seqtimeElapsed = finish - start;
		System.out.println("Concurrent sequential time " + seqtimeElapsed);
		System.out.println("Concurrent parallel time " + paralleltimeElapsed);

		assertTrue((seqtimeElapsed * 2.5) >= paralleltimeElapsed);
	}

}
