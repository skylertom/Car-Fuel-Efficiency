import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

class ParallelCoord {
	
  ArrayList<HashMap<String,Float>> data;
  ArrayList<Boolean> marked;
  boolean marks[];
  ArrayList<Integer> markedIndexes;
  String labels[];
  HashMap<String,Range> dimensions; 
  HashMap<String,Float> min, max;
  HashMap<String,Axis> axes;
  Viewport vp;
  Rectangle selectArea = null;

  ParallelCoord(Viewport vp, ArrayList<HashMap<String,Float>> data, String[] labels) {
    this.data = data;
    marks = new boolean[data.size()];
    Arrays.fill(marks, false);
    this.labels = labels;
    this.vp = vp;
    updateMinMax();
    axes = new HashMap<String,Axis>();
    dimensions = new HashMap<String,Range>();
    markedIndexes = new ArrayList<Integer>();
    Viewport ax_vp;
    for (int i = 0; i < labels.length; i++) {
      ax_vp = new Viewport(vp, ((float)i) / (labels.length - 1),
       (float)0.0,
       (float)0.3,
       (float)1.0);
      axes.put(labels[i], new Axis(ax_vp, labels[i], min.get(labels[i]), max.get(labels[i]), 5));
            dimensions.put(labels[i], new Range( ax_vp.getX(), ax_vp.getY() ,  ax_vp.getW(), ax_vp.getH()));


    }
  }

  void updateMinMax() {
		//initialize these
    min = new HashMap<String,Float>();
    max = new HashMap<String,Float>();
    for (int i = 0; i < labels.length; i++) {
      min.put(labels[i], data.get(0).get(labels[i]));
      max.put(labels[i], data.get(0).get(labels[i]));
    }

    Float a,b,c;
    for (int i = 0; i < data.size(); i++) {
      for (int j = 0; j < labels.length; j++) {
        a = min.get(labels[j]);
        b = max.get(labels[j]);
        c = data.get(i).get(labels[j]);
        if (a > c)
          min.put(labels[j], c);
        if (b < c)
          max.put(labels[j], c);
      }
    }
  }

  void draw() {
   
    hover();
    drawData();
    Iterator<String> iter = axes.keySet().iterator();
    while(iter.hasNext()) {
      String key = iter.next();
      axes.get(key).draw();
    }
     drawSelectedArea();

     
  }

  void drawData() {
    HashMap<String,Float> datum; // tmp loop data point
    Axis ax1,ax2; // tmp loop axes
    float y1,y2; // tmp loop floats

    for (int i = 0; i < data.size(); i++) {
      datum = data.get(i);
      stroke(0, 120);
      if (marks[i]) stroke(255, 0, 0);
      for (int j = 0; j < labels.length - 1; j++) {
        ax1 = axes.get(labels[j]);
        ax2 = axes.get(labels[j+1]);
        y1 = datum.get(labels[j]);
        y2 = datum.get(labels[j+1]);
        line(ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2));
      }

      for (int m = 0; m <axes.size(); m++){
        axes.get(labels[m]).moveAxis((float)mouseX/width);
      }
    }
  }

  public void switchAxis() {
      for (int m = 0; m <axes.size(); m++){
        if (axes.get(labels[m]).isOnMe())
          axes.get(labels[m]).switchAxis();
      }
  }

  public void clearSelectedArea() {
    selectArea = null;
  }

  public void setSelectedArea(float x, float y, float x1, float y1) {
    selectArea = new Rectangle(x, y, x1, y1);
  }
  
  
  public void drawSelectedArea() {
    pushStyle();
    for (int i = 0; i < axes.size(); i++){
      //Limit the selection just to Y axises. 
      if (mouseY < axes.get(labels[i]).getH()*0.999 ){
        if ( mouseX < axes.get(labels[i]).getX()+40) {
        if (selectArea != null) {
          fill(171,217,233,80);
          rectMode(CORNER);
          rect(selectArea.p1.x, selectArea.p1.y,
            selectArea.p2.x - selectArea.p1.x, selectArea.p2.y - selectArea.p1.y);

        }
      }
  }
}
    popStyle();
  }

  void hover() {
    Arrays.fill(marks, false);
    HashMap<String,Float> datum; // tmp loop data point
    Axis ax1,ax2; // tmp loop axes
    float y1,y2; // tmp loop floats
    

    for (int i = 0; i < data.size(); i++) {
      datum = data.get(i);
      for (int j = 0; j < labels.length - 1; j++) {
        ax1 = axes.get(labels[j]);
        ax2 = axes.get(labels[j+1]);
        y1 = datum.get(labels[j]);
        y2 = datum.get(labels[j+1]);
        float tempLine[] = {ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2)};
        if (selectArea != null) {
          if (intersect(selectArea, tempLine)) {
            marks[i] = true;
            break;
          }
        }
        else if (intersect(mouseX, mouseY, tempLine)) {
          marks[i] = true;
          markedIndexes.add(i);
          break;
        }
      }
    }
  }

  boolean intersect(Rectangle r, float myline[]) {
    //positive slope
    if (r.p2.x<=myline[0] || r.p1.x>=myline[2]) {
      return false;
    }
    if (myline[3]>myline[1]) {
      if (r.p2.y <= myline[1] || r.p1.y >= myline[3]) {
        return false;
      }
      float realslope = (myline[3]-myline[1])/(myline[2]-myline[0]);
      float x1, y1, x2, y2;
      float minslope = (r.p1.y-myline[1])/(r.p1.x-myline[0]);
      float maxslope = (r.p2.y-myline[1])/(r.p1.x-myline[0]);
      return (realslope < maxslope && realslope > minslope);
    }
    //negative slope
    if (r.p2.y <= myline[3] || r.p1.y >= myline[1]) {
        return false;
    }
    float realslope = (myline[3]-myline[1])/(myline[2]-myline[0]);
    float x1, y1, x2, y2;
    float minslope = (r.p1.y-myline[1])/(r.p1.x-myline[0]);
    float maxslope = (r.p2.y-myline[1])/(r.p1.x-myline[0]);
    return (realslope < maxslope && realslope > minslope);
  }

  //myline should be an array with form {x1, y1, x2, y2}
  boolean intersect(float myx, float myy, float myline[]) {
    if (myx < myline[0] || myx > myline[2] || myy < Math.min(myline[1],myline[3]) || myy > Math.max(myline[3],myline[1]))
      return false;
    float realslope = (myline[3]-myline[1])/(myline[2]-myline[0]);
    float myslope = (myline[3]-myy)/(myline[2]-myx);
    return Math.abs(realslope-myslope) < .03;
  }
}

class Rectangle { // model dragging area
  PVector p1 = null;
  PVector p2 = null;

  Rectangle(float x1_, float y1_, float x2_, float y2_) {
    float x1 = x1_ < x2_ ? x1_ : x2_;
    float x2 = x1_ >= x2_ ? x1_ : x2_;
    float y1 = y1_ < y2_ ? y1_ : y2_;
    float y2 = y1_ >= y2_ ? y1_ : y2_;
    p1 = new PVector(x1, y1);
    p2 = new PVector(x2, y2);
  }
}

class Range { 
  float x;
  float y; 
  float w; 
  float h;

  Range(float x1_, float y1_, float w1_, float h1_) {
    x= x1_;
    y= y1_;
    w= w1_;
    h= h1_;
  }
}


