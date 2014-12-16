import java.util.Iterator;
import java.lang.Iterable;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

TypeGraph typemarks = null;
class TypeGraph {
    ArrayList<String>types;
    ArrayList<Float>percents;
};

class Controller {
    protected Message preMsg = null;
    ParallelCoord pc;
    ParallelCoord pc15;
    ClassGraph class_bg;
    ClassGraph class_bg15;
    BrandGraph brand_bg;
    BrandGraph brand_bg15;
    String carSize = null; //the car type in the brand graph
    boolean pcmarks[] = null;

    public Controller() {
        initViews();
    }

    public void initViews(){
      pclabels = new String[] {"Cyl", "Air Pollution Score","City MPG","Hwy MPG","Cmb MPG","Greenhouse Gas Score"};
        pc = new ParallelCoord(pc_vp, pclabels, data_00);
        pc.setController(this);
        pc15 = new ParallelCoord(pc_vp, pclabels, data_15);
        pc15.setController(this);
        class_bg = new ClassGraph(class_vp, data_00);
        class_bg.setController(this);
        class_bg15 = new ClassGraph(class_vp, data_15);
        class_bg15.setController(this);
        brand_bg = new BrandGraph(brand_vp, data_00, class_bg.vehClasses);
        brand_bg.setController(this);
        brand_bg15 = new BrandGraph(brand_vp, data_15, class_bg15.vehClasses);
        brand_bg15.setController(this);
        findMax();
    }
    private void findMax() {
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

    public void drawViews() {
        if (!year_toggle) {
            pc.draw();
            class_bg.drawGraph();
            brand_bg.drawGraph(); 
        }
        else {
            pc15.draw();
            class_bg15.drawGraph();
            brand_bg15.drawGraph(); 
        }
    }

    public void mousePressed() {
        resetMarks();
    }
    public void mouseReleased() {
        if (!year_toggle) {
            pc.mouseClick();
            class_bg.mouseClick();
//            brand_bg.mouseClick();
        }
        else {
            pc15.mouseClick();
            class_bg15.mouseClick();
//            brand_bg15.mouseClick();
        }
    }
    
    public void resetMarks() {
        if (!year_toggle) pcmarks = new boolean[data_00.getRowCount()];
        else pcmarks = new boolean[data_15.getRowCount()];
        carSize = null;
        setMarksOfViews();
    }


    public void setMarksOfViews(){
        if (!year_toggle) pc.setMarks(pcmarks);
        else pc15.setMarks(pcmarks);
    }

    private void makeMarks(Message msg) {
        resetMarks();
        for (int i = 0; i < data_00.getRowCount(); i++) {
            TableRow datum = data_00.getRow(i);
            pcmarks[i] = false;
            if (checkConditions(msg.conds, datum)) {
                pcmarks[i] = true;
            }
            else if (msg.condsOR != null) {
                checkORS(msg, datum, i);
            }
        }
        if (msg.action.equals("new graph")) {
          carSize = msg.conds[0].value;
        } else {
          carSize = null; 
        }
    }

    private void checkORS(Message msg, TableRow r, int i) {
        for (int j = 0; j < msg.condsOR.size(); j++) {
            if (checkCondition(msg.condsOR.get(j), r)) {
                pcmarks[i] = true;
            }
        }
    }

    public void receiveMsg(Message msg) {
        if (msg.action.equals("clean")) {
            resetMarks();
            return;
        }
        if (msg.action.equals("new graph")) {
            if (!year_toggle) brand_bg.setMode(msg.conds[0].value);
            else brand_bg15.setMode(msg.conds[0].value);
            carSize = msg.conds[0].value;
        }
        makeMarks(msg);
        setMarksOfViews();
    }

};
