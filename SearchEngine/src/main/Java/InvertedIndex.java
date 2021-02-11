import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author geoaldana
 *
 *         This Inverted Index class has functions that deal with the following
 *         tasks needed for Project 1: An add function that adds to the nested
 *         data structure. A writeFile function that outputs data to the given
 *         or default file. This class basically helps with the building of an
 *         in-memory inverted index to store the mapping from word stems to the
 *         documents and position within those documents where those word stems
 *         were found.
 */
public class InvertedIndex {

	/**
	 * This is a nested data structure that will store the word, file, and location
	 * from the file given.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;

	/**
	 * This is a data structure that will store the word counts of the file.
	 */
	private final TreeMap<String, Integer> wordCount;

	/**
	 * Constructor
	 */
	public InvertedIndex() {

		/**
		 * This initializes the TreeMap.
		 */
		this.map = new TreeMap<>();
		this.wordCount = new TreeMap<>();
	}

	/**
	 * This is an add function that adds to the nested data structure. word = string
	 * in TreeMap / path = string in TreeMap / position = integer in Treeset The
	 * words are stemmed here with uniqueStems from TextFileStemmer.
	 *
	 * @param word     the word that will be added to the Inverted Index
	 * @param path     the path that will be added to the Inverted Index
	 * @param position the position that will be added to the Inverted Index
	 */
	public void add(String word, String path, int position) {
		// if does not contain word
		map.putIfAbsent(word, new TreeMap<>());
		// if does not contain word and path
		map.get(word).putIfAbsent(path, new TreeSet<>());
		// adds data to data structure
		// adds count to counts data structure
		if (map.get(word).get(path).add(position)) {
			wordCount.put(path, Math.max(wordCount.getOrDefault(path, 0), position));
		}
	}


	/**
	 * This is function that works the same way as path checker but for the index
	 * flag. Calls asInverted function that will output the data into pretty Json
	 * format.
	 *
	 * @param path that is received from the argument
	 * @throws IOException if an IO error occurs
	 */
	public void writeFile(Path path) throws IOException {
		SimpleJsonWriter.asInverted(map, path);
	}

	/**
	 * This is function that works the same way as writeFile but for the counts
	 * flag. Calls asObject function the word counts into pretty Json format.
	 * 
	 * @param path that is received from the argument
	 * @throws IOException if an IO error occurs
	 */
	public void countsWriter(Path path) throws IOException {
		SimpleJsonWriter.asObject(wordCount, path);
	}

	/**
	 * Returns the word if the Inverted Index contains it
	 * 
	 * @param word the word that will be returned
	 * @return returns true if the word is there
	 */
	public boolean contains(String word) {
		return map.containsKey(word);
	}

	/**
	 * Returns the location if the Inverted Index contains it
	 * 
	 * @param word     the word that matches with the location
	 * @param location the location that will be returned
	 * @return returns true if the location is there
	 */
	public boolean contains(String word, String location) {
		return map.containsKey(word) && map.get(word).containsKey(location);
	}

	/**
	 * Returns the position if the Inverted Index contains that word
	 * 
	 * @param word     the word that matches with the position
	 * @param location the location that matches the position
	 * @param position the position that will be returned
	 * @return returns true if the position is there
	 */
	public boolean contains(String word, String location, int position) {
		return map.containsKey(word) && map.get(word).containsKey(location)
				&& map.get(word).get(location).contains(position);
	}

	/**
	 * Number of words that exist in the InvertedIndex
	 * 
	 * @return the number of words in the InvertedIndex
	 */
	public int words() {
		return map.size();
	}

	/**
	 * Number of paths in the the InvertedIndex
	 * 
	 * @param word word inside of the file that is read
	 * @return the number of paths associated with word in the InvertedIndex
	 */
	public int paths(String word) {
		if (contains(word)) {
			return map.get(word).size();
		} else {
			return 0;
		}
	}

	/**
	 * Number of positions in the the InvertedIndex
	 * 
	 * @param word word inside of the file that is read
	 * @param path path of the file that is read
	 * @return the number of paths associated with word in the InvertedIndex
	 */
	public int positions(String word, String path) {
		if (contains(word, path)) {
			return map.get(word).get(path).size();
		} else {
			return 0;
		}
	}

	/**
	 * Safely returns or accesses the word in the InvertedIndex
	 * 
	 * @return a set of the words in the map
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * Safely returns or accesses the location in the InvertedIndex
	 * 
	 * @param word the word that matches the location
	 * @return the set of locations
	 */
	public Set<String> getLocations(String word) {
		if (map.containsKey(word)) {
			return Collections.unmodifiableSet(map.get(word).keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * Safely returns or accesses the position in the InvertedIndex
	 * 
	 * @param word     the word that matches the position
	 * @param location the location that matches the position
	 * @return the set of positions
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (map.containsKey(word)) {
			if (map.get(word).containsKey(location)) {
				return Collections.unmodifiableSet(map.get(word).get(location));
			}
		}
		return Collections.emptySet();
	}

	/**
	 * This is a function that returns the wordCount of a specific location that is
	 * called upon.
	 * 
	 * @param location that wants the wordCount
	 * @return the word count of that location
	 */
	public int wordCount(String location) {
		return wordCount.get(location);
	}

	/**
	 * This is a function that deals with the results when exact or partial search
	 * is called. It sets the different result values which are the where, count,
	 * wordCounts, and score.
	 * 
	 * @param word    word that is being searched
	 * @param results search results
	 * @param lookup  to determine if search result is already in map
	 */
	private void resultHandler(String word, ArrayList<SearchResult> results, HashMap<String, SearchResult> lookup) {
		for (String location : map.get(word).keySet()) {
			// if the location is in the map, get the result and update its matches and score
			if (lookup.containsKey(location)) {
				lookup.get(location).updateResults(word);
			} else {
				if (!lookup.containsKey(location)) { 
					SearchResult newResult = new SearchResult(location);
					results.add(newResult);
					lookup.put(location, newResult);
				}
				lookup.get(location).updateResults(word);
			}
		}
	}

	/**
	 * This is a function that does an exact search when called. It works by
	 * checking to see if the inverted index contains an exact word or match that is
	 * being searched for. It puts the results of information in an Array List.
	 * 
	 * @param queries these are the queries that are used to search
	 * @return returns an ArrayList of results that will be output in Json format
	 */
	public ArrayList<SearchResult> exactSearch(Collection<String> queries) {
		HashMap<String, SearchResult> lookup = new HashMap<>();
		ArrayList<SearchResult> results = new ArrayList<>();
		for (String queryKey : queries) {
			if (map.containsKey(queryKey)) {
				resultHandler(queryKey, results, lookup);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * This is a function that does an partial search when called. It works by
	 * checking to see if the inverted index contains a word or match starts with
	 * the query word. It puts the results of information in an Array List.
	 * 
	 * @param queries these are the queries that are used to search
	 * @return returns an ArrayList of results that will be output in Json format
	 */
	public ArrayList<SearchResult> partialSearch(Collection<String> queries) {
		HashMap<String, SearchResult> lookup = new HashMap<>();
		ArrayList<SearchResult> results = new ArrayList<>();
		for (String queryKey : queries) {
			// used tailMap instead of get because instead of returning a stem that matches
			// it returns the whole word with it
			for (String word : map.tailMap(queryKey).keySet()) {
				if (word.startsWith(queryKey)) {
					resultHandler(word, results, lookup);
				}
				else break;
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * This is a function that checks to see if the exact flag is called. If yes
	 * then an exactSearch is called if not a partialSearch is called.
	 * 
	 * @param queries these are the queries that are used to search
	 * @param exact   this is a boolean that will be checked to determine what
	 *                search to do
	 * @return returns a search - either exactSearch or partialSearch
	 */
	public ArrayList<SearchResult> search(Collection<String> queries, boolean exact) {
		if (exact) {
			return exactSearch(queries);
		} else {
			return partialSearch(queries);
		}
	}

	@Override
	public String toString() {
		return map.toString();
	}

	/**
	 * This is a function that will merge the local data and the shared data together
	 * 
	 * @param local the local data that will be merged
	 */
	public void merge(InvertedIndex local) {
		for (String word : local.map.keySet()) {
			if (!map.containsKey(word)) {
				this.map.put(word, local.map.get(word));
			}
			else {
				for (String path : local.map.get(word).keySet()) {
					if (!map.get(word).containsKey(path)) {
						map.get(word).put(path, local.map.get(word).get(path));
					}
					else {
						map.get(word).get(path).addAll(local.map.get(word).get(path));
					}
				}
			}
		}

		for (String path: local.wordCount.keySet()) {
			if (!this.wordCount.containsKey(path)) {
				this.wordCount.put(path, local.wordCount(path));
			}
			else {
				this.wordCount.put(path, Math.max(this.wordCount.get(path), local.wordCount.get(path)));
			}
		}
	}

	/**
	 * @author geoaldana This is a class that stores the search results and
	 *         implements the Comparable Interface. This is where the different
	 *         values for the search result are initialized.
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/**
		 * Initializes the location or where for result.
		 */
		private final String where;

		/**
		 * Initializes the number of matches or count for result.
		 */
		private int count;

		/**
		 * Initializes the score for result.
		 */
		private double score;

		/**
		 * @param where location
		 */
		public SearchResult(String where) {
			this.where = where;
		}

		/**
		 * @return the location
		 */
		public String getWhere() {
			return where;
		}

		/**
		 * @return the count or number of matches
		 */
		public int getCount() {
			return count;
		}

		/**
		 * @return the score calculated
		 */
		public double getScore() {
			return score;
		}

		/**
		 * @return the number of words in file
		 */
		public int getwordCounts() {
			return wordCount(where);
		}

		/**
		 * @param word that will be taken to initiate the updating of results
		 */
		private void updateResults(String word) {
			int newCount = map.get(word).get(where).size();
			count = count + newCount;
			int wordCounts = wordCount(where);
			score = (double) count / wordCounts;
		}

		@Override
		public int compareTo(SearchResult o) {
			int comparing = Double.compare(o.getScore(), getScore());
			if (comparing == 0) {
				comparing = Integer.compare(o.getCount(), getCount());
				if (comparing == 0) {
					comparing = getWhere().compareTo(o.getWhere());
				}
				return comparing;
			}
			return comparing;
		}
	}
}
