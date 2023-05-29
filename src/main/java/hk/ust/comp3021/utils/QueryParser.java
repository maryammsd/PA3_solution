package hk.ust.comp3021.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class QueryParser {
	private final String queryfilePath;
	private boolean isErr;
	private final ArrayList<Query> queries;

	public QueryParser(String queryfilePath) {
		this.queryfilePath = queryfilePath;
		this.isErr = false;
		this.queries = new ArrayList<Query>();
	}

	public String getQueryfilePath() {
		return queryfilePath;
	}

	public boolean isErr() {
		return isErr;
	}

	public ArrayList<Query> getQueries() {
		return queries;
	}

	public void parse() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(queryfilePath));
			Query query = null;
			String line = reader.readLine();

			while (line != null) {
				query = new Query(line);
				if (query.getValidity()) {
					// query is valid, let's get its content
					queries.add(query);
					// process the query : create a new thread to process it !
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			isErr = true;
		}
	}
}
