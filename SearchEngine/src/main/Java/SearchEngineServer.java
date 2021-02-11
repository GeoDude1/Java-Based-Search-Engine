import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author geoaldana
 * Server that will be used to carry out tasks needed for the search engine.
 */
public class SearchEngineServer {

	/**
	 * Index that will be used to do the searches
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * PORT that will be used to start the server.
	 */
	private final int PORT;

	/**
	 * @param index used to search
	 * @param port to start server
	 */
	public SearchEngineServer(ThreadSafeInvertedIndex index, int port) {
		this.index = index;
		this.PORT = port;
	}

	/**
	 * @param port to start server
	 * @throws Exception thrown if any errors occur
	 */
	public void StartSearchEngineServer(int port) throws Exception {
		// create the jetty server
		Server server = new Server(PORT);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet()), "/");
		handler.addServletWithMapping(new ServletHolder(new SearchEngineResultServlet(index)), "/resultspage");
		server.setHandler(handler);
		server.start();
		server.join();	
	}
}
