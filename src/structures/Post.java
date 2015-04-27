/**
 * 
 */
package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import json.JSONException;
import json.JSONObject;

/**
 * @author hongning
 * @version 0.1
 * @category data structure data structure for a Yelp review document You can
 *           create some necessary data structure here to store the processed
 *           text content, e.g., bag-of-word representation
 */
public class Post {

	public HashMap<String, Token> term_frequency;
	public HashSet<String> terms;
	public HashMap<String, Token> vector_TFIDF;
	
	public ArrayList<Double> random_projection;

	// Cosine similarity score
	double m_score;

	public void setScore(double score) {
		m_score = score;
	}

	public double getScore() {
		return m_score;
	}

	// Magnitude of vector space model
	double v_space_mag;

	public void setMag(double mag) {
		v_space_mag = mag;
	}

	public double getMag() {
		return v_space_mag;
	}

	// unique review ID from Yelp
	String m_ID;

	public void setID(String ID) {
		m_ID = ID;
	}

	public String getID() {
		return m_ID;
	}

	// author's displayed name
	String m_author;

	public String getAuthor() {
		return m_author;
	}

	public void setAuthor(String author) {
		this.m_author = author;
	}

	// author's location
	String m_location;

	public String getLocation() {
		return m_location;
	}

	public void setLocation(String location) {
		this.m_location = location;
	}

	// review text content
	String m_content;

	public String getContent() {
		return m_content;
	}

	public void setContent(String content) {
		if (!content.isEmpty())
			this.m_content = content;
	}

	public boolean isEmpty() {
		return m_content == null || m_content.isEmpty();
	}

	// timestamp of the post
	String m_date;

	public String getDate() {
		return m_date;
	}

	public void setDate(String date) {
		this.m_date = date;
	}

	// overall rating to the business in this review
	double m_rating;

	public double getRating() {
		return m_rating;
	}

	public void setRating(double rating) {
		this.m_rating = rating;
	}

	boolean positive;

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean pos) {
		positive = pos;
	}

	public Post(String ID) {
		m_ID = ID;
	}

	double bayes_classifier;

	public double getBayes_classifier() {
		return bayes_classifier;
	}

	public void setBayes_classifier(double bayes_classifier) {
		this.bayes_classifier = bayes_classifier;
	}

	double precision;

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	double recall;
	
	public double getFP() {
		return FP;
	}

	public void setFP(double fP) {
		FP = fP;
	}

	public double getTP() {
		return TP;
	}

	public void setTP(double tP) {
		TP = tP;
	}

	public double getFN() {
		return FN;
	}

	public void setFN(double fN) {
		FN = fN;
	}

	double FP;
	double TP;
	double FN;

	public Post(JSONObject json) {
		m_score = 0;
		v_space_mag = 0;
		term_frequency = new HashMap<String, Token>();
		terms = new HashSet<String>();
		vector_TFIDF = new HashMap<String, Token>();
		random_projection = new ArrayList<Double>();
		positive = false;
		try {
			m_ID = json.getString("ReviewID");
			setAuthor(json.getString("Author"));

			setDate(json.getString("Date"));
			setContent(json.getString("Content"));
			setRating(json.getDouble("Overall"));
			setLocation(json.getString("Author_Location"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("ReviewID", m_ID);// must contain
		json.put("Author", m_author);// must contain
		json.put("Date", m_date);// must contain
		json.put("Content", m_content);// must contain
		json.put("Overall", m_rating);// must contain
		json.put("Author_Location", m_location);// must contain

		return json;
	}

}
