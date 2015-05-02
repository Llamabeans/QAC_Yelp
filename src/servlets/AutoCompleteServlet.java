package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lucene.TokenAutoSuggester;

import org.apache.lucene.search.suggest.Lookup.LookupResult;

import structures.Token;

import com.google.gson.Gson;

@WebServlet("/AutoComplete")
public class AutoCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ArrayList<Token> tokens = new ArrayList<Token>();

	public AutoCompleteServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		String query = String.valueOf(request.getParameter("input"));

		String[] parse = query.split("\\s+");
		String last = "";
		if (parse.length > 1) {
			last = parse[parse.length - 2] + " " + parse[parse.length - 1];
		} else {
			last = parse[parse.length - 1];
		}

		System.out.println("Last token: " + last);
		System.out.println("Size of query: " + parse.length);
		System.out.println("Size of vocab: " + tokens.size());

		TokenAutoSuggester bigram_suggester = new TokenAutoSuggester(tokens);

		// String term = request.getParameter("term");
		List<LookupResult> lookupResult = bigram_suggester.search(last, 5);

		List<String> returnList = new ArrayList<String>();
		for (LookupResult result : lookupResult) {
			String trigram = result.key.toString();
			String[] last_only = trigram.split("\\s+");
			if (parse.length > 1) {
				returnList.add(last_only[1] + " " + last_only[2]);
			} else {
				returnList.add(trigram);
			}
		}

		// without ajax
		/*
		 * for (int i = 1; i <= lookupResult.size(); i++) {
		 * request.setAttribute("result"+i,
		 * lookupResult.get(i-1).key.toString()); }
		 * request.getRequestDispatcher("index.jsp").forward(request, response);
		 */

		String blah = new Gson().toJson(returnList);
		response.getWriter().write(blah);
		System.out.println(blah);

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
					.getResourceAsStream("/WEB-INF/trigram_vocab.txt");

			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader reader = new BufferedReader(isr);
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.isEmpty()) {
						String[] parse = line.split("\\s+");
						String term = parse[1] + " " + parse[2] + " "
								+ parse[3];
						tokens.add(new Token(term, Integer.parseInt(parse[0])));
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
