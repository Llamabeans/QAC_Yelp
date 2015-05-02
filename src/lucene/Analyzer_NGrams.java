package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;

public class Analyzer_NGrams extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String arg0) {
		TokenStream filter;
		Tokenizer source = new LowerCaseTokenizer();
		
		filter = new KStemFilter(source);
		
		ShingleFilter bigram = new ShingleFilter(filter);
		bigram.setOutputUnigrams(false);
		
		filter = bigram;
		
		return new TokenStreamComponents(source, filter);
	}

}