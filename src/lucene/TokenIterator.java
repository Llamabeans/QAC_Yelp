package lucene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import structures.Token;

public final class TokenIterator implements InputIterator {
	
	private Token current;
	private final Iterator<Token> tokenIterator;

	public TokenIterator(Iterator<Token> iterator) {
		this.tokenIterator = iterator;
	}

	public TokenIterator(ArrayList<Token> list) {
		this.tokenIterator = list.iterator();
	}

	@Override
	public BytesRef next() {
		if (tokenIterator.hasNext()) {
			current = tokenIterator.next();
			return new BytesRef(current.getContent());
		}
		return null;
	}

	@Override
	public long weight() {
		return (long) current.getTF();
	}

	@Override
	public Set<BytesRef> contexts() {
		if (tokenIterator.hasNext()) {
			Set<BytesRef> data = new HashSet<BytesRef>();
			data.add(new BytesRef(tokenIterator.next().getContent()));
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
