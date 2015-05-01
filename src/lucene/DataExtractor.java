package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import structures.Post;


public class DataExtractor {
	
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
	}

	// Pull a single review from file, place into a Post
	public void processReview(JSONObject json) {
		try {
			JSONArray jarray = json.getJSONArray("Reviews");
			for (int i = 0; i < jarray.length(); i++) {
				Post review = new Post(jarray.getJSONObject(i));

			}
		} catch (JSONException e) {
			e.printStackTrace();
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
	
	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException 
	{
		  Document doc = new Document();
		  // A text field will be tokenized
		  doc.add(new TextField("title", title, Field.Store.YES));
		  // We use a string field for isbn because we don\'t want it tokenized
		  doc.add(new StringField("isbn", isbn, Field.Store.YES));
		  w.addDocument(doc);
	}
	
	public static void main(String[] args)
	{
		Analyzer analyzer = new StandardAnalyzer();
		DataExtractor dumb = new DataExtractor();
		
		Analyzer1 analyzer2 = new Analyzer1();
		Analyzer_Unigram token = new Analyzer_Unigram();
		
		String str = "disgusting";
		
		
		try {
			TokenStream ts = token.tokenStream("content", new StringReader(str));
			CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
			    String term = charTermAttribute.toString();
			    System.out.println(term);
			}
			ts.end();
			ts.close();
			
			ts = token.tokenStream("content", new StringReader(str));
			ts.reset();
			while (ts.incrementToken()) {
			    String term = charTermAttribute.toString();
			    System.out.println(term);
			}
			
			
			ts.end();
			ts.close();
			
		} catch (Exception e){
			
		}
		
		token.close();
		
		// code referenced
		try
		{
			//	Specify the analyzer for tokenizing text.
		    //	The same analyzer should be used for indexing and searching
			
			//	Code to create the index
			Directory index = new RAMDirectory();
			
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			IndexWriter w = new IndexWriter(index, config);
			addDoc(w, "Lucene in Action", "193398817");
			addDoc(w, "Lucene for Dummies", "55320055Z");
			addDoc(w, "Managing Gigabytes", "55063554A");
			addDoc(w, "The Art of Computer Science", "9900333X");
			addDoc(w, "My name is teja", "12842d99");
			addDoc(w, "Lucene demo by teja", "23k43413");
			w.close();
			
			//	Text to search
			String querystr = args.length > 0 ? args[0] : "teja";
			
			//	The "title" arg specifies the default field to use when no field is explicitly specified in the query
			Query q = new QueryParser("title", analyzer).parse(querystr);
			// Searching code
			int hitsPerPage = 10;
		    IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		    searcher.search(q, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    //	Code to display the results of search
		    System.out.println("Found " + hits.length + " hits.");
		    for(int i=0;i<hits.length;++i) 
		    {
		      int docId = hits[i].doc;
		      Document d = searcher.doc(docId);
		      System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
		    }
		    
		    // reader can only be closed when there is no need to access the documents any more
		    reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}


}