package structures;

import org.apache.lucene.util.BytesRef;

public class WordFreq {
 public long count;
 public BytesRef term;

 public WordFreq(BytesRef term, long count) {
  this.term = term;
  this.count = count;
 }

 public WordFreq(String term, long count) {
  this(new BytesRef(term), count);
 }
}