package hk.ust.comp3021.person;

import hk.ust.comp3021.resource.Comment;
import hk.ust.comp3021.resource.Label;

import java.util.*;
import java.util.stream.Collectors;

public class User extends Person {
	private final ArrayList<Comment> userComments = new ArrayList<>();
	private ArrayList<Label> userLabels = new ArrayList<>();

	public User(String id, String name, Date registerDate) {
		super(id, name);
	}

	public void appendNewComment(Comment comment) {
		userComments.add(comment);
	}

	public ArrayList<Comment> searchCommentByPaperObjID(String id) {
		ArrayList<Comment> res = new ArrayList<>();
		for (Comment comment : userComments) {
			if (comment.getType() == Comment.CommentType.COMMENT_OF_PAPER) {
				if (comment.getCommentObjId().equals(id)) {
					res.add(comment);
				}
			}
		}
		return res;
	}

	public ArrayList<Comment> searchCommentByPaperObjIDByLambda(String id) {
		ArrayList<Comment> res = new ArrayList<>();
		res = (ArrayList<Comment>) userComments.stream()
				.filter(comment -> comment.getType() == Comment.CommentType.COMMENT_OF_PAPER
						|| comment.getCommentObjId().equals(id))
				.collect(Collectors.toList());
		return res;
	}

	public ArrayList<Comment> searchCommentByCommentObjID(String id) {
		ArrayList<Comment> res = new ArrayList<>();
		for (Comment comment : userComments) {
			if (comment.getType() == Comment.CommentType.COMMENT_OF_COMMENT) {
				if (comment.getCommentObjId().equals(id)) {
					res.add(comment);
				}
			}
		}
		return res;
	}

	public ArrayList<Comment> searchCommentByCommentObjIDByLambda(String id) {
		ArrayList<Comment> res = new ArrayList<>();
		res = (ArrayList<Comment>) userComments.stream()
				.filter(comment -> comment.getType() == Comment.CommentType.COMMENT_OF_COMMENT
						|| comment.getCommentObjId().equals(id))
				.collect(Collectors.toList());
		return res;
	}

	public void appendNewLabel(Label label) {
		userLabels.add(label);
		System.out.println("adding label " + label.getContent() + " " + label.getPaperID());
	}

	public ArrayList<Label> searchLabelByPaperID(String id) {
		ArrayList<Label> res = new ArrayList<>();
		for (Label label : userLabels) {
			if (label.getPaperID().equals(id)) {
				res.add(label);
			}
		}
		return res;
	}

	public ArrayList<Label> searchLabelByPaperIDByLambda(String id) {
		ArrayList<Label> res = new ArrayList<>();
		res = (ArrayList<Label>) userLabels.stream().filter(label -> label.getPaperID().equals(id))
				.collect(Collectors.toList());
		return res;
	}

	public Label getLabel(String content) {
		for (Label label : userLabels)
			if (label.getContent().equals(content))
				return label;
		return null;
	}

	public ArrayList<Label> getlabels() {
		return userLabels;
	}

	public void setUserLabels(ArrayList<Label> userLabels) {
		this.userLabels = userLabels;
	}

	public ArrayList<String> updateUserLabels(String labelValue, String newLabel, Date creationDate) {
		ArrayList<String> papers = new ArrayList<String>();
		ArrayList<Label> newLabels = new ArrayList<>();
		ArrayList<Label> labelstobeupdated = new ArrayList<>();

		for (Label label : userLabels) {
			if (label.getContent().equals(labelValue)) {
				if (!papers.contains(label.getPaperID())) {
					papers.add(label.getPaperID());
				}

				String labelID = "Label" + String.valueOf(userLabels.size() + 1);
				Label modifiedLabel = new Label(labelID, label.getPaperID(), creationDate, labelValue, this);
				newLabels.add(modifiedLabel);
				labelstobeupdated.add(label);
				System.out.println("updating label " + label.getContent() + " " + label.getPaperID());
			}
		}
		userLabels.removeAll(labelstobeupdated);
		userLabels.addAll(newLabels);
		return papers;
	}

	public ArrayList<String> removeUserLabel(String labelValue) {
		ArrayList<String> papers = new ArrayList<String>();
		ArrayList<Label> removedLabels = new ArrayList<Label>();
		for (Label label : userLabels) {
			if (label.getContent().equals(labelValue)) {
				papers.add(label.getPaperID());
				removedLabels.add(label);
				System.out.println("deleting label " + label.getContent() + " " + label.getPaperID());
			}
		}
		userLabels.removeAll(removedLabels);
		return papers;
	}
}
