/**
 * 
 */
package analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import structures.Bigram;
import structures.Post;
import structures.Token;
import structures.Trigram;

public class DocAnalyzer_Part1 {

	Tokenizer tokenizer;

	ArrayList<Bigram> sort_bigram;
	ArrayList<Trigram> sort_trigram;

	HashMap<String, Bigram> tf_bigram;
	HashMap<String, Trigram> tf_trigram;
	ArrayList<Post> m_reviews;

	int totalBigrams = 0;
	int totalTrigrams = 0;
	int totalReviews = 0;

	public DocAnalyzer_Part1() {
		sort_bigram = new ArrayList<Bigram>();
		sort_trigram = new ArrayList<Trigram>();

		tf_bigram = new HashMap<String, Bigram>();
		tf_trigram = new HashMap<String, Trigram>();

		m_reviews = new ArrayList<Post>();

		try {
			tokenizer = new TokenizerME(new TokenizerModel(new FileInputStream(
					"./data/Model/en-token.bin")));
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Test to tokenizer
	public void TokenizerDemon(String text) {
		for (String token : tokenizer.tokenize(text)) {
			System.out.println(normalize(token));
		}
	}

	// Text Normalization
	public String normalize(String token) {
		try {
			Double.valueOf(token);
			token = "NUM";
			return token;
		} catch (NumberFormatException e) {
		}
		token = token.replaceAll("[^a-zA-Z0-9]", " ");
		token = token.toLowerCase();
		return token;
	}

	// Snowball stemmer
	public String snowballStem(String token) {
		SnowballStemmer stemmer = new englishStemmer();
		stemmer.setCurrent(token);
		if (stemmer.stem())
			return stemmer.getCurrent();
		else
			return token;
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
		String first = "";
		String second = "";
		for (String token : tokenizer.tokenize(normalize(review.getContent()))) {
			token = normalize(token);
			if (token == "" || token.equals("eos")) {
				continue;
			}
			token = snowballStem(token);

			// Bigram TF
			if (first != "") {
				totalBigrams++;
				String bigram = first + "-" + token;
				if (tf_bigram.containsKey(bigram)) {
					tf_bigram.get(bigram).addTF(1);
				} else {
					tf_bigram.put(bigram, new Bigram(first, token));
				}

				// Trigram TF
				if (second != "") {
					totalTrigrams++;
					String trigram = first + "-" + second + "-" + token;
					if (tf_trigram.containsKey(trigram)) {
						tf_trigram.get(trigram).addTF(1);
					} else {
						tf_trigram.put(trigram, new Trigram(first, second,
								token));
					}
				}
			}
			first = second;
			second = token;
		}
	}
	
	public void findProbabilities() {
		for (String key : tf_bigram.keySet()) {
			tf_bigram.get(key).setProb((double)tf_bigram.get(key).getTF()/totalBigrams);
			sort_bigram.add(tf_bigram.get(key));
		}
		for (String key : tf_trigram.keySet()) {
			tf_trigram.get(key).setProb((double)tf_trigram.get(key).getTF()/totalTrigrams);
			sort_trigram.add(tf_trigram.get(key));
		}
	}

	public void WriteToFile() {
		// BIGRAMS
		Collections.sort(sort_bigram, new Comparator<Bigram>() {
			public int compare(Bigram t1, Bigram t2) {
				return t2.getTF() - t1.getTF();
			}
		});

		try {
			File file = new File("data/bigram_vocab.csv");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			int count = 1;
			for (Bigram b : sort_bigram) {
				b.setId_num(count);

				if (b.getTF() > 5) {
					bw.write(count + "," + b.getProb() + "," + b.getFirst() + ","
							+ b.getSecond());
					bw.write("\n");
				}
				count++;
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
					bw.write(count + "," + t.getProb() + "," + t.getFirst() + ","
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
		DocAnalyzer_Part1 analyzer = new DocAnalyzer_Part1();
		
		analyzer.loadDirectory("./data/1", ".json");
		analyzer.findProbabilities();
		analyzer.WriteToFile();

	}
	
}
