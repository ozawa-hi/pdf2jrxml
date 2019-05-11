package com.hozawa.pdf2jrxml;

import java.io.IOException;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
 
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;

public class PDFStreamEngineCustom extends PDFStreamEngine {
	private List<String> imageList = new ArrayList<String>();
	private String imgDir = "./img/";

    public PDFStreamEngineCustom() throws IOException
    {
        // preparing PDFStreamEngine
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
    }
    
    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
    {
        String operation = operator.getName();
        if( "Do".equals(operation) ) {
            COSName objectName = (COSName)operands.get(0);
            PDXObject xobject = getResources().getXObject(objectName);

            if( xobject instanceof PDImageXObject) {	// check if image
                PDImageXObject image = (PDImageXObject)xobject;
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
 
                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                float imageXScale = ctmNew.getScalingFactorX();
                float imageYScale = ctmNew.getScalingFactorY();
                
                File directory = new File(imgDir);
                if (!directory.exists()) {
                	directory.mkdir();
                }
                
                File outputFilename = new File(imgDir + "/" + objectName.getName() + ".png");
                ImageIO.write(((PDImageXObject) image).getImage(), "png", outputFilename);
                
               imageList.add(String.format("%s,%f,%f,%f,%f",
            		   objectName.getName() + ".png",
            		   ctmNew.getTranslateX(),
            		   ctmNew.getTranslateY(),
            		   imageXScale,
            		   imageYScale
            		   ));
            }
            else if(xobject instanceof PDFormXObject)
            {
                PDFormXObject form = (PDFormXObject)xobject;
                showForm(form);
            }
        }
        else
        {
            super.processOperator( operator, operands);
        }
    }
    
    public void setImgDir(String imgDir) {
    	this.imgDir = imgDir;
    }
    
    public List<String> getImageInfo() {
    	return this.imageList;
    }
}
