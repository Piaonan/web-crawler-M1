package backup;

import java.util.HashMap;
import java.util.HashSet;

import server.Notice;

public class Domain {
	private HashMap<String, Notice> urls;
	private HashMap<String, HashSet<String>> terms;
	
	public Domain(HashMap<String, Notice> m1, HashMap<String, HashSet<String>>  m2) {
		urls = m1;
		terms = m2;
	}
	
	public HashMap<String, Notice> getUrls() {
		return urls;
	}
	
	public HashMap<String, HashSet<String>> getTerms(){
		return terms;
	}
}
