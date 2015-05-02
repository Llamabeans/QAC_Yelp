package analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import lucene.Analyzer_Unigram;
import structures.Post;
import structures.Unigram;

public class Positive_Rank {

	List<Unigram> sort_unigram;

	HashMap<String, Unigram> unigram_pos;
	HashMap<String, Unigram> unigram_neg;
	HashMap<String, Unigram> unigram_stats;

	List<String> features;
	List<Post> m_reviews;

	int posReviews = 0;
	int negReviews = 0;
	int totalPosTerms = 0;
	int totalNegTerms = 0;

	int totalReviews = 0;

	public Positive_Rank() {		
		sort_unigram = new ArrayList<Unigram>();

		unigram_pos = new HashMap<String, Unigram>();
		unigram_neg = new HashMap<String, Unigram>();
		unigram_stats = new HashMap<String, Unigram>();

		features = new ArrayList<String>();
		m_reviews = new ArrayList<Post>();
	}
	
	public JSONObject loadJson(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "UTF-8"));
			StringBuffer buffer = new StringBuffer(1024);
			String line;

			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();

			return new JSONObject(buffer.toString());
		} catch (IOException e) {
			System.err.format("[Error]Failed to open file %s!", filename);
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			System.err.format("[Error]Failed to parse json file %s!", filename);
			e.printStackTrace();
			return null;
		}
	}
	
	public void readFeatures() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("data/features.txt"), "UTF-8"));
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] tokens = line.split("\\s+");
					features.add(tokens[1]);
					unigram_pos.put(tokens[1], new Unigram(tokens[1]));
					unigram_neg.put(tokens[1], new Unigram(tokens[1]));
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.format("[Error]Failed to open file %s!!",
					"data/Features_temp.txt");
		}
	}
	
	// Load files from directory
	public void loadDirectory(String folder, String suffix) {
		int count = 0;
		File dir = new File(folder);
		int size = m_reviews.size();
		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().endsWith(suffix)) {
				processReview(loadJson(f.getAbsolutePath()));
				count++;
				if (count % 10 == 0) {
					System.out.println(count);
				}
			} else if (f.isDirectory())
				loadDirectory(f.getAbsolutePath(), suffix);
		}
		size = m_reviews.size() - size;
		System.out.println("Loading " + size + " review documents from "
				+ folder);
	}

	// Pull a single review from file, place into a Post
	public void processReview(JSONObject json) {
		try {
			JSONArray jarray = json.getJSONArray("Reviews");
			for (int i = 0; i < jarray.length(); i++) {
				Post review = new Post(jarray.getJSONObject(i));
				determinePos(review);
				findDFTF(review);
				m_reviews.add(review);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Classify if post is positive/negative
	public void determinePos(Post review) {
		if (review.getRating() <= 3) {
			review.setPositive(false);
			negReviews++;
		} else {
			posReviews++;
		}
	}

	// Extract DFs and TFs of unigrams in reviews
	public void findDFTF(Post review) {
		
		HashSet<String> unique = new HashSet<String>();
		Analyzer_Unigram analyzer = new Analyzer_Unigram();
		String str = review.getContent();
		try {
			TokenStream ts = analyzer.tokenStream("content", new StringReader(
					str));
			CharTermAttribute charTermAttribute = ts
					.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				String token = charTermAttribute.toString();
				// DF
				unique.add(token);

				for (String key : unique) {
					if (unigram_pos.containsKey(key)) {
						if (review.isPositive()) {
							unigram_pos.get(key).addDF(1);
							unigram_pos.get(key).addReview(review.getID());
						} else {
							unigram_neg.get(key).addDF(1);
							unigram_neg.get(key).addReview(review.getID());
						}
					}
				}
			}
			ts.end();
			ts.close();

			// TF
			ts = analyzer.tokenStream("content", new StringReader(str));
			ts.reset();
			while (ts.incrementToken()) {
				String token = charTermAttribute.toString();
				if (unigram_pos.containsKey(token)) {
					if (review.isPositive()) {
						unigram_pos.get(token).addTF(1);
						totalPosTerms++;
					} else {
						unigram_neg.get(token).addTF(1);
						totalNegTerms++;
					}
				}
			}
			ts.end();
			ts.close();

		} catch (Exception e) {
		}
		analyzer.close();
		
		m_reviews.add(review);
	}

	public void smoothedLM() {
		for (Unigram u : unigram_pos.values()) {
			double prob = (u.getTF() + 0.1)
					/ (totalPosTerms + 0.1 * u.getDF());
			u.setProbPos(prob);
		}
		for (Unigram u : unigram_neg.values()) {
			double prob = (u.getTF() + 0.1)
					/ (totalNegTerms + 0.1 * u.getDF());
			u.setProbNeg(prob);
		}
	}

	public void posteriorEstimator() {
		for (Unigram u1 : unigram_pos.values()) {
			for (Unigram u2 : unigram_neg.values()) {
				if (u1.getFirst().equals(u2.getFirst())) {
					u1.setProbRatio(Math.log(u1.getProbPos()
							/ u2.getProbNeg()));
				}
			}
		}
	}
	
	public void writePosRank() {
		for (String key : unigram_pos.keySet()) {
			sort_unigram.add(unigram_pos.get(key));
		}

		Collections.sort(sort_unigram, new Comparator<Unigram>() {
			public int compare(Unigram t1, Unigram t2) {
				return Double.compare(t2.getProbRatio(), t1.getProbRatio());
			}
		});

		try {
			File file = new File("data/positive_rank.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			int count = 1;
			for (Unigram u : sort_unigram) {
				u.setId_num(count);
				count++;
				bw.write(u.getId_num() + " " + u.getProbRatio() + " "
						+ u.getFirst() + "\n");
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Positive_Rank analyzer = new Positive_Rank();

		// Load features
		analyzer.readFeatures();
		// Separate TF/DF by pos/neg docs
		analyzer.loadDirectory("./data/test", ".json");
		// Generate language model
		analyzer.smoothedLM();
		// Determine positivty of features
		analyzer.posteriorEstimator();
		// Write to file
		analyzer.writePosRank();

	}
}
