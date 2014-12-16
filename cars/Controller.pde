import java.util.Iterator;
import java.lang.Iterable;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

boolean pcmarks[] = null;
TypeGraph typemarks = null;
class TypeGraph {
    ArrayList<String>types;
    ArrayList<Float>percents;
};

class Controller {
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

    public void mousePressed() {
        //reset
    }
    public void mouseReleased() {
        pc.mouseClick();
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
        pcmarks = new boolean[data_00.getRowCount()];
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
            //make brand graph
        }
        makeMarks(msg);
        setMarksOfViews();
    }

};
