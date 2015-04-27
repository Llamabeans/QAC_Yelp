/**
 * 
 */
package structures;

import java.util.HashSet;

/**
 * @author hongning Suggested structure for constructing N-gram language model
 *         and vector space representation
 */
public class Token {

	// Stores docs in which this token appears
	HashSet<String> in_reviews;

	public void addReview(String s) {
		in_reviews.add(s);
	}

	public HashSet<String> getReview() {
		return in_reviews;
	}

	// the numerical ID you assigned to this token/N-gram
	int m_id;

	public int getID() {
		return m_id;
	}

	public void setID(int id) {
		this.m_id = id;
	}

	// the actual text content of this token/N-gram
	String m_token;

	public String getToken() {
		return m_token;
	}

	public void setToken(String token) {
		this.m_token = token;
	}

	// the DF
	double DF;

	public double getDF() {
		return DF;
	}

	public void setDF(double value) {
		this.DF = value;
	}

	public void addOneDF() {
		DF++;
	}
	public void addXDF(double x) {
		DF += x;
	}

	// the TF
	double TF;

	public double getTF() {
		return TF;
	}

	public void setTF(double value) {
		this.TF = value;
	}

	public void addOneTF() {
		TF++;
	}

	public void addX(double x) {
		TF += x;
	}

	double TFIDF;

	public double getTFIDF() {
		return TFIDF;
	}

	public void setTFIDF(double tFIDF) {
		TFIDF = tFIDF;
	}

	// information gain feature value
	double IG;

	public double getIG() {
		return IG;
	}

	public void setIG(double x) {
		IG = x;
	}

	double CHI;

	public double getCHI() {
		return CHI;
	}

	public void setCHI(double x) {
		CHI = x;
	}
	
	double naive_bayes;

	public double getNaive_bayes() {
		return naive_bayes;
	}

	public void setNaive_bayes(double naive_bayes) {
		this.naive_bayes = naive_bayes;
	}

	double ProbPos;

	public double getProbPos() {
		return ProbPos;
	}

	public void setProbPos(double probPos) {
		ProbPos = probPos;
	}

	double ProbNeg;

	public double getProbNeg() {
		return ProbNeg;
	}

	public void setProbNeg(double probNeg) {
		ProbNeg = probNeg;
	}

	// default constructor
	public Token(String token) {
		in_reviews = new HashSet<String>();
		m_token = token;
		m_id = -1;
		DF = 1;
		TF = 0;
	}

	// default constructor
	public Token(int id, String token, double df, double tf) {
		in_reviews = new HashSet<String>();
		m_token = token;
		m_id = id;
		DF = df;
		TF = tf;
	}
}
