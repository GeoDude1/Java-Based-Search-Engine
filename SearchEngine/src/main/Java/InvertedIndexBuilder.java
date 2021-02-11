import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author geoaldana
 * 
 * This is a builder class that has functions that are required for
 * Project 1: A function that processes all text files in a directory
 * and its subdirectories. A read function that reads the given path.
 * This is also where the words/text are cleaned and parsed into word
 * stems.
 *
 */
public class InvertedIndexBuilder {

	/**
	 * Constructor for InvertedIndexBuilder
	 * @param index that it will use to build
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * Index that is initialized
	 */
	private final InvertedIndex index;

	/**
	 * This is a function that builds the inverted index by calling the pathChecker 
	 * function. It also checks for .md files by calling read directly if the description
	 * fits.
	 * 
	 * @param directory that is received from the argument
	 * @throws IOException if an IO error occurs
	 */
	public void buildIndex(Path directory) throws IOException {
		if (Files.isDirectory(directory)) {
			this.pathChecker(directory);
		}
		// reads any files like .md
		else {
			this.read(directory);
		}
	}

	/**
	 * This is function that checks whether or not a path that is passed is a
	 * directory or not. If it is a directory it checks the paths in the listing and
	 * it keeps on going if more subdirectories are there. It is basically the
	 * traverse directory example in the directory demo. If there are no more
	 * subdirectories then it checks if the file ends with .txt or .text. Then it
	 * calls the read function.
	 * 
	 * @param directory that is received from the argument
	 * @throws IOException if an IO error occurs
	 */
	private void pathChecker(Path directory) throws IOException {
		// if argument is directory
		String directoryHelper = directory.toString().toLowerCase();
		if (Files.isDirectory(directory)) {
			// initialize directory stream
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
				for (Path paths : listing)
					// calls itself till it gets the file
					pathChecker(paths);
			}
		} 
		else if (directoryHelper.endsWith(".txt") || directoryHelper.endsWith(".text")) {
			// if not directory
			read(directory);
		}
	}

	/** 
	 * This is a function that basically reads a path that either originates from
	 * the function path checker or a path that is just a file. Parsing of the words
	 * is done here.
	 * 
	 * @param path the path that the read function will read
	 * @throws IOException if an IO error occurs
	 */
	public void read(Path path) throws IOException {
		read(path, this.index);
	}

	/**
	 * This is a function that basically reads a path that either originates from
	 * the function path checker or a path that is just a file. Parsing of the words
	 * is done here.
	 * 
	 * @param path the path that the read function will read
	 * @param index to call InvertedIndex
	 * @throws IOException if an IO error occurs
	 */
	public static void read(Path path, InvertedIndex index) throws IOException {
		// calls to read the file/path
		try(BufferedReader reader = Files.newBufferedReader(path)){
			String line;
			// increment i for location or position of word - can not start in 0 for array
			int i = 1;
			Stemmer stemmer = new SnowballStemmer(TextFileStemmer.DEFAULT);
			String location = path.toString();
			while ((line = reader.readLine()) != null) {
				// parses words
				String[] wordsInLine = TextParser.parse(line);
				// for the words in the array they are added
				for (String word : wordsInLine) {
					index.add(stemmer.stem(word).toString(), location, i++);
				}
			}
		}
	}
}
