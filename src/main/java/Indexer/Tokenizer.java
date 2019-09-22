package Indexer;

import java.util.ArrayList;

/**
 * @author brandonskane This class tokenizes, removes stop words and then uses
 *         the Porter Stemming Algorithm (via the Stemmer class) to stem words.
 */
public class Tokenizer {

	ArrayList<String> allWords;
	ArrayList<String> stopWords;
	ArrayList<String> stemmedWords;

	/**
	 * @param seperatedWords
	 *            the words from the input file. They are potentially dirty but
	 *            will be cleaned
	 * @param stopWords
	 *            the stop words list. They must be properly formatted prior
	 */
	public Tokenizer(String[] seperatedWords, String[] stopWords) {
		this.allWords = new ArrayList<String>();
		this.stopWords = new ArrayList<String>();
		this.stemmedWords = new ArrayList<String>();

		tokenizer(seperatedWords);
		removeEmptyWords();
		System.out.println("Token");
		System.out.println(allWords);
		removeStopWords(stopWords);
		System.out.println("Remove Stop");
		System.out.println(allWords);
		stemWords();
	}

	public Tokenizer(ArrayList<String> seperatedWords, String[] stopWords) {
		this.allWords = new ArrayList<String>();
		this.stopWords = new ArrayList<String>();
		this.stemmedWords = new ArrayList<String>();

		tokenizer(seperatedWords);
		removeEmptyWords();
//		System.out.println("Token");
//		System.out.println(allWords);
		removeStopWords(stopWords);
//		System.out.println("Remove Stop");
//		System.out.println(allWords);
		stemWords();
	}

	/**
	 * @return All of the final tokens from the stemmedWords list (the completed
	 *         product)
	 */
	public String getProcessedWords() {
		StringBuilder stringBuilder = new StringBuilder();

		for (String word : stemmedWords) {
			stringBuilder.append(word + " ");
		}

		return stringBuilder.toString();
	}

	/**
	 * The Stemmer class requires words to be build via chars. Go through each
	 * word in the the allWords array and create Stemmer objects Then convert
	 * back to a String and add to stemmedWords (final product state)
	 */
	private void stemWords() {

		char[] wordCharArray;
		Stemmer s = new Stemmer();

		for (String word : allWords) {
			wordCharArray = word.toCharArray();

			for (int i = 0; i < word.length(); i++) {
				s.add(wordCharArray[i]);
			}
			s.stem(); // call to "stem" the word
			stemmedWords.add(s.toString());
		}
	}

	/**
	 * Clean the words -- remove paragraphs, split hyphens and remove links
	 * 
	 * @param seperatedWords
	 *            the raw words from the input file
	 */
	private void tokenizer(String[] seperatedWords) {
		for (String word : seperatedWords) {
			word = word.replace("\r", "");
			word = word.replace("\n", "");
			if (word.length() != 0 && !word.isEmpty() && !word.equals(" ")
					&& !word.equals("")) {

				if (word.contains("http://") || word.contains("www.")) {
					processURL(word);
				} else {
					if (word.contains("-")) {
						String[] splitWords = splitHyphenWord(word);
						for (String string : splitWords) {
							allWords.add(cleanString(string));
						}
					} else {
						allWords.add(cleanString(word));
					}
				}
			}
		}

		finalPassToSplitWordsWithAttachedNumbers();
	}

	private void tokenizer(ArrayList<String> seperatedWords) {
		for (String word : seperatedWords) {
			word = word.replace("\r", "");
			word = word.replace("\n", "");
			if (word.length() != 0 && !word.isEmpty() && !word.equals(" ")
					&& !word.equals("")) {

				if (word.contains("http://") || word.contains("www.")) {
					processURL(word);
				} else {
					if (word.contains("-")) {
						String[] splitWords = splitHyphenWord(word);
						for (String string : splitWords) {
							allWords.add(cleanString(string));
						}
					} else {
						allWords.add(cleanString(word));
					}
				}
			}
		}

		//finalPassToSplitWordsWithAttachedNumbers();
	}

	/**
	 * If a number has been attached to a word, it must be separated. The
	 * separated word is inserted, in order, into allWords
	 */
	private void finalPassToSplitWordsWithAttachedNumbers() {
		// add(int index, E element)

		for (int i = 0; i < allWords.size(); i++) {
			String wordToTest = allWords.get(i);
			int index = containsLettersAndNumbers(wordToTest);

			if (index >= 0) { //if less than 0, no digit was found
				{
					String[] word = allWords.get(i).split(
							"(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
					allWords.set(i, word[0]);
					allWords.add(i + 1, word[1]);
				}
			}
		}
	}

	/**
	 * This is a helper method for finalPassToRemoveAnyAttachedNumbers()
	 * 
	 * @param wordToTest
	 *            A candidate word to split by a digit
	 * @return The index of the digit (where to split)
	 */
	private int containsLettersAndNumbers(String wordToTest) {

		ArrayList<Integer> indexes = new ArrayList<Integer>();
		boolean letterFound = false;

		for (int i = 0; i < wordToTest.length(); i++) {
			if (!Character.isLetter(wordToTest.charAt(i))) {
				indexes.add(i);
			}
			if (Character.isLetter(wordToTest.charAt(i))) {
				letterFound = true;
			}
		}

		if (!indexes.isEmpty() && letterFound) {
			return indexes.get(0);
		}

		return -1;

	}

	private void processURL(String URL) {
		URL = URL.replaceAll("\\W", " ");
		URL = URL.replaceAll("_", " ");

		String[] URLs = URL.split(" ");

		for (String string : URLs) {
			if (string.length() != 0)
				allWords.add(cleanString(string));
		}
	}

	/**
	 * Removes all instances of stop words from the allWords array
	 * 
	 * @param stopWords
	 *            the stop words from the stopWord input file
	 */
	private void removeStopWords(String[] stopWords) {

		for (String string : stopWords) {
			this.stopWords.add(string);
		}

		for (int j = 0; j < stopWords.length; j++) {
			for (int i = 0; i < allWords.size(); i++) {
				if (allWords.get(i).equals(this.stopWords.get(j))) {
					allWords.remove(i);
				}
			}
		}

	}

	/**
	 * @param word
	 *            a "word" containing a hyphen to be split
	 * @return an array of 2 words after being split from the array
	 */
	private String[] splitHyphenWord(String word) {
		String[] splitWords = null;
		if (word.contains("-")) {
			splitWords = word.split("-");

		}
		return splitWords;
	}

	/**
	 * Using regex, remove numbers and non-alphanumeric chars, make lower case
	 * and remove trailing spaces
	 * 
	 * @param word
	 *            the word to "clean"
	 * @return a "cleaned" word
	 */
	private String cleanString(String word) {
		// word = word.replaceAll("[0-9]", "");
		word = word.replaceAll("\\W", "");
		return word.toLowerCase().trim();
	}

	/**
	 * If any empty words have made it into the list this will remove them
	 */
	private void removeEmptyWords() {
		for (int i = 0; i < allWords.size(); i++) {
			if (allWords.get(i).length() == 0) {
				allWords.remove(i);
			}
		}
	}
}
