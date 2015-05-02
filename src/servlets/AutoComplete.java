package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AutoComplete")
public class AutoComplete extends HttpServlet {
	private static final long serialVersionUID = 1L;


	public AutoComplete() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("blah");
		
		/*
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>");
		*/
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	public void init() throws ServletException {
	}

}
