package hk.ust.comp3021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import hk.ust.comp3021.action.Action;
import hk.ust.comp3021.action.AddCommentAction;
import hk.ust.comp3021.action.AddLabelAction;
import hk.ust.comp3021.action.DownloadPaperAction;
import hk.ust.comp3021.action.LabelAction;
import hk.ust.comp3021.action.LabelActionList;
import hk.ust.comp3021.action.ParallelImportAction;
import hk.ust.comp3021.action.QueryAction;
import hk.ust.comp3021.action.SearchMultipleKeywordsAction;
import hk.ust.comp3021.action.SearchPaperAction;
import hk.ust.comp3021.action.SearchPaperAction.SearchPaperKind;
import hk.ust.comp3021.action.SearchResearcherAction;
import hk.ust.comp3021.action.SearchResearcherAction.SearchResearcherKind;
import hk.ust.comp3021.action.SortPaperAction;
import hk.ust.comp3021.action.SortPaperAction.SortBase;
import hk.ust.comp3021.action.SortPaperAction.SortKind;
import hk.ust.comp3021.action.StatisticalInformationAction;
import hk.ust.comp3021.action.StatisticalInformationAction.InfoKind;
import hk.ust.comp3021.action.UploadPaperAction;
import hk.ust.comp3021.person.Researcher;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.resource.Comment;
import hk.ust.comp3021.resource.Comment.CommentType;
import hk.ust.comp3021.resource.Label;
import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.utils.BibExporter;
import hk.ust.comp3021.utils.BibParser;
import hk.ust.comp3021.utils.Query;
import hk.ust.comp3021.utils.UserRegister;

public class MiniMendeleyEngine {
	private final String defaultBibFilePath = "resources/bibdata/PAData.bib";
	private final HashMap<String, Paper> paperBase = new HashMap<>();
	private final ArrayList<User> users = new ArrayList<>();
	private final ArrayList<Researcher> researchers = new ArrayList<>();

	private final ArrayList<Comment> comments = new ArrayList<>();

	private final ArrayList<Label> labels = new ArrayList<>();

	private final ArrayList<Action> actions = new ArrayList<>();

	// PA3
	private Semaphore searchSemaphore;
	private Semaphore querySemaphore;
	private Lock importLock;
	private Lock searchLock;
	private Lock queryLock;
	private Lock labelLock = new ReentrantLock();
	private Condition queryCondition;
	protected int activeThreads = 0;

	public MiniMendeleyEngine() {
		populatePaperBaseWithDefaultBibFile();
	}

	public void populatePaperBaseWithDefaultBibFile() {
		User user = new User("User_0", "root_user", new Date());
		users.add(user);
		UploadPaperAction action = new UploadPaperAction("Action_0", user, new Date(), defaultBibFilePath);
		processUploadPaperAction(user, action);
		paperBase.putAll(action.getUploadedPapers());
	}

	public String getDefaultBibFilePath() {
		return defaultBibFilePath;
	}

	public HashMap<String, Paper> getPaperBase() {
		return paperBase;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public ArrayList<Researcher> getResearchers() {
		return researchers;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}

	public ArrayList<Label> getLabels() {
		return labels;
	}

	public ArrayList<Action> getActions() {
		return actions;
	}

	public User processUserRegister(String id, String name, Date date) {
		UserRegister ur = new UserRegister(id, name, date);
		User curUser = ur.register();
		users.add(curUser);
		return curUser;
	}

	public Comment processAddCommentAction(User curUser, AddCommentAction action) {
		actions.add(action);
		if (action.getCommentType() == CommentType.COMMENT_OF_COMMENT) {
			String objCommentID = action.getObjectId();
			for (Comment comment : comments) {
				if (objCommentID.equals(comment.getCommentID())) {
					String commentID = "Comment" + String.valueOf(comments.size() + 1);
					Comment newComment = new Comment(commentID, action.getTime(), action.getCommentStr(),
							action.getUser(), action.getCommentType(), action.getObjectId());
					comments.add(newComment);
					comment.appendComment(newComment);
					curUser.appendNewComment(newComment);
					action.setActionResult(true);
					return newComment;
				}
			}
		} else if (action.getCommentType() == CommentType.COMMENT_OF_PAPER) {
			String objCommentID = action.getObjectId();
			for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
				String paperID = entry.getKey();
				if (paperID.equals(objCommentID)) {
					String commentID = "Comment" + String.valueOf(comments.size() + 1);
					Comment newComment = new Comment(commentID, action.getTime(), action.getCommentStr(),
							action.getUser(), action.getCommentType(), action.getObjectId());
					comments.add(newComment);
					entry.getValue().appendComment(newComment);
					curUser.appendNewComment(newComment);
					action.setActionResult(true);
					return newComment;
				}
			}
		}
		action.setActionResult(false);
		return null;
	}

	public Label processAddLabelAction(User curUser, AddLabelAction action) {
		actions.add(action);
		String paperID = action.getPaperID();
		String labelID = "Label" + String.valueOf(labels.size() + 1);
		Label newLabel = new Label(labelID, action.getPaperID(), action.getTime(), action.getLabelStr(),
				action.getUser());

		if (paperBase.containsKey(paperID)) {
			paperBase.get(paperID).appendLabelContent(newLabel);
			curUser.appendNewLabel(newLabel);
			labels.add(newLabel);
			action.setActionResult(true);
			return newLabel;
		} else {
			action.setActionResult(false);
			return null;
		}
	}

	public void processDownloadPaperAction(User curUser, DownloadPaperAction action) {
		actions.add(action);
		String path = action.getDownloadPath();
		String content = "";
		HashMap<String, Paper> downloadedPapers = new HashMap<>();
		for (String paperID : action.getPaper()) {
			if (paperBase.containsKey(paperID)) {
				downloadedPapers.put(paperID, paperBase.get(paperID));
			} else {
				action.setActionResult(false);
				return;
			}
		}
		BibExporter exporter = new BibExporter(downloadedPapers, path);
		exporter.export();
		action.setActionResult(!exporter.isErr());
	}

	public ArrayList<Paper> processSearchPaperAction(User curUser, SearchPaperAction action) {
		actions.add(action);
		switch (action.getKind()) {
		case ID:
			for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
				if (action.getSearchContent().equals(entry.getKey())) {
					action.appendToActionResult(entry.getValue());
				}
			}
			break;
		case TITLE:
			for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
				if (action.getSearchContent().equals(entry.getValue().getTitle())) {
					action.appendToActionResult(entry.getValue());
				}
			}
			break;
		case AUTHOR:
			for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
				if (entry.getValue().getAuthors().contains(action.getSearchContent())) {
					action.appendToActionResult(entry.getValue());
				}
			}
			break;
		case JOURNAL:
			for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
				if (action.getSearchContent().equals(entry.getValue().getJournal())) {
					action.appendToActionResult(entry.getValue());
				}
			}
			break;
		default:
			break;
		}
		return action.getActionResult();
	}

	/**
	 * TODO: Rewrite the searching part with Lambda expressions using functional
	 * interfaces. You should follow the original logic in
	 * `processSearchPaperAction` to complete four kinds of searching. The things
	 * you need to do are: 1) implement the functional interfaces; 2) fulfill the
	 * logic here. The prototypes for the functional interfaces are in
	 * `SearchPaperAction`.
	 */
	public ArrayList<Paper> processSearchPaperActionByLambda(User curUser, SearchPaperAction action) {
		actions.add(action);
		switch (action.getKind()) {
		case ID:
			paperBase.entrySet().forEach(entry -> {
				if (action.isEqual.test(entry.getKey()))
					action.appendToActionResultByLambda.accept(entry.getValue());
			});
			break;
		case TITLE:
			paperBase.entrySet().forEach(entry -> {
				if (action.isEqual.test(entry.getValue().getTitle()))
					action.appendToActionResultByLambda.accept(entry.getValue());
			});
			break;
		case AUTHOR:
			paperBase.entrySet().forEach(entry -> {
				if (action.isContain.test(entry.getValue().getAuthors()))
					action.appendToActionResultByLambda.accept(entry.getValue());
			});
			break;
		case JOURNAL:
			paperBase.entrySet().forEach(entry -> {
				if (action.isEqual.test(entry.getValue().getJournal()))
					action.appendToActionResultByLambda.accept(entry.getValue());
			});
			break;
		default:
			break;
		}
		return action.getActionResult();
	}

	/**
	 * TODO: Implement the custom comparators for various scenarios. The things you
	 * need to do: 1) implement the functional interfaces; 2) fulfill the logic
	 * here. The prototypes for the functional interfaces are in `SortPaperAction`.
	 * PS: You should operate directly on `actionResult` since we have already put
	 * the papers into it in the original order.
	 */
	public List<Paper> processSortPaperActionByLambda(User curUser, SortPaperAction action) {
		actions.add(action);
		paperBase.entrySet().forEach(entry -> {
			action.appendToActionResultByLambda.accept(entry.getValue());
		});
		switch (action.getBase()) {
		case ID:
			action.comparator = (paper1, paper2) -> stringProcessNullSafe(paper1.getPaperID(), paper2.getPaperID());
			if (action.kindPredicate.test(action.getKind()))
				action.comparator = action.comparator.reversed();
			break;
		case TITLE:
			action.comparator = (paper1, paper2) -> stringProcessNullSafe(paper1.getTitle(), paper2.getTitle());
			if (action.kindPredicate.test(action.getKind()))
				action.comparator = action.comparator.reversed();
			break;
		case AUTHOR:
			action.comparator = (paper1, paper2) -> {
				return stringProcessNullSafe(String.join(",", paper1.getAuthors()),
						String.join(",", paper2.getAuthors()));
			};
			if (action.kindPredicate.test(action.getKind()))
				action.comparator = action.comparator.reversed();
			break;
		case JOURNAL:
			action.comparator = (paper1, paper2) -> stringProcessNullSafe(paper1.getJournal(), paper2.getJournal());
			if (action.kindPredicate.test(action.getKind()))
				action.comparator = action.comparator.reversed();
			break;
		default:
			break;
		}
		action.sortFunc.get();
		return action.getActionResult();
	}

	/**
	 * TODO: Implement the new searching method with Lambda expressions using
	 * functional interfaces. The thing you need to do is to implement the three
	 * functional interfaces, i.e., searchFunc1 / searchFunc2 /searchFunc3. The
	 * prototypes for the functional interfaces are in `SearchResearcherAction`. PS:
	 * You should operate directly on `actionResult` since we have already put the
	 * papers into it.
	 */
	public HashMap<String, List<Paper>> processSearchResearcherActionByLambda(User curUser,
			SearchResearcherAction action) {
		actions.add(action);
		paperBase.entrySet().forEach(entry -> {
			entry.getValue().getAuthors().forEach(author -> action.appendToActionResult(author, entry.getValue()));
		});
		switch (action.getKind()) {
		case PAPER_WITHIN_YEAR:
			action.searchFunc1.get();
			break;
		case JOURNAL_PUBLISH_TIMES:
			action.searchFunc2.get();
			break;
		case KEYWORD_SIMILARITY:
			action.searchFunc3.get();
			break;

		default:
			break;
		}
		return action.getActionResult();
	}

	int stringProcessNullSafe(String str1, String str2) {
		if (str1 == null && str2 == null)
			return 0;
		if (str1 == null)
			return -1;
		if (str2 == null)
			return 1;
		return str1.compareTo(str2);
	}

	/**
	 * TODO: Implement the new profiling method with Lambda expressions using
	 * functional interfaces. The thing you need to do is to implement the two
	 * functional interfaces, i.e., InfoObtainer1 / InfoObtainer2. The prototypes
	 * for the functional interfaces are in `StatisticalInformationAction`.
	 */
	public Map<String, Double> processStatisticalInformationActionByLambda(User curUser,
			StatisticalInformationAction action) {
		actions.add(action);
		List<Paper> paperList = new ArrayList<Paper>();
		paperBase.entrySet().forEach(entry -> paperList.add(entry.getValue()));
		switch (action.getKind()) {
		case AVERAGE:
			action.obtainer1.apply(paperList);
			break;
		case MAXIMAL:
			action.obtainer2.apply(paperList);
			break;
		default:
			break;
		}
		return action.getActionResult();
	}

	public void processUploadPaperAction(User curUser, UploadPaperAction action) {
		actions.add(action);
		BibParser parser = new BibParser(action.getBibfilePath());
		parser.parse();
		action.setUploadedPapers(parser.getResult());
		for (String paperID : action.getUploadedPapers().keySet()) {
			Paper paper = action.getUploadedPapers().get(paperID);
			paperBase.put(paperID, paper);
			for (String researcherName : paper.getAuthors()) {
				Researcher existingResearch = null;
				for (Researcher researcher : researchers) {
					if (researcher.getName().equals(researcherName)) {
						existingResearch = researcher;
						break;
					}
				}
				if (existingResearch == null) {
					Researcher researcher = new Researcher("Researcher_" + researchers.size(), researcherName);
					researcher.appendNewPaper(paper);
					researchers.add(researcher);
				} else {
					existingResearch.appendNewPaper(paper);
				}
			}
		}
		action.setActionResult(!parser.isErr());
	}

	public void processParallelImport(User curUser, ParallelImportAction action) {
		// TODO Auto-generated method stub
		actions.add(action);

		System.out.println("Let's upload the files ");
		List<UploadPaperAction> importActions = new ArrayList<>();
		for (String path : action.getFilePaths()) {
			System.out.println("path is " + path);
			UploadPaperAction importaction = new UploadPaperAction("Action_" + path, curUser, new Date(), path);
			importActions.add(importaction);
		}

		if (importActions.size() <= ParallelImportAction.maxNumberofFiles && importActions.size() > 0) {

			// create a shared lock
			importLock = new ReentrantLock();

			// create a thread pool with a number of threads equal to the number of
			// available processors
			List<Thread> threads = new ArrayList<>();

			// submit tasks for each file to the executor and store the resulting future
			// object in a list
			for (UploadPaperAction uploadPaperAction : importActions) {
				Thread thread = new Thread(() -> {

					processUploadPaperAction(curUser, uploadPaperAction);

					if (uploadPaperAction.getActionResult()) {
						System.out.println("Succeed! The number of uploaded papers are:"
								+ uploadPaperAction.getUploadedPapers().size() + " in thread "
								+ Thread.currentThread().getId());

						importLock.lock();
						try {
							action.addUploadedPapers(uploadPaperAction.getUploadedPapers());
						} finally {
							importLock.unlock();
						}
					} else {
						System.out.println("Fail! You need to specify an existing bib file.");
					}

				});
				threads.add(thread);
				thread.start();

			}

			for (Thread thread : threads) {
				try {
					System.out.println("Thread " + thread.getId() + " finished");
					thread.join();
				} catch (InterruptedException e) {
					// Handle exception
				}
			}
			action.setCompleted(true);
			System.out.println("Total number of papers imported " + action.getImportedPapers().size());

		} else {
			System.out.println("Please enter less than 10 file paths");
			action.setCompleted(false);
		}
	}

	public void processMultiKeywordSearch(User curUser, SearchMultipleKeywordsAction searchMultipleKeywordsAction)
			throws InterruptedException {

		actions.add(searchMultipleKeywordsAction);
		Thread[] threads = new Thread[SearchMultipleKeywordsAction.getNumThreads()];
		searchLock = new ReentrantLock();
		searchSemaphore = new Semaphore(SearchMultipleKeywordsAction.getNumThreads());

		if (paperBase.isEmpty()) {
			System.out.println("Fail: No entries to search within. Please import bib files. ");
		} else {

			for (int i = 0; i < SearchMultipleKeywordsAction.getNumThreads(); i++) {
				final int index = i;

				threads[index] = new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

						try {
							searchSemaphore.acquire();
							int temp = index;
							for (; temp < paperBase.size(); temp += SearchMultipleKeywordsAction.numThreads) {

								int counter = 0;
								if (temp < paperBase.keySet().size()) {
									Object paperId = paperBase.keySet().toArray()[temp];
									if (paperId != null) {
										for (String word : searchMultipleKeywordsAction.getWords()) {
											boolean isFound = false;
											if (paperBase.get(paperId).getTitle() != null) {
												if (paperBase.get(paperId).getTitle().contains(word))
													isFound = true;
											}
											if (paperBase.get(paperId).getAbsContent() != null) {
												if (paperBase.get(paperId).getAbsContent().contains(word))
													isFound = true;
											}
											if (isFound)
												counter++;

										}
										if (counter == searchMultipleKeywordsAction.getWords().size()) {

											searchLock.lock();
											searchMultipleKeywordsAction.increaseFound();
											try {
												searchMultipleKeywordsAction.addFoundResult(paperBase.get(paperId));
											} finally {
												searchLock.unlock();
											}
										} else
											System.out.println("!" + paperBase.get(paperId).getTitle());
									} else
										System.out.println("paper doesn't exist");
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
				System.out.println(searchMultipleKeywordsAction.getFoundResult());
				System.out.print(" papers are found.");
				for (Paper paper : searchMultipleKeywordsAction.getResults())
					System.out.println(" Paper matches: " + paper.getTitle());
			} else {
				searchMultipleKeywordsAction.setFound(false);
				System.out.println("Fail: No result is found");
			}
		}

	}

	public Runnable processAddLabel(User curUser, LabelActionList actionList) {
		// TODO Auto-generated method stub
		Runnable addRunnable = new Runnable() {

			@Override
			public void run() {
				while (!actionList.isFinished()) {
					labelLock.lock();
					if (actionList.getHead() != null) {
						if (actionList.getHead().getActionType().equals(Action.ActionType.ADD_LABEL)) {
							LabelAction action = actionList.dequeue();
							if (action != null) {
								System.out.println(" add a label " + action.getId());
								String labelID = "Label" + String.valueOf(labels.size() + 1);
								String paperId = action.getPaperID();
								Label newLabel = new Label(labelID, paperId, new Date(), action.getLabel(), curUser);
								if (paperBase.containsKey(paperId)) {
									paperBase.get(paperId).appendLabelContent(newLabel);
									curUser.appendNewLabel(newLabel);
									labels.add(newLabel);
									action.setSuccessful(true);
									actionList.setNumOfAdded(actionList.getNumOfAdded() + 1);
									actionList.addProcessedLabel(action.getLabel());
								} else {
									System.out.println("Fail: no such a paper to update its label.");
								}
							}
						}
					}
					labelLock.unlock();
				}
			}
		};

		return addRunnable;

	}

	public Runnable processUpdateLabel(User curUser, LabelActionList actionList) {
		// TODO Auto-generated method stub
		Runnable updateRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int count = 0;
				while (!actionList.isFinished()) {

					labelLock.lock();
					if (actionList.getHead() != null) {
						if (actionList.getHead().getActionType().equals(Action.ActionType.UPDATE_LABELS)) {

							LabelAction labelElement = actionList.dequeue();
							if (labelElement != null) {

								String inputLabel = labelElement.getLabel();
								String newValue = labelElement.getNewLabel();
								ArrayList<String> papers = new ArrayList<>();

								papers.addAll(curUser.updateUserLabels(inputLabel, newValue, labelElement.getTime()));
								if (papers.size() > 0) {
									labelElement.setSuccessful(true);
									actionList.addProcessedLabel(inputLabel);
									System.out.println(
											"Upding the label with content " + inputLabel + " with " + newValue);
								}
								// if no one is updating @paperBase, then update it
								for (String id : paperBase.keySet()) {
									if (paperBase.containsKey(id)) {
										for (int i = 0; i < paperBase.get(id).getLabels().size(); i++) {
											Label paperlabel = paperBase.get(id).getLabels().get(i);
											if (paperlabel.getContent().equals(inputLabel)) {
												paperBase.get(id).getLabels().remove(i);
												if (curUser.getLabel(inputLabel) != null) {
													paperBase.get(id).appendLabelContent(curUser.getLabel(inputLabel));
													count++;
												}
											}
										}
									}
								}
								if (count > 0) {
									actionList.setNumOfUpdated(actionList.getNumOfUpdated() + count);
									System.out.println("actionList # updated" + actionList.getNumOfUpdated());
								}
							}
						}
					}

					labelLock.unlock();
				}

			}
		};
		return updateRunnable;

	}

	public Runnable processDeleteLabel(User curUser, LabelActionList actionList) {
		// TODO Auto-generated method stub
		Runnable deleteRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int count = 0;

				while (!actionList.isFinished()) {

					labelLock.lock();

					if (actionList.getHead() != null) {
						if (actionList.getHead().getActionType().equals(Action.ActionType.DELETE_LABELS)) {
							ArrayList<String> papers = new ArrayList<>();
							LabelAction labelElement = actionList.dequeue();
							if (labelElement != null) {
								System.out.println(" delete a label " + labelElement.getLabel());
								String inputLabel = labelElement.getLabel();
								papers.addAll(curUser.removeUserLabel(inputLabel));
								if (papers.size() > 0) {
									labelElement.setSuccessful(true);
									actionList.addProcessedLabel(inputLabel);
									System.out.println("Deleting the label with content " + inputLabel);

								}

								// if no one is updating @paperBase, then update it
								for (String id : papers) {
									if (paperBase.containsKey(id)) {
										for (int i = 0; i < paperBase.get(id).getLabels().size(); i++) {
											Label paperlabel = paperBase.get(id).getLabels().get(i);
											if (paperlabel.getContent().equals(inputLabel)) {
												paperBase.get(id).getLabels().remove(i);
												count++;
											}
										}
									}
								}
								if (count > 0) {
									actionList.setNumOfDeleted(actionList.getNumOfDeleted() + count);
									System.out.println("actionList # deleted" + actionList.getNumOfDeleted());
								}
							}
						}
					}
					labelLock.unlock();
				}
			}
		};
		return deleteRunnable;

	}

	public void processConcurrentQuery(User curUser, QueryAction action) {
		try {
			actions.add(action);
			BufferedReader reader = new BufferedReader(new FileReader(action.getFilePath()));
			String line = reader.readLine();

			while (line != null) {
				Query query = new Query(line);
				if (query.getValidity()) {
					action.addQuery(query);
				}
				line = reader.readLine();
			}
			reader.close();

		} catch (IOException e) {

		}

		ExecutorService executor = Executors.newFixedThreadPool(8);
		for (int i = 0; i < action.getQueries().size(); i++) {
			Query toBeProcessed = action.getQueries().get(i);
			executor.execute(() -> {
				ArrayList<Paper> modifiedPapers = new ArrayList<>();
				Query.QueryType type = toBeProcessed.getType();
				Query.Target target = toBeProcessed.getObject();
				String key = toBeProcessed.getCondition();

				Paper newPaper = null;
				// prepare new paper
				if ((type.equals(Query.QueryType.ADD) || type.equals(Query.QueryType.UPDATE))
						&& toBeProcessed.getObject().equals(Query.Target.PAPER)) {
					BibParser parser = new BibParser("");
					if (type.equals(Query.QueryType.ADD)) {
						newPaper = parser.parseLineToPaper(toBeProcessed.getValue(), null);
					} else {
						newPaper = parser.parseLineToPaper(toBeProcessed.getValue(),
								paperBase.get(toBeProcessed.getCondition()).getPaperID());
					}

				}
				synchronized (action) {
					try {
						// wait for correct order
						while (action.getQueries().indexOf(toBeProcessed) != action.getNumOfProcessed()) {
							action.wait();
						}
						// read stage
						if (type.equals(Query.QueryType.REMOVE) || type.equals(Query.QueryType.UPDATE)) {
							synchronized (this) {
								switch (target) {
								case PAPER:
									modifiedPapers.add(paperBase.get(key));
									break;
								case AUTHOR:
									modifiedPapers = new ArrayList<>(paperBase.values().stream()
											.filter(e -> e.getAuthors().contains(key)).toList());
									break;
								case JOURNAL:
									modifiedPapers = new ArrayList<>(paperBase.values().stream()
											.filter(e -> (e.getJournal() == null && key.isEmpty())
													|| (e.getJournal() != null && e.getJournal().equals(key)))
											.toList());
									break;
								case YEAR:
									modifiedPapers = new ArrayList<>(paperBase.values().stream()
											.filter(e -> Integer.toString(e.getYear()).equals(key)).toList());
									break;
								case KEYWORDS:
									modifiedPapers = new ArrayList<>(paperBase.values().stream()
											.filter(e -> e.getKeywords().contains(key)).toList());
									break;
								case TITLE:
									modifiedPapers = new ArrayList<>(
											paperBase.values().stream()
													.filter(e -> (e.getTitle() == null && key.isEmpty())
															|| (e.getTitle() != null && e.getTitle().equals(key)))
													.toList());
									break;
								default:
								}
							}
						}

						// write stage
						if (type.equals(Query.QueryType.UPDATE) && target.equals(Query.Target.PAPER)) {
							paperBase.put(newPaper.getPaperID(), newPaper);
							modifiedPapers.add(newPaper);
						}
						if (type.equals(Query.QueryType.UPDATE)) {
							String newValue = toBeProcessed.getValue();
							synchronized (this) {
								if (target.equals(Query.Target.PAPER)) {
									paperBase.put(key, newPaper);
								}
								for (Paper paper : modifiedPapers) {
									switch (toBeProcessed.getObject()) {
									case AUTHOR:
										paper.getAuthors().remove(key);
										paper.getAuthors().add(newValue);
										break;
									case JOURNAL:
										paper.setJournal(newValue);
										break;
									case YEAR:
										paper.setYear(Integer.parseInt(newValue));
										break;
									case KEYWORDS:
										paper.getKeywords().remove(key);
										paper.getKeywords().add(newValue);
										break;
									case TITLE:
										paper.setTitle(newValue);
										break;
									default:
									}
								}
							}

						}
						if (type.equals(Query.QueryType.REMOVE)) {
							synchronized (this) {
								if (target.equals(Query.Target.PAPER)) {
									paperBase.remove(key);
								}
								for (Paper paper : modifiedPapers) {
									switch (target) {
									case AUTHOR:
										paper.getAuthors().remove(key);
										break;
									case JOURNAL:
										paper.setJournal(null);
										break;
									case YEAR:
										paper.setYear(1900);
										break;
									case KEYWORDS:
										paper.getKeywords().remove(key);
										break;
									case TITLE:
										paper.setTitle(null);
										break;
									default:
									}
								}
							}

						}
						// conclusion
						if (modifiedPapers.size() > 0) {
							toBeProcessed.setCompleted(true);
							toBeProcessed.setCompletedDate();
						}
						action.increaseNumOfProcessed();
						action.notifyAll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		executor.shutdown();
		while (true) {
			if (executor.isTerminated()) {
				break;
			}
		}
		action.setCompleted(true);
	}

	public User userInterfaceForUserCreation() {
		System.out.println("Please enter your name.");
		Scanner scan2 = new Scanner(System.in);
		if (scan2.hasNextLine()) {
			String name = scan2.nextLine();
			System.out.println("Create the account with the name: " + name);
			String userID = "User_" + users.size();
			User curUser = processUserRegister(userID, name, new Date());
			System.out.println("Account created!");
			return curUser;
		}
		return null;
	}

	public void userInterfaceForPaperSearch(User curUser) {
		System.out.println("Please specify the search kind:");
		System.out.println("  1: Search by ID");
		System.out.println("  2: Search by title");
		System.out.println("  3: Search by author");
		System.out.println("  4: Search by journal");
		while (true) {
			Scanner scan3 = new Scanner(System.in);
			if (scan3.hasNextInt()) {
				int k = scan3.nextInt();
				if (k < 1 || k > 4) {
					System.out.println("You should enter 1~4.");
				} else {
					System.out.println("Please specify the search word:");
					Scanner scan4 = new Scanner(System.in);
					if (scan4.hasNextLine()) {
						String word = scan4.nextLine();
						SearchPaperAction action = new SearchPaperAction("Action_" + actions.size(), curUser,
								new Date(), word, SearchPaperKind.values()[k - 1]);
						actions.add(action);
						processSearchPaperAction(curUser, action);

						if (action.getActionResult().size() > 0) {
							System.out.println("Paper found! The paper IDs are as follows:");
							for (Paper paper : action.getActionResult()) {
								System.out.println(paper.getPaperID());
							}
						} else {
							System.out.println("Paper not found!");
						}
						break;
					}
				}
			}
		}
	}

	public void userInterfaceForPaperSearchByLambda(User curUser) {
		System.out.println("Please specify the search kind:");
		System.out.println("  1: Search by ID");
		System.out.println("  2: Search by title");
		System.out.println("  3: Search by author");
		System.out.println("  4: Search by journal");
		while (true) {
			Scanner scan1 = new Scanner(System.in);
			if (scan1.hasNextInt()) {
				int k = scan1.nextInt();
				if (k < 1 || k > 4) {
					System.out.println("You should enter 1~4.");
				} else {
					System.out.println("Please specify the search word:");
					Scanner scan2 = new Scanner(System.in);
					if (scan2.hasNextLine()) {
						String word = scan2.nextLine();
						SearchPaperAction action = new SearchPaperAction("Action_" + actions.size(), curUser,
								new Date(), word, SearchPaperKind.values()[k - 1]);
						actions.add(action);
						processSearchPaperActionByLambda(curUser, action);

						if (action.getActionResult().size() > 0) {
							System.out.println("Paper found! The paper IDs are as follows:");
							for (Paper paper : action.getActionResult()) {
								System.out.println(paper);
							}
						} else {
							System.out.println("Paper not found!");
						}
						break;
					}
				}
			}
		}
	}

	public void userInterfaceForPaperSortByLambda(User curUser) {
		System.out.println("Please specify the sort base:");
		System.out.println("  1: Sort by ID");
		System.out.println("  2: Sort by title");
		System.out.println("  3: Sort by author");
		System.out.println("  4: Sort by journal");
		while (true) {
			Scanner scan1 = new Scanner(System.in);
			if (scan1.hasNextInt()) {
				int k = scan1.nextInt();
				if (k < 1 || k > 4) {
					System.out.println("You should enter 1~4.");
				} else {
					System.out.println("Please specify the sort kind:");
					System.out.println("  1: Sort in ascending order");
					System.out.println("  2: Sort in descending order");
					Scanner scan2 = new Scanner(System.in);
					if (scan2.hasNextLine()) {
						int m = scan2.nextInt();
						SortPaperAction action = new SortPaperAction("Action_" + actions.size(), curUser, new Date(),
								SortBase.values()[k - 1], SortKind.values()[m - 1]);
						actions.add(action);
						processSortPaperActionByLambda(curUser, action);

						if (action.getActionResult().size() > 0) {
							System.out.println("Paper sorted! The paper are sorted as follows:");
							for (Paper paper : action.getActionResult()) {
								System.out.println(paper);
							}
						} else {
							System.out.println("Paper not sorted!");
						}
						break;
					}
				}
			}
		}
	}

	public void userInterfaceForResearcherSearchByLambda(User curUser) {
		System.out.println("Please specify the search kind:");
		System.out.println("  1: Search researchers who publish papers more than X times in the recent Y years");
		System.out.println(
				"  2: Search researchers whose papers published in the journal X have abstracts more than Y words");
		System.out.println(
				"  3: Search researchers whoes keywords have more than similarity X% as one of those of the researcher Y");
		while (true) {
			Scanner scan1 = new Scanner(System.in);
			if (scan1.hasNextInt()) {
				int k = scan1.nextInt();
				if (k < 1 || k > 3) {
					System.out.println("You should enter 1~3.");
				} else {
					System.out.println("Please specify the X:");
					Scanner scan2 = new Scanner(System.in);
					if (scan2.hasNextLine()) {
						String factorX = scan2.nextLine();
						System.out.println("Please specify the Y:");
						Scanner scan3 = new Scanner(System.in);
						if (scan3.hasNextLine()) {
							String factorY = scan3.nextLine();
							SearchResearcherAction action = new SearchResearcherAction("Action_" + actions.size(),
									curUser, new Date(), factorX, factorY, SearchResearcherKind.values()[k - 1]);
							actions.add(action);
							processSearchResearcherActionByLambda(curUser, action);

							if (action.getActionResult().size() > 0) {
								System.out.println("Researcher found! The researcher information is as follows:");
								for (Map.Entry<String, List<Paper>> entry : action.getActionResult().entrySet()) {
									System.out.println(entry.getKey());
									for (Paper paper : entry.getValue()) {
										System.out.println(paper);
									}
								}
							} else {
								System.out.println("Researcher not found!");
							}
							break;
						}
					}
				}
			}
		}
	}

	public void userInterfaceForStatisticalInformationByLambda(User curUser) {
		System.out.println("Please specify the information:");
		System.out.println("  1: Obtain the average number of papers published by researchers per year");
		System.out.println("  2: Obtain the journals that receive the most papers every year");
		while (true) {
			Scanner scan1 = new Scanner(System.in);
			if (scan1.hasNextInt()) {
				int k = scan1.nextInt();
				if (k < 1 || k > 2) {
					System.out.println("You should enter 1~2.");
				} else {
					StatisticalInformationAction action = new StatisticalInformationAction("Action_" + actions.size(),
							curUser, new Date(), InfoKind.values()[k - 1]);
					actions.add(action);
					processStatisticalInformationActionByLambda(curUser, action);

					if (action.getActionResult().size() > 0) {
						System.out.println("Information Obtained! The information is as follows:");
						for (Map.Entry<String, Double> entry : action.getActionResult().entrySet()) {
							System.out.println(entry.getKey() + ": " + entry.getValue());
						}
					} else {
						System.out.println("Information not obtained!");
					}
					break;
				}
			}
		}
	}

	public void userInterfaceForPaperUpload(User curUser) {
		System.out.println("Please specify the absolute path of the bib file:");
		Scanner scan5 = new Scanner(System.in);
		if (scan5.hasNextLine()) {
			String name = scan5.nextLine();
			UploadPaperAction action = new UploadPaperAction("Action_" + actions.size(), curUser, new Date(), name);
			actions.add(action);
			processUploadPaperAction(curUser, action);
			if (action.getActionResult()) {
				System.out.println("Succeed! The uploaded papers are as follows:");
				for (String id : action.getUploadedPapers().keySet()) {
					System.out.println(id);
				}
			} else {
				System.out.println("Fail! You need to specify an existing bib file.");
			}
		}
	}

	public void userInterfaceForPaperDownload(User curUser) {
		System.out.println("Please specify the absolute path of the bib file:");
		Scanner scan6 = new Scanner(System.in);
		if (scan6.hasNextLine()) {
			String path = scan6.nextLine();
			DownloadPaperAction action = new DownloadPaperAction("Action_" + actions.size(), curUser, new Date(), path);
			System.out.println("Please enter the paper ID line by line and end with END");
			while (true) {
				Scanner scan7 = new Scanner(System.in);
				if (scan7.hasNextLine()) {
					String name = scan7.nextLine();
					if (name.equals("END")) {
						break;
					} else {
						action.appendPapers(name);
					}
				}
			}
			actions.add(action);
			processDownloadPaperAction(curUser, action);
			if (action.getActionResult()) {
				System.out.println("Succeed! The downloaded paper is stored in your specified file.");
			} else {
				System.out.println("Fail! Some papers not found!");
			}
		}
	}

	public void userInterfaceForAddLabel(User curUser) {
		System.out.println("Please specify the paper ID:");
		Scanner scan8 = new Scanner(System.in);
		if (scan8.hasNextLine()) {
			String paperID = scan8.nextLine();
			System.out.println("Please specify the label");
			Scanner scan9 = new Scanner(System.in);
			if (scan9.hasNextLine()) {
				String newlabel = scan9.nextLine();
				AddLabelAction action = new AddLabelAction("Action_" + actions.size(), curUser, new Date(), newlabel,
						paperID);
				actions.add(action);
				processAddLabelAction(curUser, action);

				if (action.getActionResult()) {
					System.out.println("Succeed! The label is added.");
				} else {
					System.out.println("Fail!");
				}
			}
		}
	}

	public void userInterfaceForAddComment(User curUser) {
		System.out.println("Please specify the commented object ID:");
		Scanner scan10 = new Scanner(System.in);
		if (scan10.hasNextLine()) {
			String objID = scan10.nextLine();
			System.out.println("Please specify the comment");
			Scanner scan11 = new Scanner(System.in);
			if (scan11.hasNextLine()) {
				String newCommentStr = scan11.nextLine();
				CommentType t = null;
				if (objID.startsWith("Comment")) {
					t = CommentType.COMMENT_OF_COMMENT;
				} else {
					t = CommentType.COMMENT_OF_PAPER;
				}
				AddCommentAction action = new AddCommentAction("Action_" + actions.size(), curUser, new Date(),
						newCommentStr, t, objID);
				actions.add(action);
				processAddCommentAction(curUser, action);

				if (action.getActionResult()) {
					System.out.println("Succeed! The comment is added.");
				} else {
					System.out.println("Fail!");
				}
			}
		}
	}

	/**
	 * 
	 * TODO Receive and infer the absolute path of the files from the user for
	 * importing and check the validity of input string
	 * 
	 * @param curUser: current user who is performing this action
	 */
	private void userInterfaceForParallelImport(User curUser) {
		// TODO Auto-generated method stub
		System.out.println(
				"Please specify the absolute path of the bib files to import in one line separated by \",\" (e.g. /temp/1.bib,/temp/2.bib):");
		// Retrieve the file locations from @name

		ParallelImportAction importAction = new ParallelImportAction("Action_" + actions.size(), curUser, new Date());
		Scanner scan10 = new Scanner(System.in);
		if (scan10.hasNextLine()) {
			String name = scan10.nextLine();
			System.out.println("File paths are " + name);
			ArrayList<String> filepaths = processInputParallelImport(name);
			for (String path : filepaths) {
				System.out.println("path is " + path);
			}
			importAction.setFilePaths(filepaths);
			processParallelImport(curUser, importAction);
		}

	}

	private ArrayList<String> processInputParallelImport(String name) {
		// TODO Auto-generated method stub
		ArrayList<String> filepaths = new ArrayList<>();
		String[] paths = name.split(",");
		if (paths.length == 0 && name.length() > 0) {
			filepaths.add(name);
		} else if (paths.length > 0) {
			for (String temp : paths)
				filepaths.add(temp);
		}
		return filepaths;
	}

	/***
	 * TODO Receive and infer the absolute path of the files from the user for
	 * importing and check the validity of input string
	 * 
	 * @param curUser
	 * @throws InterruptedException
	 */
	private void userInterfaceForMultiKeywordSearch(User curUser) throws InterruptedException {
		System.out.println(
				"Please enter at most 20 keywords for searching separated by \"+ \" (e.g. word1 + word2 + word3):");
		ArrayList<String> words = new ArrayList();
		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction(
				"Action_" + actions.size(), curUser, new Date());
		Scanner scan11 = new Scanner(System.in);
		if (scan11.hasNextLine()) {
			String name = scan11.nextLine();

			// split the words
			String[] keywords = name.split("\\+");

			// remove the spaces
			for (int i = 0; i < keywords.length; i++) {
				System.out.println("Word " + i + " : " + keywords[i]);
				words.add(keywords[i].replaceFirst(" ", ""));
			}
			searchMultipleKeywordsAction.setWords(words);
			// process the multikeyword search operation
			processMultiKeywordSearch(curUser, searchMultipleKeywordsAction);

		}
	}

	/**
	 * TODO In this function, the program interactively asks the user @curUser for
	 * adding, updating or removing labels and performs the operation in the
	 * background
	 * 
	 * @param curUser
	 */
	private void userInterfaceModifyLabels(User curUser) {

		boolean exit = false;
		Thread[] threads = new Thread[3];
		Runnable[] runnables = new Runnable[3];

		LabelActionList actionList = new LabelActionList();

		// create the thread for adding new labels
		runnables[0] = processAddLabel(curUser, actionList);
		threads[0] = new Thread(runnables[0]);
		threads[0].start();

		// create the thread for updating labels
		runnables[1] = processUpdateLabel(curUser, actionList);
		threads[1] = new Thread(runnables[1]);
		threads[1].start();

		// create the thread for removing the labels
		runnables[2] = processDeleteLabel(curUser, actionList);
		threads[2] = new Thread(runnables[2]);
		threads[2].start();

		while (!exit) {
			System.out.println("Please choose from the below operations: ");
			System.out.println("(1) Add a label (2) Update a label (3) Delete a label (4) Exit:");
			@SuppressWarnings("resource")
			Scanner scan13 = new Scanner(System.in);

			if (scan13.hasNextInt()) {
				int k = scan13.nextInt();
				if (k < 1 || k > 4) {
					System.out.println("You should enter 1~4.");
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					switch (k) {
					case 1:
						System.out.println("Please enter the paperId:");
						scan13 = new Scanner(System.in);
						if (scan13.hasNextLine()) {
							String paperId = scan13.nextLine();
							if (paperBase.keySet().contains(paperId)) {
								System.out.println("Please enter a new label:");
								scan13 = new Scanner(System.in);
								if (scan13.hasNextLine()) {
									String label = scan13.nextLine();
									LabelAction labelAction = new LabelAction("Action_" + actions.size(), curUser,
											new Date(), Action.ActionType.ADD_LABEL, label);
									labelAction.setPaperID(paperId);
									actionList.enqueue(labelAction);
									actions.add(labelAction);
								}

							} else {
								System.out.println("Fail: no paper with id " + paperId + " exist!");
							}

						}
						break;
					case 2:
						System.out.println(
								"Please enter the target labels to update separated by \",\" (e.g, label1,label2,label3, ... :");
						scan13 = new Scanner(System.in);
						if (scan13.hasNextLine()) {
							String labels = scan13.nextLine();
							ArrayList<String> inputLabels = new ArrayList<>();
							inputLabels.addAll(processInputLabels(labels));

							if (inputLabels.size() > 0) {
								String newlabel = "";
								System.out.println("Please enter the new label:");
								scan13 = new Scanner(System.in);
								if (scan13.hasNextLine()) {
									newlabel = scan13.nextLine();
									for (String label : inputLabels) {
										LabelAction labelAction = new LabelAction("Action_" + actions.size(), curUser,
												new Date(), Action.ActionType.UPDATE_LABELS, label);
										labelAction.setNewLabel(newlabel);
										actions.add(labelAction);
										actionList.enqueue(labelAction);
									}
								}
							} else {
								System.out.println("Fail: no input label is entered!");
							}
						} else {
							System.out.println("Fail: Please enter the input labels.");
						}
						break;
					case 3:
						System.out.println(
								"Please the target labels to reomve separate by \",\" (e.g, label1,label2,label3, ... :");
						scan13 = new Scanner(System.in);
						if (scan13.hasNextLine()) {
							String labels = scan13.nextLine();
							ArrayList<String> inputLabels = new ArrayList<>();
							inputLabels.addAll(processInputLabels(labels));
							for (String label : inputLabels) {
								LabelAction labelAction = new LabelAction("Action_" + actions.size(), curUser,
										new Date(), Action.ActionType.DELETE_LABELS, label);
								actions.add(labelAction);
								actionList.enqueue(labelAction);
							}

						}
					case 4:
						exit = true;
						actionList.setFinished(true);
						break;
					}
				}
			}
		}

		LabelAction labelAction = new LabelAction("Action_1", curUser, new Date(), Action.ActionType.ADD_LABEL,
				"TEST1");
		labelAction.setPaperID(getPaperBase().keySet().toArray()[0].toString());
		actionList.enqueue(labelAction);

		LabelAction labelAction1 = new LabelAction("Action_2", curUser, new Date(), Action.ActionType.ADD_LABEL,
				"TEST2");
		labelAction1.setPaperID(getPaperBase().keySet().toArray()[0].toString());
		actionList.enqueue(labelAction1);

		LabelAction labelAction2 = new LabelAction("Action_3", curUser, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST2");
		actionList.enqueue(labelAction2);

		LabelAction labelAction3 = new LabelAction("Action_4", curUser, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST1");
		actionList.enqueue(labelAction3);

		LabelAction labelAction4 = new LabelAction("Action_5", curUser, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST1");
		actionList.enqueue(labelAction4);
		System.out.println("processed " + actionList.getProcessedLabels().size());

		actionList.setFinished(true);

	}

	private ArrayList<String> processInputLabels(String labels) {
		// TODO Auto-generated method stub
		ArrayList<String> labelList = new ArrayList<String>();
		if (labels.length() != 0) {
			String[] temp = labels.split(",");
			if (temp.length > 0) {
				for (String label : temp) {
					labelList.add(label.replaceFirst(" ", ""));
				}
			} else {
				labelList.add(labels);
			}
		}
		return labelList;
	}

	public void userInterfaceConcurrentQueryProcess(User curUser) {
		QueryAction action = new QueryAction("Action_" + actions.size(), curUser, new Date(),
				Action.ActionType.PROCESS_QUERY);
		System.out.println("Please specify the absolute path of the file containing the queries:");
		// Retrieve the file locations from @name
		Scanner scan13 = new Scanner(System.in);
		if (scan13.hasNextLine()) {
			String name = scan13.nextLine();
			action.setFilePath(name);
			processConcurrentQuery(curUser, action);
		} else {
			System.out.println("Fail: No filepath is entered");
		}

	}

	public void userInterface() {
		System.out.println("----------------------------------------------------------------------");
		System.out.println("MiniMendeley is running...");
		System.out.println("Initial paper base has been populated!");
		User curUser = null;

		while (true) {
			System.out.println("----------------------------------------------------------------------");
			System.out.println("Please select the following operations with the corresponding numbers:");
			System.out.println("  0: Register an account");
			System.out.println("  1: Search papers");
			System.out.println("  2: Upload papers");
			System.out.println("  3: Download papers");
			System.out.println("  4: Add labels");
			System.out.println("  5: Add comments");
			System.out.println("  6: Search papers via Lambda");
			System.out.println("  7: Sort papers via Lambda");
			System.out.println("  8: Search researchers via Lambda");
			System.out.println("  9: Obtain statistical information via Lambda");
			System.out.println("  10: Import several bib files in parallel");
			System.out.println("  11: Multiple Keyword Search");
			System.out.println("  12: Update or Delete Labels");
			System.out.println("  13: Parallel Query Execution");
			System.out.println("  14: Exit");
			System.out.println("----------------------------------------------------------------------");
			Scanner scan1 = new Scanner(System.in);
			if (scan1.hasNextInt()) {
				int i = scan1.nextInt();
				if (i < 0 || i > 14) {
					System.out.println("You should enter 0~11.");
					continue;
				}
				if (curUser == null && i != 0) {
					System.out.println("You need to register an account first.");
					continue;
				}
				switch (i) {
				case 0: {
					curUser = userInterfaceForUserCreation();
					break;
				}
				case 1: {
					userInterfaceForPaperSearch(curUser);
					break;
				}
				case 2: {
					userInterfaceForPaperUpload(curUser);
					break;
				}
				case 3: {
					userInterfaceForPaperDownload(curUser);
					break;
				}
				case 4: {
					userInterfaceForAddLabel(curUser);
					break;
				}
				case 5: {
					userInterfaceForAddComment(curUser);
					break;
				}
				case 6: {
					userInterfaceForPaperSearchByLambda(curUser);
					break;
				}
				case 7: {
					userInterfaceForPaperSortByLambda(curUser);
					break;
				}
				case 8: {
					userInterfaceForResearcherSearchByLambda(curUser);
					break;
				}
				case 9: {
					userInterfaceForStatisticalInformationByLambda(curUser);
					break;
				}
				case 10: {
					userInterfaceForParallelImport(curUser);
					break;
				}
				case 11: {
					try {
						userInterfaceForMultiKeywordSearch(curUser);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				case 12: {
					userInterfaceModifyLabels(curUser);
				}
					break;
				case 13: {
					userInterfaceConcurrentQueryProcess(curUser);
				}
					break;
				default:
					break;
				}
				if (i == 14)
					break;
			} else {
				System.out.println("You should enter integer 0~6.");
			}
		}
	}

}
