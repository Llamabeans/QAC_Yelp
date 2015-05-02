package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lucene.AutoSuggester;

import org.apache.lucene.search.suggest.Lookup.LookupResult;

import structures.Unigram;

import com.google.gson.Gson;

@WebServlet("/AutoComplete")
public class AutoCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


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
		
		ArrayList<Unigram> subset = new ArrayList<Unigram>();
		subset.add(new Unigram("ba", 10));
		subset.add(new Unigram("ball", 50));
		subset.add(new Unigram("balloon", 20));
		subset.add(new Unigram("balls", 30));
		
		String query = String.valueOf(request.getParameter("input"));
		
		AutoSuggester suggester = new AutoSuggester(subset);
		// String term = request.getParameter("term");
		List<LookupResult> lookupResult = suggester.search(query, 5);
		
		List<String> returnList = new ArrayList<String>();
		for (LookupResult result : lookupResult) {
			returnList.add(result.key.toString());
		}
		
		String blah = new Gson().toJson(returnList);
		
		response.getWriter().write(blah);
		
		/*
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>");
		*/
	}

	public void init() throws ServletException {
	}

}