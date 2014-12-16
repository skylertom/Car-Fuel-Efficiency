import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.text.DecimalFormat; 
import java.text.DecimalFormat; 
import java.util.Iterator; 
import java.lang.Iterable; 
import java.awt.Rectangle; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.ArrayList; 
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
boolean year_toggle;
float toggle_x, toggle_y, toggle_w, toggle_h;

String pclabels[];
Viewport root_vp = new Viewport();
Viewport pc_vp = new Viewport(root_vp, 0.1f, 0.03f, 0.80f, 0.40f);
Viewport class_vp = new Viewport(root_vp, 0.07f, 0.50f, 0.30f, 0.40f);
Viewport brand_vp = new Viewport(root_vp, 0.45f, 0.50f, 0.51f, 0.40f);

//views:
ClassGraph class_bg;
ClassGraph class_bg15;
BrandGraph brand_bg;
BrandGraph brand_bg15;

Controller contr;

public void setup() {
  size(900,600);
  smooth();
  background(255);
  frame.setResizable(true);
  parseData(true); 
  year_toggle = false;  
  contr = new Controller();
}

public void draw() {
  background(255);
  drawToggle();
  contr.drawViews();
}

public void drawToggle() {
  fill(255);
  toggle_x = width * .92f;
  toggle_y = height *.03f;
  toggle_w = 75;
  toggle_h = 25;  
//  rect(width*.92, height*.03, 100, 30);
  rect(toggle_x, toggle_y, toggle_w, toggle_h);
  String toggleText = null;
  if (!year_toggle) {
    toggleText = "2000";
  } else {
    toggleText = "2015"; 
  }
  fill(0,0,0);
  textAlign(CENTER);
  text(toggleText, toggle_x + toggle_w/2, toggle_y + toggle_h/2); 
  textAlign(LEFT);

}
public void mousePressed() {
  switchYear();
  contr.mousePressed();
}

public void switchYear() {
  if (mouseX >= toggle_x && mouseX <= toggle_x + toggle_w && mouseY >= toggle_y && mouseY <= toggle_y + toggle_h) {
    year_toggle = !year_toggle;
  }  
}

public void mouseDragged() {
  contr.mouseDragged();
}

public void mouseReleased() {
  contr.mouseReleased();
}

public void parseData(boolean notloaded) {
  if (notloaded) {
    data_15 = loadTable("all_alpha_15.csv", "header");
    parseTable(data_15);
    saveTable(data_15, "parsed_15.csv");
    
    data_00 = loadTable("all_alpha_00.csv", "header");
    parseTable(data_00);
    saveTable(data_00, "parsed_00.csv");
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
	Viewport tempvp;
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
			fill(0, 0, 0);
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
		tempvp = null;
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

	public boolean isinWidth() {
		return (getX()<=mouseX && getX()+getW()>=mouseX) || labelCoord.isIntersecting();
	}

	public boolean moveAxis(float x){
		if (labelCoord != null && labelCoord.isIntersecting()){
			if (tempvp == null) tempvp = new Viewport(this.vp);
			vp.setX(x);
			return true;
		}
		return false;
  	}

  	public void swap(Axis a) {
  		a.vp = this.vp;
  		this.vp = a.tempvp;
  		a.tempvp = null;
  	}

  	public void resetAxis() {
  		if (tempvp != null) {
  			vp = tempvp;
  			tempvp = null;
  		}
  	}
};



public class BrandGraph {
  Viewport vp;
  Table data;
  HashMap<String, HashMap<String, Float>> classBrands;
  HashMap<String, Float> brandsMPG;
  String[] brands;
  String mode;
  Float min, max;
  int vert_ticks = 7;
  int intersect;
  String name;
  Controller controller;
 
  BrandGraph(Viewport vp, Table d, String[] vehClasses) {
    this.name = "brandgraph";
    this.vp = vp;
    data = d;
    mode = null;
    brands = null;
    brandsMPG = null;
    min = 0.0f;
    max = 0.0f;
    classBrands = new HashMap<String, HashMap<String, Float>>();
    for (int i = 0; i < vehClasses.length; i++) {
      classBrands.put(vehClasses[i], new HashMap<String, Float>()); 
    }
    filterData();
    getMinMax();
  }

  public void setController(Controller x) {
    this.controller = x;
  }
  
  public void setMode(String m) {
    mode = m;
  }

  public void drawGraph() {
    drawHoriz();
    drawVert();
    vertDetails();
    if (mode != null) {
      brandsMPG = classBrands.get(mode);
      brands = brandsMPG.keySet().toArray(new String[0]);
      drawDetails();
      drawLabel();
    } else {
      textSize(20);
      text("Click on a bar on the left", vp.getX() + (vp.getW() / 2), vp.getY() - 12);
      textSize(12);       
    }
  }
  
  public void hover() {
    intersect = -1;
    for (int i = 0; i < brandsMPG.size(); i++) {
      float horiz_dist = vp.getW() / brandsMPG.size();      
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);  
      float bar_height = (brandsMPG.get(brands[i]) / max) * vp.getH();
      if (horiz_dist/2 > 20) {
        horiz_dist = 50; 
      }      
      if ((mouseX >= h_ticks - (horiz_dist / 4)) && (mouseX <= h_ticks - (horiz_dist / 4) + horiz_dist / 2)) {
        if ((mouseY >= (vp.getY() + vp.getH() - bar_height)) && (mouseY <= (vp.getY() + vp.getH()))) {
          intersect = i; 
        }
      }
    }
  }  
  
  public void mouseClick() {
    Message msg = new Message();
    msg.src = this.name;    
    for (int i = 0; i < brandsMPG.size(); i++) {
      float horiz_dist = vp.getW() / brandsMPG.size();      
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);  
      float bar_height = (brandsMPG.get(brands[i]) / max) * vp.getH();
      if (horiz_dist/2 > 20) {
        horiz_dist = 50; 
      }      
      if ((mouseX >= h_ticks - (horiz_dist / 4)) && (mouseX <= h_ticks - (horiz_dist / 4) + horiz_dist / 2)) {
        if ((mouseY >= (vp.getY() + vp.getH() - bar_height)) && (mouseY <= (vp.getY() + vp.getH()))) {
          msg.action = "brands";
          Condition brand_cond = new Condition("Brand","=",brands[i]);
          Condition class_cond = new Condition("Veh Class","=",mode);
          Condition[] conditions = new Condition[] {brand_cond, class_cond};
          msg.setConditions(conditions);
          controller.receiveMsg(msg);
          return;
        }
      }
    }     
  }
  
  public void drawHoriz() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX() + vp.getW(), vp.getY() + vp.getH());     
  }
  
  public void drawVert() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX(), vp.getY());     
  }
  
  public void drawDetails() {
    textSize(20);
    String toPrint = mode.substring(0, 1).toUpperCase() + mode.substring(1);
    text(toPrint + "s", vp.getX() + (vp.getW() / 2), vp.getY() - 12);
    textSize(12);
    hover();
    horizDetails();
  }
  
  public void horizDetails() {
    for (int i = 0; i < brandsMPG.size(); i++) {
      float horiz_dist = vp.getW() / brandsMPG.size();
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);
      line(h_ticks, vp.getY() + vp.getH(), h_ticks, vp.getY() + vp.getH() + 5);
      float bar_height = (brandsMPG.get(brands[i]) / max) * vp.getH();
      if (brands[i].equals(controller.brand)) {
        fill(255,0,0); 
      } else if (this.intersect == i) {
        fill(100,100,100);
      } else {
        fill(0,0,0);
      }
      if (horiz_dist/2 > 20) {
        horiz_dist = 50; 
      }
      rect(h_ticks - (horiz_dist / 4), vp.getY() + (vp.getH() - bar_height), horiz_dist / 2, bar_height);
      pushMatrix();
      translate(h_ticks, vp.getY() + vp.getH() + 10);
      rotate(HALF_PI);
      textAlign(LEFT);
      text(brands[i], -7, 6);
      popMatrix();
      fill(0,0,0);
    }     
  }
  
  public void vertDetails() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX(), vp.getY());
    float vert_dist = vp.getH() / vert_ticks;
    float iter = 0;
    float max_label = ceil(max);
    DecimalFormat df = new DecimalFormat("##.0");    
    for (int i = 0; i < vert_ticks; i++) {
      float v_ticks = (vert_dist * i) + vp.getY();
      line(vp.getX() - 5, v_ticks, vp.getX(), v_ticks);
      iter = (max_label / vert_ticks) * (vert_ticks - i);
      textAlign(CENTER, CENTER);
      text(df.format(iter), vp.getX() - 20, v_ticks);
    } 
    pushMatrix();
    translate(vp.getX() - (vp.getX() * .11f), vp.getY() + (vp.getH() / 2));
    rotate(-HALF_PI);
    text("Average Cmb MPG", 0, 0);
    popMatrix();     
  }
  
  public void getMinMax() {
    for (HashMap<String, Float> brandsMPG : classBrands.values()) {
      for (Float f : brandsMPG.values()) {
        if (min == 0.0f) {
          min = f;
        } else if (min > f) {
          min = f; 
        }
        if (max < f) {
          max = f; 
        }
      }
    }  
  }
  
  public void filterData() {
    for (int i = 0; i < data.getRowCount(); i++) {
      String vehClass = data.getString(i, "Veh Class");
      String rowBrand = data.getString(i, "Brand");
      Float rowMPG = data.getFloat(i, "Cmb MPG");
      HashMap<String, Float> brandsMPG = classBrands.get(vehClass);
      Float avg = brandsMPG.get(rowBrand);
      if (avg == null) {
        brandsMPG.put(rowBrand, rowMPG);
        classBrands.put(vehClass, brandsMPG);
      } else {
        avg = (avg + rowMPG) / 2;
        brandsMPG.put(rowBrand, avg);
        classBrands.put(vehClass, brandsMPG);
      }
    }
   }
  
  public void drawLabel() {
    DecimalFormat df = new DecimalFormat("##.0");
    if (controller.brand != null) {
      for (int i = 0; i < brands.length; i++) {
        if (controller.brand.equals(brands[i])) {
          intersect = i;
          break;
        } 
      }      
      float horiz_dist = vp.getW() / brandsMPG.size();
      float h_ticks = vp.getX() + (horiz_dist * intersect) + (horiz_dist / 2);
      float bar_height = (brandsMPG.get(brands[intersect]) / max) * vp.getH();      
      fill(0,0,0);
      textAlign(CENTER);
      text(df.format(brandsMPG.get(brands[intersect])), h_ticks, vp.getY() + (vp.getH() - bar_height) - 7);
      textAlign(LEFT);
    }
  }
  
}


public class ClassGraph {
  Viewport vp;
  Table data;
  HashMap<String, Float> classMPG;
  String[] vehClasses;
  Float min, max;
  int vert_ticks = 7;
  int extend = 10;
  int intersect;
  DecimalFormat df;
  Controller controller;
  String name;
  
  ClassGraph(Viewport vp, Table d) {
    this.name = "classgraph";
     this.vp = vp;
     data = d;
     classMPG = new HashMap<String, Float>();
     min = 0.0f;
     max = 0.0f;
     filterData();
     getMinMax();
  }

  public void setController(Controller x) {
    this.controller = x;
  }

  public void filterData() {
    for (int i = 0; i < data.getRowCount(); i++) {
       String vehClass = data.getString(i, "Veh Class");
       Float avg = classMPG.get(vehClass);
       Float rowMPG = data.getFloat(i, "Cmb MPG");
       if (avg == null) {
         classMPG.put(vehClass, rowMPG);
       } else {
         avg = (avg + rowMPG) / 2;
         classMPG.put(vehClass, avg);
       }
    }
    vehClasses = classMPG.keySet().toArray(new String[0]);
  }

  public void getMinMax() {
    for (Float f : classMPG.values()) {
      if (min == 0.0f) {
        min = f; 
      } else if (min > f) {
        min = f; 
      }
      if (f > max) {
        max = f; 
      }
    }     
  }
  
  public void drawGraph() {
    hover();
    drawHoriz();
    drawVert();
    drawLabel();
  }
  
  public void hover() {
    intersect = -1;
    float horiz_dist = vp.getW() / classMPG.size();
    for (int i = 0; i < classMPG.size(); i++) {
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);  
      float bar_height = (classMPG.get(vehClasses[i]) / max) * vp.getH();    
      if ((mouseX >= h_ticks - (horiz_dist / 4)) && (mouseX <= h_ticks - (horiz_dist / 4) + horiz_dist / 2)) {
        if ((mouseY >= (vp.getY() + vp.getH() - bar_height)) && (mouseY <= (vp.getY() + vp.getH()))) {
          intersect = i; 
        }
      }
    }
  }

  public void mouseClick() {
    Message msg = new Message();
    msg.src = this.name;
    float horiz_dist = vp.getW() / classMPG.size();
    for (int i = 0; i < classMPG.size(); i++) {
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);  
      float bar_height = (classMPG.get(vehClasses[i]) / max) * vp.getH();    
      if ((mouseX >= h_ticks - (horiz_dist / 4)) && (mouseX <= h_ticks - (horiz_dist / 4) + horiz_dist / 2)) {
        if ((mouseY >= (vp.getY() + vp.getH() - bar_height)) && (mouseY <= (vp.getY() + vp.getH()))) {
          msg.action = "new graph";
          msg.addCondition(new Condition("Veh Class","=",vehClasses[i]));
          controller.receiveMsg(msg);
          return;
        }
      }
    }
  }
  
  public void drawHoriz() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX() + vp.getW(), vp.getY() + vp.getH());
    float horiz_dist = vp.getW() / classMPG.size();
    for (int i = 0; i < classMPG.size(); i++) {
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);
      line(h_ticks, vp.getY() + vp.getH(), h_ticks, vp.getY() + vp.getH() + 5);
      float bar_height = (classMPG.get(vehClasses[i]) / max) * vp.getH();
      if (vehClasses[i].equals(controller.carSize)) {
        fill(255,0,0); 
      } else if (this.intersect == i) {
        fill(100,100,100);
      } else {
        fill(0,0,0);
      }
      rect(h_ticks - (horiz_dist / 4), vp.getY() + (vp.getH() - bar_height), horiz_dist / 2, bar_height);
      pushMatrix();
      translate(h_ticks, vp.getY() + vp.getH() + 10);
      rotate(HALF_PI/4);
      textAlign(LEFT);
      text(vehClasses[i], -7, 6);
      popMatrix();
      fill(0,0,0);
    }
  }
  
  public void drawVert() {
    //vertical axis
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX(), vp.getY());
    float vert_dist = vp.getH() / vert_ticks;
    float iter = 0;
    float max_label = ceil(max);
    DecimalFormat df = new DecimalFormat("##.0");
    for (int i = 0; i < vert_ticks; i++) {
      float v_ticks = (vert_dist * i) + vp.getY();
      line(vp.getX() - 5, v_ticks, vp.getX(), v_ticks);
      iter = (max_label / vert_ticks) * (vert_ticks - i);
      textAlign(CENTER, CENTER);
      text(df.format(iter), vp.getX() - 20, v_ticks);
    } 
    pushMatrix();
    translate(vp.getX() - (vp.getX() * .7f), vp.getY() + (vp.getH() / 2));
    rotate(-HALF_PI);
    text("Average Cmb MPG", 0, 0);
    popMatrix();
  }
  
  public void drawLabel() {
    DecimalFormat df = new DecimalFormat("##.00");
    if (controller.carSize != null) {
      for (int i = 0; i < vehClasses.length; i++) {
        if (controller.carSize.equals(vehClasses[i])) {
          intersect = i;
          break;
        } 
      }
      float horiz_dist = vp.getW() / classMPG.size();
      float h_ticks = vp.getX() + (horiz_dist * intersect) + (horiz_dist / 2);
      float bar_height = (classMPG.get(vehClasses[intersect]) / max) * vp.getH();      
      fill(0,0,0);
      text(df.format(classMPG.get(vehClasses[intersect])), h_ticks, vp.getY() + (vp.getH() - bar_height) - 10);      
    }
  }
}
class Condition {
    String col = null;
    String operator = null;
    String value = "-1"; 

    // create a new Condition object that specifies some data column
    // should have some relationship to some value
    //   col: column name of data the relationship applies to
    //   op: operator (e.g. "<=")
    //   value: value to compare to
    Condition(String col, String op, String value) {
        this.col = col;
        this.operator = op;
        this.value = value;
    }
    
    public String toString() {
        return col + " " + operator + " " + value + " ";
    }
    
    public boolean equals(Condition cond){
        return operator.equals(cond.operator) && 
        value == cond.value && 
        col.equals(cond.col);
    }

};

    public static boolean checkConditions(Condition[] conds, TableRow r) {
        if(conds == null || r == null){
            return false;
        }
        boolean and = true;
        for (int i = 0; i < conds.length; i++) {
            if (!checkCondition(conds[i], r)) return false;
        }
        return true;
    }

    public static boolean checkCondition(Condition cond, TableRow r) {
        if (cond == null || cond.value == null || r == null) return false;
        if (cond.operator.equals("=")) { 
            return r.getString(cond.col).equals(cond.value);
        }
        return false;
    }







TypeGraph typemarks = null;
class TypeGraph {
    ArrayList<String>types;
    ArrayList<Float>percents;
};

class Controller {
    protected Message preMsg = null;
    ParallelCoord pc;
    ParallelCoord pc15;
    ClassGraph class_bg;
    ClassGraph class_bg15;
    BrandGraph brand_bg;
    BrandGraph brand_bg15;
    String carSize = null; //the car type in the brand graph
    String brand = null;
    boolean pcmarks[] = null;

    public Controller() {
        initViews();
    }

    public void initViews(){
      pclabels = new String[] {"Cyl", "Air Pollution Score","City MPG","Hwy MPG","Cmb MPG","Greenhouse Gas Score"};
        pc = new ParallelCoord(pc_vp, pclabels, data_00);
        pc.setController(this);
        pc15 = new ParallelCoord(pc_vp, pclabels, data_15);
        pc15.setController(this);
        class_bg = new ClassGraph(class_vp, data_00);
        class_bg.setController(this);
        class_bg15 = new ClassGraph(class_vp, data_15);
        class_bg15.setController(this);
        brand_bg = new BrandGraph(brand_vp, data_00, class_bg.vehClasses);
        brand_bg.setController(this);
        brand_bg15 = new BrandGraph(brand_vp, data_15, class_bg15.vehClasses);
        brand_bg15.setController(this);
        findMax();
    }
    private void findMax() {
      if (class_bg.max > class_bg15.max) {
        class_bg15.max = class_bg.max; 
      } else {
        class_bg.max = class_bg15.max; 
      }
      if (brand_bg.max > brand_bg15.max) {
        brand_bg15.max = brand_bg.max; 
      } else {
        brand_bg.max = brand_bg15.max; 
      }  
    }

    public void drawViews() {
        if (!year_toggle) {
            pc.draw();
            class_bg.drawGraph();
            brand_bg.drawGraph(); 
        }
        else {
            pc15.draw();
            class_bg15.drawGraph();
            brand_bg15.drawGraph(); 
        }
    }

    public void mousePressed() {
        resetMarks();
    }

    public void mouseDragged() {
        if (!year_toggle) pc.mouseDragged();
        else pc15.mouseDragged();
    }
    public void mouseReleased() {
        if (!year_toggle) {
            pc.mouseReleased();
            class_bg.mouseClick();
            if (brand_bg.brandsMPG != null) {
              brand_bg.mouseClick();
            }
        }
        else {
            pc15.mouseReleased();
            class_bg15.mouseClick();
            if (brand_bg15.brandsMPG != null) {
              brand_bg15.mouseClick();            
            }
        }
    }
    
    public void resetMarks() {
        if (!year_toggle) pcmarks = new boolean[data_00.getRowCount()];
        else pcmarks = new boolean[data_15.getRowCount()];
        carSize = null;
        brand = null;
        setMarksOfViews();
    }


    public void setMarksOfViews(){
        if (!year_toggle) pc.setMarks(pcmarks);
        else pc15.setMarks(pcmarks);
    }

    private void makeMarks(Message msg) {
        resetMarks();
        for (int i = 0; i < data_00.getRowCount(); i++) {
            TableRow datum = data_00.getRow(i);
            pcmarks[i] = false;
            if (checkConditions(msg.conds, datum)) {
                pcmarks[i] = true;
            }
            else if (msg.condsOR != null) {
                checkORS(msg, datum, i);
            }
        }
        if (msg.action.equals("new graph")) {
          carSize = msg.conds[0].value;
          brand = null;
        } else if (msg.action.equals("brands")) {
          brand = msg.conds[0].value;
          carSize = msg.conds[1].value; 
        } else {
          carSize = null; 
          brand = null;
        }
    }

    private void checkORS(Message msg, TableRow r, int i) {
        for (int j = 0; j < msg.condsOR.size(); j++) {
            if (checkCondition(msg.condsOR.get(j), r)) {
                pcmarks[i] = true;
            }
        }
    }

    public void receiveMsg(Message msg) {
        if (msg.action.equals("clean")) {
            resetMarks();
            return;
        }
        if (msg.action.equals("new graph")) {
            if (!year_toggle) brand_bg.setMode(msg.conds[0].value);
            else brand_bg15.setMode(msg.conds[0].value);
            carSize = msg.conds[0].value;
        }
        if (msg.action.equals("brands")) {
 
        }
        makeMarks(msg);
        setMarksOfViews();
    }

};


class Message {
    String src = null;
    Condition[] conds = null;
    ArrayList<Condition> condsOR = null;
    String action = "normal";

    Message() {}
    public Message addcondOR(Condition con) {
        if (condsOR == null) condsOR = new ArrayList<Condition>();
        condsOR.add(con);
        return this;
    }

    public Message setSource(String str) {
        this.src = str;
        return this;
    }

    public Message setAction(String str) {
        this.action = str;
        return this;
    }

    public Message addCondition(Condition x) {
        if (this.conds == null) this.conds = new Condition[1];
        this.conds[0] = x;
        return this;
    }

    public Message setConditions(Condition[] conds) {
        this.conds = conds;
        return this;
    }

    public boolean equals(Message msg) {
        if (msg == null) {
            return false;
        }
        if (src == null && msg.src == null) {
            return true;
        }
        if (src == null || msg.src == null) {
            return false;
        }
        if (!src.equals(msg.src)) {
            return false;
        }
        if (conds != null && msg.conds != null) {
            if (conds.length != msg.conds.length) {
                return false;
            }
            for (int i = 0; i < conds.length; i++) {
                if (!conds[i].equals(msg.conds[i])) {
                    return false;
                }
            }
            return true;
        } else {
            if (conds == null && msg.conds == null) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String toString() {
        String str = "";
        for (Condition cond: conds) {
            str += cond.toString();
        }
        return str + "\n\n";
    }
}






class ParallelCoord {
  String name = "ParallelCoordinates";
  Table _data;
  boolean dragging = false; // whether or not the mouse is being dragged
  int currentaxis;
  PVector cornerA = new PVector(0,0);
  ArrayList<Boolean> marked;
  String labels[];
  HashMap<String,Range> dimensions; 
  HashMap<String,Float> min, max;
  HashMap<String,Axis> axes;
  Viewport vp;
  Rectangle selectArea = null;
  Controller controller;
  boolean pcmarks[] = null;

  ParallelCoord(Viewport vp, String[] labels, Table _data) {
    this._data = _data;
    this.labels = labels;
    this.currentaxis = -1;
    this.vp = vp;
    updateMinMax();
    axes = new HashMap<String,Axis>();
    dimensions = new HashMap<String,Range>();
    Viewport ax_vp;
    for (int i = 0; i < labels.length; i++) {
      ax_vp = new Viewport(vp, ((float)i) / (labels.length - 1),
       (float)0.0f,
       (float)0.3f,
       (float)1.0f);
      axes.put(labels[i], new Axis(ax_vp, labels[i], min.get(labels[i]), max.get(labels[i]), 5));
            dimensions.put(labels[i], new Range( ax_vp.getX(), ax_vp.getY() ,  ax_vp.getW(), ax_vp.getH()));
    }
    drawData();
  }

  public void setMarks(boolean m[]) {
    this.pcmarks = m;
  }

  public void setController(Controller x) {
    this.controller = x;
  }

  public void mousePressed() {
    dragging = false;
    cornerA.x = mouseX;
    cornerA.y = mouseY;
  }

  public void mouseDragged() {
    if (dragging) {
      axes.get(labels[currentaxis]).moveAxis((float)mouseX/width);
      return;
    }
    for (int m = 0; m <axes.size(); m++){
      if (axes.get(labels[m]).moveAxis((float)mouseX/width)) {
        currentaxis = m;
      }
    }
    dragging = true;
  }

  public void completeMove() {
    for (int i = 0; i < labels.length; i++) {
      if (i != currentaxis && axes.get(labels[i]).isinWidth()) {
        axes.get(labels[i]).swap(axes.get(labels[currentaxis]));
        String temp = labels[i];
        labels[i] = labels[currentaxis];
        labels[currentaxis] = temp;
        return;
      }
    }
    axes.get(labels[currentaxis]).resetAxis();
  }

  public void mouseReleased() {
    if (!dragging) switchAxis();
    if (dragging) completeMove();
    mouseClick();
    dragging = false;
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
    textAlign(LEFT);
    for (int i = 0; i < labels.length; i++) {
      axes.get(labels[i]).draw();
    }
    drawData();
    drawSelectedArea();
  }

  public void drawData() {
    for (int i = 0; i < _data.getRowCount(); i++) {
      TableRow datum = _data.getRow(i);
      stroke(0, 120);
      if (pcmarks!=null && pcmarks[i]) {
        stroke(255, 0, 0);
        strokeWeight(2);
      }
      for (int j = 0; j < labels.length - 1; j++) {
        Axis ax1 = axes.get(labels[j]);
        Axis ax2 = axes.get(labels[j+1]);
        float y1 = datum.getFloat(labels[j]);
        float y2 = datum.getFloat(labels[j+1]);
        //mylines.add(new float[]{ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2), 0});
        line(ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2));
      }
      strokeWeight(1);

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



  public void mouseClick() {
    Message msg = new Message();
    msg.src = this.name;
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
            msg.addcondOR(new Condition("Model","=",datum.getString("Model")));
            break;
          }
        }
        else if (intersect(mouseX, mouseY, tempLine)) {
          msg.addcondOR(new Condition("Model","=",datum.getString("Model")));
          break;
        }
      }
    }
    if (msg.condsOR == null) msg.action = "clean";
    controller.receiveMsg(msg);
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

	Viewport(Viewport v) {
		this.x = v.getrelX();
		this.y = v.getrelY();
		this.w = v.getrelW();
		this.h = v.getrelH();
	}
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
