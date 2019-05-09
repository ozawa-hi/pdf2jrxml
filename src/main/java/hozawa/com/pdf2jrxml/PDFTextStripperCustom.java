package hozawa.com.pdf2jrxml;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class PDFTextStripperCustom extends PDFTextStripper {

	public PDFTextStripperCustom() throws IOException {
		super();
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
        
        writeString(String.format("[%s , %s , %f, %f, %s, %f, %f, %b, %b, %b, %s]", text.getXDirAdj(),
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
}
