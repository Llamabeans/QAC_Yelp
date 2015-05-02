package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lucene.BigramAutoSuggester;

import org.apache.lucene.search.suggest.Lookup.LookupResult;

import structures.Bigram;

import com.google.gson.Gson;

@WebServlet("/AutoComplete")
public class AutoCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ArrayList<Bigram> bigrams = new ArrayList<Bigram>();

	public AutoCompleteServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String query = String.valueOf(request.getParameter("input"));
		
		String[] tokens = query.split("\\s+");
		String last = tokens[tokens.length-1];
		
		System.out.println("Last token: " + last);
		System.out.println("Size of token: " + tokens.length);
		System.out.println("Size of bigrams: " + bigrams.size());
		
		BigramAutoSuggester bigram_suggester = new BigramAutoSuggester(bigrams);
		
		
		// String term = request.getParameter("term");
		List<LookupResult> lookupResult = bigram_suggester.search(last, 5);

		List<String> returnList = new ArrayList<String>();
		for (LookupResult result : lookupResult) {
			returnList.add(result.key.toString());
		}

		String blah = new Gson().toJson(returnList);
		response.getWriter().write(blah);

	}

	public void init() throws ServletException {
		ServletContext context = getServletContext();

		/*
		 * try { InputStream is = context
		 * .getResourceAsStream("/WEB-INF/positive_rank.txt");
		 * 
		 * if (is != null) { InputStreamReader isr = new InputStreamReader(is);
		 * BufferedReader reader = new BufferedReader(isr); String line; while
		 * ((line = reader.readLine()) != null) { if (!line.isEmpty()) {
		 * String[] tokens = line.split("\\s+"); features.add(new
		 * Unigram(tokens[2], Double .parseDouble(tokens[1]) + 10)); } }
		 * reader.close(); }
		 * 
		 * } catch (IOException e) {
		 * System.err.format("[Error]Failed to open file %s!!",
		 * "data/positive_rank.txt"); }
		 */
		try {
			InputStream is = context
					.getResourceAsStream("/WEB-INF/bigram_vocab.txt");

			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader reader = new BufferedReader(isr);
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.isEmpty()) {
						String[] tokens = line.split("\\s+");
						String term = tokens[1] + " " + tokens[2];
						bigrams.add(new Bigram(term, Integer.parseInt(tokens[0])));
					}
				}
				reader.close();
			}

		} catch (IOException e) {
			System.err.format("[Error]Failed to open file %s!!",
					"data/positive_rank.txt");
		}

	}
}
