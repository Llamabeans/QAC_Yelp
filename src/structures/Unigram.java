package structures;

import java.util.HashSet;

public class Unigram {
	private int id_num;

	public int getId_num() {
		return id_num;
	}

	public void setId_num(int id_num) {
		this.id_num = id_num;
	}

	// Stores docs in which this token appears
	HashSet<String> in_reviews;

	public void addReview(String s) {
		in_reviews.add(s);
	}

	public HashSet<String> getReview() {
		return in_reviews;
	}

	private String first;

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	private int termFreq;

	public int getTF() {
		return termFreq;
	}

	public void setTF(int count) {
		this.termFreq = count;
	}

	public void addTF(int num) {
		this.termFreq += num;
	}

	private int docFreq;

	public int getDF() {
		return docFreq;
	}

	public void setDF(int docFreq) {
		this.docFreq = docFreq;
	}

	public void addDF(int num) {
		this.docFreq += num;
	}

	private double chi;

	public double getChi() {
		return chi;
	}

	public void setChi(double chi) {
		this.chi = chi;
	}

	private double probPos;
	private double probNeg;
	private double probRatio;

	public double getProbPos() {
		return probPos;
	}

	public void setProbPos(double probPos) {
		this.probPos = probPos;
	}

	public double getProbNeg() {
		return probNeg;
	}

	public void setProbNeg(double probNeg) {
		this.probNeg = probNeg;
	}

	public double getProbRatio() {
		return probRatio;
	}

	public void setProbRatio(double probRatio) {
		this.probRatio = probRatio;
	}

	// Constructor 1
	public Unigram(String first) {
		this.in_reviews = new HashSet<String>();
		this.first = first;
		this.termFreq = 0;
		this.docFreq = 0;
	}

	public Unigram(String first, double chi) {
		this.first = first;
		this.chi = chi;
	}
}
