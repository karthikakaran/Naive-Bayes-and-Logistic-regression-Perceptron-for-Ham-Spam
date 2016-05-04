
import java.util.HashMap;

public class Structure {

	private String fileName;
	private HashMap<String,Integer> tokenCountMap;
	private int classValue;
		
	public Structure(String file,HashMap<String,Integer> map,int classValue){
		this.setFileName(file);
		this.setTokenCountMap(map);
		this.setClassValue(classValue);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setTokenCountMap(HashMap<String,Integer> tokenCountMap) {
		this.tokenCountMap = tokenCountMap;
	}

	public HashMap<String,Integer> getTokenCountMap() {
		return tokenCountMap;
	}

	public void setClassValue(int classValue) {
		this.classValue = classValue;
	}

	public int getClassValue() {
		return classValue;
	}
}