package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LabelActionList {

	private Queue<LabelAction> labelActionsQueue = new LinkedList<>();
	private ArrayList<String> processedLabels = new ArrayList<>();
	private boolean isFinished = false;

	private Lock labelLock = new ReentrantLock();
	private Condition notEmpty = labelLock.newCondition();
	private int numOfDeleted = 0;
	private int numOfAdded = 0;
	private int numOfUpdated = 0;

	public void enqueue(LabelAction item) {
		labelActionsQueue.offer(item);
	}

	public LabelAction dequeue()  {
		if(!labelActionsQueue.isEmpty())
			return labelActionsQueue.poll();
		return null;

	}

	public LabelAction getHead() {
		if(!labelActionsQueue.isEmpty())
			return labelActionsQueue.element();
		return null;
	}

	public int getNumOfUpdated() {
		return numOfUpdated;
	}

	public int getNumOfAdded() {
		return numOfAdded;
	}

	public int getNumOfDeleted() {
		return numOfDeleted;
	}

	public void increateNumOfAdded() {
		numOfAdded++;
	}

	public void increateNumOfupdated() {
		numOfUpdated++;
	}

	public void increateNumOfDeleted() {
		numOfDeleted++;
	}

	public void setNumOfUpdated(int numOfUpdated) {
		this.numOfUpdated = numOfUpdated;
	}

	public void setNumOfAdded(int numOfAdded) {
		this.numOfAdded = numOfAdded;
	}

	public void setNumOfDeleted(int numOfDeleted) {
		this.numOfDeleted = numOfDeleted;
	}

	public ArrayList<String> getProcessedLabels() {
		return processedLabels;
	}

	public void addProcessedLabel(String label) {
		processedLabels.add(label);
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

}
