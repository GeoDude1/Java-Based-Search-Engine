import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		InvertedIndex data = null;
		InvertedIndexBuilder builder = null;
		ArgumentMap argumentMap = new ArgumentMap(args);
		QueryBuilderInterface queryMap = null;
		WorkQueue workQueue = null;
		SearchEngineServer Server = null;

		int threads;
		if (argumentMap.hasFlag("-threads")) {
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			data = threadSafeIndex;
			try {
				threads = Integer.parseInt(argumentMap.getString("-threads", "5"));
			} catch (NumberFormatException e) {
				return;
			}
			if (threads <= 0) {
				threads = 5;
			}
			workQueue = new WorkQueue(threads);
			builder = new MultiThreadedInvertedIndexBuilder(threadSafeIndex, workQueue);
			queryMap = new MultiThreadedQueryBuilder(threadSafeIndex, workQueue);

			//implement other flags here - same concept as threads
			if (argumentMap.hasFlag("-url")) {
				String seed = argumentMap.getString("-url");
				int max = 1;
				try {
					threads = Integer.parseInt(argumentMap.getString("-threads", "5"));
				} catch (NumberFormatException e) {
					return;
				}
				if (threads <= 0) {
					threads = 5;
				}
				data = threadSafeIndex;
				workQueue = new WorkQueue(threads);
				queryMap = new MultiThreadedQueryBuilder(threadSafeIndex, workQueue);
				if (argumentMap.hasFlag("-max")) {
					max = Integer.parseInt(argumentMap.getString("-max", "1"));
					if (max <= 0) {
						max = 1;
					}
				}
				WebCrawler crawler = new WebCrawler(workQueue, data, max);
				crawler.buildWebCrawler(seed);
			}

			if (argumentMap.hasFlag("-server")) {
				int port = argumentMap.getInteger("-server", 8080);
				Server = new SearchEngineServer(threadSafeIndex, port);
				try {
					Server.StartSearchEngineServer(port);
				} catch (Exception e) {
					System.out.println("Unable to start the server with the port given: " + port);
				}
			}
		}

		//implement server flag and make sure to use multithreading
		else {
			data = new InvertedIndex();
			builder = new InvertedIndexBuilder(data);
			queryMap = new QueryBuilder(data);
		}

		// check if flag has -path
		if (argumentMap.hasValue("-path")) {
			Path path = argumentMap.getPath("-path");
			try {
				builder.buildIndex(path);
			} catch (Exception e) {
				System.out.println("Unable to build the inverted index from path: " + path);
			}
		}

		// check if flag has query
		if (argumentMap.hasFlag("-queries")) {
			Path queryPath = argumentMap.getPath("-queries");
			try {
				queryMap.queryReader(queryPath, argumentMap.hasFlag("-exact"));
			} catch (Exception e) {
				System.out.println("Unable to parse the queries given : " + queryPath);
			}
		}

		// check if flag has index
		if (argumentMap.hasFlag("-index")) {
			// getPath helps set default value / Path.of helps with that
			Path nameOfFile = argumentMap.getPath("-index", Path.of("index.json"));
			try {
				data.writeFile(nameOfFile);
			} catch (Exception e) {
				System.out.println("There was an error writing to the .json file given : " + nameOfFile);
			}
		}

		// check if flag has counts
		if (argumentMap.hasFlag("-counts")) {
			Path countsPath = argumentMap.getPath("-counts", Path.of("counts.json"));
			try {
				data.countsWriter(countsPath);
			} catch (Exception e) {
				System.out.println("There was an error writing to the .json file given : " + countsPath);
			}
		}

		// check if flag has results
		if (argumentMap.hasFlag("-results")) {
			Path resultFile = argumentMap.getPath("-results", Path.of("results.json"));
			try {
				queryMap.queryWriter(resultFile);
			} catch (Exception e) {
				System.out.println("There was an error writing to the .json file given: " + resultFile);
			}
		}
		if (workQueue != null) {
			workQueue.shutdown();
		}
	}
}
