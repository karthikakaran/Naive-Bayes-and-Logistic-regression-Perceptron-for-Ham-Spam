import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NaiveBayes {
	
	private String folderName;
	private int type;
	private static int numSpam;
	private static int numHam;
	private static int countDocs;
	private static float prior[] = new float[2] ;
	private static float score[] = new float[2] ;
	private static HashMap<String, Float> conProbSpam = new HashMap<String, Float>();
	private static HashMap<String, Float> conProbHam = new HashMap<String, Float>();
	private static HashMap<String, Integer> totalVocab = new HashMap<String, Integer>();	
	private static HashMap<String, String> resultClass = new HashMap<String, String>();
	private static ArrayList<String> stopWords = new ArrayList<String>();
	
	public NaiveBayes(String folderName, int type, List<String> stopWords2){
		this.folderName = folderName;
		this.type = type;
		stopWords.addAll(stopWords2);
	}
	public static int getNumSpam() {
		return numSpam;
	}
	public static int getNumHam() {
		return numHam;
	}
	public static int getCountDocs() {
		return countDocs;
	}
	public HashMap<String, Integer> vocabularyBag() throws FileNotFoundException{
		HashMap<String, Integer> currentHashMap = new HashMap<String, Integer>();
		//read the file
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			if(type == 0){
				numSpam++;
			} else if(type == 1){
				numHam++;
			}
			countDocs++;
		    if (file.isFile()) {
				Scanner sc = new Scanner(new FileReader(folderName+"/"+file.getName()));
		
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				String[] pairs = line.split(" ");
				
				for(int i=0; i<pairs.length; i++){
					 int ocurr = 1;
					 pairs[i] = pairs[i].replaceAll("[^a-zA-Z0-9]", "").trim().toLowerCase();
					 if(pairs[i].length() > 1 && stopWords.contains(pairs[i]) == false){
						 if(currentHashMap.size() >  0 && currentHashMap.containsKey(pairs[i])){
							 ocurr = currentHashMap.get(pairs[i]);
							 ocurr++;
							 currentHashMap.put(pairs[i], ocurr);	 
						 } else
							 currentHashMap.put(pairs[i], ocurr);
					 }
				}
		    }
		    sc.close();
		  }
	   }
	   return currentHashMap;
	}
	public static void trainDataSet(HashMap<String, Integer> spamVocab, HashMap<String, Integer> hamVocab){
		//String[] type = {"Spam", "Ham"};
		int spamVocTotal = 0,  hamVocTotal = 0;
		
		for (Entry<String, Integer> entry : spamVocab.entrySet()) {
			spamVocTotal += entry.getValue();
		}
		for (Entry<String, Integer> entry : hamVocab.entrySet()) {
			hamVocTotal += entry.getValue();
		}
			
		prior[0] = (float)getNumSpam()/(float)getCountDocs();
		prior[1] = (float)getNumHam()/(float)getCountDocs();
		totalVocab.putAll(spamVocab);
		totalVocab.putAll(hamVocab);
		for (Entry<String, Integer> entry : totalVocab.entrySet()) {
			if(spamVocab.containsKey(entry.getKey())){
				float condProb = (float)(entry.getValue() + 1)/(float)(spamVocTotal+ 1);
				
				conProbSpam.put(entry.getKey(), condProb);
			}
			if(hamVocab.containsKey(entry.getKey())){
				float condProb = (float)(entry.getValue() + 1)/(float)(hamVocTotal+ 1);
				conProbHam.put(entry.getKey(), condProb);
			}
		}
	}
	public static void classifyTestData(String fName, int type) throws FileNotFoundException{
		int resType = 0;
		Scanner sc = new Scanner(new FileReader(fName));
		HashMap<String, Integer> tokenHashMap = new HashMap<String, Integer>();
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			String[] pairs = line.split(" ");
			
			for(int i=0; i<pairs.length; i++){
				 int ocurr = 1;
				 pairs[i] = pairs[i].replaceAll("[^a-zA-Z0-9]", "").trim().toLowerCase();
				
				 if(pairs[i].length() > 1  && stopWords.contains(pairs[i]) == false){
					 if(tokenHashMap.size() >  0 && tokenHashMap.containsKey(pairs[i])){
						 ocurr = tokenHashMap.get(pairs[i]);
						 ocurr++;
						 tokenHashMap.put(pairs[i], ocurr);	 
					 } else
						 tokenHashMap.put(pairs[i], ocurr);
				 }
			}
	    }
	    sc.close();
	    score[0] += Math.log10(prior[0])/Math.log10(2);
	    score[1] += Math.log10(prior[1])/Math.log10(2);
	    for (Entry<String, Integer> entry : tokenHashMap.entrySet()) {
	    	if(conProbSpam.containsKey(entry.getKey())){
				score[0] += Math.log10(entry.getValue())/Math.log10(2);
			}
	    	if(conProbHam.containsKey(entry.getKey())){
				score[1] += Math.log10(entry.getValue())/Math.log10(2);
			}
	    	if(score[0] < score[1])
		    	resType = 1;
		    if(type == resType)
		    	resultClass.put(fName, "Same");
		    else if(type != resType)
		    	resultClass.put(fName, "Change");
	    }   
	}
	public static void main(String[] args) throws FileNotFoundException {
		List<String> stopWords = new ArrayList<>();
		List<String> stopWordsTrim = new ArrayList<>();
		String trainFolder = args[0];
		String testFolder = args[1];
		NaiveBayes nbSpam = new NaiveBayes(trainFolder+"/spam/", 0, stopWords);
		HashMap<String, Integer> spamVocab = nbSpam.vocabularyBag();
		
		NaiveBayes nbHam = new NaiveBayes(trainFolder+"/ham/", 1,  stopWords);
		HashMap<String, Integer> hamVocab = nbHam.vocabularyBag();
		
		trainDataSet(spamVocab, hamVocab);
		
		File folder = new File(testFolder+"/spam/");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			classifyTestData(folder+"/"+file.getName(), 0);
		}
		File folder2 = new File(testFolder+"/ham/");
		File[] listOfFiles2 = folder2.listFiles();
		for (File file : listOfFiles2) {
			classifyTestData(folder2+"/"+file.getName(), 1);
		}
		int sameCount = 0, totalCount = 0;
		for (Entry<String, String> entry : resultClass.entrySet()) {
			totalCount++;
			if(entry.getValue().equalsIgnoreCase("Same"))
				sameCount++;
		}
		
		float accuracy = ((float)sameCount/(float)totalCount) * 100;
		System.out.println("Accuracy of Spam/Ham classification for test data = "+accuracy);
		
		Scanner sc = new Scanner(new FileReader("Stopwords.txt"));
		while(sc.hasNextLine()){
			String word = sc.nextLine();
			stopWords.add(word);
		}	
		for(String stopW : stopWords){
			stopW = stopW.replaceAll("[^a-zA-Z0-9]", "").trim().toLowerCase();
			stopWordsTrim.add(stopW);
		}
		
		resultClass.clear(); totalVocab.clear(); conProbSpam.clear(); conProbHam.clear(); numSpam = 0; numHam = 0; countDocs = 0; 
		prior[0] = prior[1] = 0.0f; score[0] = score[1] = 0.0f; 
		spamVocab.clear(); hamVocab.clear();
		
		NaiveBayes nbSpam2 = new NaiveBayes(trainFolder+"/spam/", 0, stopWordsTrim);
		spamVocab = nbSpam2.vocabularyBag();
		
		NaiveBayes nbHam2 = new NaiveBayes(trainFolder+"/ham/", 1,  stopWordsTrim);
		hamVocab = nbHam2.vocabularyBag();
		
		trainDataSet(spamVocab, hamVocab);
		
		for (File file : listOfFiles) {
			classifyTestData(folder+"/"+file.getName(), 0);
		}
		for (File file : listOfFiles2) {
			classifyTestData(folder2+"/"+file.getName(), 1);
		}
		int sameCountStop = 0, totalCountStop = 0;
		for (Entry<String, String> entry : resultClass.entrySet()) {
			totalCountStop++;
			if(entry.getValue().equalsIgnoreCase("Same"))
				sameCountStop++;
		}
		
		float accuracyStop = ((float)sameCountStop/(float)totalCountStop) * 100;
		System.out.println("Accuracy of Spam/Ham classification for test data withour stop words = "+accuracyStop);
	}
}
