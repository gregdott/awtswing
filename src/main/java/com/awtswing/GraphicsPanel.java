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
        initShapeColours();         // initialise colours List
        initShapes();               // initialise the Lists of different shapes
        addMouseMotionListener(ma); // for mouse movement events
        addMouseListener(ma);       // for mouse click events
    }

    /**
     * Create the basic shape objects we want to display and store them in arrays
     */
    private void initShapes() {

        // create 10 of each shape and add each shape to its respective List...

        for (int i = 0; i < 10; i++) {
            Rectangle rect = new Rectangle(i*100 + 100, 100, 50, 90);
            rectangles.add(rect);
        }

        for (int i = 0; i < 10; i++) {
            RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(i*100f + 100f, 200f, 50f, 90f, 10f, 10f);    
            roundedRectangles.add(roundRect);
        }

        for (int i = 0; i < 10; i++) {
            Ellipse2D.Float circle = new Ellipse2D.Float(i*100f + 100f, 300f, 90f, 90f);
            circles.add(circle);
        }

        for (int i = 0; i < 10; i++) {
            /*
             * For polygons, due to how their shape is described, we can not set separate initial location coordinates aside
             * from how we describe the points in the 2d plane that make up the polygon. So when we describe the shape
             * of a polygon, it is necessarily at a particular position defined by the points making it up. 
             * 
             * So if you want to create a polygon and place it somewhere, you must do a translate like below.
             */
            int xPoly[] = {60,62,67,75,57,40};
            int yPoly[] = {10,12,22,25,37,30};
            Polygon polygon = new Polygon(xPoly, yPoly, xPoly.length);            
            polygon.translate(i*100 + 70, 400); // put it where we want it
            polygons.add(polygon);
        }
    }

    /**
     * Gets called in the beginning and then each time repaint() is called, so when relevant mouse events
     * happen.
     */
    public void paint(Graphics g) {  
        
        super.paint(g);
        colourIndex = 0;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // make things smooooooth
        // g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // make things smooooooth (don't have text here yet so this is not currently needed)

        /*
         * We loop through each array of shapes below. Shapes can have had their position changed due to 
         * clicking and dragging on the shape, which then triggers a repaint.
         */
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
            Polygon polygon = polygons.get(i);
            paintShape(g2, polygon);
        }
    }

    /**
     * paintShape - given a Shape object and a Graphics2D object, paint the shape...
     * 
     * @param g2 Graphics2D object cast from Graphics object passed to paint() method
     * @param shape Shape object to be painted. Children of Shape (eg. Rectangle) are what get passed, so this paints various shapes.
     */
    private void paintShape(Graphics2D g2, Shape shape) {
        if (colourIndex > 120) {
            colourIndex = 0;
        }
        
        g2.setColor(colours.get(colourIndex));
        g2.fill(shape); // fills the shape defined by the Shape object with colour

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.draw(shape); // draws a line around the shape. remove this to remove the shape's borders

        colourIndex++;
    }

    // read hex colour values from Colours.java and create Color objects
    private void initShapeColours() {
        for (int i = 0; i < Colours.cols.size(); i++) {
            colours.add(Color.decode(Colours.cols.get(i)));
        }
    }


    /*
     * To keep track of which shape is being dragged, we create a simple object that stores
     * the array the object being dragged belongs to, along with its index in that array.
     * 
     * If we don't keep track of this, we have a rather annoying effect take place where shapes start to 
     * stick together if you drag one shape past another. This is facilitated by how the detection of which
     * shape to move is done below. So this is really just to stop that weird effect from happening and make
     * sure that only one shape is dragged at a time.
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

    /*
     * This is what we use for registering mouse events and doing things
     */
    class MovementAdapter extends MouseAdapter {

        private int x;
        private int y;
        private Dragging drg = new Dragging("", -1); // used for keeping track of which shape is being dragged

        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
        }

        public void mouseReleased(MouseEvent e) {
            // reset the drg object (essentially saying: "Nothing is currently being dragged")
            drg.update("", -1);
        }

        public void mouseDragged(MouseEvent e) {
            int dx = e.getX() - x;
            int dy = e.getY() - y;

            /*
             * Due to the Shape Object (parent object of all the shapes below) not having a method getBounds2D,
             * we can't just store all our shapes in a single List<Shape> and call getBounds2D. This results in 
             * quite a bit of redundancy in the code below. Need to see if there is a way to make this nicer.
             * 
             * To be 100% clear I don't like the redundancy below. I'm sure there must be a better way to do this.
             */
            
            // Check if we are moving a rectangle
            for (int i = 0; i < rectangles.size(); i++) {
                Rectangle rect = rectangles.get(i);
                // If (x, y) is within the bounds of the shape and we are not currently dragging anything, or we are dragging this particular shape
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
                // If (x, y) is within the bounds of the shape and we are not currently dragging anything, or we are dragging this particular shape
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
                // If (x, y) is within the bounds of the shape and we are not currently dragging anything, or we are dragging this particular shape
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
                // If (x, y) is within the bounds of the shape and we are not currently dragging anything, or we are dragging this particular shape
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
