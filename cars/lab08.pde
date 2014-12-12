boolean dragging = false; // whether or not the mouse is being dragged
PVector cornerA = new PVector(0,0);
ArrayList<HashMap<String,Float>> data = new ArrayList<HashMap<String,Float>>();
String filename = "iris.csv";

// Viewports:
Viewport root_vp = new Viewport(this);
Viewport pc_vp = new Viewport(root_vp, 0.1, 0.05, 0.80, 0.90);

// Views:
ParallelCoord pc;

Table _data;
String _labels[];

void setup() {
  size(900,400);
  smooth();
  background(255);
  frame.setResizable(true);


  parse(filename);
  //String labels[] = parseData(filename, data);
  pc = new ParallelCoord(pc_vp, _data, _labels);
}

void parse(String filename) {
  _data = loadTable(filename, "header");
  _labels = new String[] {"sepal length","sepal width","petal length","petal width","class"};
}

void draw() {
  background(255); 
  pc.draw();
}

void mousePressed() {

  pc.clearSelectedArea();
  cornerA.x = mouseX;
  cornerA.y = mouseY;
  for (int i = 0; i < pc.markedIndexes.size();i++){
    print("heey");
     pc.marks[pc.markedIndexes.get(i)] = true;
  }
}

void mouseDragged() {
  dragging = true;
  pc.setSelectedArea(cornerA.x, cornerA.y, mouseX, mouseY);
   pc.marks[pc.markedIndexes.get(4)] = true;
  
}

void mouseReleased() {
  if (!dragging) pc.switchAxis();
}



