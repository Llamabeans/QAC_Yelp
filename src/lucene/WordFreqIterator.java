package lucene;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import structures.WordFreq;

public final class WordFreqIterator implements InputIterator {
	private WordFreq current;
	private final Iterator<WordFreq> wordFreqIterator;

	public WordFreqIterator(Iterator<WordFreq> wordFreqIterator) {
		this.wordFreqIterator = wordFreqIterator;
	}

	public WordFreqIterator(WordFreq[] list) {
		this(Arrays.asList(list).iterator());
	}

	@Override
	public BytesRef next() {
		if (wordFreqIterator.hasNext()) {
			current = wordFreqIterator.next();
			return current.term;
		}
		return null;
	}

	@Override
	public long weight() {
		return current.count;
	}

	@Override
	public Set<BytesRef> contexts() {
		if (wordFreqIterator.hasNext()) {
			Set<BytesRef> data = new HashSet<BytesRef>();
			data.add(wordFreqIterator.next().term);
			return data;
		}
		return null;
	}

	@Override
	public boolean hasContexts() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPayloads() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BytesRef payload() {
		// TODO Auto-generated method stub
		return null;
	}
}
