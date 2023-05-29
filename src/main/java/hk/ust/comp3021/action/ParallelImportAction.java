package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import hk.ust.comp3021.person.User;
import hk.ust.comp3021.resource.Paper;

public class ParallelImportAction extends Action {
	public static int maxNumberofFiles = 10;
	private List<UploadPaperAction> importActions = new ArrayList<>();
	private ArrayList<String> filePaths = new ArrayList<>();

	public static HashMap<String, Paper> importedPapers = new HashMap<>();
	private boolean isCompleted = true;

	public ParallelImportAction(String id, User user, Date time) {
		super(id, user, time, ActionType.UPLOAD_PARALLEL);

	}

	public void setFilePaths(ArrayList<String> filePaths) {
		this.filePaths = filePaths;
	}

	public void setImportActions(List<UploadPaperAction> uploadActions) {
		for (UploadPaperAction action : uploadActions)
			this.importActions.add(action);
	}

	public List<UploadPaperAction> getUploadActions() {
		return importActions;
	}

	public HashMap<String, Paper> getImportedPapers() {
		return importedPapers;
	}

	public void setImportedPapers(HashMap<String, Paper> importActions) {
		ParallelImportAction.importedPapers = importActions;
	}

	public void addUploadedPapers(HashMap<String, Paper> importActions) {
		for (String id : importActions.keySet())
			ParallelImportAction.importedPapers.put(id, importActions.get(id));
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isValidImport(List<UploadPaperAction> uploadActions) {
		if (uploadActions.size() <= maxNumberofFiles && uploadActions.size() > 0)
			return true;
		return false;

	}

	public ArrayList<String> getFilePaths() {
		return filePaths;
	}

	public int maxNumberofThreads() {
		return maxNumberofFiles;
	}
}
