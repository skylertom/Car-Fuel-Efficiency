//min max 
//number of ticks
//location
//draw
//animation
import java.util.*;
import processing.core.*; 

class Axis {
	//add x y values
	float minValue; 
	float maxValue;
	float intervalValue;
	int numTicks;
	Viewport vp;
	float intervalH;
	String label;
	float margin = (float).90;
	boolean flipped = false;

	class TextLabel {  
  //Vector coordinates for the box around text
		processing.core.PVector p1 = null;
		processing.core.PVector p2 = null;
		float a  = vp.p.textAscent() * (float)0.4;
		String label = ""; 
		boolean intersect;
		TextLabel(float tx, float ty,  String label_) {
			float x1_ = tx;
			float y1_ = ty - a; 
			float x2_ = x1_ + vp.p.textWidth(label_);
			float y2_ = y1_+ a;
			float x1 = x1_ < x2_ ? x1_ : x2_;
			float x2 = x1_ >= x2_ ? x1_ : x2_;
			float y1 = y1_ < y2_ ? y1_ : y2_;
			float y2 = y1_ >= y2_ ? y1_ : y2_;
			p1 = new processing.core.PVector(x1, y1);
			p2 = new processing.core.PVector(x2, y2);
			label = label_;
		}

		void drawTextLabel(){
			if (isIntersecting()){
				vp.p.fill(255, 0, 0    );
			} else {
				vp.p.fill(0, 0, 0);
			}
			vp.p.text(label, p1.x, p2.y);
		}

		boolean isIntersecting(){
			if (vp.p.mouseX > p1.x-10 && vp.p.mouseX < p2.x+10 && vp.p.mouseY > p1.y-30 && vp.p.mouseY < p2.y+40){
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
	}

	void switchAxis() {
		if (flipped) flipped = false;
		else flipped = true;
	}

	void draw() {
		vp.p.fill(200);
		vp.p.rect(vp.getX(), vp.getY(), vp.getW()/30, vp.getH()*margin);
		vp.p.stroke(0);
		vp.p.fill(0);
		drawTicksLabels();
		labelCoord = new TextLabel( vp.getX()-(vp.p.textWidth(label)/(float)2.4), vp.getY() + vp.getH(), label); 
		labelCoord.drawTextLabel();
	}

	void drawTicksLabels(){
		tickLabels = new HashMap<Integer, TextLabel>();
		float current =  (float)(Math.round(maxValue * 100.0) / 100.0); 
		float incrementValue = (vp.getH()*margin)/numTicks;
		if (flipped) {
			current =  (float)(Math.round(minValue * 100.0) / 100.0); 
			for (int i = 0; i <= numTicks; i++){
				vp.p.line(vp.getX() - vp.getW()/39,
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
				vp.p.line(vp.getX() - vp.getW()/39,
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
		return (getY()<vp.p.mouseY&&getX()<vp.p.mouseX&&(getX()+getW())>vp.p.mouseX&&(getY()+getH())>vp.p.mouseY);
	}

	void moveAxis(float x){
		if (vp.p.mousePressed){
			if (labelCoord != null && labelCoord.isIntersecting()){
  		    vp.setX(x);
  		}
  	}
  }
}

