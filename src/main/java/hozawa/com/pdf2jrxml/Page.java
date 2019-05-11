package hozawa.com.pdf2jrxml;

import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPage;

import net.sf.jasperreports.engine.design.JRDesignElement;

/**
 * Class containing information from pdf page that will be used to generate jrxml file.
 * 
 * @author Hitoshi Ozawa
 */
public class Page {
	private PDPage page;
	private List<JRDesignElement> elementList;
	private String title;
	private Map<String, Object> fields;
	
	// page
	public void setPage(PDPage page) {
		this.page = page;
	}
	public PDPage getPage() {
		return this.page;
	}
	
	// elementList
	public void setElementList(List<JRDesignElement> elementList) {
		this.elementList = elementList;
	}
	public void addElementList(List<JRDesignElement> elementList) {
		this.elementList.addAll(elementList);
	}
	public List<JRDesignElement> getElementList() {
		return this.elementList;
	}

	// String
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return this.title;
	}
	
	// Map<String, Object>
	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}
	public void addFields(Map<String, Object> fields) {
		this.fields.putAll(fields);
	}
	public Map<String, Object> getFields() {
		return this.fields;
	}
}
