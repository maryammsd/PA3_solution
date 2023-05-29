package hk.ust.comp3021.actions;

import hk.ust.comp3021.action.SearchMultipleKeywordsAction;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.MiniMendeleyEngine;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class SearchMultipleKeywordsTest {

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_ActionsSize() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		int originalSize = engine.getActions().size();
		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		int currentSize = engine.getActions().size();
		assertEquals(currentSize, originalSize + 1);
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_IsFound() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertTrue(searchMultipleKeywordsAction.isFound());
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_CheckResults() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		ArrayList<Paper> foundResults = searchMultipleKeywordsAction.getResults();
		// assertEquals(uploadedPaperIDs.size(), 3);

	}

	@Tag(TestKind.HIDDEN)
	@Test
	void testSearchMultipleKeywords_CheckResults1() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		ArrayList<Paper> foundResults = searchMultipleKeywordsAction.getResults();
		// assertEquals(uploadedPaperIDs.size(), 3);

	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_CheckResults2() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		ArrayList<Paper> foundResults = searchMultipleKeywordsAction.getResults();
		// assertEquals(uploadedPaperIDs.size(), 3);

	}

	@Tag(TestKind.HIDDEN)
	@Test
	void testSearchMultipleKeywords_CheckResultsCounter() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		// assertEquals(searchMultipleKeywordsAction.getFoundResult(), 3);

	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_CheckNumberOfWords() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertFalse(searchMultipleKeywordsAction.getWords().size() > 10);
	}

	@Tag(TestKind.HIDDEN)
	@Test
	void testSearchMultipleKeywords_CheckNumberOfWords1() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertTrue(searchMultipleKeywordsAction.getWords().size() < 10);
		assertTrue(searchMultipleKeywordsAction.isFound());
	}

	@Tag(TestKind.HIDDEN)
	@Test
	void testSearchMultipleKeywords_CheckNumberOfWords2() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");
		words.add("constraint");
		words.add("security");
		words.add("blockchain");
		words.add("components");
		words.add("compiler");
		words.add("static");
		words.add("fuzzing");
		words.add("dynamic");
		words.add("graph");
		words.add("book");
		words.add("cat");
		words.add("kebab");
		words.add("paper");
		words.add("journal");
		words.add("sand");
		words.add("snake");
		words.add("bab");
		words.add("par");
		words.add("jourl");
		words.add("and");
		words.add("tom");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertTrue(searchMultipleKeywordsAction.getWords().size() > 20);
	}

	@Tag(TestKind.HIDDEN)
	@Test
	void testSearchMultipleKeywords_CheckPerformance() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");
		words.add("constraint");
		words.add("security");
		words.add("blockchain");
		words.add("components");
		words.add("compiler");
		words.add("static");
		words.add("fuzzing");
		words.add("dynamic");
		words.add("graph");
		words.add("hybrid");
		words.add("neural");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);

		long start = System.nanoTime();
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);
		long finish = System.nanoTime();
		long paralleltimeElapsed = finish - start;

		start = System.nanoTime();
		Thread t =  new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<Paper> results = new ArrayList<>();
				for (String paperId : engine.getPaperBase().keySet()) {
					int count = 0;
					boolean found = false;
					for (String word : words) {
						if (engine.getPaperBase().get(paperId).getTitle() != null) {
							if (engine.getPaperBase().get(paperId).getTitle().contains(word)) {
								found = true;
							}
						}
						if (engine.getPaperBase().get(paperId).getAbsContent() != null) {
							if (engine.getPaperBase().get(paperId).getAbsContent().contains(word)) {
								found = true;
							}
						}
						if (found) {
							count++;
						}
					}
					if (count == words.size())
						results.add(engine.getPaperBase().get(paperId));

				}
				// print the results out
				if (searchMultipleKeywordsAction.getFoundResult() > 0) {
					searchMultipleKeywordsAction.setFound(true);
					for (Paper paper : searchMultipleKeywordsAction.getResults())
						System.out.println(" Paper matches: " + paper.getTitle());
				} else {
					searchMultipleKeywordsAction.setFound(false);
					System.out.println("Fail: No result is found");
				}
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish = System.nanoTime();

		long seqtimeElapsed = finish - start;

		System.out.println("Search sequential time " + seqtimeElapsed);
		System.out.println("Search parallel time " + paralleltimeElapsed);
		assertTrue(paralleltimeElapsed <= seqtimeElapsed, "Performance is achieved with Multithreading");
	}

	@Tag(TestKind.HIDDEN)
	@Test
	void testSearchMultipleKeywords_CheckPerformance1() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");
		words.add("constraint");
		words.add("security");
		words.add("blockchain");
		words.add("components");
		words.add("compiler");
		words.add("static");
		words.add("fuzzing");
		words.add("dynamic");
		words.add("graph");
		words.add("hybrid");
		words.add("neural");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);

		long start = System.nanoTime();
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);
		long finish = System.nanoTime();
		long paralleltimeElapsed = finish - start;

		start = System.nanoTime();
		Thread[] threads = new Thread[SearchMultipleKeywordsAction.getNumThreads()];
		Lock searchLock = new ReentrantLock();
		Semaphore searchSemaphore = new Semaphore(SearchMultipleKeywordsAction.getNumThreads());

		for (int i = 0; i < SearchMultipleKeywordsAction.getNumThreads(); i++) {
			final int index = i;

			threads[index] = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {
						searchSemaphore.acquire();
						for (int j = 0; j < engine.getPaperBase().size(); j++) {

							int counter = 0;
							if (j < engine.getPaperBase().keySet().size()) {
								Object paperId = engine.getPaperBase().keySet().toArray()[j];
								if (paperId != null) {
									for (String word : searchMultipleKeywordsAction.getWords()) {
										boolean isFound = false;
										if (engine.getPaperBase().get(paperId).getTitle() != null) {
											if (engine.getPaperBase().get(paperId).getTitle().contains(word))
												isFound = true;
										}
										if (engine.getPaperBase().get(paperId).getAbsContent() != null) {
											if (engine.getPaperBase().get(paperId).getAbsContent().contains(word))
												isFound = true;
										}
										if (isFound)
											counter++;

									}
									if (counter == searchMultipleKeywordsAction.getWords().size()) {
										searchLock.lock();
										searchMultipleKeywordsAction.increaseFound();
										try {
											searchMultipleKeywordsAction
													.addFoundResult(engine.getPaperBase().get(paperId));
										} finally {
											searchLock.unlock();
										}
									}
								}
							}
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						searchSemaphore.release();
					}
				}
			});
			threads[i].start();
		}

		for (Thread thread : threads) {
			thread.join();
		}
		// print the results out
		if (searchMultipleKeywordsAction.getFoundResult() > 0) {
			searchMultipleKeywordsAction.setFound(true);
			for (Paper paper : searchMultipleKeywordsAction.getResults())
				System.out.println(" Paper matches: " + paper.getTitle());
		} else {
			searchMultipleKeywordsAction.setFound(false);
			System.out.println("Fail: No result is found");
		}

		finish = System.nanoTime();
		long seqtimeElapsed = finish - start;

		System.out.println("Search sequential time " + seqtimeElapsed);
		System.out.println("Search parallel time " + paralleltimeElapsed);
		assertTrue(paralleltimeElapsed <= 2*seqtimeElapsed, "Performance is achieved with Multithreading");
	}
}
