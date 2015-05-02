package lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

import structures.Unigram;

public class TokenAutoSuggester {

	private ArrayList<Unigram> dataset;
	
	public TokenAutoSuggester(ArrayList<Unigram> data) {
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
		
		ArrayList<Unigram> subset = new ArrayList<Unigram>();
		subset.add(new Unigram("ball room", 10));
		subset.add(new Unigram("ballroom tour", 50));
		subset.add(new Unigram("balls across the room", 20));
		subset.add(new Unigram("balling", 30));
		

		TokenAutoSuggester test = new TokenAutoSuggester(subset);
		List<LookupResult> results = test.search("ba", 4);

		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}

}
