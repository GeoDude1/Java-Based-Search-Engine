import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author geoaldana
 * This is a class that builds the index from a seed URL with a work queue.
 */
public class WebCrawler {

	/**
	 * Work Queue that will be used
	 */
	private final WorkQueue queue;

	/**
	 * InvertedIndex that will be used
	 */
	private final InvertedIndex index;

	/**
	 * HashSet that will store the links
	 */
	private HashSet<URL> WebLinks;

	/**
	 * max amount of URLs to crawl
	 */
	private final int max;

	/**
	 * @param queue that will be used
	 * @param index that will be accessed
	 * @param max amount of URLs
	 */
	public WebCrawler (WorkQueue queue, InvertedIndex index, int max) {
		this.queue = queue;
		this.index = index;
		this.max = max;
		WebLinks = new HashSet<URL>();
	}

	/**
	 * @param seed URL that the web crawler will use to build the index
	 */
	public void buildWebCrawler(String seed) {
		URL url;
		try {
			url = LinkParser.normalize(new URL(seed));
			WebLinks.add(url);
			queue.execute(new Task(url));
			queue.finish();
		} catch (MalformedURLException e) {
			System.out.println("There was an error that prevented the building of the index.");
		} catch (URISyntaxException e) {
			System.out.println("There was an error that prevented the building of the index.");
		}
	}

	/**
	 * @author geoaldana
	 * This is a task that implements Runnable and runs a task for the WebCrawler.
	 */
	private class Task implements Runnable {

		/**
		 * url that will be used
		 */
		private final URL url;

		/**
		 * @param url that will be used
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			String html = HtmlFetcher.fetch(url, 3);
			String cleaned = HtmlCleaner.stripBlockElements(html);
			html = HtmlCleaner.stripHtml(html);
			synchronized(WebLinks) {
				ArrayList<URL> URLs = LinkParser.getValidLinks(url, cleaned);
				for (URL URL : URLs) {
					if (WebLinks.size() >= max) {
						break;
					}
					else if (!WebLinks.contains(URL)) {
						WebLinks.add(URL);
						queue.execute(new Task(URL));
					}
				}
				Stemmer stemmer = new SnowballStemmer(TextFileStemmer.DEFAULT);
				int i = 1;
				String[] wordsHTML = TextParser.parse(html);
				for (String word: wordsHTML) {
					String stemmedWord = stemmer.stem(word).toString();
					index.add(stemmedWord, url.toString(), i++);
				}
			}
		}
	}
}
