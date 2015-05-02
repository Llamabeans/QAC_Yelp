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

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import lucene.Analyzer_NGrams;
import opennlp.tools.tokenize.Tokenizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import structures.Bigram;
import structures.Post;
import structures.Trigram;

public class Generate_NGrams {

	Tokenizer tokenizer;

	ArrayList<Bigram> sort_bigram;
	ArrayList<Trigram> sort_trigram;

	HashMap<String, Bigram> tf_bigram;
	HashMap<String, Trigram> tf_trigram;
	ArrayList<Post> m_reviews;

	int totalReviews = 0;

	public Generate_NGrams() {
		sort_bigram = new ArrayList<Bigram>();
		sort_trigram = new ArrayList<Trigram>();

		tf_bigram = new HashMap<String, Bigram>();
		tf_trigram = new HashMap<String, Trigram>();

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
				findTermFrequencies(review);

				m_reviews.add(review);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Calculate TFs of new bigrams and trigrams
	public void findTermFrequencies(Post review) {
		
		Analyzer_NGrams analyzer = new Analyzer_NGrams();
		String str = review.getContent();

		try {
			TokenStream ts = analyzer.tokenStream("content", new StringReader(
					str));
			CharTermAttribute charTermAttribute = ts
					.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				String term = charTermAttribute.toString();
				if (tf_bigram.containsKey(term)) {
					tf_bigram.get(term).addTF(1);
				} else {
					tf_bigram.put(term, new Bigram(term));
				}
			}
			ts.end();
			ts.close();

		} catch (Exception e) {

		}
		analyzer.close();
	}

	public void WriteToFile() {
		for (Bigram b : tf_bigram.values()) {
			sort_bigram.add(b);
		}
		// BIGRAMS
		Collections.sort(sort_bigram, new Comparator<Bigram>() {
			public int compare(Bigram t1, Bigram t2) {
				return t2.getTF() - t1.getTF();
			}
		});

		try {
			File file = new File("data/bigram_vocab.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			for (Bigram b : sort_bigram) {
				if (b.getTF() > 5) {
					bw.write(b.getTF() + " " + b.getFirst() + " "
							+ b.getSecond());
					bw.write("\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TRIGRAMS
		Collections.sort(sort_trigram, new Comparator<Trigram>() {
			public int compare(Trigram t1, Trigram t2) {
				return t2.getTF() - t1.getTF();
			}
		});

		try {
			File file = new File("data/trigram_vocab.csv");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			int count = 1;
			for (Trigram t : sort_trigram) {
				t.setId_num(count);

				if (t.getTF() > 5) {
					bw.write(count + "," + t.getTF() + "," + t.getFirst() + ","
							+ t.getSecond() + "," + t.getThird());
					bw.write("\n");
				}
				count++;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Generate_NGrams analyzer = new Generate_NGrams();

		analyzer.loadDirectory("./data/1", ".json");
		analyzer.WriteToFile();
		System.out.println(analyzer.tf_bigram.size());

	}

}
