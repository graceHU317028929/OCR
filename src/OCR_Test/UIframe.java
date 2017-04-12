package OCR_Test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.tess4j.TesseractException;
import problemtype.MultipleCompletion;

public class UIframe extends JFrame implements ActionListener {

	private JPanel panel1;
	private JPanel panel2;
	private JLabel lbErrorMsg;
	private Font font;
	private Color fgColor;
	private int fontSize;
	private JFileChooser fileChooser;// 文件选择器
	private String filePath;// 文件路径
	private JTextField jtf_filePath;// “文件路径”的文本框
	private JButton jb_chooseFile;// “选择文件”按钮
	private JButton jb_partition;// “分块”按钮
	private JLabel lbPictureAddr;// 选择路径的标签
	private JTextArea ta;
	private String indexPath;
	private JScrollPane scroll;
	private int frameWidth = 1000;
	private int frameHeight = 800;
	private int textArea_rows = 15;
	private int textArea_cols = 50;
	private JComboBox<String> jcb_problemPatterns = new JComboBox<String>(); // 题型下拉框
	private String problemType = Test.SINGLE_COMPLETION; // 初始默认单填空

	private JFormattedTextField multi_numJMT;
	private JLabel jl_multi;

	private JComboBox<String> jcb_multilayout = new JComboBox<String>(); // layout下拉框
	private String layout = Test.VERTICAL;// 记录layout
	private JFormattedTextField choice_numJMT;
	private JLabel jl_choice;

	public UIframe(String title) throws HeadlessException {
		super(title);
		// 1.设置布局管理器
		panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());// 流式布局
		panel2 = new JPanel();

		Container panel = getContentPane();// 主布局容器
		panel.setLayout(new GridLayout(2, 1));// 网格布局，2行1列
		panel.add(panel1);
		panel.add(panel2);
		// 第一行
		lbPictureAddr = new JLabel("Source File Path:");// 初始化一个选择路径标签
		jtf_filePath = new JTextField(20);// 初始化文本框：文件路径
		jb_chooseFile = new JButton("Choose File");// 初始化选择文件按钮
		// 第二行

		jb_partition = new JButton("Matching and Recognizing");// 初始化分块按钮
		lbErrorMsg = new JLabel();// 初始化错误信息标签
		lbErrorMsg.setForeground(Color.red);

		fontSize = 12;
		font = new Font("宋体", Font.PLAIN, fontSize);// 定义一个字体对象
		fgColor = new Color(0, 0, 0);// 定义一个颜色对象
		lbPictureAddr.setFont(font);
		lbPictureAddr.setForeground(fgColor);
 
		// 第三行
		ta = new JTextArea(textArea_rows, textArea_cols);
		ta.setEditable(false);
		ta.setLineWrap(true); // 自动换行
		scroll = new JScrollPane(ta);
		// 把定义的JTextArea放到JScrollPane里面去
		// multi隐藏的那些
		multi_numJMT = new JFormattedTextField();
		jl_multi = new JLabel("answerbox num:");
		// choice隐藏的那些
		choice_numJMT = new JFormattedTextField();
		jl_choice = new JLabel("choice num:");

		jcb_problemPatterns.addItemListener(new ItemListener() { // 下拉框事件监听

			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					// System.out.println("选中" + event.getItem());
					problemType = (String) e.getItem();
					if (problemType.equals(Test.MULTI_COMPLETION)) {
						jl_choice.setVisible(false);
						choice_numJMT.setVisible(false);

						panel1.setLayout(new FlowLayout());
						multi_numJMT.setText("4");
						multi_numJMT.addKeyListener(new java.awt.event.KeyAdapter() {
							public void keyReleased(java.awt.event.KeyEvent evt) {
								String old = multi_numJMT.getText();
								JFormattedTextField.AbstractFormatter formatter = multi_numJMT.getFormatter();
								if (!old.equals("")) {
									if (formatter != null) {
										String str = multi_numJMT.getText();
										try {
											int page = (Integer) formatter.stringToValue(str);
											multi_numJMT.setText(page + "");
										} catch (ParseException pe) {
											multi_numJMT.setText("1");// 解析异常直接将文本框中值设置为1
										}
									}
								}
							}
						});

						// 加一个设置布局的组件
						jcb_multilayout.addItemListener(new ItemListener() {
							// 下拉框事件监听
							@Override
							public void itemStateChanged(ItemEvent e) {
								switch (e.getStateChange()) {
								case ItemEvent.SELECTED:
									System.out.println("选中的layout类型是：" + layout);
									layout = (String) e.getItem();

									break;
								case ItemEvent.DESELECTED:
									System.out.println("取消选中题目类型" + e.getItem());
									break;
								}
							}
						});
						jcb_multilayout.addItem(Test.VERTICAL);
						jcb_multilayout.addItem(Test.HORIZONTAL);
						panel1.add(jl_multi);
						panel1.add(multi_numJMT);
						panel1.add(jcb_multilayout);
						jl_multi.setVisible(true);
						multi_numJMT.setVisible(true);
						jcb_multilayout.setVisible(true);
						panel1.revalidate();

					} else if (problemType.equals(Test.CHOICE)) {
						jl_multi.setVisible(false);
						multi_numJMT.setVisible(false);
						jcb_multilayout.setVisible(false);

						panel1.setLayout(new FlowLayout());
						choice_numJMT.setText("5");
						choice_numJMT.addKeyListener(new java.awt.event.KeyAdapter() {
							public void keyReleased(java.awt.event.KeyEvent evt) {
								String old = choice_numJMT.getText();
								JFormattedTextField.AbstractFormatter formatter = choice_numJMT.getFormatter();
								if (!old.equals("")) {
									if (formatter != null) {
										String str = choice_numJMT.getText();
										try {
											int page = (Integer) formatter.stringToValue(str);
											choice_numJMT.setText(page + "");
										} catch (ParseException pe) {
											choice_numJMT.setText("1");// 解析异常直接将文本框中值设置为1
										}
									}
								}
							}
						});
						panel1.add(jl_choice);
						panel1.add(choice_numJMT);
						jl_choice.setVisible(true);
						choice_numJMT.setVisible(true);
						panel1.revalidate();

					} else

					{
						jl_multi.setVisible(false);
						multi_numJMT.setVisible(false);
						jcb_multilayout.setVisible(false);
						jl_choice.setVisible(false);
						choice_numJMT.setVisible(false);
					}
					System.out.println("选中的题目类型是：" + problemType);
					break;
				case ItemEvent.DESELECTED:
					System.out.println("取消选中题目类型" + e.getItem());
					break;
				}

			}
		});
		jcb_problemPatterns.addItem(Test.SINGLE_COMPLETION);
		jcb_problemPatterns.addItem(Test.MULTI_COMPLETION); // 下拉框里的选项
		jcb_problemPatterns.addItem(Test.APPLICATION);
		jcb_problemPatterns.addItem(Test.CHOICE);

		// 分别设置垂直滚动条自动出现
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		jb_chooseFile.addActionListener(this);
		jb_partition.addActionListener(this);

		panel1.add(lbPictureAddr);// 将标签布局在panel1上
		panel1.add(jtf_filePath);// 将标签布局在panel1上
		panel1.add(jb_chooseFile);// 将选择文件按钮布局在panel1上
		panel1.add(jcb_problemPatterns);// 将下拉框布局在panel1上
		panel1.add(scroll);
		panel1.add(lbErrorMsg);// 将错误信息提示布局在panel1上

		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width / 2; // 获取屏幕的宽
		int width = this.getWidth();

		jb_partition.setLocation(screenWidth - width / 2, 5);
		panel2.add(jb_partition);

		// 设置窗口大小，位置
		pack();// 根据控件占据总大小设置JFrame窗口大小

		setSize(frameWidth, frameHeight);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - this.getWidth()) / 2, (dim.height - this.getHeight()) / 2); // 设置窗口在正中间
		setVisible(true);// 设置窗口可见
		addWindowListener(new WindowAdapter()// 设置点击窗口右上角的关闭按钮，关闭窗口同时终止当前进程，如不设置，窗口虽然关闭了，可程序仍在后台运行
		{
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}

	/**
	 * 点击打开打开文件
	 * 
	 * @return 成功则返回文件路径，失败则给出对应的错误提示，如果是为空，则报空错误，如果是超长，则报超长提示，若
	 */
	public boolean openFile(JTextField jtf_filePath) {
		fileChooser = new JFileChooser("choose path");// 定义
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("Open File");
		int ret = fileChooser.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			// 文件夹路径
			System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
			filePath = fileChooser.getSelectedFile().getAbsolutePath();
			jtf_filePath.setText(filePath);
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new UIframe("Recognize Tool");// 运行JFrame
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String multi_num_s = "";
		int multi_num = 0;
		if (e.getSource() == jb_chooseFile) {
			openFile(jtf_filePath);
		} else if (e.getSource() == jb_partition) {
			ta.append("start partition..." + "\r\n");
			ta.paintImmediately(ta.getBounds());
			if (problemType.isEmpty()) {
				lbErrorMsg.setText("Please choose a problem pattern to partition");
			}

			if (filePath.isEmpty()) {
				lbErrorMsg.setText("Please choose a worksheet to be recognized");

			}
//			// 如果是和1对多填空题有关的题目
//			if (problemType.contains(Test.MULTI_COMPLETION)) {
//				// 如果是初始多填空题
//				if (problemType.equals(Test.MULTI_COMPLETION)) {
//					if (!multi_numJMT.getText().isEmpty()) {
//						// 处理数字
//						multi_num_s = multi_numJMT.getText();
//						multi_num = Integer.parseInt(multi_num_s);
//						addItem(problemType, multi_num, layout);
//					}
//				} else {
//					problemType =problemType.replace("+", "\\\\");
//					String[] param = problemType.split("\\\\");
//					multi_num_s = param[2];
//					multi_num_s =multi_num_s.substring(0, 1);
//					multi_num = Integer.parseInt(multi_num_s);
//					String layout_String = param[4];
//					layout_String=layout_String.replace(" ", "\\\\");
//					String[] layout_arr = layout_String.split("\\\\");
//					layout = layout_arr[0];
//					
//				}
			if (problemType.equals(Test.MULTI_COMPLETION)) {
				if (!multi_numJMT.getText().isEmpty()) {
					// 处理数字
					multi_num_s = multi_numJMT.getText();
					multi_num = Integer.parseInt(multi_num_s);
					//addItem(problemType, multi_num, layout);
				}
			}
			else if (problemType.contains(Test.CHOICE)) {
				if (problemType.equals(Test.CHOICE)) {
					multi_num_s = choice_numJMT.getText();
					multi_num = Integer.parseInt(multi_num_s);
					//addItem(problemType, multi_num, "");
				}
			}

			// 运行解析
			indexPath = getIndexPath(multi_num, layout);
			if (!indexPath.isEmpty()) {
				ta.append("path of partition result:" + indexPath + "\r\n");
				ta.paintImmediately(ta.getBounds());
				// 读取结果
				try { // 以缓冲区方式读取文件内容

					File file1 = new File(indexPath);
					FileReader fr = new FileReader(file1);
					BufferedReader br = new BufferedReader(fr);
					String aline;
					while ((aline = br.readLine()) != null)// 按行读取文本
						ta.append(aline + "\r\n");
						
					fr.close();
					br.close();
					multi_numJMT.setText("");
					choice_numJMT.setText("");
					
				} catch (IOException ioe) {
					lbErrorMsg.setText("Index open fail");
					System.out.println(ioe);
				}

			}
		}

	}

	/**
	 * 运行解析,处理异常
	 * 
	 * @return 返回解析结果的index文件路径
	 */
	private String getIndexPath(int multi_num, String layout) {
		// TODO Auto-generated method stub
		Test test = new Test();
		try {

			indexPath = test.run(test, filePath, problemType, multi_num, layout);

		} catch (IOException e) {
			lbErrorMsg.setText("The File path is wrong");
			ta.append("Partition fail" + "\r\n");
			e.printStackTrace();

		} catch (ArrayIndexOutOfBoundsException e) {
			lbErrorMsg.setText("This worksheet may not fit for the pattern,please choose another one");
			ta.append("Partition fail" + "\r\n");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			lbErrorMsg.setText("The File is not a jpeg or a png");
			ta.append("Partition fail" + "\r\n");
			e.printStackTrace();
		} catch (NotPartitionException e) {
			lbErrorMsg.setText(e.getMessage());
			ta.append("Partition fail" + "\r\n");
			e.printStackTrace();
		} catch (TesseractException e) {
			lbErrorMsg.setText(e.getMessage());
			ta.append("OCR Recognize fail" + "\r\n");
			e.printStackTrace();
		}
		return indexPath;
	}

//	/**
//	 * 
//	 * @param problemtype
//	 * @param num
//	 *            如果是multiple则是multiple的
//	 * @param layout
//	 *            如果存在solution 则是1 如果不存在 则是2
//	 */
//	public void addItem(String problemtype, int num, String layout) {
//		// String solution_s = setSolution(solution);
//		panel1.setLayout(new FlowLayout());
//		if (problemtype.equals(Test.MULTI_COMPLETION)) {
//			jcb_problemPatterns.addItem(problemtype + "+" + num + "questions" + "+" + layout + " layout");
//		}
//		if (problemtype.equals(Test.CHOICE)) {
//
//			jcb_problemPatterns.addItem(problemtype + "+" + num + "choices");
//		}
//		// else if(problemtype.equals(Test.APPLICATION)){}
//
//		panel1.add(jcb_problemPatterns);
//		jcb_problemPatterns.setVisible(true);
//		panel1.revalidate();
//	}
}
