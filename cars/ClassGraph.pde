public class BarGraph {
  Viewport vp;
  Table data;
  Map<String, Float> classes;
  
  BarGraph(Viewport vp, Table d) {
     this.vp = vp;
     data = d;
     classes = new HashMap<String, Float>();
     filterData();
  }
  
  void filterData() {
    for (int i = 0; i < data.getRowCount(); i==0 {
       String vehClass = data.getString(i, "Veh Class");
       float avg = classes.get(vehClass);
       if (avg == null) {
         classes.put(vehClass, data.getFloat(i, "Cmb MPG"));
       } else {
         float rowVal = data.getFloat(i, "Cmb MPG");
          
       }
    }
  }
}
