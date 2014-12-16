//min max 
//number of ticks
//location
//draw
//animation

class Axis {
	//add x y values
	float minValue; 
	float maxValue;
	float intervalValue;
	int numTicks;
	Viewport vp;
	Viewport tempvp;
	float intervalH;
	String label;
	float margin = (float).90;
	boolean flipped = false;

	class TextLabel {  
  //Vector coordinates for the box around text
		PVector p1 = null;
		PVector p2 = null;
		float a  = textAscent() * (float)0.4;
		String label = ""; 
		boolean intersect;
		TextLabel(float tx, float ty,  String label_) {
			float x1_ = tx;
			float y1_ = ty - a; 
			float x2_ = x1_ + textWidth(label_);
			float y2_ = y1_+ a;
			float x1 = x1_ < x2_ ? x1_ : x2_;
			float x2 = x1_ >= x2_ ? x1_ : x2_;
			float y1 = y1_ < y2_ ? y1_ : y2_;
			float y2 = y1_ >= y2_ ? y1_ : y2_;
			p1 = new PVector(x1, y1);
			p2 = new PVector(x2, y2);
			label = label_;
		}

		void drawTextLabel(){
			fill(0, 0, 0);
			text(label, p1.x, p2.y);
		}

		boolean isIntersecting(){
			if (mouseX > p1.x-10 && mouseX < p2.x+10 && mouseY > p1.y-30 && mouseY < p2.y+40){
				return true;
			} else {
				return false;
			}
		}
	}
	HashMap<Integer, TextLabel> tickLabels;
	TextLabel labelCoord;

	Axis(Viewport vp, String label, float min_, float max_, int numTicks){
		this.vp = vp;
		this.label = label;
		minValue = min_;
		maxValue = max_;
		intervalH = vp.getH()*margin/numTicks;
		intervalValue = (maxValue - minValue) / numTicks;
		this.numTicks = numTicks;
		tempvp = null;
	}

	void switchAxis() {
		if (flipped) flipped = false;
		else flipped = true;
	}

	void draw() {
		fill(200);
		rect(vp.getX(), vp.getY(), vp.getW()/30, vp.getH()*margin);
		stroke(0);
		fill(0);
		drawTicksLabels();
		labelCoord = new TextLabel( vp.getX()-(textWidth(label)/(float)2.4), vp.getY() + vp.getH(), label); 
		labelCoord.drawTextLabel();
	}

	void drawTicksLabels(){
		tickLabels = new HashMap<Integer, TextLabel>();
		float current =  (float)(Math.round(maxValue * 100.0) / 100.0); 
		float incrementValue = (vp.getH()*margin)/numTicks;
		if (flipped) {
			current =  (float)(Math.round(minValue * 100.0) / 100.0); 
			for (int i = 0; i <= numTicks; i++){
				line(vp.getX() - vp.getW()/39,
					vp.getY() + (incrementValue*i),
					vp.getX() + vp.getW()/16,
					vp.getY() + (incrementValue*i));
				TextLabel tempText = new TextLabel(vp.getX()-50, vp.getY()+(incrementValue*i), Float.toString((float)(Math.round(current * 100.0) / 100.0))); 
				tempText.drawTextLabel();
				tickLabels.put(i, tempText);
				current += (float)(Math.round(intervalValue * 100.0) / 100.0);  
			}
		}
		else {
			for (int i = 0; i <= numTicks; i++){
				line(vp.getX() - vp.getW()/39,
					vp.getY() + (incrementValue*i),
					vp.getX() + vp.getW()/16,
					vp.getY() + (incrementValue*i));
				TextLabel tempText = new TextLabel(vp.getX()-50, vp.getY()+(incrementValue*i), Float.toString((float)(Math.round(current * 100.0) / 100.0))); 
				tempText.drawTextLabel();
				tickLabels.put(i, tempText);
				current -= (float)(Math.round(intervalValue * 100.0) / 100.0);  
			}
		}
	}
	//Getter functions
    //since axises are just viewports the value is stored within here
	float getX(){return vp.getX(); }
	float getY(){return vp.getY(); }
	float getRange(){return maxValue-minValue; }
	float getH(){return vp.getH()*margin; }
	float getW(){return vp.getW()/30; }
	float getMin(){return minValue; }
	float getMax(){return maxValue; }
	String getName(){return label; }

  // Convert from data coordinates to axis / window coordinates
	float getLoc(float y) {
		if(flipped) return getY() + getH()*margin * (y - getMin()) / getRange();
		else return getY() + getH()*margin - getH()*margin * (y - getMin()) / getRange();
	}

	boolean isOnMe() {
		return (getY()<mouseY&&getX()<mouseX&&(getX()+getW())>mouseX&&(getY()+getH())>mouseY);
	}

	boolean isinWidth() {
		return (getX()<=mouseX && getX()+getW()>=mouseX) || labelCoord.isIntersecting();
	}

	boolean moveAxis(float x){
		if (labelCoord != null && labelCoord.isIntersecting()){
			if (tempvp == null) tempvp = new Viewport(this.vp);
			vp.setX(x);
			return true;
		}
		return false;
  	}

  	void swap(Axis a) {
  		a.vp = this.vp;
  		this.vp = a.tempvp;
  		a.tempvp = null;
  	}

  	void resetAxis() {
  		if (tempvp != null) {
  			vp = tempvp;
  			tempvp = null;
  		}
  	}
};

