import java.util.Iterator;
import java.lang.Iterable;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

boolean pcmarks[] = null;

class Controller {
    
    //static HashMap<String,String[]> heatMarks;
    //HashMap<String,Boolean> netMarks;
    //int catMarks[][];
    protected Message preMsg = null;
    ParallelCoord pc;
    //Graph sizeGraph;
    //Graph brandGraph;
    String carSize = null; //the car type in the brand graph

    public Controller() {
        initViews();
    }

    public void initViews(){
        pc = new ParallelCoord(pc_vp, pclabels, data_00);
        pc.setController(this);
    }


    public void drawViews() {
        pc.draw();
        //sizeGraph.draw();
        //brandGraph.draw();
    }

    //don't do hovering?
    public void hover() {
        /*
        if (hm.isOnMe()) {
            hm.hover();
        } else if (nv.isOnMe()) {
            nv.hover();
        } else if (cv.isOnMe()) {
            cv.hover();
        }
        */
    }
    
    public void resetMarks() {
        pcmarks = new boolean[data_00.getRowCount()];
        setMarksOfViews();
    }

    public void setMarksOfViews(){
    }

    //Possible messages to receive:
        //Type of Car -> pc needs list of all marks, need to make brand graph
        //Type of Car + Brand -> pc needs list of all marks
        //List of Models
    private void makeMarks(Message msg) {
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
    }

    private void checkORS(Message msg, TableRow r, int i) {
        for (Condition[] list : msg.condsOR) {
            if (checkConditions(list, r)) {
                pcmarks[i] = true;
                return;
            }
        }
    }

    public void receiveMsg(Message msg) {
        if (msg.equals(preMsg)) {
            return;
        }
        preMsg = msg;
        if (msg.action.equals("clean")) {
            resetMarks();
            return;
        }
        makeMarks(msg);
        setMarksOfViews();
    }

};
