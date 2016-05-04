import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;

public class processFiles {
	static String stopWordsFile = "Stopwords.txt";
	static ArrayList<String> stopWordsList;
	static processFiles instance;
	public static boolean isRemoveStopWords = false;

	public static processFiles getInstance() {
		if (instance == null)
			instance = new processFiles();
		return instance;
	}
	public String readTextFromFile(String filePath) throws IOException,	FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		StringBuffer buff = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			buff.append(line + " ");
		}
		br.close();
		return buff.toString();
	}
	public ArrayList<String> readFiles(String filePath, String targetclass) {
		ArrayList<String> files = new ArrayList<String>();
		StringBuilder temp= new StringBuilder();
		temp.append(filePath);
		temp.append("/"+targetclass);
		String temp1 ;
		temp1=temp.toString();
		File file = new File(temp1);	
		String[] fileNames = file.list();
		for (String s : fileNames) {
			files.add(s);
		}
		return files;
	}
	boolean isStopWord(String word){
		return stopWordsList.contains(word);
	}
	public void collectStopWords()
			throws FileNotFoundException, IOException {
		String text = readTextFromFile(stopWordsFile);
		String[] stopWordsArray = text.split(" ");
		stopWordsList = new ArrayList<String>();

		for (String s : stopWordsArray)
			stopWordsList.add(s);


	}
	public ArrayList<String> getWordListFromText(String text,String nonWords)throws IOException{
		String[] listOfWords = text.split(nonWords);
		ArrayList<String> wordArrayList = new ArrayList<String>();
		for(String word:listOfWords){
			if(isRemoveStopWords){
				if(!isStopWord(word)){
					wordArrayList.add(word);
				}
			
			}else{
				wordArrayList.add(word);
			}
		}

		return wordArrayList;
	}
}
