package lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

import structures.Bigram;

public class BigramAutoSuggester {

	private ArrayList<Bigram> dataset;
	
	public BigramAutoSuggester(ArrayList<Bigram> data) {
		dataset = data;
	}

	public List<LookupResult> search(String s, int num) {
		AnalyzingSuggester suggester = new AnalyzingSuggester(
				new StandardAnalyzer());
		try {
			suggester.build(new BigramIterator(dataset));
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
		
		ArrayList<Bigram> subset = new ArrayList<Bigram>();
		subset.add(new Bigram("ball room", 10));
		subset.add(new Bigram("ball course", 50));
		subset.add(new Bigram("baller effect", 20));
		subset.add(new Bigram("ball sunk", 30));
		

		BigramAutoSuggester test = new BigramAutoSuggester(subset);
		List<LookupResult> results = test.search("ba", 4);

		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}
	
}
