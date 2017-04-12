package problemtype;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import OCR_Test.QA;
import OCR_Test.TableCell;
import OCR_Test.Test;
import OCR_Test.WriteMsg;

public class SingleCompletion extends WriteMsg{
	
	String name;
	
	public SingleCompletion(String name) {
		super(name);
		this.name = name;
	}


	/**
	 * 从两个维度来判断这两个answerbox是不是匹配的
	 * 
	 * @return 返回装有问题答题框的
	 * @throws Exception
	 */
	public HashMap<Integer, QA> match(List<TableCell> tableCellList) throws ArrayIndexOutOfBoundsException {
		if (tableCellList.size() == 0) {
			Logger.getLogger("tablelist为空");

		}
		HashMap<Integer, QA> qaPair = new HashMap<Integer, QA>();
		for (int i = 0; i < tableCellList.size(); i += 2) {
			QA qa = new QA();
			TableCell tb1 = tableCellList.get(i);
			TableCell tb2 = tableCellList.get(i + 1);
			// 判断是否1包含2（1为问题2为答题框）
			if (tb1.getY() <= tb2.getY() && tb1.getY() + tb1.getHeight() >= tb2.getY() + tb2.getHeight()) {

				qa.setQuestion(tb1);
				qa.setAnswerbox(tb2);
			} // 判断是否2包含1（1为答题框2为问题）
			else if (tb2.getY() <= tb1.getY() && tb2.getY() + tb2.getHeight() >= tb1.getY() + tb1.getHeight()) {

				qa.setQuestion(tb2);
				qa.setAnswerbox(tb1);
			}
			qaPair.put((i + 1) / 2 + 1, qa);
		}

		return qaPair;
	}
	
	
	/**
	 * 填空1对1 保存目录和文件
	 * 
	 * @param iter_single
	 *            hashmap的循环器
	 * @param bufferWritter
	 *            index文件的写入流
	 */
	public List<String> saveIndexAndFile(Iterator<Entry<Integer, QA>> iter_single, BufferedWriter bufferWritter,
			Test test, String srcpath, String dirName) throws IOException {
		List<String> files = new ArrayList<String>();
		bufferWritter.append(name+ "\t\n");
		System.out.println(name);
		// 循环 1 存成文件 2 写入index.txt
		while (iter_single.hasNext()) {
			Entry entry = (Entry) iter_single.next();
			int key = (Integer) entry.getKey();
			String questionImgName = key + "-question";
			String answerboxImgName = key + "-answerbox";
			QA qa = (QA) entry.getValue();
			TableCell question = qa.getQuestion();
			TableCell answerbox = qa.getAnswerbox();
			test.cutTableCell(questionImgName, question, dirName, srcpath);
			test.cutTableCell(answerboxImgName, answerbox, dirName, srcpath);
			// 设置QA list的名字
			question.setName(questionImgName);
			answerbox.setName(answerboxImgName);

			// 写入index.txt
			files = writeToIndex(bufferWritter,key,questionImgName,answerboxImgName,files);

			// 同时写入一个hashmap里面
			printToConsole(bufferWritter,key,questionImgName,answerboxImgName);

		}
		bufferWritter.flush();
		
		return files;
	}

}
