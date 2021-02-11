import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author geoaldana
 *
 */
public class MultiThreadedQueryBuilder implements QueryBuilderInterface {

	/**
	 * WorkQueue that will be used
	 */
	private final WorkQueue queue;

	/**
	 * ThreadSafeInvertedIndex that will be used
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * This is a data structure that will hold the word query and the results that
	 * match with it.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> queryMap;

	/**
	 * @param index ThreadSafeInvertedIndex
	 * @param queue WorkQueue
	 */
	public MultiThreadedQueryBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
		this.queryMap = new TreeMap<>();
	}

	/**
	 * This is a function that basically reads a path that either originates from
	 * the function path checker or a path that is just a file. Parsing of the words
	 * is done here.
	 *
	 * @param path  the path that the read function will read
	 * @param exact boolean that checks if exact is called
	 * @throws IOException if an IO error occurs
	 */
	public void queryReader(Path path, boolean exact) throws IOException {
		QueryBuilderInterface.super.queryReader(path, exact);
		queue.finish();
	}

	/**
	 * This is a function that parses the queries and puts the data in the queryMap.
	 * 
	 * @param queries queries that will be used
	 * @param exact boolean to find out what search to call
	 */
	public void queryParser(String queries, boolean exact) {
		queue.execute(new Task(queries, exact));
	}

	/**
	 * This is function that works the same way as writeFile but for the results
	 * flag. Calls asQuery function the word counts into pretty Json format.
	 * 
	 * @param path that is received from the argument
	 * @throws IOException if an IO error occurs
	 */
	public void queryWriter(Path path) throws IOException {
		synchronized (queryMap) {
			SimpleJsonWriter.asQuery(queryMap, path);
		}
	}

	/**
	 * @author geoaldana
	 * This is a task that implements Runnable and runs a task for the MultiThreadedQueryBuilder.
	 */
	private class Task implements Runnable {

		/**
		 * queries which for search
		 */
		private final String queries;

		/**
		 * exact search or partial
		 */
		private final boolean exact;

		/**
		 * Initial Task
		 * 
		 * @param queries that will be used
		 * @param exact boolean that checks if exact is called
		 */
		public Task(String queries, boolean exact) {
			this.queries = queries;
			this.exact = exact;
		}

		@Override
		public void run() {
			TreeSet<String> lines = TextFileStemmer.uniqueStems(queries);
			String queryFormat = String.join(" ", lines);
			synchronized (queryMap) {
				if (queryMap.containsKey(queryFormat) || lines.isEmpty()) {
					return;
				}
			}
			var local = index.search(lines, exact);
			synchronized (queryMap) {
				queryMap.put(queryFormat, local);
			}
		}
	}
}
