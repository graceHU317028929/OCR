package OCR_Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TesseractOCRSample {

	public boolean runTesseract(String path, List<String> files, BufferedWriter bufferWritter)
			throws IOException, TesseractException {

		for (int i = 0; i < files.size(); i++) {
			File file = new File(path + "//" + files.get(i));
			Tesseract instance = Tesseract.getInstance(); // JNA Interface
															// Mapping
			instance.setLanguage("chi_tra");
			String result;
			try {
				result = instance.doOCR(file);
				bufferWritter.append(files.get(i).substring(0, files.get(i).length()-4)+"\t\n");
				bufferWritter.append(result + "\t\n");
				System.out.println(files.get(i).substring(0, files.get(i).length()-4));
				System.out.println(result);

			} catch (TesseractException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		bufferWritter.flush();
		bufferWritter.close();
		return true;
	}

}
