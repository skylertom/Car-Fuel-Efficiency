class Condition {
    String col = null;
    String operator = null;
    String value = "-1"; 

    // create a new Condition object that specifies some data column
    // should have some relationship to some value
    //   col: column name of data the relationship applies to
    //   op: operator (e.g. "<=")
    //   value: value to compare to
    Condition(String col, String op, String value) {
        this.col = col;
        this.operator = op;
        this.value = value;
    }
    
    public String toString() {
        return col + " " + operator + " " + value + " ";
    }
    
    boolean equals(Condition cond){
        return operator.equals(cond.operator) && 
        value == cond.value && 
        col.equals(cond.col);
    }

    boolean checkConditions(Condition[] conds, String e) {
        if(conds == null || e == null){
            return false;
        }
        boolean and = true;
        for (int i = 0; i < conds.length; i++) {
            if (!checkCondition(conds[i], e)) return false;
        }
        return true;
    }

    boolean checkCondition(Condition cond, String e) {
        if (cond == null || cond.value == null || e == null) return false;
        if (cond.operator.equals("=")) { 
            return e.equals(cond.value);
        }
        return false;
    }

}
