import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * this class implements the main functionalities of this project.
 */
class SimilarSounds
{
	// ******DO NO CHANGE********//

	/**
	 * wordToSound maps each word to its corresponding sound.
	 */
	static Map<String, String> wordToSound;

	/**
	 * soundGroupToSimilarWords maps each sound-group to a BST containing all the words that share that sound-group.
	 */
	static Map<String, BST<String>> soundGroupToSimilarWords;

	/**
	 * Do not change.
	 * @param words one or more words passed on the command line.
	 */
	public static void processWords(String words[]) {

		ArrayList<String> lines = (ArrayList<String>)Extractor.readFile("word_to_sound.txt");
		populateWordToSoundMap(lines);
		
		populateSoundGroupToSimilarWordsMap(lines);

		if (words.length >= 2) {
			// check which of the words in the list have matching sounds
			findSimilarWordsInList(words);
		}
		else if (words.length == 1) {
			// get the list of words with matching sounds as this word
			findSimilarWordsTo(words[0]);
		}
	}

	/**
	 *  Main Method.
	 *
	 *  @param args args
	 */
	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("Wrong number of arguments, expecting:");
			System.out.println("java SimilarSounds word1 word2 word3...");
			System.out.println("java SimilarSounds word");
			System.exit(-1);
		}

		wordToSound = new java.util.HashMap<>(); // maps <word, sound>
		soundGroupToSimilarWords = new java.util.HashMap<>(); // maps <sound-group, sorted list of words with similar sounds>

		processWords(args);
	}
	// ******DO NO CHANGE********//





	/**
	 * Given a list of all entries in the database, this method populates the wordToSound map
	 * as follows: the key is the word, and the value is the sound (i.e., the sequence of unisounds)
	 * For example, if the line entry is "moderated M AA1 D ER0 EY2 T IH0 D", the key would be "moderated"
	 * and the value would be "M AA1 D ER0 EY2 T IH0 D"
	 * To achieve this, you need to use the methods in the Extractor class
	 * @param lines lines
	 */
	public static void populateWordToSoundMap(List<String> lines) {
		//lines stands for a list of word, we want to word
		for(String line: lines)
		{
			wordToSound.put(Extractor.extractWordFromLine(line), Extractor.extractSoundFromLine(line) );
		}

	}

	/**
	 * Given a list of all entries in the database, this method populates the
	 * soundGroupToSimilarWords map as follows: the key is the sound-group,
	 * and the value is a BST containing all the words that share that sound-group.
	 * For example, if the line entry is "moderated M AA1 D ER0 EY2 T IH0 D", the key would
	 * be "EY2 T IH0 D" and the value would be a BST containing "moderated" and all other
	 * words in the database that share the sound-group "EY2 T IH0 D"
	 * To achieve this, you need to use the methods in the Extractor class
	 * @param lines content of the database
	 */
	public static void populateSoundGroupToSimilarWordsMap(List<String> lines) {
		String sound, groupSound, word;

		for (String line : lines) {


			sound = Extractor.extractSoundFromLine(line);
			groupSound = Extractor.extractSoundGroupFromSound(sound);
			word = Extractor.extractWordFromLine(line);


			if (soundGroupToSimilarWords.get(groupSound) != null)
				soundGroupToSimilarWords.get(groupSound).insert(word);


			else{
				BST<String> similarSoundWordsBST = new BST<String>();
				similarSoundWordsBST.insert(word);
				soundGroupToSimilarWords.put(groupSound, similarSoundWordsBST);
			}
		}
	}

	/**
	 * Given a list of words, e.g., [word1, word2, word3, word4], this method checks whether
	 * word1 is similar to word2, word3, and word4. Then checks whether word2 is similar
	 * to word3 and word4, and finally whether word3 is similar to word4.
	 *
	 * <p>For example if the list contains: [calculated legislated hello world miscalleneous
	 * miscalculated encapsulated LIBERATED Sophisticated perculated hello],
	 * the output should exactly be as follows:
	 *
	 * <p>"calculated" sounds similar to: "legislated"
	 *	"hello" sounds similar to: none
	 *	"world" sounds similar to: none
	 *	"miscalculated" sounds similar to: "encapsulated" "LIBERATED" "Sophisticated"
	 *	Unrecognized words: "miscalleneous" "perculated"
	 *
	 * 	<p>Note however that:
	 * a) if a word was already found similar, then it will be ignored hereafter
	 * b) the behavior is case insensitive
	 * c) the subsequent occurrence of a given word is ignored
	 * d) words that couldn’t be found in the database are deemed unrecognizable
	 * e) words are displayed within quotes
	 * @param words list of words to examine
	 */
	public static void findSimilarWordsInList(String words[]) {


		String word, sound, groupSound, word1;
		String unrecognizedWords = "";
		String similarWords;

		for(int i = 0; i< words.length; i++) {
			word = words[i].toUpperCase();
			similarWords = "";
			BST<String> similarSoundWords = new BST<String>();

			if (wordToSound.get(word) != null) {
				sound = wordToSound.get(word);
				groupSound = Extractor.extractSoundGroupFromSound(sound);
				similarSoundWords = soundGroupToSimilarWords.get(groupSound);

				for (int j = i + 1; j < words.length; j++) {
					word1 = words[j].toUpperCase();
					if(words[i].equals(words[j]))
					{
						words[j] = "";
					}
					else if (similarSoundWords.find(word1) != null) {
						similarWords += " " + "\"" + words[j] + "\"";
						words[j] = "";
					}

				}
				if(similarWords == "")
				{
					similarWords = "none";
				}
				else
				{
					similarWords = similarWords.substring(1);
				}

				System.out.println("\"" + words[i] + "\"" + " sounds similar to: " + similarWords);
			} else {
				if(words[i] != "")
				{unrecognizedWords += " "+ "\"" + words[i] + "\"";}
			}
		}
		if (unrecognizedWords == "")
		{
			unrecognizedWords = "none";
		}
		else {
			unrecognizedWords = unrecognizedWords.substring(1);
		}
		System.out.println("Unrecognized words: " + unrecognizedWords);

	}


	/**
	 *Given a passed word this method prints all similarly sounding words in ascending order (including the passed word)
	 * For example:	java SimilarSounds dimension
	 * Words similar to "dimension": "ASCENSION" "ATTENTION" "CONTENTION" "CONVENTION" "DECLENSION"
	 * "DETENTION" "DIMENSION" "DISSENSION" "EXTENSION" "GENTIAN" "HENSCHEN" "LAURENTIAN"
	 * "MENTION" "PENSION" "PRETENSION" "PREVENTION" "RETENTION" "SUSPENSION" "TENSION"
	 *
	 * <p>Note how the word passed as an argument must still appear in the output.
	 * However, if it cannot be found in the database an appropriate error message should be displayed
	 * @param theWord word to process
	 */
	public static void findSimilarWordsTo(String theWord) {
		String word, sound, groupSound;
		BST<String> similarSoundWords = new BST<String>();
		word = theWord.toUpperCase();
		if(wordToSound.get(word) != null)
		{
			sound = wordToSound.get(word);
			groupSound = Extractor.extractSoundGroupFromSound(sound);
			similarSoundWords = soundGroupToSimilarWords.get(groupSound);
			System.out.println("Words similar to " + "\"" + theWord +  "\"" + ": " + similarSoundWords);
		}
		else{
			System.out.println("Unrecognized word: " + theWord);
		}
	}
}
