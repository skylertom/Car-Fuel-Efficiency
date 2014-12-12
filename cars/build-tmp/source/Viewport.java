import processing.core.*;

public class Viewport {
  PApplet p;
	private float x, y, w, h;
	Viewport(PApplet parent) {
		x = 0;
		y = 0;
		w = 1;
		h = 1;
    p = parent;
	}
	Viewport(Viewport parent, float _x, float _y, float _w, float _h) {
		x = parent.getrelX() + _x*parent.getrelW();
		y = parent.getrelY() + _y*parent.getrelH();
		w = _w * parent.getrelW();
		h = _h * parent.getrelH();
    this.p = parent.p;
	}

	public float getrelX() {return x;}
	public float getrelY() {return y;}
	public float getrelW() {return w;}
	public float getrelH() {return h;}
	public float getX() {return p.width * x;}
	public float getY() {return p.height * y;}
	public float getW() {return p.width * w;}
	public float getH() {return p.height * h;}
	public void setX(float newX) {x = newX; }
};
