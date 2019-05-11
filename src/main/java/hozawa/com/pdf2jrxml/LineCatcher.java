package hozawa.com.pdf2jrxml;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;



public class LineCatcher extends PDFGraphicsStreamEngine {
	private List<String> lineList = new ArrayList<String>();
	
	private static final GeneralPath linePath = new GeneralPath();
    private int clipWindingRule = -1;

    public LineCatcher(PDPage page) {
        super(page);
    }


    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
        // to ensure that the path is created in the right direction, we have to create
        // it by combining single lines instead of creating a simple rectangle
        linePath.moveTo((float) p0.getX(), (float) p0.getY());
        linePath.lineTo((float) p1.getX(), (float) p1.getY());
        linePath.lineTo((float) p2.getX(), (float) p2.getY());
        linePath.lineTo((float) p3.getX(), (float) p3.getY());

        // close the subpath instead of adding the last line so that a possible set line
        // cap style isn't taken into account at the "beginning" of the rectangle
        linePath.closePath();
    }

    @Override
    public void drawImage(PDImage pdi) throws IOException {
    }

    @Override
    public void clip(int windingRule) throws IOException {
        // the clipping path will not be updated until the succeeding painting operator is called
        clipWindingRule = windingRule;
    }

    @Override
    public void moveTo(float x, float y) throws IOException {
        linePath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) throws IOException {
        linePath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        linePath.curveTo(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint() throws IOException {
        return linePath.getCurrentPoint();
    }

    @Override
    public void closePath() throws IOException {
        linePath.closePath();
    }

    @Override
    public void endPath() throws IOException {
        if (clipWindingRule != -1) {
            linePath.setWindingRule(clipWindingRule);
            getGraphicsState().intersectClippingPath(linePath);
            clipWindingRule = -1;
        }
        linePath.reset();

    }

    @Override
    public void strokePath() throws IOException {
        Rectangle2D rect = linePath.getBounds2D();
        Line2D line = new Line2D.Double(rect.getX(), rect.getY(),
                rect.getX() + rect.getWidth(),
                rect.getY() + rect.getHeight());
        
        boolean isRect = false;
        
        List<double[]> pointList = new ArrayList<double[]>();
        double[] coords = new double[6];
        int numSubPaths = 0;
        for (PathIterator pi = linePath.getPathIterator(null); ! pi.isDone(); pi.next()) {
            switch (pi.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO:
                pointList.add(Arrays.copyOf(coords, 2));
                ++ numSubPaths;
                break;
            case PathIterator.SEG_LINETO:
                pointList.add(Arrays.copyOf(coords, 2));
                break;
            case PathIterator.SEG_CLOSE:
            	isRect = true;
            default:
                //throw new IllegalArgumentException("Path contains curves");
            }
        }
        lineList.add(String.format("%f,%f,%f,%f,%b",
     		   rect.getX(),
     		   rect.getY(),
     		   rect.getWidth(),
     		   rect.getHeight(),
     		   isRect
     		   ));
        
        linePath.reset();
    }

    @Override
    public void fillPath(int windingRule) throws IOException {
        linePath.reset();
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
        linePath.reset();
    }

    @Override
    public void shadingFill(COSName cosn) throws IOException {
    }
    
    public List<String> getLineInfo() {
    	return this.lineList;
    }
}
