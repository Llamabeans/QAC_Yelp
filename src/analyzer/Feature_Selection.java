package analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import lucene.Analyzer_Unigram;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import structures.Post;
import structures.Unigram;

public class Feature_Selection extends Analyzer {

	List<Unigram> sort_unigram;

	HashMap<String, Unigram> unigram_stats;

	List<String> features;
	List<Post> m_reviews;

	int totalReviews = 0;

	public Feature_Selection() {
		super();

		sort_unigram = new ArrayList<Unigram>();

		unigram_stats = new HashMap<String, Unigram>();

		features = new ArrayList<String>();
		m_reviews = new ArrayList<Post>();
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
		}
	}

	// Extract DFs and TFs of unigrams in reviews
	public void findDFTF(Post review) {
		HashMap<String, Unigram> unique = new HashMap<String, Unigram>();
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
				Unigram aToken = new Unigram(token);
				unique.put(token, aToken);

				for (String key : unique.keySet()) {
					if (unigram_stats.containsKey(key)) {
						unigram_stats.get(key).addDF(1);
					} else {
						unigram_stats.put(key, unique.get(key));
						unigram_stats.get(key).addDF(1);
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
				unigram_stats.get(token).addTF(1);
				unigram_stats.get(token).addReview(review.getID());
			}
			ts.end();
			ts.close();

		} catch (Exception e) {
		}
		analyzer.close();
	}

	public void filterDF() {
		for (Iterator<Map.Entry<String, Unigram>> it = unigram_stats.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, Unigram> entry = it.next();
			if (entry.getValue().getDF() < 50) {
				it.remove();
			}
		}
	}

	public void featureSelect() {
		int count = 0;
		for (Unigram t : unigram_stats.values()) {
			count++;
			System.out.println(count);

			// Entropy when token is present
			double A = 0.0; // Positive and T
			double B = 0.0; // Positive and not T
			double C = 0.0; // Negative and T
			double D = 0.0; // Negative and not T
			for (Post r : m_reviews) {
				if (t.getReview().contains(r.getID())) {
					if (r.isPositive()) {
						A++;
					} else {
						C++;
					}
				} else {
					if (r.isPositive()) {
						B++;
					} else {
						D++;
					}
				}
			}
			// Chi Squared
			double chi = ((A + B + C + D) * (A * D - B * C) * (A * D - B * C))
					/ ((A + C) * (B + D) * (A + B) * (C + D));
			t.setChi(chi);
		}
	}

	public void filterChi_Write() {

		for (Unigram u : unigram_stats.values()) {
			sort_unigram.add(u);
		}
		unigram_stats.clear();

		Collections.sort(sort_unigram, new Comparator<Unigram>() {
			public int compare(Unigram t1, Unigram t2) {
				return Double.compare(t2.getChi(), t1.getChi());
			}
		});

		try {
			sort_unigram = sort_unigram.subList(0, 5000);
		} catch (IndexOutOfBoundsException e) {
		}

		// Write to Features.txt
		try {
			File file = new File("data/features.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			int count = 1;
			for (Unigram t : sort_unigram) {
				bw.write(count + " " + t.getFirst() + "\n");
				count++;
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Feature_Selection analyzer = new Feature_Selection();

		// Load TF/DF/positivity of docs
		analyzer.loadDirectory("./data/test", ".json");
		// remove < 50 DF
		analyzer.filterDF();
		// Run chi squred on unigrams
		analyzer.featureSelect();
		// Write features for next stage
		analyzer.filterChi_Write();

	}
}
