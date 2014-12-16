public class ClassGraph {
  Viewport vp;
  Table data;
  HashMap<String, Float> classMPG;
  String[] vehClasses;
  Float min, max;
  int vert_ticks = 7;
  int extend = 10;
  int intersect;
  
  ClassGraph(Viewport vp, Table d) {
     this.vp = vp;
     data = d;
     classMPG = new HashMap<String, Float>();
     min = 0.0;
     max = 0.0;
     filterData();
     getMinMax();
  }
  
  void filterData() {
    for (int i = 0; i < data.getRowCount(); i++) {
       String vehClass = data.getString(i, "Veh Class");
       Float avg = classMPG.get(vehClass);
       Float rowMPG = data.getFloat(i, "Cmb MPG");
       if (avg == null) {
         classMPG.put(vehClass, rowMPG);
       } else {
         float rowVal = data.getFloat(i, "Cmb MPG");
         avg = (avg + rowMPG) / 2;
         classMPG.put(vehClass, avg);
       }
    }
    vehClasses = classMPG.keySet().toArray(new String[0]);
    println(classMPG);
  }
  
  void getMinMax() {
    for (Float f : classMPG.values()) {
      if (min == 0.0) {
        min = f; 
      } else if (min > f) {
        min = f; 
      }
      if (f > max) {
        max = f; 
      }
    }     
  }
  
  void drawGraph() {
    hover();
    drawHoriz();
    drawVert();
    drawLabel();
  }
  
  void hover() {
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
  
  void drawHoriz() {
    //horizontal axis and bars
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX() + vp.getW(), vp.getY() + vp.getH());
    float horiz_dist = vp.getW() / classMPG.size();
    for (int i = 0; i < classMPG.size(); i++) {
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);
      line(h_ticks, vp.getY() + vp.getH(), h_ticks, vp.getY() + vp.getH() + 5);
      float bar_height = (classMPG.get(vehClasses[i]) / max) * vp.getH();
      if (i == intersect) {
        fill(255,0,0); 
      } else {
        fill(0,0,0);
      }
      rect(h_ticks - (horiz_dist / 4), vp.getY() + (vp.getH() - bar_height), horiz_dist / 2, bar_height);
      fill(0,0,0);
      pushMatrix();
      translate(h_ticks, vp.getY() + vp.getH() + 10);
      rotate(HALF_PI/4);
      textAlign(LEFT);
      text(vehClasses[i], -7, 6);
      popMatrix();
    }
  }
  
  void drawVert() {
    //vertical axis
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX(), vp.getY());
    float vert_dist = vp.getH() / vert_ticks;
    int iter = 0;
    int max_label = ceil(max);
    for (int i = 0; i < vert_ticks; i++) {
      float v_ticks = (vert_dist * i) + vp.getY();
      line(vp.getX() - 5, v_ticks, vp.getX(), v_ticks);
      iter = (max_label / vert_ticks) * (vert_ticks - i);
      textAlign(CENTER, CENTER);
      text(iter, vp.getX() - 15, v_ticks);
    } 
    pushMatrix();
    translate(vp.getX() - (vp.getX() * .5), vp.getY() + (vp.getH() / 2));
    rotate(-HALF_PI);
    text("Average MPG", 0, 0);
    popMatrix();
  }
  
  void drawLabel() {
    if (intersect != -1) {
      fill(0,0,0);
      text(str(classMPG.get(vehClasses[intersect])), mouseX, mouseY-20);
    } 
  }
}
