package lucene;

import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

import structures.WordFreq;

public class AutoSuggester {

	public WordFreq wordFreqs[] = new WordFreq[] { new WordFreq("ball", 50),
			new WordFreq("bar", 10), new WordFreq("ba", 12),
			new WordFreq("balloon", 6), new WordFreq("ballooning", 8), new WordFreq("balloonings", 8) };

	private List<LookupResult> search(String s) {
		AnalyzingSuggester suggester = new AnalyzingSuggester(
				new StandardAnalyzer());
		try {
			suggester.build(new WordFreqIterator(wordFreqs));
			List<LookupResult> results = suggester.lookup(s, false, 2);
			return results;
		} catch (Exception e) {
			return null;
		}

	}

	public static void main(String args[]) {

		AutoSuggester test = new AutoSuggester();
		List<LookupResult> results = test.search("balloon");

		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}

}
