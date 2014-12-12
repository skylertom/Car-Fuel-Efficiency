import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.Iterator; 
import java.util.Arrays; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class parallelcoordstest extends PApplet {

boolean dragging = false; // whether or not the mouse is being dragged
PVector cornerA = new PVector(0,0);
ArrayList<HashMap<String,Float>> data = new ArrayList<HashMap<String,Float>>();
String filename = "iris.csv";

// Viewports:
Viewport root_vp = new Viewport();
Viewport pc_vp = new Viewport(root_vp, 0.1f, 0.05f, 0.80f, 0.90f);

// Views:
ParallelCoord pc;

public void setup() {
  size(900,400);
  smooth();
  background(255);
  frame.setResizable(true);
  String labels[] = parseData(filename, data);
  pc = new ParallelCoord(pc_vp, data, labels);
}

public void draw() {
  background(255); 
  pc.draw();
}

public void mousePressed() {

  pc.clearSelectedArea();
  cornerA.x = mouseX;
  cornerA.y = mouseY;
  for (int i = 0; i < pc.markedIndexes.size();i++){
     pc.marks[pc.markedIndexes.get(i)] = true;
  }
}

public void mouseDragged() {
  dragging = true;
  pc.setSelectedArea(cornerA.x, cornerA.y, mouseX, mouseY);
   pc.marks[pc.markedIndexes.get(4)] = true;
  
}

public void mouseReleased() {
  if (!dragging) pc.switchAxis();
}



//min max 
//number of ticks
//location
//draw
//animation

class Axis {
	//add x y values
	float minValue; 
	float maxValue;
	float intervalValue;
	int numTicks;
	Viewport vp;
	float intervalH;
	String label;
	float margin = (float).90f;
	boolean flipped = false;

	class TextLabel {  
  //Vector coordinates for the box around text
		PVector p1 = null;
		PVector p2 = null;
		float a  = textAscent() * (float)0.4f;
		String label = ""; 
		boolean intersect;
		TextLabel(float tx, float ty,  String label_) {
			float x1_ = tx;
			float y1_ = ty - a; 
			float x2_ = x1_ + textWidth(label_);
			float y2_ = y1_+ a;
			float x1 = x1_ < x2_ ? x1_ : x2_;
			float x2 = x1_ >= x2_ ? x1_ : x2_;
			float y1 = y1_ < y2_ ? y1_ : y2_;
			float y2 = y1_ >= y2_ ? y1_ : y2_;
			p1 = new PVector(x1, y1);
			p2 = new PVector(x2, y2);
			label = label_;
		}

		public void drawTextLabel(){
			if (isIntersecting()){
				fill(255, 0, 0    );
			} else {
				fill(0, 0, 0);
			}
			text(label, p1.x, p2.y);
		}

		public boolean isIntersecting(){
			if (mouseX > p1.x-10 && mouseX < p2.x+10 && mouseY > p1.y-30 && mouseY < p2.y+40){
				return true;
			} else {
				return false;
			}
		}
	}
	HashMap<Integer, TextLabel> tickLabels;
	TextLabel labelCoord;

	Axis(Viewport vp, String label, float min_, float max_, int numTicks){
		this.vp = vp;
		this.label = label;
		minValue = min_;
		maxValue = max_;
		intervalH = vp.getH()*margin/numTicks;
		intervalValue = (maxValue - minValue) / numTicks;
		this.numTicks = numTicks;
	}

	public void switchAxis() {
		if (flipped) flipped = false;
		else flipped = true;
	}

	public void draw() {
		fill(200);
		rect(vp.getX(), vp.getY(), vp.getW()/30, vp.getH()*margin);
		stroke(0);
		fill(0);
		drawTicksLabels();
		labelCoord = new TextLabel( vp.getX()-(textWidth(label)/(float)2.4f), vp.getY() + vp.getH(), label); 
		labelCoord.drawTextLabel();
	}

	public void drawTicksLabels(){
		tickLabels = new HashMap<Integer, TextLabel>();
		float current =  (float)(Math.round(maxValue * 100.0f) / 100.0f); 
		float incrementValue = (vp.getH()*margin)/numTicks;
		if (flipped) {
			current =  (float)(Math.round(minValue * 100.0f) / 100.0f); 
			for (int i = 0; i <= numTicks; i++){
				line(vp.getX() - vp.getW()/39,
					vp.getY() + (incrementValue*i),
					vp.getX() + vp.getW()/16,
					vp.getY() + (incrementValue*i));
				TextLabel tempText = new TextLabel(vp.getX()-50, vp.getY()+(incrementValue*i), Float.toString((float)(Math.round(current * 100.0f) / 100.0f))); 
				tempText.drawTextLabel();
				tickLabels.put(i, tempText);
				current += (float)(Math.round(intervalValue * 100.0f) / 100.0f);  
			}
		}
		else {
			for (int i = 0; i <= numTicks; i++){
				line(vp.getX() - vp.getW()/39,
					vp.getY() + (incrementValue*i),
					vp.getX() + vp.getW()/16,
					vp.getY() + (incrementValue*i));
				TextLabel tempText = new TextLabel(vp.getX()-50, vp.getY()+(incrementValue*i), Float.toString((float)(Math.round(current * 100.0f) / 100.0f))); 
				tempText.drawTextLabel();
				tickLabels.put(i, tempText);
				current -= (float)(Math.round(intervalValue * 100.0f) / 100.0f);  
			}
		}
	}
	//Getter functions
    //since axises are just viewports the value is stored within here
	public float getX(){return vp.getX(); }
	public float getY(){return vp.getY(); }
	public float getRange(){return maxValue-minValue; }
	public float getH(){return vp.getH()*margin; }
	public float getW(){return vp.getW()/30; }
	public float getMin(){return minValue; }
	public float getMax(){return maxValue; }
	public String getName(){return label; }

  // Convert from data coordinates to axis / window coordinates
	public float getLoc(float y) {
		if(flipped) return getY() + getH()*margin * (y - getMin()) / getRange();
		else return getY() + getH()*margin - getH()*margin * (y - getMin()) / getRange();
	}

	public boolean isOnMe() {
		return (getY()<mouseY&&getX()<mouseX&&(getX()+getW())>mouseX&&(getY()+getH())>mouseY);
	}

	public void moveAxis(float x){
		if (mousePressed){
			if (labelCoord != null && labelCoord.isIntersecting()){
  		    vp.setX(x);
  		}
  	}
  }
}






class ParallelCoord {
	
  ArrayList<HashMap<String,Float>> data;
  ArrayList<Boolean> marked;
  boolean marks[];
  ArrayList<Integer> markedIndexes;
  String labels[];
  HashMap<String,Range> dimensions; 
  HashMap<String,Float> min, max;
  HashMap<String,Axis> axes;
  Viewport vp;
  Rectangle selectArea = null;

  ParallelCoord(Viewport vp, ArrayList<HashMap<String,Float>> data, String[] labels) {
    this.data = data;
    marks = new boolean[data.size()];
    Arrays.fill(marks, false);
    this.labels = labels;
    this.vp = vp;
    updateMinMax();
    axes = new HashMap<String,Axis>();
    dimensions = new HashMap<String,Range>();
    markedIndexes = new ArrayList<Integer>();
    Viewport ax_vp;
    for (int i = 0; i < labels.length; i++) {
      ax_vp = new Viewport(vp, ((float)i) / (labels.length - 1),
       (float)0.0f,
       (float)0.3f,
       (float)1.0f);
      axes.put(labels[i], new Axis(ax_vp, labels[i], min.get(labels[i]), max.get(labels[i]), 5));
            dimensions.put(labels[i], new Range( ax_vp.getX(), ax_vp.getY() ,  ax_vp.getW(), ax_vp.getH()));


    }
  }

  public void updateMinMax() {
		//initialize these
    min = new HashMap<String,Float>();
    max = new HashMap<String,Float>();
    for (int i = 0; i < labels.length; i++) {
      min.put(labels[i], data.get(0).get(labels[i]));
      max.put(labels[i], data.get(0).get(labels[i]));
    }

    Float a,b,c;
    for (int i = 0; i < data.size(); i++) {
      for (int j = 0; j < labels.length; j++) {
        a = min.get(labels[j]);
        b = max.get(labels[j]);
        c = data.get(i).get(labels[j]);
        if (a > c)
          min.put(labels[j], c);
        if (b < c)
          max.put(labels[j], c);
      }
    }
  }

  public void draw() {
   
    hover();
    drawData();
    Iterator<String> iter = axes.keySet().iterator();
    while(iter.hasNext()) {
      String key = iter.next();
      axes.get(key).draw();
    }
     drawSelectedArea();

     
  }

  public void drawData() {
    HashMap<String,Float> datum; // tmp loop data point
    Axis ax1,ax2; // tmp loop axes
    float y1,y2; // tmp loop floats

    for (int i = 0; i < data.size(); i++) {
      datum = data.get(i);
      stroke(0, 120);
      if (marks[i]) stroke(255, 0, 0);
      for (int j = 0; j < labels.length - 1; j++) {
        ax1 = axes.get(labels[j]);
        ax2 = axes.get(labels[j+1]);
        y1 = datum.get(labels[j]);
        y2 = datum.get(labels[j+1]);
        line(ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2));
      }

      for (int m = 0; m <axes.size(); m++){
        axes.get(labels[m]).moveAxis((float)mouseX/width);
      }
    }
  }

  public void switchAxis() {
      for (int m = 0; m <axes.size(); m++){
        if (axes.get(labels[m]).isOnMe())
          axes.get(labels[m]).switchAxis();
      }
  }

  public void clearSelectedArea() {
    selectArea = null;
  }

  public void setSelectedArea(float x, float y, float x1, float y1) {
    selectArea = new Rectangle(x, y, x1, y1);
  }
  
  
  public void drawSelectedArea() {
    pushStyle();
    for (int i = 0; i < axes.size(); i++){
      //Limit the selection just to Y axises. 
      if (mouseY < axes.get(labels[i]).getH()*0.999f ){
        if ( mouseX < axes.get(labels[i]).getX()+40) {
        if (selectArea != null) {
          fill(171,217,233,80);
          rectMode(CORNER);
          rect(selectArea.p1.x, selectArea.p1.y,
            selectArea.p2.x - selectArea.p1.x, selectArea.p2.y - selectArea.p1.y);

        }
      }
  }
}
    popStyle();
  }

  public void hover() {
    Arrays.fill(marks, false);
    HashMap<String,Float> datum; // tmp loop data point
    Axis ax1,ax2; // tmp loop axes
    float y1,y2; // tmp loop floats
    

    for (int i = 0; i < data.size(); i++) {
      datum = data.get(i);
      for (int j = 0; j < labels.length - 1; j++) {
        ax1 = axes.get(labels[j]);
        ax2 = axes.get(labels[j+1]);
        y1 = datum.get(labels[j]);
        y2 = datum.get(labels[j+1]);
        float tempLine[] = {ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2)};
        if (selectArea != null) {
          if (intersect(selectArea, tempLine)) {
            marks[i] = true;
            break;
          }
        }
        else if (intersect(mouseX, mouseY, tempLine)) {
          marks[i] = true;
          markedIndexes.add(i);
          break;
        }
      }
    }
  }

  public boolean intersect(Rectangle r, float myline[]) {
    //positive slope
    if (r.p2.x<=myline[0] || r.p1.x>=myline[2]) {
      return false;
    }
    if (myline[3]>myline[1]) {
      if (r.p2.y <= myline[1] || r.p1.y >= myline[3]) {
        return false;
      }
      float realslope = (myline[3]-myline[1])/(myline[2]-myline[0]);
      float x1, y1, x2, y2;
      float minslope = (r.p1.y-myline[1])/(r.p1.x-myline[0]);
      float maxslope = (r.p2.y-myline[1])/(r.p1.x-myline[0]);
      return (realslope < maxslope && realslope > minslope);
    }
    //negative slope
    if (r.p2.y <= myline[3] || r.p1.y >= myline[1]) {
        return false;
    }
    float realslope = (myline[3]-myline[1])/(myline[2]-myline[0]);
    float x1, y1, x2, y2;
    float minslope = (r.p1.y-myline[1])/(r.p1.x-myline[0]);
    float maxslope = (r.p2.y-myline[1])/(r.p1.x-myline[0]);
    return (realslope < maxslope && realslope > minslope);
  }

  //myline should be an array with form {x1, y1, x2, y2}
  public boolean intersect(float myx, float myy, float myline[]) {
    if (myx < myline[0] || myx > myline[2] || myy < Math.min(myline[1],myline[3]) || myy > Math.max(myline[3],myline[1]))
      return false;
    float realslope = (myline[3]-myline[1])/(myline[2]-myline[0]);
    float myslope = (myline[3]-myy)/(myline[2]-myx);
    return Math.abs(realslope-myslope) < .03f;
  }
}

class Rectangle { // model dragging area
  PVector p1 = null;
  PVector p2 = null;

  Rectangle(float x1_, float y1_, float x2_, float y2_) {
    float x1 = x1_ < x2_ ? x1_ : x2_;
    float x2 = x1_ >= x2_ ? x1_ : x2_;
    float y1 = y1_ < y2_ ? y1_ : y2_;
    float y2 = y1_ >= y2_ ? y1_ : y2_;
    p1 = new PVector(x1, y1);
    p2 = new PVector(x2, y2);
  }
}

class Range { 
  float x;
  float y; 
  float w; 
  float h;

  Range(float x1_, float y1_, float w1_, float h1_) {
    x= x1_;
    y= y1_;
    w= w1_;
    h= h1_;
  }
}


//import processing.core.*;

public class Viewport {
	private float x, y, w, h;
	Viewport() {
		x = 0;
		y = 0;
		w = 1;
		h = 1;
	}
	Viewport(Viewport parent, float _x, float _y, float _w, float _h) {
		x = parent.getrelX() + _x*parent.getrelW();
		y = parent.getrelY() + _y*parent.getrelH();
		w = _w * parent.getrelW();
		h = _h * parent.getrelH();
	}

	public float getrelX() {return x;}
	public float getrelY() {return y;}
	public float getrelW() {return w;}
	public float getrelH() {return h;}
	public float getX() {return width * x;}
	public float getY() {return height * y;}
	public float getW() {return width * w;}
	public float getH() {return height * h;}
	public void setX(float newX) {x = newX; }
};

public String[] parseData(String filename, ArrayList<HashMap<String,Float>> data) {

  String t0[] = loadStrings(filename);
  String t1[];
  HashMap<String,Float> curr;

  String labels[] = splitTokens(t0[0],",");

  for (int i = 1; i < t0.length; i++) {
    t1 = splitTokens(t0[i], ",");
    curr = new HashMap<String,Float>();
    for (int j = 0; j < t1.length; j++) {
      curr.put(labels[j], Float.parseFloat(t1[j]));
    }
    data.add(curr);
  }
  return labels;

}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "parallelcoordstest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}