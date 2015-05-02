package structures;

public class Token {

	private String content;
	private int termFreq;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getTF() {
		return termFreq;
	}

	public void setTF(int termFreq) {
		this.termFreq = termFreq;
	}
	
	public void addTF(int x) {
		this.termFreq += x;
	}

	public Token(String content, int freq) {
		this.content = content;
		this.termFreq = freq;
	}

}
