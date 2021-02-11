import java.io.IOException;
import java.nio.file.Path;

/**
 * @author geoaldana
 *
 */
public class MultiThreadedInvertedIndexBuilder extends InvertedIndexBuilder {

	/**
	 * ThreadSafeInvertedIndex index
	 */
	private final ThreadSafeInvertedIndex data;

	/**
	 * WorkQueue queue
	 */
	private final WorkQueue queue;

	/**
	 * @param index that will be used to build index
	 * @param queue that will be used to build index
	 */
	public MultiThreadedInvertedIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		super(index);
		this.data = index;
		this.queue = queue;
	}

	@Override
	public void buildIndex(Path directory) throws IOException {
		super.buildIndex(directory);
		queue.finish();
	}

	@Override
	public void read(Path path) throws IOException {
		queue.execute(new Task(path));
	}

	/**
	 * @author geoaldana
	 * This is a task that implements Runnable and runs a task for the MultiThreadedInvertedIndexBuilder.
	 */
	private class Task implements Runnable {
		/**
		 * the path that the read function will read
		 */
		private final Path path;

		/**
		 * @param path that the read function will read
		 */
		public Task(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			try {
				InvertedIndexBuilder.read(path, local);
				data.merge(local);
			} catch (IOException e) {
				System.out.println("Not able to read the path.");
			}
		}
	}
}
