Table data_00;
Table data_15;
int numRows_00;
int numRows_15;
boolean year_toggle;
float toggle_x, toggle_y, toggle_w, toggle_h;

String pclabels[];
Viewport root_vp = new Viewport();
Viewport pc_vp = new Viewport(root_vp, 0.1, 0.03, 0.80, 0.40);
Viewport class_vp = new Viewport(root_vp, 0.07, 0.50, 0.30, 0.40);
Viewport brand_vp = new Viewport(root_vp, 0.45, 0.50, 0.51, 0.40);

//views:
ClassGraph class_bg;
ClassGraph class_bg15;
BrandGraph brand_bg;
BrandGraph brand_bg15;

Controller contr;

void setup() {
  size(900,600);
  smooth();
  background(255);
  frame.setResizable(true);
  parseData(true); 
  year_toggle = false;  
  pclabels = new String[] {"Cyl", "Air Pollution Score","City MPG","Hwy MPG","Cmb MPG","Greenhouse Gas Score"};
  class_bg = new ClassGraph(class_vp, data_00);
  class_bg15 = new ClassGraph(class_vp, data_15);
  brand_bg = new BrandGraph(brand_vp, data_00, class_bg.vehClasses);
  brand_bg15 = new BrandGraph(brand_vp, data_15, class_bg15.vehClasses);
  findMax();
  contr = new Controller();
}

void draw() {
  background(255);
  drawToggle();
  contr.drawViews();
  if (!year_toggle) {
    //class_bg.drawGraph();
    //brand_bg.drawGraph(); 
  } else {
    //class_bg15.drawGraph();
    //brand_bg15.drawGraph(); 
  }
}

void findMax() {
  if (class_bg.max > class_bg15.max) {
    class_bg15.max = class_bg.max; 
  } else {
    class_bg.max = class_bg15.max; 
  }
  if (brand_bg.max > brand_bg15.max) {
    brand_bg15.max = brand_bg.max; 
  } else {
    brand_bg.max = brand_bg15.max; 
  }  
}

void drawToggle() {
  fill(255);
  toggle_x = width * .92;
  toggle_y = height *.03;
  toggle_w = 75;
  toggle_h = 25;  
//  rect(width*.92, height*.03, 100, 30);
  rect(toggle_x, toggle_y, toggle_w, toggle_h);
  String toggleText = null;
  if (!year_toggle) {
    toggleText = "2000";
  } else {
    toggleText = "2015"; 
  }
  fill(0,0,0);
  textAlign(CENTER);
  text(toggleText, toggle_x + toggle_w/2, toggle_y + toggle_h/2); 
  textAlign(LEFT);

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
  displayBrands();
  switchYear();
}

void displayBrands() {
  if (!year_toggle) {
    if (class_bg.intersect != -1) {
      brand_bg.setMode(class_bg.vehClasses[class_bg.intersect]); 
    }
  } else if (year_toggle) {
    if (class_bg15.intersect != -1) {
      brand_bg15.setMode(class_bg15.vehClasses[class_bg15.intersect]);
    }
  }  
}

void switchYear() {
  if (mouseX >= toggle_x && mouseX <= toggle_x + toggle_w && mouseY >= toggle_y && mouseY <= toggle_y + toggle_h) {
    year_toggle = !year_toggle;
    if (year_toggle) {
      if (brand_bg.mode != null && !brand_bg.mode.equals("SUV")) {
        brand_bg15.setMode(brand_bg.mode); 
      } else {
        brand_bg15.setMode(null); 
      }
    } else {
      if (brand_bg15.mode != null && !brand_bg15.mode.equals("standard SUV") && !brand_bg15.mode.equals("small SUV")) {
        brand_bg.setMode(brand_bg15.mode); 
      } else {
        brand_bg.setMode(null); 
      }
    }
    contr.resetMarks();
  }  
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
    data_15 = loadTable("all_alpha_15.csv", "header");
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
//    else if (row.getString("Veh Class").equals("small SUV"))
//      row.setString("Veh Class", "SUV");
//    else if (row.getString("Veh Class").equals("standard SUV"))
//      row.setString("Veh Class", "SUV");
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
