import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class SimpleJsonWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Integer> iterate = elements.iterator();
		writer.write("[\n");
		if (!iterate.hasNext()) {
			indent("]", writer, level);
		} else {
			indentAndQuote(iterate.next(), writer, level + 1);
		}
		while (iterate.hasNext()) {
			writer.write(",");
			writer.write('\n');
			indentAndQuote(iterate.next(), writer, level + 1);
		}
		writer.write('\n');
		indent("]", writer, level);
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		String element;
		Integer value;
		Iterator<String> setIterator = elements.keySet().iterator();
		indent("{", writer, level);
		writer.write('\n');
		if (!setIterator.hasNext()) {
			writer.write("}");
		} else {
			if (setIterator.hasNext()) {
				element = setIterator.next();
				value = elements.get(element);
				indentAndQuote(element, writer, level + 1);
				writer.write(": ");
				writer.write(value.toString());
			}
			while (setIterator.hasNext()) {
				writer.write(",");
				element = setIterator.next();
				writer.write('\n');
				value = elements.get(element);
				indentAndQuote(element, writer, level + 1);
				writer.write(": ");
				writer.write(value.toString());
			}
			writer.write('\n');
			indent("}", writer, level);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {

		String element;
		Iterator<String> setIterator = elements.keySet().iterator();
		indent("{", writer, level - 1);
		if (setIterator.hasNext()) {
			element = setIterator.next();
			writer.write('\n');
			indentAndQuote(element, writer, level + 1);
			writer.write(": ");
			asArray(elements.get(element), writer, level + 1);
		}
		while (setIterator.hasNext()) {
			writer.write(',');
			element = setIterator.next();
			writer.write('\n');
			indentAndQuote(element, writer, level + 1);
			writer.write(": ");
			asArray(elements.get(element), writer, level + 1);
		}
		writer.write('\n');
		indent("}", writer, level);
	}

	/**
	 * Indents using a tab character by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException if an IO error occurs
	 */
	public static void indentAndQuote(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the integer element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indentAndQuote(Writer, int)
	 */
	public static void indentAndQuote(Integer element, Writer writer, int times) throws IOException {
		indentAndQuote(writer, times);
		writer.write(element.toString());
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indentAndQuote(Writer, int)
	 */
	public static void indentAndQuote(String element, Writer writer, int times) throws IOException {
		indentAndQuote(writer, times);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the text element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indentAndQuote(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indentAndQuote(writer, times);
		writer.write(element);
	}

	/**
	 * Writes a map entry in pretty JSON format.
	 *
	 * @param entry  the nested entry to write
	 * @param writer the writer to use
	 * @param level  the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeEntry(Entry<String, Integer> entry, Writer writer, int level) throws IOException {
		writer.write('\n');
		indentAndQuote(entry.getKey(), writer, level);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	/*
	 * These methods are provided for you. No changes are required.
	 */

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file for InvertedIndex.
	 * 
	 * @param elements the elements to write
	 * @param path     the file path this is in the argument
	 * @throws IOException if an IO error occurs
	 * 
	 */
	public static void asInverted(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asInverted(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object for the InvertedIndex
	 * data structure.
	 * 
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 */
	public static void asInverted(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer, int level)
			throws IOException {

		Iterator<String> setIterator = elements.keySet().iterator();
		writer.write("{");
		if (setIterator.hasNext()) {
			writer.write("\n");
			String element = setIterator.next();
			indentAndQuote(element, writer, level);
			writer.write(": ");
			asNestedArray(elements.get(element), writer, level + 1);
		}
		while (setIterator.hasNext()) {
			writer.write(",\n");
			String element = setIterator.next();
			indentAndQuote(element, writer, level);
			writer.write(": ");
			asNestedArray(elements.get(element), writer, level + 1);
		}
		indent("\n}", writer, level);
	}

	/**
	 * Writes the elements as pretty JSON object to file for query results.
	 * 
	 * @param elements the elements to write
	 * @param path     the file path this is in the argument
	 * @throws IOException if an IO error occurs
	 */
	public static void asQuery(TreeMap<String, ArrayList<InvertedIndex.SearchResult>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asQuery(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as pretty JSON object for the queryMap data structure.
	 * 
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asQuery(TreeMap<String, ArrayList<InvertedIndex.SearchResult>> elements, Writer writer, int level)
			throws IOException {
		Iterator<String> setIterator = elements.keySet().iterator();
		writer.write("{");
		while (setIterator.hasNext()) {
			String element = setIterator.next();
			writer.write('\n');
			Iterator<InvertedIndex.SearchResult> resultIterator = elements.get(element).iterator();
			indentAndQuote(element, writer, level + 1);
			writer.write(": [");
			writer.write('\n');
			while (resultIterator.hasNext()) {
				InvertedIndex.SearchResult result = resultIterator.next();
				String where = result.getWhere();
				int count = result.getCount();
				String counts = String.valueOf(count);
				DecimalFormat df = new DecimalFormat("0.00000000");
				String score = df.format(result.getScore());
				indent("{\n", writer, level + 2);
				indent("\"where\": ", writer, level + 3);
				indentAndQuote(where, writer, level);
				writer.write(",\n");
				indent("\"count\": ", writer, level + 3);
				writer.write(counts);
				writer.write(",\n");
				indent("\"score\": ", writer, level + 3);
				writer.write(score);
				writer.write("\n");
				indent("}", writer, level + 2);
				if (resultIterator.hasNext()) {
					writer.write(",");
				}
				writer.write('\n');
			}
			indent("]", writer, level + 1);
			if (setIterator.hasNext()) {
				writer.write(",");
			}
		}
		writer.write('\n');
		writer.append("}");
	}
}
