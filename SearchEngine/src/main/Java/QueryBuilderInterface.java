import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author geoaldana
 * This is an interface that contains methods that are used by the QueryBuilder and the MultiThreadedQueryBuilder.
 */
public interface QueryBuilderInterface {

	/**
	 * This is a function that basically reads a path that either originates from
	 * the function path checker or a path that is just a file. Parsing of the words
	 * is done here.
	 *
	 * @param path  the path that the read function will read
	 * @param exact boolean that checks if exact is called
	 * @throws IOException if an IO error occurs
	 */
	public default void queryReader(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String queries;
			while ((queries = reader.readLine()) != null) {
				queryParser(queries, exact);
			}
		}
	}
	
	/**
	 * This is a function that parses the queries and puts them in proper format.
	 * 
	 * @param queries queries that will be parsed
	 * @param exact flag to be called
	 */
	public void queryParser(String queries, boolean exact);

	/**
	 * This is a function that outputs the query results in Json format.
	 * 
	 * @param path that will be used
	 * @throws IOException if an IO error occurs
	 */
	public void queryWriter(Path path) throws IOException;
}
