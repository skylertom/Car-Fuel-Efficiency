public class BrandGraph {
  Viewport vp;
  Table data;
  HashMap<String, HashMap<String, Float>> classBrands;
  HashMap<String, Float> brandsMPG;
  String[] brands;
  String mode;
  Float min, max;
  int vert_ticks = 7;
 
  BrandGraph(Viewport vp, Table d, String[] vehClasses) {
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
  }
  
  void setMode(String m) {
    mode = m;
  }
  
  void drawGraph() {
    drawHoriz();
    drawVert();
    if (mode != null) {
      brandsMPG = classBrands.get(mode);
      brands = brandsMPG.keySet().toArray(new String[0]);
      getMinMax();
      drawDetails();
    }
  }
  
  void drawHoriz() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX() + vp.getW(), vp.getY() + vp.getH());     
  }
  
  void drawVert() {
    line(vp.getX(), vp.getY() + vp.getH(), vp.getX(), vp.getY());     
  }
  
  void drawDetails() {
    text(mode, vp.getX() + (vp.getW() / 2), vp.getY());
    //horizontal axis details and bars
    for (int i = 0; i < brandsMPG.size(); i++) {
      float horiz_dist = vp.getW() / brandsMPG.size();
      float h_ticks = vp.getX() + (horiz_dist * i) + (horiz_dist / 2);
      line(h_ticks, vp.getY() + vp.getH(), h_ticks, vp.getY() + vp.getH() + 5);
      float bar_height = (brandsMPG.get(brands[i]) / max) * vp.getH();
      fill(0,0,0);
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
    }
    //vertical axis details
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
    translate(vp.getX() - (vp.getX() * .07), vp.getY() + (vp.getH() / 2));
    rotate(-HALF_PI);
    text("Average MPG", 0, 0);
    popMatrix();    
  }
  
  void getMinMax() {
    for (Float f : brandsMPG.values()) {
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
   println(classBrands);
   }
  
  
  
}
