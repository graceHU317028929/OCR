package problemtype;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import OCR_Test.QA;
import OCR_Test.TableCell;
import OCR_Test.Test;

public class Choice {
	
String name;
	

	//public static int MUlTI_NUM = 4;// 每道题是4个空格
	private int multi_num;
	
	
	
	/**
	 * 填空1对多 判断answerbox是不是match，区别在于比对1-2 1-3 1-4 1-5 1-6 2-7 2-8 2-9 2-10
	 * 
	 * @param tableCellList
	 * @return
	 */
	public HashMap<Integer, HashMap<Integer, QA>> match(List<TableCell> tableCellList) {
		
		if (tableCellList.size() == 0) {
			Logger.getLogger("tablelist为空");

		}
		HashMap<Integer, HashMap<Integer, QA>> choice_qaPair = new HashMap<Integer, HashMap<Integer, QA>>();// 配对好的表格

		// 外层控制题目 内层控制答题框
		for (int i = 0; i < tableCellList.size() / (multi_num + 1); i++) {
			//必须每次添加到multiqa 都是个全新的qapair
			HashMap<Integer, QA> qaPair = new HashMap<Integer, QA>();
			// 第一次题目是0，第二次题目是5
			int tb1_index = (multi_num + 1) * i;
			TableCell tb1 = tableCellList.get(tb1_index);
			for (int j = 1; j <= multi_num; j++) {
				QA qa = new QA();
				// 1,2,3,4---6,7,8,9 --11,12,13,14 --16,17,18,19 --20,21,22,23
				int tb2_index = j + i * (multi_num+1);
				TableCell tb2 = tableCellList.get(tb2_index);
				// 判断是否1包含2（1为问题2为答题框）
				if (tb1.getY() <= tb2.getY() && tb1.getY() + tb1.getHeight() >= tb2.getY() + tb2.getHeight()) {

					qa.setQuestion(tb1);
					qa.setAnswerbox(tb2);
				} // 判断是否2包含1（1为答题框2为问题）
				else if (tb2.getY() <= tb1.getY() && tb2.getY() + tb2.getHeight() >= tb1.getY() + tb1.getHeight()) {

					qa.setQuestion(tb2);
					qa.setAnswerbox(tb1);
				}
				// 所以每一题的每小问的编号是j%4 比如 1.（1）（2）
				qaPair.put(j % multi_num, qa);
			}
			// 给answerbox排序
			qaPair = orderAnswerbox(qaPair);
			choice_qaPair.put((i + 1) / 2 + 1, qaPair);
		}

		return choice_qaPair;
	}
	
	/**
	 * 在每个小题都添加进去之后，给这些小题排序，依照他们的左上角的y值
	 * 
	 * @param qaPair2
	 */
	private HashMap<Integer, QA> orderAnswerbox(HashMap<Integer, QA> qaPair2) {
		// 保存排序好的qalist
		HashMap<Integer, QA> qaPair_gen = new HashMap<Integer, QA>();
		// 将map.entrySet()转换成list
		List<Map.Entry<Integer, QA>> list = new ArrayList<Map.Entry<Integer, QA>>(qaPair2.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, QA>>() {
			// 降序排序
			@Override
			public int compare(Entry<Integer, QA> o1, Entry<Integer, QA> o2) {
				TableCell tb1 = (TableCell) o1.getValue().getAnswerbox();
				TableCell tb2 = (TableCell) o2.getValue().getAnswerbox();
				
					return new Double(tb1.getY()).compareTo(new Double(tb2.getY()));
				
				
			}
		});
		int index = 1;
		for (Map.Entry<Integer, QA> mapping : list) {

			qaPair_gen.put(index, mapping.getValue());
			index++;
		}
		return qaPair_gen;

	}
	
	/**
	 * 填空1对多 保存目录和文件
	 * 
	 * @param iter_choice
	 *            hashmap的循环器
	 * @param bufferWritter
	 *            index文件的写入流
	 * @param mUlTI_NUM
	 *            题目的长度
	 */
	public List<String> saveIndexAndFile_choice(Iterator<Entry<Integer, HashMap<Integer, QA>>> iter_choice,
			BufferedWriter bufferWritter, int mUlTI_NUM, Test test, String srcpath, String dirName) throws IOException {
				List<String> files = new ArrayList<String>();
				Iterator<Entry<Integer, QA>> iter;
				bufferWritter.append(name+ "\t\n");
				System.out.println(name);
		// 循环 1 存成文件 2 写入index.txt
				while (iter_choice.hasNext()) {
					List<TableCell> answerList = new ArrayList<TableCell>();
					Entry entry = (Entry) iter_choice.next();
					//获取大题题号
					int key = (Integer) entry.getKey();
					HashMap<Integer, QA> qaPair_temp = (HashMap<Integer, QA>) entry.getValue();
					iter = qaPair_temp.entrySet().iterator();
					//记录小题题号
					int index_small=1;
					String questionImgName = key + "-"+"question";
					//写入大题名字
					bufferWritter.append("Question" + key + ":" + "\t\n");
					bufferWritter.append("Pic of question：" + questionImgName + "\t\n");
					//打印出大题名字
					System.out.println("question" + key + ":");
					System.out.println("Pic of question：" + questionImgName);
					files.add(questionImgName+".jpg");
					
					String answerboxImgName = key +"-answerbox";
					bufferWritter.append("Pic of answerbox：" + answerboxImgName + "\t\n");
					System.out.println("Pic of answerbox：" + answerboxImgName);
					files.add(answerboxImgName+".jpg");
					while(index_small<=multi_num){						
						Entry entry_small = (Entry) iter.next();
						QA qa = (QA) entry_small.getValue();
						TableCell question = qa.getQuestion();
						TableCell answerbox = qa.getAnswerbox();
						int choice_num = index_small-1;							
						String choiceImgName = key +"-("+ choice_num +")choice";
					if(index_small!=1){
						TableCell choice = new TableCell();
						choice.setX(answerbox.getX());
						choice.setY(answerbox.getY());
						choice.setWidth(answerbox.getWidth());
						choice.setHeight(answerbox.getHeight());
						choice.setName(choiceImgName);
						test.cutTableCell(choiceImgName, choice, dirName, srcpath);
						// 设置QA list的名字
						

						// 写入index.txt
						bufferWritter.append("Pic of choice：" + choiceImgName + "\t\n");

						// 同时写入一个hashmap里面
						System.out.println("Pic of choice：" + choiceImgName);
						files.add(choiceImgName+".jpg");
						
					}else{
					test.cutTableCell(answerboxImgName, answerbox, dirName, srcpath);	
					answerList.add(answerbox);
					test.cutTableCell_question(questionImgName, question, dirName, srcpath,answerList);
					question.setName(questionImgName);
					answerbox.setName(answerboxImgName);
					}
					index_small++;
				}
				}
				bufferWritter.flush();
				
				return files;


	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMulti_num() {
		return multi_num;
	}
	public void setMulti_num(int multi_num) {
		this.multi_num = multi_num;
	}
	public Choice(String name,int multi_num) {
		this.name = name;
		this.multi_num = multi_num;
	}


}
