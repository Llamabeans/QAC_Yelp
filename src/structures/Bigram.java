package structures;

public class Bigram {
	private int id_num;

	public int getId_num() {
		return id_num;
	}

	public void setId_num(int id_num) {
		this.id_num = id_num;
	}

	private String first;
	private String second;

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}
	
	private String entire;
	

	public String getEntire() {
		return entire;
	}

	public void setEntire(String entire) {
		this.entire = entire;
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
	
	private double prob;
	
	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	// Constructor 1
	public Bigram(String entire) {
		this.entire = entire;
		String[] tokens = entire.split("\\s+");
		this.first = tokens[0];
		this.second = tokens[1];
	}

	public Bigram(String entire, int TF) {
		this.entire = entire;
		String[] tokens = entire.split("\\s+");
		this.first = tokens[0];
		this.second = tokens[1];
		this.termFreq = TF;
	}
}
