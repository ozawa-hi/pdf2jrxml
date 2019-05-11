package com.hozawa.pdf2jrxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceRGBColor;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class PDFTextStripperCustom extends PDFTextStripper {
	private List<Map<String, Object>> charList = new ArrayList<Map<String, Object>>();	// hold character color

	public PDFTextStripperCustom() throws IOException {
		//super();
//        addOperator(new SetStrokingColorSpace());
        addOperator(new SetNonStrokingColorSpace());
//        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
//        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
//        addOperator(new SetStrokingDeviceGrayColor());
//        addOperator(new SetStrokingColor());
//        addOperator(new SetStrokingColorN());
        addOperator(new SetNonStrokingColor());
//        addOperator(new SetNonStrokingColorN());
	}

    @Override
    protected void processTextPosition(TextPosition text)
    {
        super.processTextPosition(text);

        //PDColor strokingColor = getGraphicsState().getStrokingColor();
        PDColor nonStrokingColor = getGraphicsState().getNonStrokingColor();
        String unicode = text.getUnicode();
        Map<String, Object> charMap = new HashMap<String, Object>();
        charMap.put("char", unicode);
        charMap.put("color", nonStrokingColor);
        charList.add(charMap);
    }
    
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException{
        TextPosition text = textPositions.get(0);

        float width;
        try {
        	width = text.getFont().getStringWidth(string) / 1000 * text.getFontSize();
        } catch (IllegalArgumentException e) {
        	PDFont font = PDType1Font.TIMES_ROMAN;
        	width = font.getStringWidth(string) / 1000 * text.getFontSize() * 1.5f;
        }
        float height = (text.getFont().getFontDescriptor().getCapHeight()) / 1000 * text.getFontSize();
        
        writeString(String.format("%s,%s,%f,%f,%s,%f,%f,%b,%b,%b,%s", text.getXDirAdj(),
        		text.getYDirAdj(),
        		width,
        		height,
        		text.getFont().getName(),
        		text.getFontSizeInPt(),
        		text.getFont().getFontDescriptor().getFontWeight(),
        		text.getFont().getName().toLowerCase().contains("bold"),
        		text.getFont().getFontDescriptor().isForceBold(),
        		text.getFont().getFontDescriptor().isItalic(),
        		string));

    }
    
    /**
     * Return color of characters.
     * 
     * @return color of characters.
     */
    public List<Map<String, Object>> getCharList() {
    	return this.charList;
    }
}
