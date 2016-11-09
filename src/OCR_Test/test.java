package OCR_Test;

import com.asprise.ocr.Ocr;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.awt.Rectangle;  
import java.awt.image.BufferedImage;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.IOException;  
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;  
import javax.imageio.ImageReadParam;  
import javax.imageio.ImageReader;  
import javax.imageio.stream.ImageInputStream;  


/**
 * Created by hxp on 16/4/12.
 */
public class test {
	//split according to "cell"
	private String gap_cell = "<cell|</cell>";
	private String gap_x = "x=\"|\" y=";
	private String gap_y = "y=\"|\" width=";
	private String gap_width = "width=\"|\" height=";
	private String gap_height = "height=\"|\" row=|\" words=";
	
		// 源图片路径名称。如：c:\\1.jpg  
	  private String srcpath = "1.jpg";  
	  // 剪切图片存放路径名称。如：c:\\2.jpg  
	  private String subpath ;  
	
	public List<TableCell> tableCellList = new ArrayList<TableCell>();
	
	public HashMap<Integer,QA> qApair = new HashMap<Integer,QA>();
	
	public TableCell tableCell;
	/**
	 * 主方法
	 * 
	 * @param args
	 */
    public static void main(String[] args){
    	
    	//第一步.start OCR
    	test test = new test();
    	test.run(test);   		      		   	
    	    	    	    	
    }
    
    public void run(test test){
String s = test.startOCR();  	  
    	
    	//第二步.read XML
    	test.readXML(s);
    	
    	test.runSample(1);
    	//第三步.把list里面的cell匹配起来
    	HashMap<TableCell,TableCell> qApair = test.match(tableCellList);
    	//第四步.把每个tablecell切割成一个图片
   	  try{
   		 for(int i=0;i<test.tableCellList.size();i++){
   			test.tableCell = test.tableCellList.get(i);
   			//把图片切割
   			test.cutTableCell(i,test.tableCell);
   				 
   			 }  
   		 }catch(Exception e){
   	   		  e.printStackTrace();
   	   	  }
    	
    }
    /**
     * 从两个维度来判断这两个answerbox是不是匹配的 
     * @return
     */
    private HashMap<TableCell, TableCell> match(List<TableCell> tableCellList) {
		// TODO Auto-generated method stub 
    	
    	
		return null;
	}

	public void runSample(int num){
    	if(num == 2){
    		
    	}
    }

  /**
   * start OCR
   */
    public String startOCR(){
    	
    	Ocr.setUp(); // one time setup
    	Ocr ocr = new Ocr(); // create a new OCR engine
    	ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
    	String s = ocr.recognize(new File[] {new File(srcpath)}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);
    	// ocr more images here ...
    	ocr.stopEngine();    
    	return s;
    }
    /**
     * read XML and get the coordinate
     * @param s
     */
    public void readXML(String s){
    	
    	//get out the <block> tab
    	char[] characters = s.toCharArray();
    	
    	 String[] arr = s.split(gap_cell);
    	 //i start from 1 because the spit strategy,the array[0] is a useless one
    	  for(int i=1; i<arr.length; i++)
    	  {
    		
    		tableCell= new TableCell();
    		System.out.println("Result " + i + ":"+arr[i]);
    		//add some function to delete the empty item in the array
    		
    		//iterate the array,for each non-empty item, split x to the tablecell
    		if(arr[i].contains("x")&&arr[i].contains("y")){
    			String Xarray[]=arr[i].split(gap_x);
    			for(int j = 0;j<Xarray.length;j++){
    				if(!Xarray[j].contains("=")){
    					tableCell.setX(Integer.parseInt(Xarray[j]));    	
    					break;
    				}
    			}
    			System.out.println("tabelcell.x:" +tableCell.getX());
    			
    		}
    		//iterate the array,for each non-empty item, split y to the tablecell
    		if(arr[i].contains("x")&&arr[i].contains("y")){
    			String Yarray[]=arr[i].split(gap_y);
    			for(int j = 0;j<Yarray.length;j++){
    				if(!Yarray[j].contains("=")){
    					tableCell.setY(Integer.parseInt(Yarray[j]));    	
    					break;
    				}
    			}
    			System.out.println("tabelcell.y:" +tableCell.getY());
    		}
    		//split width to the tablecell
    		if(arr[i].contains("x")&&arr[i].contains("y")){
    			String widthArray[]=arr[i].split(gap_width);
    			for(int j = 0;j<widthArray.length;j++){
    				if(!widthArray[j].contains("=")){
    					tableCell.setWidth(Integer.parseInt(widthArray[j]));    	
    					break;
    				}
    			}
    			System.out.println("tabelcell.width:" +tableCell.getWidth());
    		}
    		
    		//split height to the tablecell
    		if(arr[i].contains("x")&&arr[i].contains("y")){
    			String heightArray[]=arr[i].split(gap_height);
    			for(int j = 0;j<heightArray.length;j++){
    				if(!heightArray[j].contains("=")){
    					tableCell.setHeight(Integer.parseInt(heightArray[j]));    	
    					break;
    				}
    			}
    			System.out.println("tabelcell.height:" +tableCell.getHeight());
    		
    		
    		
    		//for each array[i] there will be store in a tablecell[i]
    		if(tableCell.getX()!=0&&tableCell.getY()!=0&&tableCell.getWidth()!=0&&tableCell.getHeight()!=0){
    			tableCellList.add(tableCell);
    		}
    		System.out.println("*******************************************************");
    	  }
    	  
    	  
    	  }
    	
    	
    	  
    }
    
    public void cutTableCell(int i,TableCell tableCell) throws IOException {  
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
    	    Iterator<ImageReader> it = ImageIO  
    	      .getImageReadersByFormatName(suffix);  
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
    	    Rectangle rect = new Rectangle(tableCell.getX(), tableCell.getY(), tableCell.getWidth(), tableCell.getHeight());
    	    System.out.println("cut picture*****");
    	    System.out.println("tabelcell.height:" +tableCell.getHeight());
    	    // 提供一个 BufferedImage，将其用作解码像素数据的目标。  
    	    param.setSourceRegion(rect);  
    	    /* 
    	     * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的 
    	     * BufferedImage 返回。 
    	     */  
    	    BufferedImage bi = reader.read(0, param);  
    	    // 保存新图片  
    	    subpath = "1-"+i+".jpg";
    	    ImageIO.write(bi, suffix, new File(subpath)); 
    	    
    	    
    	    
    	    //把保存的照片加入到
    	   } finally {
    	    if (is != null)  
    	     is.close();  
    	    if (iis != null)  
    	     iis.close();  
    	   }  
    	  }   
    
    

}
