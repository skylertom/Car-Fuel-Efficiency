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
     min = 0.0;
     max = 0.0;
     filterData();
  }
  
  void filterData() {
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
  
  void drawAxes() {
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
      if (min == 0.0) {
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
