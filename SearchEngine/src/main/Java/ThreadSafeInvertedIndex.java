import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author geoaldana
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;

	/**
	 * Initializes a thread-safe indexed set.
	 */
	public ThreadSafeInvertedIndex() {
		// NOTE: DO NOT MODIFY THIS METHOD
		super();
		lock = new SimpleReadWriteLock();
	}

	/**
	 * Returns the identity hashcode of the lock object. Not particularly useful.
	 *
	 * @return the identity hashcode of the lock object
	 */
	public int lockCode() {
		// NOTE: DO NOT MODIFY THIS METHOD
		return System.identityHashCode(lock);
	}

	@Override
	public void add(String word, String path, int position) {
		lock.writeLock().lock();
		try {
			super.add(word, path, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void writeFile(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.writeFile(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void countsWriter(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.countsWriter(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();
		try {
			return super.contains(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.contains(word, location, position);
		} finally {
			lock.readLock().unlock();
		}
	}


	@Override
	public int words() {
		lock.readLock().lock();
		try {
			return super.words();
		} finally {
			lock.readLock().unlock();
		}
	}


	@Override
	public int paths(String word) {
		lock.readLock().lock();
		try {
			return super.paths(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int positions(String word, String path) {
		lock.readLock().lock();
		try {
			return super.positions(word, path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int wordCount(String location) {
		lock.readLock().lock();
		try {
			return super.wordCount(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<SearchResult> exactSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<SearchResult> partialSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void merge(InvertedIndex local) {
		lock.writeLock().lock();
		try {
			super.merge(local);
		} finally {
			lock.writeLock().unlock();
		}
	}
}
