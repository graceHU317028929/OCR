package OCR_Test;
/**
 * 每一道题目的区块
 * 题目主体 question
 * 答题框   answerbox
 * @author hxp
 *
 */
public class QA {
	
	private TableCell question;
	private TableCell answerbox;
	
	
	public void setAnswerbox(TableCell answerbox) {
		this.answerbox = answerbox;
	}
	public TableCell getAnswerbox() {
		return answerbox;
	}
	public void setQuestion(TableCell question) {
		this.question = question;
	}
	public TableCell getQuestion() {
		return question;
	}

}
