package OCR_Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImagePreprocessing {
//	/**
//	 * 初始化灰度化
//	 * @param alpha
//	 * @param red
//	 * @param green
//	 * @param blue
//	 * @return
//	 */
//	private static int colorToRGB(int alpha, int red, int green, int blue) {
//
//		int newPixel = 0;
//		newPixel += alpha;
//		newPixel = newPixel << 8;
//		newPixel += red;
//		newPixel = newPixel << 8;
//		newPixel += green;
//		newPixel = newPixel << 8;
//		newPixel += blue;
//
//		return newPixel;
//
//}
//	/**
//	 * 灰度化
//	 * @param oriImg
//	 * @return
//	 */
//	public static String grayScale(String oriImg){
//		
//		BufferedImage bufferedImage;
//		try {
//			bufferedImage = ImageIO.read(new File(oriImg));
//		
//		BufferedImage grayImage = 
//			new BufferedImage(bufferedImage.getWidth(), 
//							  bufferedImage.getHeight(), 
//							  bufferedImage.getType());
//			
//		
//		for (int i = 0; i < bufferedImage.getWidth(); i++) {
//			for (int j = 0; j < bufferedImage.getHeight(); j++) {
//				final int color = bufferedImage.getRGB(i, j);
//				final int r = (color >> 16) & 0xff;
//				final int g = (color >> 8) & 0xff;
//				final int b = color & 0xff;
//				int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);;
//				int newPixel = colorToRGB(255, gray, gray, gray);
//				grayImage.setRGB(i, j, newPixel);
//			}
//		}
//		File newFile = new File(oriImg.substring(0, oriImg.length()-3)+"_gray.jpg");
//		ImageIO.write(grayImage, "jpg", newFile);
//		return newFile.getAbsolutePath();
//		//return newFile.getAbsolutePath();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return oriImg;		
//	}
	/**
	 * 输入
	 * @param args
	 * @return
	 * @throws IOException
	 */
public  static void main(String[] args)  {
	String oriImg = "/Users/hxp/6-程序输入题型/题目jpeg已测试/填空-一行三个--可识别.jpg";
	String binarizationImg = ImagePreprocessing.binarization_otsu(oriImg);
}

	
	/**
     * 区域二值化
     * 
     * @param oriImg 原始图片
     * @param outputImg 输出图片
     */
    public static String binarization(String oriImg) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img = Imgcodecs.imread(oriImg);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
        //
        Imgproc.adaptiveThreshold(img, img, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);
        File newFile = new File(oriImg.substring(0, oriImg.length()-3)+"_binarization.jpg");
        Imgcodecs.imwrite(newFile.getAbsolutePath(), img);
        return newFile.getAbsolutePath();
    }
    
    /**
     * otsu2值化
     * @param oriImg
     * @return
     */
    public static String binarization_otsu(String oriImg){
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img = Imgcodecs.imread(oriImg);
        //灰度化
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
        //otsu二值化
        Imgproc.threshold(img, img, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);  
        File newFile = new File(oriImg.substring(0, oriImg.length()-3)+"_binarization.jpg");
        Imgcodecs.imwrite(newFile.getAbsolutePath(), img);
        return newFile.getAbsolutePath();
    	

    }
}
