Table data_00;
Table data_15;
int numRows_00;
int numRows_15;

String[] data_001;
String pclabels[];
Viewport root_vp = new Viewport();
Viewport pc_vp = new Viewport(root_vp, 0.1, 0.05, 0.80, 0.90);


//views:
ParallelCoord pc;

void setup() {
  size(900,400);
  smooth();
  background(255);
  frame.setResizable(true);
  parseData(); 
  pclabels = new String[] {"Cyl", "Air Pollution Score","City MPG","Hwy MPG","Cmb MPG","Greenhouse Gas Score"};
  pc = new ParallelCoord(pc_vp, pclabels, data_00);
}

void parse(String filename) {
}

void draw() {
  background(255); 
  pc.draw();
}

void mousePressed() {
  pc.mousePressed();
}

void mouseDragged() {
  pc.mouseDragged();
}

void mouseReleased() {
  pc.mouseReleased();
}

void parseData() {
  data_00 = loadTable("all_alpha_00.csv", "header");
  parseTable(data_00);
  numRows_00 = data_00.getRowCount();
  println(numRows_00);
  
  data_001 = loadStrings("all_alpha_00.csv");
  println(data_001.length);
  println(data_001[1]);
  println(data_001[1546]);
  
  data_15 = loadTable("all_alpha_00.csv", "header");
  parseTable(data_15);
  saveTable(data_15, "x.csv");
  numRows_15 = data_15.getRowCount();
  println(numRows_15);
}

void parseTable(Table t) {
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
      row.setString("Brand", x.substring(0, x.indexOf(' ')));
    }
  }
}
