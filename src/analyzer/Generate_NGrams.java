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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import structures.Post;
import structures.Token;

public class Generate_NGrams {

	HashMap<String, Token> tf_tokens;
	ArrayList<Token> sort_tokens;
	ArrayList<Post> m_reviews;

	int totalReviews = 0;

	public Generate_NGrams() {
		tf_tokens = new HashMap<String, Token>();
		sort_tokens = new ArrayList<Token>();
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
				if (tf_tokens.containsKey(term)) {
					tf_tokens.get(term).addTF(1);
				} else {
					tf_tokens.put(term, new Token(term, 1));
				}
			}
			ts.end();
			ts.close();

		} catch (Exception e) {

		}
		analyzer.close();
	}

	public void WriteToFile() {
		for (Token t : tf_tokens.values()) {
			sort_tokens.add(t);
		}
		// BIGRAMS
		Collections.sort(sort_tokens, new Comparator<Token>() {
			public int compare(Token t1, Token t2) {
				return t2.getTF() - t1.getTF();
			}
		});

		try {
			File file = new File("data/trigram_vocab.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			for (Token t : sort_tokens) {
				if (t.getTF() > 50) {
					bw.write(t.getTF() + " " + t.getContent());
					bw.write("\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Generate_NGrams analyzer = new Generate_NGrams();

		analyzer.loadDirectory("./data/train", ".json");
		analyzer.WriteToFile();
		System.out.println(analyzer.tf_tokens.size());

	}

}
