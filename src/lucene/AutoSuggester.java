package lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

import structures.Unigram;

public class AutoSuggester {

	private ArrayList<Unigram> dataset;
	
	public AutoSuggester(ArrayList<Unigram> data) {
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
		subset.add(new Unigram("ba", 10));
		subset.add(new Unigram("ball", 50));
		subset.add(new Unigram("balloon", 20));
		subset.add(new Unigram("balls", 30));
		

		AutoSuggester test = new AutoSuggester(subset);
		List<LookupResult> results = test.search("ba", 4);

		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}

}
