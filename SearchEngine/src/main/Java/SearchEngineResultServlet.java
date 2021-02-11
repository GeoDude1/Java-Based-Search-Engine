import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;


/**
 * @author geoaldana
 * This class generates the results page for the search engine.
 */
public class SearchEngineResultServlet extends HttpServlet {

	/**
	 * default serial Version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * html template that will be used
	 */
	private final String resultTemplate;

	/**
	 * title of the webpage
	 */
	private final String TITLE = "Geo's Search Engine";

	/**
	 * data structure that will hold results
	 */
	private ArrayList<InvertedIndex.SearchResult> searchresults;

	/**
	 * String that will hold whatever is search
	 */
	private String name;

	/**
	 * index that will be used to call the search
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * @param index that will be used to call search
	 * @throws IOException if error occurs
	 */
	public SearchEngineResultServlet(ThreadSafeInvertedIndex index) throws IOException {
		super();
		this.index = index;
		resultTemplate = Files.readString(Path.of("html", "result.html"), StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);

		values.put("method", "POST");
		values.put("action", "/");
		values.put("query", name);
		values.put("timestamp", getDate());

		StringSubstitutor replacer = new StringSubstitutor(values);
		String foot = replacer.replace(resultTemplate);

		PrintWriter out = response.getWriter();
		for (InvertedIndex.SearchResult results : searchresults) {
			out.println(String.format("<p><a href=\"%s\">%s</a></p>", results.getWhere(), results.getWhere()));
		}
		out.println(foot);
		out.flush();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		name = request.getParameter("name");
		name = StringEscapeUtils.escapeHtml4(name);
		TreeSet<String> queries = new TreeSet<String>();
		queries = TextFileStemmer.uniqueStems(name);

		// check if partial
		if (request.getParameter("Partial") != null) {
			searchresults = index.search(queries, false);
		}

		// check if exact
		if (request.getParameter("Exact") != null) {
			searchresults = index.search(queries, true);
		}
		response.setContentType("text/html");

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}
