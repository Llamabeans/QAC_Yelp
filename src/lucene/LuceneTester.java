package lucene;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import structures.Post;

public class LuceneTester {

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

	// Pull a single review out of a json file.
	public Post extractPost(int index, JSONObject json) {
		try {
			JSONArray jarray = json.getJSONArray("Reviews");
			return new Post(jarray.getJSONObject(index));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Analyzer_NGrams analyzer2 = new Analyzer_NGrams();
		Analyzer_Unigram token = new Analyzer_Unigram();

		String str = "disgusting choices will end up being awful decisions";

		try {
			TokenStream ts = analyzer2
					.tokenStream("content", new StringReader(str));
			CharTermAttribute charTermAttribute = ts
					.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				String term = charTermAttribute.toString();
				System.out.println(term);
			}
			ts.end();
			ts.close();

		} catch (Exception e) {

		}

		token.close();
		analyzer2.close();

	}

}