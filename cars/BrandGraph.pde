import java.text.DecimalFormat;

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
    min = 0.0;
    max = 0.0;
    classBrands = new HashMap<String, HashMap<String, Float>>();
    for (int i = 0; i < vehClasses.length; i++) {
      classBrands.put(vehClasses[i], new HashMap<String, Float>()); 
    }
    filterData();
    getMinMax();
  }

  void setController(Controller x) {
    this.controller = x;
  }
  
  void setMode(String m) {
    mode = m;
  }

  void drawGraph() {
//    hover();
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
  
  void hover() {
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
  
  void mouseClick() {
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
  
  void drawHoriz() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX() + vp.getW(), vp.getY() + vp.getH());     
  }
  
  void drawVert() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX(), vp.getY());     
  }
  
  void drawDetails() {
    textSize(20);
    String toPrint = mode.substring(0, 1).toUpperCase() + mode.substring(1);
    text(toPrint + "s", vp.getX() + (vp.getW() / 2), vp.getY() - 12);
    textSize(12);
    hover();
    horizDetails();
//    vertDetails();
  }
  
  void horizDetails() {
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
  
  void vertDetails() {
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
    translate(vp.getX() - (vp.getX() * .11), vp.getY() + (vp.getH() / 2));
    rotate(-HALF_PI);
    text("Average Cmb MPG", 0, 0);
    popMatrix();     
  }
  
  void getMinMax() {
    for (HashMap<String, Float> brandsMPG : classBrands.values()) {
      for (Float f : brandsMPG.values()) {
        if (min == 0.0) {
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
  
  void filterData() {
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
//    brands = brandMPG.keySet().toArray(new String[0]);
   }
  
  void drawLabel() {
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
/*    if (this.intersect != -1) {
      float horiz_dist = vp.getW() / brandsMPG.size();
      float h_ticks = vp.getX() + (horiz_dist * this.intersect) + (horiz_dist / 2);
      float bar_height = (brandsMPG.get(brands[this.intersect]) / max) * vp.getH();      
      fill(0,0,0);
      textAlign(CENTER);
      text(df.format(brandsMPG.get(brands[this.intersect])), h_ticks, vp.getY() + (vp.getH() - bar_height) - 7);
      textAlign(LEFT);      
    }    */
  }
  
}
