import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

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

/*
void setup() {
  parseData(); 
}
*/

public void parseData() {
  data_00 = loadTable("all_alpha_00.csv", "header");
  numRows_00 = data_00.getRowCount();
  println(numRows_00);
//  println(data_00.getString(
  
  data_001 = loadStrings("all_alpha_00.csv");
  println(data_001.length);
  println(data_001[1]);
  println(data_001[1546]);
  
  data_15 = loadTable("all_alpha_00.csv", "header");
  numRows_15 = data_15.getRowCount();
  println(numRows_15);
}

boolean dragging = false; // whether or not the mouse is being dragged
PVector cornerA = new PVector(0,0);
ArrayList<HashMap<String,Float>> data = new ArrayList<HashMap<String,Float>>();
String filename = "iris.csv";

// Viewports:
Viewport root_vp = new Viewport(this);
Viewport pc_vp = new Viewport(root_vp, 0.1f, 0.05f, 0.80f, 0.90f);

// Views:
ParallelCoord pc;

Table _data;
String _labels[];

public void setup() {
  size(900,400);
  smooth();
  background(255);
  frame.setResizable(true);


  parse(filename);
  //String labels[] = parseData(filename, data);
  pc = new ParallelCoord(pc_vp, _data, _labels);
}

public void parse(String filename) {
  _data = loadTable(filename, "header");
  _labels = new String[] {"sepal length","sepal width","petal length","petal width","class"};
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
    print("heey");
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



/*
String[] parseData(String filename, ArrayList<HashMap<String,Float>> data) {

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
*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "cars" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
