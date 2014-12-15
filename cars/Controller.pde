import java.util.Iterator;
import java.lang.Iterable;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        setMarksOfViews();
    }

    public void setMarksOfViews(){
        /*
        hm.setMarks(heatMarks);
        */
    }

    private void makeMarks(Message msg) {
        if (msg.src == "SizeGraph") {

        }
        /*
        heatMarks = new HashMap<String,String[]>();
        netMarks = new HashMap<String,Boolean>();
        initCatMarks();
        for (Event e : events) {
            if (Condition.checkConditions(msg.conds, e)) {
                String x[] = {e.dstPort, e.time};
                heatMarks.put(e.dstPort+e.time, x);
                netMarks.put(e.srcIP, true);
            }
            else if (msg.condsOR != null) {
                for (Condition[] list : msg.condsOR) {
                    if (Condition.checkConditions(list, e)) {
                        String x[] = {e.dstPort, e.time};
                        heatMarks.put(e.dstPort+e.time, x);
                        netMarks.put(e.dstIP, true);
                    }
                }
            }
        }
        */
    }



    //Possible messages to receive:
        //Type of Car
        //Type of Car + Brand
        //List of Models
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
