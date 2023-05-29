package hk.ust.comp3021.utils;

import java.util.Date;

public class Query {
	public enum QueryType {
		ADD, REMOVE, UPDATE,
	};

	public enum Target {
		PAPER, AUTHOR, JOURNAL, YEAR, KEYWORDS, TITLE
	};

	private QueryType type;
	private Target object;
	private String value = "";
	private String query = "";
	private String condition = "";
	private boolean valid = false;
	private boolean completed = false;
	private Date completedDate;

	public Query(String query) {
		this.query = query;
		this.valid = processQuery(query);
	}

	private boolean processQuery(String query) {
		String[] content = query.split(";");
		if (content.length == 3) {
			if (content[0].equals("ADD")) {
				type = QueryType.ADD;
			} else if (content[0].equals("REMOVE")) {
				type = QueryType.REMOVE;
			} else {
				return false;
			}
			object = getTarget(content[1]);
			value = content[2];
			if (!value.isEmpty() && object != null) {
				return true;
			}
		} else if (content.length == 4) {
			type = QueryType.UPDATE;
			object = getTarget(content[1]);
			condition = content[2];
			value = content[3];
			if (object != null && !condition.isEmpty() && !value.isEmpty())
				return true;
		}
		return false;
	}

	private Target getTarget(String target) {
		if (target.equals("PAPER"))
			return Target.PAPER;
		else if (target.equals("AUTHOR"))
			return Target.AUTHOR;
		else if (target.equals("JOURNAL"))
			return Target.JOURNAL;
		else if (target.equals("YEAR"))
			return Target.YEAR;
		else if (target.equals("KEYWORDS"))
			return Target.KEYWORDS;
		else if (target.equals("TITLE"))
			return Target.TITLE;

		return null;
	}

	public Target getObject() {
		return object;
	}

	public String getQuery() {
		return query;
	}

	public QueryType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public boolean getValidity() {
		return valid;
	}

	public String getCondition() {
		return condition;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setObject(Target object) {
		this.object = object;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setType(QueryType type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
		this.setCompletedDate();
	}

	public void setCompletedDate() {
		this.completedDate = new Date();
	}

	public Date getCompletedDate() {
		return completedDate;
	}
}
