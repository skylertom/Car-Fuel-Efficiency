public class BrandGraph {
  Viewport vp;
  Table data;
  HashMap<String, Float> brandMPG;
  String[] brands;
 
  BrandGraph(Viewport vp, Table d) {
    this.vp = vp;
    data = d;
    brandMPG = new HashMap<String, Float>();
  }
  
  void drawGraph() {
    
  }
  
  void filterData() {
    for (int i = 0; i < data.getRowCount(); i++) {
       String brand = data.getString(i, "Brand");
       Float avg = brandMPG.get(brand);
       Float rowMPG = data.getFloat(i, "Cmb MPG");
       if (avg == null) {
         brandMPG.put(brand, rowMPG);
       } else {
         float rowVal = data.getFloat(i, "Cmb MPG");
         avg = (avg + rowMPG) / 2;
         brandMPG.put(brand, avg);
       }
    }
//    brands = brandMPG.keySet().toArray(new String[0]);
    println(brandMPG);
  }
  
  
  
}
