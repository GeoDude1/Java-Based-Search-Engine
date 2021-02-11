import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author geoaldana
 * 
 *         This class deals with the queries that will be used to search for any
 *         matches in the InvertedIndex. This is where the queries are read,
 *         parsed, and stemmed from the location given. In addition, this is
 *         where function is called to write the results in pretty Json format.
 */

public class QueryBuilder implements QueryBuilderInterface { 

	/**
	 * This is a data structure that will hold the word query and the results that
	 * match with it.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> queryMap;


	/**
	 * This called InvertedIndex so it can be accessed when calling query functions.
	 */
	private final InvertedIndex data;

	/**
	 * Initializes the argument map.
	 * 
	 * @param data InvertedIndex
	 */
	public QueryBuilder(InvertedIndex data) {
		this.queryMap = new TreeMap<>();
		this.data = data;
	}

	@Override
	public void queryParser(String queries, boolean exact) {
		TreeSet<String> lines = TextFileStemmer.uniqueStems(queries);
		String queryFormat = String.join(" ", lines);
		if ((!queryMap.containsKey(queryFormat)) && (!lines.isEmpty())) {
			queryMap.put(queryFormat, data.search(lines, exact));
		}
	}

	@Override
	public void queryWriter(Path path) throws IOException {
		SimpleJsonWriter.asQuery(queryMap, path);
	}
}
