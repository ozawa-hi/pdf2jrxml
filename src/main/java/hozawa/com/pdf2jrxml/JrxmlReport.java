package hozawa.com.pdf2jrxml;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.PDFTextStripper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.FillEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

/**
 * Class to read pdf info and generate jrxml file.
 * 
 * @author Hitoshi Ozawa
 * @since  2019/05/10
 */
public class JrxmlReport {

	/**
	 * Constructor to generate jrxml file from pdf file.
	 * 
	 * @param config configuration properties
	 * @param pdf_filename path to pdf file to read.
	 * @param jrxml_filename path to jrxml file to generate.
	 * @throws JRException JasperReports exception when generating jrxml file.
	 */
	public void generateJrxml(Config config, String pdf_filename, String jrxml_filename) throws JRException {
		generateJrxml(config, pdf_filename, jrxml_filename, 0);
	}
	
	/**
	 * Constructor to generate jrxml file from pdf file.
	 * 
	 * @param config configuration properties
	 * @param pdf_filename path to pdf file to read.
	 * @param jrxml_filename path to jrxml file to generate.
	 * @param pageNo page number of pdf file to read.
	 * @throws JRException JasperReports exception when generating jrxml file.
	 */
	public void generateJrxml(Config config, String pdf_filename, String jrxml_filename, int pageNo) throws JRException {
		Page page = extractElementsInPage(config, pdf_filename, pageNo);
		if (page != null) {
			JasperDesign design = generateDesign(config, page);
			JRReport report = JasperCompileManager.compileReport(design);
			JRXmlWriter.writeReport(report, jrxml_filename, config.getEncoding());	// (JRReport report, java.lang.String destFileName, java.lang.String encoding) 
		}
	}
	
	/**
	 * Extract text information from pdf file.
	 * 
	 * @param config configuration properties
	 * @param pdf_filename path to pdf file to read.
	 * @param pageNo page number of pdf file to extract information from.
	 * @return Page information extracted from pdf page.
	 */
	private Page extractElementsInPage(Config config, String pdf_filename, int pageNo) {
		int numPages;	// number of pages in pdf file
        Page pdfPage = new Page();	// pdf page content information
        
        try (PDDocument document = PDDocument.load(new File(pdf_filename))) {
        	numPages = document.getNumberOfPages();
        	if (pageNo >= 0 && pageNo < numPages) {
        		pdfPage.setPage(document.getPage(pageNo));
        		
        		// process text
        		List<JRDesignElement> textList = extractTextInPage(config, pdfPage, document, pageNo);
                pdfPage.setElementList(textList);
                
                // process images
                List<JRDesignElement> imgList = extractImageInPage(config, pdfPage, document, pageNo);
                pdfPage.addElementList(imgList);
                
                // process lines
                List<JRDesignElement> lineList = extractLineInPage(config, pdfPage, document, pageNo);
                pdfPage.addElementList(lineList);
        	} else {
        		return null;
        	}
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return pdfPage;
	}
	
	/**
	 * 
	 * @param config configuration properties
	 * @param pdfPage
	 * @param document
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	private List<JRDesignElement> extractTextInPage(Config config, Page pdfPage, PDDocument document, int pageNo) throws IOException {
		List<JRDesignElement> elementList = new ArrayList<JRDesignElement>();
		
		PDDocumentInformation pdd = document.getDocumentInformation();
		if (pdd.getTitle() != null) {
			pdfPage.setTitle(pdd.getTitle());
		} else {
			pdfPage.setTitle(config.getTitle());
		}
			
    	PDFTextStripperCustom textStripper = new PDFTextStripperCustom();
 	
        textStripper.setSortByPosition(true);
        textStripper.setStartPage(pageNo+1);
        textStripper.setEndPage(pageNo+1);
        String pdfFileText = textStripper.getText(document);

    	List<Map<String, Object>> charList = textStripper.getCharList();
        
        // split by line
        String lines[] = pdfFileText.split("\\n");
        for (int line = 0; line < lines.length; line++) {
            JRDesignStaticText element = createStaticText(lines[line], charList);
            if (element != null) {
            	elementList.add(element);
            }
        }
        return elementList;
	}
	
	/**
	 * Create static text jrxml component.
	 * 
	 * @param textInfo value of static text component.
	 * @return JRDesignStaticText jrxml static text component.
	 */
	private JRDesignStaticText createStaticText(String textInfo, List<Map<String, Object>> charList) {
		JRDesignStaticText element = null;
		if (textInfo.length() >= 9) {
			element = new JRDesignStaticText();
			String[] attributeList = textInfo.split(",");
			
			if (attributeList[10].length() < 1) {
				return element;
			}
			String text = attributeList[10];

			PDColor textColor = (PDColor)charList.get(0).get("color");
	        float r = textColor.getComponents()[0];
	        float g = textColor.getComponents()[1];
	        float b = textColor.getComponents()[2];
			
			// remove characters corresponding to text from charList
			for (int i = 0; i < text.length(); i++) {
				charList.remove(0);
			}
			
			
			int width = convertString2Int(attributeList[2]);
			int height = convertString2Int(attributeList[3]);
			
			int x = convertString2Int(attributeList[0]);
			int y = convertString2Int(attributeList[1]) - height;
			
			String fontName = attributeList[4];
			float fontSize = convertString2Float(attributeList[5]);
			
			int fontWeight = convertString2Int(attributeList[6]);
			boolean isBold = Boolean.parseBoolean(attributeList[7]);
			boolean isForceBold = Boolean.parseBoolean(attributeList[8]);
			boolean isItalic = Boolean.parseBoolean(attributeList[9]);

	
			element.setX(x);
			element.setY(y);
			element.setWidth(width);
			element.setHeight(height);
			element.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			element.setFontName(getFontName(fontName));
			element.setFontSize(fontSize);
			if (isBold || isForceBold) {
				element.setBold(true);
			}
			if (isItalic) {
				element.setItalic(true);
			}
			element.setForecolor(new Color(r, g, b));
			element.setText(text);
		}
		return element;
	}
	
	private List<JRDesignElement> extractImageInPage(Config config, Page pdfPage, PDDocument document, int pageNo) throws IOException {
		List<JRDesignElement> elementList = new ArrayList<JRDesignElement>();
		
		PDFStreamEngineCustom streamEngine = new PDFStreamEngineCustom();
		streamEngine.setImgDir(config.getImgDir());
        streamEngine.processPage(document.getPage(pageNo));
        List<String> imgInfo = streamEngine.getImageInfo();
        for (String img : imgInfo) {
        	JRDesignImage element = null;
        	if (img.length() >= 5) {
        		element = new JRDesignImage(null);
        		String[] imgAttributes = img.split(",");
        		String imgFilename = imgAttributes[0];

        		int width = convertString2Int(imgAttributes[3]);
        		int height = convertString2Int(imgAttributes[4]);

        		int x = convertString2Int(imgAttributes[1]);
        		int y = Math.round(pdfPage.getPage().getMediaBox().getHeight()) - convertString2Int(imgAttributes[2]) - height;
        		
        		element.setX(x);
        		element.setY(y);
        		element.setWidth(width);
        		element.setHeight(height);
        		
        		element.setFill(FillEnum.SOLID);
        		element.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
        		element.setHorizontalImageAlign(HorizontalImageAlignEnum.LEFT);
        		element.setVerticalImageAlign(VerticalImageAlignEnum.TOP);
        		element.setOnErrorType(OnErrorTypeEnum.BLANK);
        		element.setUsingCache(true);
//        		element.setLazy(isLazy);
        		
        		JRDesignExpression expression = new JRDesignExpression();
        		expression.setText("\"" + config.getImgDir() + imgFilename + "\"");
        		element.setExpression(expression);
        		
        		elementList.add(element);
        	}
        }
        return elementList;
	}

	private List<JRDesignElement> extractLineInPage(Config config, Page pdfPage, PDDocument document, int pageNo) throws IOException {
		List<JRDesignElement> elementList = new ArrayList<JRDesignElement>();
		
        ArrayList<Rectangle2D>rectList = new ArrayList<Rectangle2D>();
        PDPage page = pdfPage.getPage();
        double page_height = page.getCropBox().getUpperRightY();
        LineCatcher lineCatcher = new LineCatcher(page);
        lineCatcher.processPage(page);
        List<String> lineInfo = lineCatcher.getLineInfo();
        
        for (String line : lineInfo) {
        	JRDesignElement element = null;
        	if (line.length() >= 5) {
        		String[] lineAttributes = line.split(",");

        		int width = convertString2Int(lineAttributes[2]);
        		int height = convertString2Int(lineAttributes[3]);

        		int x = convertString2Int(lineAttributes[0]);
        		int y = Math.round(pdfPage.getPage().getMediaBox().getHeight()) - convertString2Int(lineAttributes[1]) - height;
        		
        		boolean isRect = Boolean.parseBoolean(lineAttributes[4]);
        		if (isRect) {
        			element = new JRDesignRectangle();
        			element.setX(x);
        			element.setY(y);
        			element.setWidth(width);
        			element.setHeight(height);
        		} else {
        			element = new JRDesignLine();
        			element.setX(x);
        			element.setY(y);
        			element.setWidth(width);
        			element.setHeight(height);
        		}
        		elementList.add(element);
        	}
        }
        
		return elementList;
	}
		
	/**
	 * Convert String to int. If specified String is null or an illegal number, return 0.
	 * 
	 * @param value String value to convert to int.
	 * @return int value of String as an rounded int.
	 */
	private int convertString2Int(String value) {
		if (value == null) {
			return 0;
		}
		try {
			return Math.round(Float.parseFloat(value));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	private float convertString2Float(String value) {
		if (value == null) {
			return 0;
		}
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	private String getFontName(String value) {
		if (value == null) {
			return null;
		}
		String fontName = value;
		String[] fontParts = value.split("\\+");
		if (fontParts.length > 0) {
			fontName = fontParts[1];
		}
		fontParts = fontName.split("-");
		if (fontParts.length > 0) {
			fontName = fontParts[0];
		}
		return fontName;
	}
	
	/**
	 * Generate JasperDesign object based on information in specified Page object.
	 * 
	 * @param config configuration properties
	 * @param page object containing information on page.
	 * @return JasperDesign jasperreports object that can be used to generate jrxml file.
	 * @throws JRException when there is JasperReports error generating JasperDesign object.
	 */
	private JasperDesign generateDesign(Config config, Page page) throws JRException {
	    //JasperDesign
	    JasperDesign jasperDesign = new JasperDesign();
	    jasperDesign.setName(page.getTitle());
	    jasperDesign.setPageWidth(Math.round(page.getPage().getMediaBox().getWidth()));
	    jasperDesign.setPageHeight(Math.round(page.getPage().getMediaBox().getHeight()));
	    jasperDesign.setColumnWidth(Math.round(page.getPage().getMediaBox().getWidth()) - config.getMarginLeft() - config.getMarginRight());
	    jasperDesign.setColumnSpacing(0);
	    jasperDesign.setLeftMargin(config.getMarginLeft());
	    jasperDesign.setRightMargin(config.getMarginRight());
	    jasperDesign.setTopMargin(config.getMarginTop());
	    jasperDesign.setBottomMargin(config.getMarginBottom());
		
		//Fonts
//		JRDesignStyle normalStyle = new JRDesignStyle();
//		normalStyle.setName("Sans_Normal");
//		normalStyle.setDefault(true);
//		normalStyle.setFontName("DejaVu Sans");
//		normalStyle.setFontSize(8f);
//		jasperDesign.addStyle(normalStyle);
//
	    // Style - not used
//		JRDesignStyle boldStyle = new JRDesignStyle();
//		boldStyle.setName("Sans_Bold");
//		boldStyle.setFontName("DejaVu Sans");
//		boldStyle.setFontSize(8f);
//		boldStyle.setBold(Boolean.TRUE);
//		jasperDesign.addStyle(boldStyle);

		// TODO need to generate query
	    //Query
//	    JRDesignQuery query = new JRDesignQuery();
//	    query.setText("SELECT * FROM address LIMIT 1");
//	    jasperDesign.setQuery(query);

	    // TODO need to generate fields from input fields
	    //Fields
//	    Map<String, Object> fields = new HashMap<String, Object>();
//	    fields.put("Id", java.lang.Integer.class);
//	    fields.put("FirstName", java.lang.String.class);
//	    fields.put("LastName", java.lang.String.class);
//	    fields.put("Street", java.lang.String.class);
//	    fields.put("City", java.lang.String.class);
//
//	    for (Map.Entry<String, Object> entry : fields.entrySet()) {
//	    	JRDesignField field = new JRDesignField();
//	    	field.setName(entry.getKey());
//	    	field.setValueClass((Class<?>) entry.getValue());
//	    	jasperDesign.addField(field);
//	    }
	    
	    //Title
	    JRDesignBand band = new JRDesignBand();
	    band.setHeight(Math.round(page.getPage().getMediaBox().getHeight()) - config.getMarginTop()- config.getMarginBottom());
	    
	    for (JRDesignElement element : page.getElementList()) {
	    	band.addElement(element);
	    }
	    jasperDesign.setTitle(band);
	    
	    return jasperDesign;
	}
}
