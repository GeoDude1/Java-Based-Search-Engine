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

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author geoaldana
 * Servlet that displays an html for the search engine. Contain's buttons that do specific tasks.
 */
public class SearchEngineServlet extends HttpServlet {

	/**
	 * default serial Version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * html template that will be used
	 */
	private final String SearchEngineTemplate;

	/**
	 * title of the webpage
	 */
	private final String TITLE = "Geo's Search Engine";

	/**
	 * data structure that will hold messages - can not use concurrent
	 */
	private final ArrayList<String> messages;

	/**
	 * Servlet responsible for search engine tasks
	 * @throws IOException that will be thrown if error occurs
	 */
	public SearchEngineServlet() throws IOException {
		super();
		messages = new ArrayList<>();
		SearchEngineTemplate = Files.readString(Path.of("html", "index.html"), StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());

		// setup form
		values.put("method", "POST");
		values.put("action", "/resultspage");
		values.put("timestamp", getDate());

		values.put("messages", String.join("\n\n", messages));

		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(SearchEngineTemplate);

		PrintWriter out = response.getWriter();

		out.println(html);

		out.flush();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String name = request.getParameter("name");
		// avoid xss attacks using apache commons text
		// comment out if you don't have this library installed
		name = StringEscapeUtils.escapeHtml4(name);
		Map<String, String> queries = new HashMap<>();
		queries.put("query", name);

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
