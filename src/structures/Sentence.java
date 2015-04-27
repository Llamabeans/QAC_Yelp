package structures;

public class Sentence {
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getLikelihood() {
		return likelihood;
	}
	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}
	private String content;
	private double likelihood;
	
	public Sentence(String con, double like) {
		content = con;
		likelihood = like;
	}
	

}
