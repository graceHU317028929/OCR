package OCR_Test;

import com.asprise.ocr.Ocr;

import net.sourceforge.tess4j.TesseractException;
import problemtype.ApplicationQestion;
import problemtype.Choice;
import problemtype.MultipleCompletion;
import problemtype.SingleCompletion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.stream.ImageInputStream;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;

/**
 * Created by hxp on 16/4/12.
 */
public class Test {
	// split according to "cell"
	private String gap_cell = "<cell|</cell>";
	private String gap_x = "x=\"|\" y=";
	private String gap_y = "y=\"|\" width=";
	private String gap_width = "width=\"|\" height=";
	private String gap_height = "height=\"|\" row=|\" words=";
	private String except = "</table>";
	private String type = "";
	public static String SINGLE_COMPLETION = "Single Completion";
	public static String MULTI_COMPLETION = "Multiple Completion";
	public static String HORIZONTAL = "Horizontal";
	public static String VERTICAL = "Vertical";
	//public static String CALCULATION ="Calculation";
	public static String APPLICATION = "Application Question";
	public static String CHOICE="Choice";
	//定义三类题型
	//private Calculation calculation;
	private ApplicationQestion application;
	private SingleCompletion singleCompletion;
	private MultipleCompletion multipleCompletion;
	private Choice choice;

	private Iterator<Entry<Integer, QA>> iter_single;
	private Iterator<Entry<Integer, HashMap<Integer, QA>>> iter_multi;
	private Iterator<Entry<Integer, QA>> iter_appl;
	private Iterator<Entry<Integer, HashMap<Integer, QA>>> iter_choice;
	private String subpath;// 剪切图片存放路径名称
	public List<TableCell> tableCellList = new ArrayList<TableCell>();
	
		

	
	public TableCell tableCell;// 每个cell包括左上顶点x，左上顶点y,宽度,高度
	String msg = "The answer box number maybe wrong";// 分块为空的异常信息

	public String run(Test test, String srcpath,String type,int multi_num,String layout)
			throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException, NotPartitionException, TesseractException {
		// 如果文件不是jpg或者png后缀则直接报错
		String prefix = srcpath.substring(srcpath.lastIndexOf(".") + 1);
		if (!"jpg".equals(prefix) && !"png".equals(prefix)) {
			throw new IllegalArgumentException();
		}
		
		String s = test.startOCR(srcpath);

		// 第二步.read XML
		test.readXML(s);

		// 第三步.把list里面的cell匹配起来
		// 情况1：是填空1对1
		if (SINGLE_COMPLETION.equals(type)) {
			singleCompletion = new SingleCompletion(SINGLE_COMPLETION);
			HashMap<Integer, QA> qApair = singleCompletion.match(tableCellList);

			if (qApair.entrySet().isEmpty()) {
				throw new NotPartitionException(msg);
			}
			// 第四步.把每个tablecell切割成一个图片

			iter_single = qApair.entrySet().iterator();
		}
		// 情况2：是填空一对多
		if (MULTI_COMPLETION.equals(type)) {
			multipleCompletion = new MultipleCompletion(MULTI_COMPLETION,multi_num,layout);
			HashMap<Integer, HashMap<Integer, QA>> multi_qApair = multipleCompletion.match(tableCellList);

			if (multi_qApair.entrySet().isEmpty()) {
				throw new NotPartitionException(msg);
			}
			// 第四步.把每个tablecell切割成一个图片

			iter_multi = multi_qApair.entrySet().iterator();
		}
		if(APPLICATION.equals(type)){
			application = new ApplicationQestion(APPLICATION);
			HashMap<Integer, QA> qaPair_appl = application.match(tableCellList);
			if (qaPair_appl.entrySet().isEmpty()) {
				throw new NotPartitionException(msg);
			}
			// 第四步.把每个tablecell切割成一个图片

			iter_appl = qaPair_appl.entrySet().iterator();
			
		}
		// 情况2：是选择
		if (CHOICE.equals(type)) {
			multi_num +=1;//输入为5个选择，实际上answerbox是6个
			choice = new Choice(CHOICE,multi_num);
			HashMap<Integer, HashMap<Integer, QA>> choice_qApair = choice.match(tableCellList);

			if (choice_qApair.entrySet().isEmpty()) {
				throw new NotPartitionException(msg);
			}
					// 第四步.把每个tablecell切割成一个图片

			iter_choice = choice_qApair.entrySet().iterator();
		}

		// 第五步 创建图片的文件夹
		String dirName[] = srcpath.split("\\.");
		File dirfile = new File(dirName[0]);
		// 如果文件夹不存在则创建
		if (!dirfile.exists() && !dirfile.isDirectory()) {
			System.out.println("//不存在");
			// 创建该图片的目录
			dirfile.mkdir();

		}

		// 第六步 创建index.txt并写入目录
		File indexFile = new File(dirName[0] + "//" + "index.txt");

		// if file doesnt exists, then create it
		if (!indexFile.exists()) {
			indexFile.createNewFile();
		}

		// true = append file
		FileOutputStream fos = new FileOutputStream(indexFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bufferWritter = new BufferedWriter(osw);
		List<String> files = new ArrayList<String>();
		// 第七步：把目录存成文件
		// 情况1：填空1对1
		if (SINGLE_COMPLETION.equals(type)) {
			files=singleCompletion.saveIndexAndFile(iter_single, bufferWritter, test, srcpath, dirName[0]);
		}
		// 情况2：填空1对多
		if (MULTI_COMPLETION.equals(type)) {
			files=multipleCompletion.saveIndexAndFile_multi(iter_multi, bufferWritter, multipleCompletion.getMulti_num(), test, srcpath, dirName[0]);
		}
		if(APPLICATION.equals(type)){
			files=application.saveIndexAndFile_appl(iter_appl, bufferWritter, test, srcpath, dirName[0]);
		}
		if(CHOICE.equals(type)){
			files=choice.saveIndexAndFile_choice(iter_choice, bufferWritter, choice.getMulti_num(), test, srcpath, dirName[0]);
			
		}
		
		
		TesseractOCRSample tesseract = new TesseractOCRSample();
		tesseract.runTesseract(dirName[0],files,bufferWritter);

		return indexFile.getAbsolutePath();

	}

	

	/**
	 * start OCR
	 */
	public String startOCR(String srcpath) {

		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English		
		String s = ocr.recognize(new File[] { new File(srcpath) }, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);
		// ocr more images here ...
		ocr.stopEngine();
		return s;
	}

	/**
	 * read XML and get the coordinate
	 * 
	 * @param s
	 */
	public void readXML(String s) {

		// get out the <block> tab
		char[] characters = s.toCharArray();

		String[] arr = s.split(gap_cell);
		// i start from 1 because the spit strategy,the array[0] is a useless
		// one
		for (int i = 1; i < arr.length; i++) {
			// 排除了cell嵌套的情况
			if (!arr[i].contains(except)) {
				tableCell = new TableCell();
				System.out.println("Result " + i + ":" + arr[i]);

				// iterate the array,for each non-empty item, split x to the
				// tablecell
				if (arr[i].contains("x") && arr[i].contains("y")) {
					String Xarray[] = arr[i].split(gap_x);
					for (int j = 0; j < Xarray.length; j++) {
						if (!Xarray[j].contains("=")) {
							tableCell.setX(Integer.parseInt(Xarray[j]));
							break;
						}
					}
					System.out.println("tabelcell.x:" + tableCell.getX());

				}
				// iterate the array,for each non-empty item, split y to the
				// tablecell
				if (arr[i].contains("x") && arr[i].contains("y")) {
					String Yarray[] = arr[i].split(gap_y);
					for (int j = 0; j < Yarray.length; j++) {
						if (!Yarray[j].contains("=")) {
							tableCell.setY(Integer.parseInt(Yarray[j]));
							break;
						}
					}
					System.out.println("tabelcell.y:" + tableCell.getY());
				}
				// split width to the tablecell
				if (arr[i].contains("x") && arr[i].contains("y")) {
					String widthArray[] = arr[i].split(gap_width);
					for (int j = 0; j < widthArray.length; j++) {
						if (!widthArray[j].contains("=")) {
							tableCell.setWidth(Integer.parseInt(widthArray[j]));
							break;
						}
					}
					System.out.println("tabelcell.width:" + tableCell.getWidth());
				}

				// split height to the tablecell
				if (arr[i].contains("x") && arr[i].contains("y")) {
					String heightArray[] = arr[i].split(gap_height);
					for (int j = 0; j < heightArray.length; j++) {
						if (!heightArray[j].contains("=")) {
							tableCell.setHeight(Integer.parseInt(heightArray[j]));
							break;
						}
					}
					System.out.println("tabelcell.height:" + tableCell.getHeight());

					// for each array[i] there will be store in a tablecell[i]
					if (tableCell.getX() != 0 && tableCell.getY() != 0 && tableCell.getWidth() != 0
							&& tableCell.getHeight() != 0) {
						tableCellList.add(tableCell);
					}
					System.out.println("*******************************************************");
				}

			}
		}
	}
	
	

	public void cutTableCell(String i, TableCell tableCell, String dirName, String srcpath) throws IOException {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			// 读取图片文件

			is = new FileInputStream(srcpath);
			// 图片后缀
			String suffix = srcpath.substring(srcpath.lastIndexOf(".") + 1);
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 . (例如 "jpeg" 或 "tiff")等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(suffix);
			ImageReader reader = it.next();
			// 获取图片流
			iis = ImageIO.createImageInputStream(is);
			/*
			 * <p>iis:读取源。true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);
			/*
			 * 描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();
			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标(x，y)、宽度和高度可以定义这个区域。
			 */
			int x= tableCell.getX()+3;
			int y = tableCell.getY()+3;
			Rectangle rect = new Rectangle(x, y, tableCell.getWidth(),
					tableCell.getHeight());

			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);
			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);
			// 保存新图片

			subpath = dirName + "//" + i + ".jpg";
			ImageIO.write(bi, suffix, new File(subpath));

			// 把保存的照片加入到
		} finally {
			if (is != null)
				is.close();
			if (iis != null)
				iis.close();
		}
	}
	public void cutTableCell_question(String i, TableCell tableCell, String dirName, String srcpath,List<TableCell> answerList) throws IOException {
//		FileInputStream is = null;
//		ImageInputStream iis = null;
//		try {
			// 读取图片文件

			//is = new FileInputStream(srcpath);
			// 图片后缀
			String suffix = srcpath.substring(srcpath.lastIndexOf(".") + 1);
			
			File _file = new File(srcpath); 
			Image src = javax.imageio.ImageIO.read(_file); // 构造Image对象  
			int question_width = src.getWidth(null); // 得到源图宽  
			int question_height = src.getHeight(null); // 得到源图长  
			BufferedImage image = new BufferedImage(question_width, question_height,  
				     BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();//获得一个图片类
			graphics.drawImage(src, 0, 0, question_width, question_height, null); // 绘制原图  
			//在原图上加框框
			for(int j=0;j<answerList.size();j++){
				int x = answerList.get(j).getX();
				int y =answerList.get(j).getY();
				int width = answerList.get(j).getWidth()+10;
				int height = answerList.get(j).getHeight()+10;
				//实例化Pen类
				graphics.setColor(Color.black);				
				//画边框
				BasicStroke wideStroke = new BasicStroke(2f);  
				graphics.setStroke(wideStroke);
				graphics.drawRect(x, y, width, height);
				graphics.setColor(Color.white);
				graphics.fillRect(x, y, width, height);
			}
			image = image.getSubimage(tableCell.getX(), tableCell.getY(), tableCell.getWidth(),
					tableCell.getHeight());
						
			subpath = dirName + "//" + i + ".jpg";
			ImageIO.write(image, suffix, new File(subpath));

			// 把保存的照片加入到
//		} finally {
//			if (is != null)
//				is.close();
//			if (iis != null)
//				iis.close();
//		}
	}
	
	
	

}
