package com.awtswing;


import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;  
import java.awt.event.MouseEvent;

import java.util.List;
import java.util.ArrayList;


/*
 * Author: Gregory Dott
 * 11-11-2022
 * 
 * This file deals with drawing basic shapes in AWT with Java and a few mouse event things.
 * 
 * Some info on the java awt shape classes here:
 * https://docstore.mik.ua/orelly/java-ent/jfc/ch04_04.htm
 * 
 */

class Dragging {
    String arr;
    int index;

    public Dragging(String arr, int index) {
        this.arr = arr;
        this.index = index;
    }

    public void update(String arr, int index) {
        this.arr = arr;
        this.index = index;
    }
}



// Should rename more sensibly when this takes shape
public class GraphicsPanel extends JPanel {
    List<Ellipse2D.Float> circles = new ArrayList<Ellipse2D.Float>();
    List<Rectangle> rectangles = new ArrayList<Rectangle>();
    List<RoundRectangle2D.Float> roundedRectangles = new ArrayList<RoundRectangle2D.Float>();
    List<Polygon> polygons = new ArrayList<Polygon>();
    List<Color> colours = new ArrayList<Color>();

    MovementAdapter ma = new MovementAdapter();

    int frameWidth = 1600;
    int frameHeight = 900;

    int colourIndex = 0;
    
    JTextArea textArea;
    JScrollPane scrollPane;

    
    @Override // needed to ensure the display size is correct. See README for futher details
    public Dimension getPreferredSize() {
        return new Dimension(frameWidth, frameHeight);
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("Graphics Examples");
        GraphicsPanel gp = new GraphicsPanel();
        gp.setDoubleBuffered(true);
        
        frame.setContentPane(gp);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public GraphicsPanel() {
        initShapeColours();
        initShapes();
        addMouseMotionListener(ma);
        addMouseListener(ma);
    }

    private void initShapeColours() {
        for (int i = 0; i < Colours.cols.size(); i++) {
            colours.add(Color.decode(Colours.cols.get(i)));
        }
    }

    private void initShapes() {

        //create 10 of each shape...

        for (int i = 0; i < 10; i++) {
            Rectangle rect = new Rectangle(100, 100, 50, 90);
            rectangles.add(rect);
        }

        for (int i = 0; i < 10; i++) {
            RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(200f, 100f, 50f, 90f, 10f, 10f);    
            roundedRectangles.add(roundRect);
        }

        for (int i = 0; i < 10; i++) {
            Ellipse2D.Float circle = new Ellipse2D.Float(300f, 100f, 100f, 100f);
            circles.add(circle);
        }

        for (int i = 0; i < 10; i++) {
            int xPoly[] = {600,620,670,750,570,400};
            int yPoly[] = {100,120,220,250,370,300};
            Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
            polygons.add(poly);
        }
    }

    /**
     * Gets called in the beginning and then each time repaint() is called, so when relevant mouse events
     * happen.
     */
    public void paint(Graphics g) {  
        
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle rect = rectangles.get(i);
            paintShape(g2, rect);
        }

        for (int i = 0; i < roundedRectangles.size(); i++) {
            RoundRectangle2D.Float roundRect = roundedRectangles.get(i);
            paintShape(g2, roundRect);
        }

        for (int i = 0; i < circles.size(); i++) {
            Ellipse2D.Float circ = circles.get(i);
            paintShape(g2, circ);
        }

        for (int i = 0; i < polygons.size(); i++) {
            Polygon poly = polygons.get(i);
            paintShape(g2, poly);
        }
    }

    private void paintShape(Graphics2D g2, Shape shape) {
        if (colourIndex > 120) {
            colourIndex = 0;
        }
        
        g2.setColor(colours.get(colourIndex));
        g2.fill(shape);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));
        g2.draw(shape);
        colourIndex++;
    }

    class MovementAdapter extends MouseAdapter {

        private int x;
        private int y;
        private Dragging drg = new Dragging("", -1);

        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
        }

        public void mouseReleased(MouseEvent e) {
            drg.update("", -1);
        }

        public void mouseDragged(MouseEvent e) {

            int dx = e.getX() - x;
            int dy = e.getY() - y;

            /*
             * Due to the Shape Object (parent object of all the shapes below) not having a method getBounds2D,
             * we can't just store all our shapes in a single List<Shape> and call getBounds2D. This results in 
             * quite a bit of redundancy in the code below. Need to see if there is a way to make this nicer.
             */
            
            // Check if we are moving a rectangle
            for (int i = 0; i < rectangles.size(); i++) {
                Rectangle rect = rectangles.get(i);

                if (rect.getBounds2D().contains(x, y) && ((drg.index == -1) || (drg.index == i && drg.arr == "rectangles"))) {
                    drg.index = i;
                    drg.arr = "rectangles";
                    rect.x += dx;
                    rect.y += dy;
                    repaint();
                }
            }

            // Check if we are moving a rounded rectangle
            for (int i = 0; i < roundedRectangles.size(); i++) {
                RoundRectangle2D.Float roundRect = roundedRectangles.get(i);

                if (roundRect.getBounds2D().contains(x, y) && ((drg.index == -1) || (drg.index == i && drg.arr == "roundedRectangles"))) {
                    drg.index = i;
                    drg.arr = "roundedRectangles";
                    roundRect.x += dx;
                    roundRect.y += dy;
                    repaint();
                }
            }

            // Check if we are moving a circle
            for (int i = 0; i < circles.size(); i++) {
                Ellipse2D.Float circle = circles.get(i);

                if (circle.getBounds2D().contains(x, y) && ((drg.index == -1) || (drg.index == i && drg.arr == "circles"))) {
                    drg.index = i;
                    drg.arr = "circles";
                    circle.x += dx;
                    circle.y += dy;
                    repaint();
                }
            }

            // Check if we are moving a polygon
            for (int i = 0; i < polygons.size(); i++) {
                Polygon polygon = polygons.get(i);

                if (polygon.getBounds2D().contains(x, y) && ((drg.index == -1) || (drg.index == i && drg.arr == "polygons"))) {
                    drg.index = i;
                    drg.arr = "polygons";
                    polygon.translate(dx, dy);
                    repaint();
                }
            }

            x += dx;
            y += dy;
        }
    }
}
