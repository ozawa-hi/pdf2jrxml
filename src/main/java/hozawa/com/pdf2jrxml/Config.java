package hozawa.com.pdf2jrxml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Read from configuration properties file 
 * 
 * @author Hitoshi Ozawa
 * @since  2019/05/10
 */
public class Config {
	
	// default property values
	private static final String PROPERTY_FILE = "pdf2jrxml.properties";
	
	private final String DEFAULT_PDF_FILENAME = "sample.pdf";
	private final String DEFAULT_JRXML_FILENAME = "sample.jrxml";
	
	private final String DEFAULT_IMG_DIR = "img";
	
	private final int DEFAULT_LEFT_MARGIN = 0;
	private final int DEFAULT_RIGHT_MARGIN = 0;
	private final int DEFAULT_TOP_MARGIN = 0;
	private final int DEFAULT_BOTTOM_MARGIN = 0;
	
	private final String DEFAULT_ENCODING = "UTF-8";
	private final String DEFAULT_TITLE="PDF2Jrxml Form";
	
	
	private Properties config = new Properties();	// properties are read from properties file if found
	
	// properties variables
	private String pdfFilename;
	private String jrxmlFilename;
	private String imgDir;
	
	private int marginLeft;
	private int marginRight;
	private int marginTop;
	private int marginBottom;
	
	private String encoding;
	private String title;
	
	/**
	 * Default constructor
	 * 
	 * @throws IOException
	 */
	public Config() {
		init(PROPERTY_FILE);
	}
	
	/**
	 * Constructor
	 * 
	 * @param propertyFilename	property file to use
	 * @throws IOException		property file not found
	 */
	public Config(String propertyFilename) {
		init(propertyFilename);
	}

	/**
	 * Initialize properties to use
	 *   1. use settings in configuration file
	 *   2. if not set in configuration file, use the default value
	 * 
	 * @param propertyFile	configuration properties file
	 */

	public void init(String propertyFile) {
		InputStream is = null;
		try {
			is = new BufferedInputStream( new FileInputStream(propertyFile));
			config.load(is);
			
			setPdfFilename(config.getProperty("pdf_filename"));
			setJrxmlFilename(config.getProperty("jrxmlFilename"));
			
			setImgDir(config.getProperty("img_dir"));
			
			setMarginLeft(config.getProperty("margin_left"));
			setMarginRight(config.getProperty("margin_right"));
			setMarginTop(config.getProperty("margin_top"));
			setMarginBottom(config.getProperty("margin_bottom"));

			setEncoding(config.getProperty("encoding"));
			setTitle(config.getProperty("title_default"));

		} catch (IOException e1) {
			// if properties file does not exist, use the internal default values
			System.out.println("configuration file not found. Using internal default values.");
		}

		try {
			if(is != null) {
				is.close();
			}
		} catch (IOException e) {
		}
	}
	
	// pdfFilename
	public String getPdfFilename() {
		return this.pdfFilename;
	}
	public void setPdfFilename(String pdfFilename) {
		if (pdfFilename == null || pdfFilename.length() < 1) {
			this.pdfFilename = DEFAULT_PDF_FILENAME;
		} else {
			this.pdfFilename = pdfFilename;
		}
	}
	
	// jrxmlFilename
	public String getJrxmlFilename() {
		return this.jrxmlFilename;
	}
	public void setJrxmlFilename(String jrxmlFilename) {
		if (jrxmlFilename == null || jrxmlFilename.length() < 1) {
			this.jrxmlFilename = DEFAULT_JRXML_FILENAME;
		} else {
			this.jrxmlFilename = jrxmlFilename;
		}
	}
	
	// imgDir
	public String getImgDir() {
		return this.imgDir;
	}
	public void setImgDir(String imgDir) {
		if (imgDir == null || imgDir.length() < 1) {
			this.imgDir = DEFAULT_IMG_DIR;
		} else {
			this.imgDir = imgDir;
		}
	}
	
	// marginLeft
	public int getMarginLeft() {
		return marginLeft;
	}
	public void setMarginLeft(String strMarginLeft){
		this.marginLeft = convertString2Int(strMarginLeft);
		if (this.marginLeft < 0) {
			this.marginLeft = DEFAULT_LEFT_MARGIN;
		}
	}
	
	// marginRight
	public int getMarginRight() {
		return this.marginRight;
	}
	public void setMarginRight(String strMarginRight) {
		this.marginRight = convertString2Int(strMarginRight);
		if (this.marginRight < 0) {
			this.marginRight = DEFAULT_RIGHT_MARGIN;
		}
	}
	
	// marginTop
	public int getMarginTop() {
		return this.marginTop;
	}
	public void setMarginTop(String strMarginTop) {
		this.marginTop = convertString2Int(strMarginTop);
		if (this.marginTop < 0) {
			this.marginTop = DEFAULT_TOP_MARGIN;
		}
	}
	
	// marginBottom
	public int getMarginBottom() {
		return this.marginBottom;
	}
	public void setMarginBottom(String strMarginBottom) {
		this.marginBottom = convertString2Int(strMarginBottom);
		if (this.marginBottom < 0) {
			this.marginBottom = DEFAULT_BOTTOM_MARGIN;
		}
	}
	
	// encoding
	public String getEncoding() {
		return this.encoding;
	}
	public void setEncoding(String encoding){
		if (encoding == null || encoding.length() < 1) {
			this.encoding = DEFAULT_ENCODING;
		} else {
			this.encoding = encoding;
		}
	}
	
	// title
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		if (title == null || title.length() < 1) {
			this.title = DEFAULT_TITLE;
		} else {
			this.title = title;
		}
	}
	
	private int convertString2Int(String strValue) {
		if (strValue == null || strValue.length() < 1) {
			return -1;
		}
		try {
			int intValue = Integer.parseInt(strValue);
			if (intValue < 0) {
				return -1;
			} else {
				return intValue;
			}
		} catch (NumberFormatException e) {
			return -1;
		}
	}

}