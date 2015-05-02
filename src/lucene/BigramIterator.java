package lucene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import structures.Bigram;

public class BigramIterator implements InputIterator {
	
	private Bigram current;
	private final Iterator<Bigram> bigramIterator;

	public BigramIterator(Iterator<Bigram> iterator) {
		this.bigramIterator = iterator;
	}

	public BigramIterator(ArrayList<Bigram> list) {
		this.bigramIterator = list.iterator();
	}

	@Override
	public BytesRef next() {
		if (bigramIterator.hasNext()) {
			current = bigramIterator.next();
			return new BytesRef(current.getEntire());
		}
		return null;
	}

	@Override
	public long weight() {
		return (long) current.getTF();
	}

	@Override
	public Set<BytesRef> contexts() {
		if (bigramIterator.hasNext()) {
			Set<BytesRef> data = new HashSet<BytesRef>();
			Bigram b = bigramIterator.next();
			data.add(new BytesRef(b.getEntire()));
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
