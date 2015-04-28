/**
 * 
 */
package structures;

import java.util.HashMap;
import java.util.HashSet;

import json.JSONException;
import json.JSONObject;

public class Post {

	public HashMap<String, Unigram> term_frequency;
	public HashSet<String> terms;

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


	public Post(JSONObject json) {
		term_frequency = new HashMap<String, Unigram>();
		terms = new HashSet<String>();
		positive = true;
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
