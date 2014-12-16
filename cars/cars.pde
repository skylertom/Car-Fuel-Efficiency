Table data_00;
Table data_15;
int numRows_00;
int numRows_15;

String pclabels[];
Viewport root_vp = new Viewport();
Viewport pc_vp = new Viewport(root_vp, 0.1, 0.05, 0.80, 0.40);
Viewport class_vp = new Viewport(root_vp, 0.07, 0.50, 0.40, 0.40);
Viewport brand_vp = new Viewport(root_vp, 0.55, 0.50, 0.40, 0.40);

//views:
ClassGraph class_bg;
BrandGraph brand_bg;

Controller contr;

void setup() {
  size(900,600);
  smooth();
  background(255);
  frame.setResizable(true);
  parseData(true); 
  pclabels = new String[] {"Cyl", "Air Pollution Score","City MPG","Hwy MPG","Cmb MPG","Greenhouse Gas Score"};
  class_bg = new ClassGraph(class_vp, data_00);
  brand_bg = new BrandGraph(brand_vp, data_00);
  contr = new Controller();
}

void draw() {
  background(255);
//  pc.draw();
  class_bg.drawGraph();
  brand_bg.drawGraph();
}
/*
void mousePressed() {
  pc.mousePressed();
  if (class_bg.intersect != -1) {
    String vehClass = class_bg.vehClasses[class_bg.intersect];
//    brand_bg.drawGraph();
  }

  contr.drawViews();
  class_bg.drawAxes();
}
*/
void mousePressed() {
  contr.mousePressed();
}

void mouseDragged() {
  //pc.mouseDragged();
}

void mouseReleased() {
  contr.mouseReleased();
}

void parseData(boolean notloaded) {
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

void parseTable(Table t) {
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
