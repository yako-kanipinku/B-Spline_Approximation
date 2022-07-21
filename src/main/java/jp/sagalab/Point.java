package jp.sagalab;

public class Point {

	public static Point create(Double _x, Double _y, Double _time){
		return new Point(_x, _y, _time);
	}

	public static Point create(Double _x, Double _y){
		return new Point(_x, _y, 0.0);
	}

	public double getX(){
		return m_x;
	}

	public double getY(){
		return m_y;
	}

	public double getDistance(Point _p){
		double x = _p.getX() - m_x;
		double y = _p.getY() - m_y;
		return Math.sqrt(x*x + y*y);
	}


	public double getTime(){
		return m_time;
	}

	private Point(Double _x, Double _y, Double _time) {
		m_x = _x;
		m_y = _y;
		m_time = _time;
	}

	private final Double m_x;
	private final Double m_y;
	private final Double m_time;
}
