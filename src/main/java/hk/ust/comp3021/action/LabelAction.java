package hk.ust.comp3021.action;

import java.util.Date;

import hk.ust.comp3021.person.User;

public class LabelAction extends Action {

	private Action.ActionType actionType;
	private String label;
	private String newLabel;
	private String paperID;
	private boolean isSuccessful = false;

	public LabelAction(String id, User user, Date time, ActionType actionType, String label) {
		super(id, user, time, actionType);
		this.label = label;
		this.actionType = actionType;
	}

	public Action.ActionType getActionType() {
		return actionType;
	}

	public String getLabel() {
		return label;
	}

	public String getNewLabel() {
		return newLabel;
	}

	public String getPaperID() {
		return paperID;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setActionType(Action.ActionType actionType) {
		this.actionType = actionType;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setNewLabel(String newLabel) {
		this.newLabel = newLabel;
	}

	public void setPaperID(String paperID) {
		this.paperID = paperID;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

}
