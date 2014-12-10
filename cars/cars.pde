Table data_00;
Table data_15;
int numRows_00;
int numRows_15;

String[] data_001;

void setup() {
  parseData(); 
}

void parseData() {
  data_00 = loadTable("all_alpha_00.csv", "header");
  numRows_00 = data_00.getRowCount();
  println(numRows_00);
//  println(data_00.getString(
  
  data_001 = loadStrings("all_alpha_00.csv");
  println(data_001.length);
  println(data_001[1546]);
  
  data_15 = loadTable("all_alpha_00.csv", "header");
  numRows_15 = data_15.getRowCount();
  println(numRows_15);
}
