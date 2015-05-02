package lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

import structures.Token;

public class TokenAutoSuggester {

	private ArrayList<Token> dataset;
	
	public TokenAutoSuggester(ArrayList<Token> data) {
		dataset = data;
	}

	public List<LookupResult> search(String s, int num) {
		AnalyzingSuggester suggester = new AnalyzingSuggester(
				new StandardAnalyzer());
		try {
			suggester.build(new TokenIterator(dataset));
			List<LookupResult> results = suggester.lookup(s, false, num);
			return results;
		} catch (Exception e) {
			return null;
		}

	}
	
	public double getSize() {
		return dataset.size();
	}

	public static void main(String args[]) {
		// Testing
		ArrayList<Token> subset = new ArrayList<Token>();
		subset.add(new Token("ball room", 10));
		subset.add(new Token("ballroom tour", 50));
		subset.add(new Token("balls across the room", 20));
		subset.add(new Token("balling", 30));
		

		TokenAutoSuggester test = new TokenAutoSuggester(subset);
		List<LookupResult> results = test.search("ba", 4);

		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}

}
