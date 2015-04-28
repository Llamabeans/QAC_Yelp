/**
 * 
 */
package analyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import json.JSONException;
import json.JSONObject;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class Analyzer {

	Tokenizer tokenizer;

	public Analyzer() {
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

	// Text Normalization
	public String normalize(String Unigram) {
		try {
			Double.valueOf(Unigram);
			Unigram = "NUM";
			return Unigram;
		} catch (NumberFormatException e) {
		}
		Unigram = Unigram.replaceAll("[^a-zA-Z0-9]", " ");
		Unigram = Unigram.toLowerCase();
		return Unigram;
	}

	// Snowball stemmer
	public String snowballStem(String Unigram) {
		SnowballStemmer stemmer = new englishStemmer();
		stemmer.setCurrent(Unigram);
		if (stemmer.stem())
			return stemmer.getCurrent();
		else
			return Unigram;
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
}
