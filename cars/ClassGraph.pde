import java.text.DecimalFormat;

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
     min = 0.0;
     max = 0.0;
     filterData();
     getMinMax();
  }

  void setController(Controller x) {
    this.controller = x;
  }

  void filterData() {
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

  void mouseClick() {
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
  
  void drawHoriz() {
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
  
  void drawVert() {
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
    translate(vp.getX() - (vp.getX() * .7), vp.getY() + (vp.getH() / 2));
    rotate(-HALF_PI);
    text("Average Cmb MPG", 0, 0);
    popMatrix();
  }
  
  void drawLabel() {
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
