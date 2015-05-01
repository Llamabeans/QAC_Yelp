package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.en.KStemFilter;

public class Analyzer_Unigram extends Analyzer {
	@Override
	protected TokenStreamComponents createComponents(String arg0) {

		Tokenizer source = new LowerCaseTokenizer();
		TokenStream filter = new KStemFilter(source);
		return new TokenStreamComponents(source, filter);
		
	}
}