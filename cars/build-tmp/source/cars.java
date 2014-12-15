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

public class cars extends PApplet {

Table data_00;
Table data_15;
int numRows_00;
int numRows_15;

String[] data_001;
String pclabels[];
Viewport root_vp = new Viewport();
Viewport pc_vp = new Viewport(root_vp, 0.1f, 0.05f, 0.80f, 0.40f);
Viewport class_vp = new Viewport(root_vp, 0.05f, 0.50f, 0.4f, 0.40f);
Viewport brand_vp = new Viewport(root_vp, 0.55f, 0.50f, 0.40f, 0.40f);

//views:
ParallelCoord pc;
ClassGraph class_bg;
BrandGraph brand_bg;

public void setup() {
  size(900,600);
  smooth();
  background(255);
  frame.setResizable(true);
  parseData(true); 
  pclabels = new String[] {"Cyl", "Air Pollution Score","City MPG","Hwy MPG","Cmb MPG","Greenhouse Gas Score"};
  pc = new ParallelCoord(pc_vp, pclabels, data_15);
  class_bg = new ClassGraph(class_vp, data_00);
}

public void draw() {
  background(255);
  pc.draw();
  class_bg.drawAxes();
}

public void mousePressed() {
  pc.mousePressed();
}

public void mouseDragged() {
  pc.mouseDragged();
}

public void mouseReleased() {
  pc.mouseReleased();
}

public void parseData(boolean notloaded) {
  if (notloaded) {
    data_00 = loadTable("all_alpha_00.csv", "header");
    parseTable(data_00);
    saveTable(data_00, "parsed_00.csv");
    data_15 = loadTable("all_alpha_00.csv", "header");
    parseTable(data_15);
    saveTable(data_15, "parsed_15.csv");
  }
  else {
    data_00 = loadTable("parsed_00.csv", "header");
    data_15 = loadTable("parsed_15.csv", "header");
  }
}

public void parseTable(Table t) {
  String lastrow = "";
  t.addColumn("Brand");
  for (int i = 0; i < t.getRowCount(); i++) {
    TableRow row = t.getRow(i);
    if (!row.getString("Fuel").equals("Gasoline"))
      t.removeRow(i--);
    else if (row.getString("City MPG").equals("N/A"))
      t.removeRow(i--);
    else if (row.getString("City MPG").equals("N/A*"))
      t.removeRow(i--);
    else if (row.getString("Air Pollution Score").equals("N/A"))
      t.removeRow(i--);
    else if (row.getString("Cyl").equals(""))
      t.removeRow(i--);
    else if (row.getString("Trans").equals(""))
      t.removeRow(i--);
    else if (row.getString("Drive").equals(""))
      t.removeRow(i--);
    else {
      String x = row.getString("Model");
      if (x.equals(lastrow)) {
        t.removeRow(i--);
        continue;
      }
      String brand = x.substring(0, x.indexOf(' '));
      if (brand.equals("ASTON")) {
        brand += " MARTIN";
      } else if (brand.equals("ALFA")) {
        brand += " ROMEO"; 
      } else if (brand.equals("LAND")) {
        brand += " ROVER"; 
      }
      row.setString("Brand", brand);
      lastrow = x;
    }
  }
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

public class BrandGraph {
  Viewport vp;
  Table data;
 
  BrandGraph(Viewport vp, Table d) {
    this.vp = vp;
    data = d;
  } 
  
  
  
}
public class ClassGraph {
  Viewport vp;
  Table data;
  HashMap<String, Float> classes;
  Float min, max;
  int vert_ticks = 7;
  int extend = 10;
  
  ClassGraph(Viewport vp, Table d) {
     this.vp = vp;
     data = d;
     classes = new HashMap<String, Float>();
     min = 0.0f;
     max = 0.0f;
     filterData();
  }
  
  public void filterData() {
    for (int i = 0; i < data.getRowCount(); i++) {
       String vehClass = data.getString(i, "Veh Class");
       Float avg = classes.get(vehClass);
       Float rowMPG = data.getFloat(i, "Cmb MPG");
       if (avg == null) {
         classes.put(vehClass, rowMPG);
       } else {
         float rowVal = data.getFloat(i, "Cmb MPG");
         avg = (avg + rowMPG) / 2;
         classes.put(vehClass, avg);
       }
    }
    println(classes);
  }
  
  public void drawAxes() {
    String[] vehClasses = classes.keySet().toArray(new String[0]);
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX() + vp.getW() + extend, vp.getY() + vp.getH());
    float horiz_dist = vp.getW() / classes.size();
    for (int i = 0; i < classes.size(); i++) {
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2) + extend;
      line(h_ticks, vp.getY() + vp.getH(), h_ticks, vp.getY() + vp.getH() + 5);
      pushMatrix();
      translate(h_ticks, vp.getY() + vp.getH() + 10);
      rotate(HALF_PI/4);
      textAlign(LEFT);
      text(vehClasses[i], -7, 6);
      popMatrix();
    }
    
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX(), vp.getY());
    for (Float f : classes.values()) {
      if (min == 0.0f) {
        min = f; 
      } else if (min > f) {
        min = f; 
      }
      if (f > max) {
        max = f; 
      }
    }
    float vert_dist = vp.getH() / vert_ticks;
    for (int i = 0; i < vert_ticks; i++) {
      float v_ticks = (vert_dist * i) + vp.getY();
      line(vp.getX() - 5, v_ticks, vp.getX(), v_ticks);
    }
//    println(vp.getX(), vp.getY(), vp.getW(), vp.getH());
  }
}





class ParallelCoord {
	
  Table _data;
  boolean dragging = false; // whether or not the mouse is being dragged
  PVector cornerA = new PVector(0,0);
  ArrayList<Boolean> marked;
  boolean marks[];
  ArrayList<Integer> markedIndexes;
  String labels[];
  HashMap<String,Range> dimensions; 
  HashMap<String,Float> min, max;
  HashMap<String,Axis> axes;
  Viewport vp;
  Rectangle selectArea = null;
  ArrayList<float[]> mylines;

  ParallelCoord(Viewport vp, String[] labels, Table _data) {
    this._data = _data;
    marks = new boolean[_data.getRowCount()];
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
    mylines = new ArrayList<float[]>();
    hover();
    drawData();
  }

  public void mousePressed() {
    clearSelectedArea();
    dragging = false;
    cornerA.x = mouseX;
    cornerA.y = mouseY;
  }

  public void mouseDragged() {
    dragging = true;
    setSelectedArea(cornerA.x, cornerA.y, mouseX, mouseY);
  }

  public void mouseReleased() {
    if (!dragging) switchAxis();
  }

  public void updateMinMax() {
    min = new HashMap<String,Float>();
    max = new HashMap<String,Float>();
    for (int i = 0; i < labels.length; i++) {
      String mytext = labels[i];
      String nums[] = _data.getStringColumn(mytext);
      float mymin = Float.parseFloat(nums[0]);
      float mymax = Float.parseFloat(nums[0]);
      for (int j = 0; j < nums.length; j++) {
        float temp = Float.parseFloat(nums[j]);
        if (temp < mymin) mymin = temp;
        if (temp > mymax) mymax = temp;
      }
      min.put(mytext, mymin);
      max.put(mytext, mymax);
    }
  }

  public void draw() {
    hover();
    drawData();
    //small();
    //drawlines();
    Iterator<String> iter = axes.keySet().iterator();
    while(iter.hasNext()) {
      String key = iter.next();
      axes.get(key).draw();
    }
    drawSelectedArea();
  }

  public void small() {
    for (int i = 0; i < mylines.size(); i++) {
      float x[] = mylines.get(i);
      if (intersect(mouseX, mouseY, x)) {
        x[4] = 1;
      }
      x[4] = 0;
    }
  }

  public void drawlines() {
    for (int i = 0; i < mylines.size(); i++) {
      float x[] = mylines.get(i);
      stroke(0, 120);
      if (x[4] == 1) stroke(255, 0, 0);
      line(x[0], x[1], x[2], x[3]);
    }
  }

  public void drawData() {
    for (int i = 0; i < _data.getRowCount(); i++) {
      TableRow datum = _data.getRow(i);
      stroke(0, 120);
      if (marks[i]) stroke(255, 0, 0);
      if (marks[i]) println(datum.getString("Model"));
      for (int j = 0; j < labels.length - 1; j++) {
        Axis ax1 = axes.get(labels[j]);
        Axis ax2 = axes.get(labels[j+1]);
        float y1 = datum.getFloat(labels[j]);
        float y2 = datum.getFloat(labels[j+1]);
        //mylines.add(new float[]{ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2), 0});
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
    TableRow datum;
    Axis ax1,ax2; // tmp loop axes
    float y1,y2; // tmp loop floats
    for (int i = 0; i < _data.getRowCount(); i++) {
      datum = _data.getRow(i);
      for (int j = 0; j < labels.length - 1; j++) {
        ax1 = axes.get(labels[j]);
        ax2 = axes.get(labels[j+1]);
        y1 = datum.getFloat(labels[j]);
        y2 = datum.getFloat(labels[j+1]);
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
};

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
};

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
};


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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "cars" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
