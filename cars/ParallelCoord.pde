import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

class ParallelCoord {
  String name = "ParallelCoordinates";
  Table _data;
  boolean dragging = false; // whether or not the mouse is being dragged
  PVector cornerA = new PVector(0,0);
  ArrayList<Boolean> marked;
  String labels[];
  HashMap<String,Range> dimensions; 
  HashMap<String,Float> min, max;
  HashMap<String,Axis> axes;
  Viewport vp;
  Rectangle selectArea = null;
  Controller controller;
  boolean pcmarks[] = null;

  ParallelCoord(Viewport vp, String[] labels, Table _data) {
    this._data = _data;
    this.labels = labels;
    this.vp = vp;
    updateMinMax();
    axes = new HashMap<String,Axis>();
    dimensions = new HashMap<String,Range>();
    Viewport ax_vp;
    for (int i = 0; i < labels.length; i++) {
      ax_vp = new Viewport(vp, ((float)i) / (labels.length - 1),
       (float)0.0,
       (float)0.3,
       (float)1.0);
      axes.put(labels[i], new Axis(ax_vp, labels[i], min.get(labels[i]), max.get(labels[i]), 5));
            dimensions.put(labels[i], new Range( ax_vp.getX(), ax_vp.getY() ,  ax_vp.getW(), ax_vp.getH()));
    }
    drawData();
  }

  void setMarks(boolean m[]) {
    this.pcmarks = m;
  }

  void setController(Controller x) {
    this.controller = x;
  }

  void mousePressed() {
    clearSelectedArea();
    dragging = false;
    cornerA.x = mouseX;
    cornerA.y = mouseY;
  }

  void mouseDragged() {
    dragging = true;
    setSelectedArea(cornerA.x, cornerA.y, mouseX, mouseY);
  }

  void mouseReleased() {
    if (!dragging) switchAxis();
  }

  void updateMinMax() {
    min = new HashMap<String,Float>();
    max = new HashMap<String,Float>();
    for (int i = 0; i < labels.length; i++) {
      String mytext = labels[i];
      String nums[] = _data.getStringColumn(mytext);
      float mymin = Float.parseFloat(nums[0]);
      float mymax = Float.parseFloat(nums[0]);
      for (int j = 0; j < nums.length; j++) {
        float temp = Float.parseFloat(nums[j]);
        if (temp < mymin) mymin = temp;
        if (temp > mymax) mymax = temp;
      }
      min.put(mytext, mymin);
      max.put(mytext, mymax);
    }
  }

  void draw() {
    textAlign(LEFT);
    drawData();
    Iterator<String> iter = axes.keySet().iterator();
    while(iter.hasNext()) {
      String key = iter.next();
      axes.get(key).draw();
    }
    drawSelectedArea();
  }

  void drawData() {
    for (int i = 0; i < _data.getRowCount(); i++) {
      TableRow datum = _data.getRow(i);
      stroke(0, 120);
      if (pcmarks!=null && pcmarks[i]) {
        stroke(255, 0, 0);
        strokeWeight(2);
      }
      for (int j = 0; j < labels.length - 1; j++) {
        Axis ax1 = axes.get(labels[j]);
        Axis ax2 = axes.get(labels[j+1]);
        float y1 = datum.getFloat(labels[j]);
        float y2 = datum.getFloat(labels[j+1]);
        //mylines.add(new float[]{ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2), 0});
        line(ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2));
      }
      strokeWeight(1);

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

  void mouseClick() {
    Message msg = new Message();
    msg.src = this.name;
    TableRow datum;
    Axis ax1,ax2; // tmp loop axes
    float y1,y2; // tmp loop floats
    for (int i = 0; i < _data.getRowCount(); i++) {
      datum = _data.getRow(i);
      for (int j = 0; j < labels.length - 1; j++) {
        ax1 = axes.get(labels[j]);
        ax2 = axes.get(labels[j+1]);
        y1 = datum.getFloat(labels[j]);
        y2 = datum.getFloat(labels[j+1]);
        float tempLine[] = {ax1.getX(), ax1.getLoc(y1), ax2.getX(), ax2.getLoc(y2)};
        if (selectArea != null) {
          if (intersect(selectArea, tempLine)) {
            msg.addcondOR(new Condition("Model","=",datum.getString("Model")));
            break;
          }
        }
        else if (intersect(mouseX, mouseY, tempLine)) {
          msg.addcondOR(new Condition("Model","=",datum.getString("Model")));
          break;
        }
      }
    }
    if (msg.condsOR == null) msg.action = "clean";
    controller.receiveMsg(msg);
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
};

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
};

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
};


