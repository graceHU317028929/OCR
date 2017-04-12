package OCR_Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WriteMsg {
	String name;
	public WriteMsg(String name){
		this.name = name;
	}
	
	public List<String> writeToIndex(BufferedWriter bufferWritter,int key,String questionImgName,String answerboxImgName,List<String> files)throws IOException{
		
		bufferWritter.append("Question" + key + ":" + "\t\n");
		bufferWritter.append("Pic of question：" + questionImgName + "\t\n");
		files.add(questionImgName+".jpg");
		bufferWritter.append("Pic of answerbox：" + answerboxImgName + "\t\n");
		files.add(answerboxImgName+".jpg");
		return files;
	}
	public void printToConsole(BufferedWriter bufferWritter,int key,String questionImgName,String answerboxImgName)throws IOException{
		
		System.out.println("question" + key + ":");
		System.out.println("Pic of question：" + questionImgName);
		System.out.println("Pic of answerbox：" + answerboxImgName);
	}


}
