/*
String[] parseData(String filename, ArrayList<HashMap<String,Float>> data) {

  String t0[] = loadStrings(filename);
  String t1[];
  HashMap<String,Float> curr;

  String labels[] = splitTokens(t0[0],",");

  for (int i = 1; i < t0.length; i++) {
    t1 = splitTokens(t0[i], ",");
    curr = new HashMap<String,Float>();
    for (int j = 0; j < t1.length; j++) {
      curr.put(labels[j], Float.parseFloat(t1[j]));
    }
    data.add(curr);
  }
  return labels;

}
*/
