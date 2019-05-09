package hozawa.com.pdf2jrxml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

public class JrxmlReport {
	private final int A4_WIDTH = 595;
	private final int A4_HEIGHT = 842;
	private final int DEFAULT_COLUMN_WIDTH = 555;
	private final int DEFAULT_LEFT_MARGIN = 20;
	private final int DEFAULT_RIGHT_MARGIN = 20;
	private final int DEFAULT_TOP_MARGIN = 20;
	private final int DEFAULT_BOTTOM_MARGIN = 20;
	private final String ENCODING = "UTF-8";
	
	public void generateJrxml(String pdf_filename, String jrxml_filename) throws JRException {
		ArrayList<JRDesignElement> elementList = extractTextInPage(pdf_filename);
		
		JasperDesign design = generateDesign(elementList);
		JRReport report = JasperCompileManager.compileReport(design);
		JRXmlWriter.writeReport(report, jrxml_filename, ENCODING);	// (JRReport report, java.lang.String destFileName, java.lang.String encoding) 
	}
	
	private ArrayList<JRDesignElement> extractTextInPage(String pdf_filename) {
		ArrayList<JRDesignElement> elementList = new ArrayList<JRDesignElement>();
		
        try (PDDocument document = PDDocument.load(new File(pdf_filename))) {
        	PDFTextStripper textStripper = new PDFTextStripperCustom();
            textStripper.setSortByPosition(true);
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                textStripper.setStartPage(page);
                textStripper.setEndPage(page);
                String pdfFileText = textStripper.getText(document);
                // split by line
                String lines[] = pdfFileText.split("\\n");
                for (int line = 0; line < lines.length; line++) {
                    JRDesignStaticText element = createStaticText(lines[line]);
                    if (element != null) {
                    	elementList.add(element);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elementList;
	}
	
	private JRDesignStaticText createStaticText(String textInfo) {
		JRDesignStaticText element = null;
		if (textInfo.length() >= 9) {
			element = new JRDesignStaticText();
			String[] attributeList = textInfo.substring(1, textInfo.length()-1).split(",");
			int x = convertString2Int(attributeList[0]);
			int y = convertString2Int(attributeList[1]);
			int width = convertString2Int(attributeList[2]);
			int height = convertString2Int(attributeList[3]);
			
			String fontName = attributeList[4];
			float fontSize = convertString2Float(attributeList[5]);
			
			int fontWeight = convertString2Int(attributeList[6]);
			boolean isBold = Boolean.parseBoolean(attributeList[7].trim());
			boolean isForceBold = Boolean.parseBoolean(attributeList[8].trim());
			boolean isItalic = Boolean.parseBoolean(attributeList[9].trim());

			String text;
			if (attributeList[10].length() > 0) {
				text = attributeList[10].substring(1);
			} else {
				text = attributeList[10];
			}
			
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
			element.setText(text);
		}
		
		return element;
	}
	
	private int convertString2Int(String value) {
		if (value == null) {
			return 0;
		}
		return Math.round(Float.parseFloat(value));
	}
	
	private float convertString2Float(String value) {
		if (value == null) {
			return 0;
		}
		return Float.parseFloat(value);
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
	
	private JasperDesign generateDesign(List<JRDesignElement> elementList) throws JRException {
	    //JasperDesign
	    JasperDesign jasperDesign = new JasperDesign();
	    jasperDesign.setName("PDF2JrxmlTest");
	    jasperDesign.setPageWidth(A4_WIDTH);
	    jasperDesign.setPageHeight(A4_HEIGHT);
	    jasperDesign.setColumnWidth(DEFAULT_COLUMN_WIDTH);
	    jasperDesign.setColumnSpacing(0);
	    jasperDesign.setLeftMargin(DEFAULT_LEFT_MARGIN);
	    jasperDesign.setRightMargin(DEFAULT_RIGHT_MARGIN);
	    jasperDesign.setTopMargin(DEFAULT_TOP_MARGIN);
	    jasperDesign.setBottomMargin(DEFAULT_BOTTOM_MARGIN);
		
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
	    band.setHeight(A4_HEIGHT - DEFAULT_TOP_MARGIN - DEFAULT_BOTTOM_MARGIN);
	    
	    for (JRDesignElement element : elementList) {
	    	band.addElement(element);
	    }
	    
	    jasperDesign.setTitle(band);
	    
	    return jasperDesign;
	}
}
