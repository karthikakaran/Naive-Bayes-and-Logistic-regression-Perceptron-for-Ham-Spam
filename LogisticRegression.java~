import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LogisticRegression{
	static String trainingDataPath;
	static String testingDataPath;
	
	static String trainingDataHam;
	static String trainingDataSpam;
	static String testingDataHam;
	static String testingDataSpam;


	static int hamFileCount = 0;
	static int spamFileCount = 0;
	static int totalFileCount = 0;

	static int ITERATIONS = 400;
	static double learningRate = 0.018;
	static double lambda = 0.8;

	static Set<String> vocabSet;
	static ArrayList<String> vocabList;

	static HashMap<String, Integer> testTokenMap;

	static processFiles fileOP = processFiles.getInstance();
	static HashMap<String, HashMap<String, Integer>> hamTokenCountMap;
	static HashMap<String, HashMap<String, Integer>> spamTokenCountMap;
	static ArrayList<Structure> StructureList;

	static double[] weightArray;
	static double[] probArray;
	static double[] deltaWeightArray;
	static boolean stopWords = false;
	static double accuracy = 0.0;
	static double spamCount = 0.0;
	static double hamCount = 0.0;
	
	static HashMap<String, HashMap<String, Integer>> tokenCountMap;

	public static void main(String[] args) {
		if(args[2].equalsIgnoreCase("no-stopwords"))
			stopWords = true;
		trainingDataPath = args[0];
		testingDataPath = args[1];
		trainingDataHam = trainingDataPath + "/ham/";
		trainingDataSpam = trainingDataPath + "/spam/";
		testingDataHam = testingDataPath + "/ham/";
		testingDataSpam = testingDataPath + "/spam/";
		
		ArrayList<String> hamFiles = fileOP.readFiles(trainingDataPath, "ham");
		ArrayList<String> spamFiles = fileOP.readFiles(trainingDataPath, "spam");
		
		hamTokenCountMap = new HashMap<String, HashMap<String, Integer>>();
		spamTokenCountMap = new HashMap<String, HashMap<String, Integer>>();
		StructureList = new ArrayList<Structure>();
		vocabSet = new HashSet<String>();
		try {
			prepareDataMatrix(hamFiles, spamFiles);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> testHamFiles = fileOP.readFiles(testingDataPath, "ham");

		for (String s : testHamFiles)
			classifyTestData(testingDataHam + s, "ham");
		double accuracyHam = accuracy * 100;

		 spamCount = 0.0;
		 hamCount = 0.0;
		 accuracy = 0.0;
		 
		ArrayList<String> testSpamFiles = fileOP.readFiles(testingDataPath,	"spam");

		for (String s : testSpamFiles)
			classifyTestData(testingDataSpam + s, "spam");
		
		double accuracyspam = accuracy * 100;
		accuracy = (accuracyHam + accuracyspam)/2;
		if(args[2].equalsIgnoreCase("no-stopwords"))
			System.out.println("Accuracy of the test data without stop words="+ " " + accuracy);
		else
			System.out.println("Accuracy of the test data with stop words="+ " " + accuracy);
	}

	static void prepareDataMatrix(ArrayList<String> hamFiles, ArrayList<String> spamFiles) throws FileNotFoundException, IOException {
		hamFileCount = hamFiles.size();
		spamFileCount = spamFiles.size();
		totalFileCount = hamFileCount + spamFileCount;
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> hamWords = new ArrayList<String>();
		ArrayList<String> spamWords = new ArrayList<String>();
		for (String file : hamFiles) {
			hamTokenCountMap.put(file, new HashMap<String, Integer>());
			hamWords = fileOP.getWordListFromText(
					fileOP.readTextFromFile(trainingDataHam + file), "\\s");
			words.addAll(hamWords);
			for (String word : hamWords) {
				if (hamTokenCountMap.get(file).containsKey(word)) {
					hamTokenCountMap.get(file).put(word,
							(hamTokenCountMap.get(file).get(word) + 1));
				} else {
					hamTokenCountMap.get(file).put(word, 1);
				}
			}
		}
		for (String file : spamFiles) {
			spamTokenCountMap.put(file, new HashMap<String, Integer>());
			spamWords = fileOP.getWordListFromText(
					fileOP.readTextFromFile(trainingDataSpam + file), "\\s");
			words.addAll(spamWords);
			for (String word : spamWords) {
				if (spamTokenCountMap.get(file).containsKey(word)) {
					spamTokenCountMap.get(file)
							.put(word, (spamTokenCountMap.get(file).get(word) + 1));
				} else {
					spamTokenCountMap.get(file).put(word, 1);
				}
			}
		}

		vocabSet = wordsList(words);
		vocabList = new ArrayList<String>(vocabSet);
		vocabList.add(0, "XO");
		buildStructureList(hamTokenCountMap, 1);
		buildStructureList(spamTokenCountMap, 0);

		weightArray = new double[vocabList.size()];
		populateWeightArray(vocabList.size());
		probArray = new double[totalFileCount];

		for (int i = 0; i < ITERATIONS; i++) {
			regress();
		}
	}
	static void regress() {
		constructDataValues();
		calculateSummation();
		regularization();
	}
	static void buildStructureList(
			HashMap<String, HashMap<String, Integer>> fileNameClassTokenCountMap,
			int classValue) {
		tokenCountMap = new HashMap<String, HashMap<String, Integer>>();
		for (String s : fileNameClassTokenCountMap.keySet()) {
			Structure instance = new Structure(s,
					fileNameClassTokenCountMap.get(s), classValue);
			StructureList.add(instance);
		}
	}
	static void populateWeightArray(int vocabSize) {
		for (int i = 0; i < vocabSize; i++) {
			weightArray[i] = Math.random();
		}
	}
	static void calculateSummation() {
		deltaWeightArray = new double[vocabList.size()];
		for (int i = 0; i < weightArray.length; i++) {
			for (int j = 0; j < StructureList.size(); j++) {
				if (StructureList.get(j).getTokenCountMap()
						.get(vocabList.get(i)) != null)
					deltaWeightArray[i] = deltaWeightArray[i]
							+ StructureList.get(j).getTokenCountMap()
									.get(vocabList.get(i))
							* (StructureList.get(j).getClassValue() - probArray[j]);
			}
		}
	}
	static void constructDataValues() {
		for (int m = 0; m < StructureList.size(); m++) {
			probArray[m] = computeProb(computeExpoProb(StructureList.get(m)));
		}
	}
	static void regularization() {
		for (int i = 0; i < deltaWeightArray.length; i++) {
			weightArray[i] = weightArray[i] + learningRate
					* (deltaWeightArray[i] - (lambda * weightArray[i]));
		}
	}
	static double computeExpoProb(Structure instance) {
		double WX = 0.0;
		for (int i = 1; i < vocabList.size(); i++) {
			if (instance.getTokenCountMap().containsKey(vocabList.get(i))) {
				WX += weightArray[i] * instance.getTokenCountMap().get(vocabList.get(i));
			} else {
				WX += 0;
			}
		}
		WX += weightArray[0];
		return Math.exp(-WX);
	}
	static double computeProb(double exp) {
		double prob = 0.0;
		prob = (double) 1.0 / (1.0 + exp);
		return prob;
	}
	static Set<String> wordsList(ArrayList<String> words) {
		Set<String> wordSet = new HashSet<String>();
		for (String s : words) {
			wordSet.add(s);
		}
		return wordSet;
	}
	static void classifyTestData(String path, String classVal) {
		testTokenMap = new HashMap<String, Integer>();

		ArrayList<String> testWords = null;
		try {
			testWords = fileOP.getWordListFromText(fileOP.readTextFromFile(path), "\\s");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String word : testWords) {
			if (testTokenMap.containsKey(word)) {
				testTokenMap.put(word, (testTokenMap.get(word) + 1));
			} else {
				testTokenMap.put(word, 1);
			}
		}
		double classifierValue = 0.0d;
		for (String s : testTokenMap.keySet()) {

			if (vocabList.indexOf(s) > 0) {
				int count = testTokenMap.get(s);
				double weight = weightArray[vocabList.indexOf(s)];
				classifierValue += weightArray[0] + count * weight;
			}
		}
		if (classifierValue > 0.0) {
			hamCount++;
		} else {
			spamCount++;
		}
		if (classVal.equalsIgnoreCase("spam"))
			accuracy = (double) spamCount / (hamCount + spamCount);
		else
			accuracy = (double) hamCount / (hamCount + spamCount);
	}
}
