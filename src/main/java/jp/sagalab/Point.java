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

	public double getParameter(){
		return m_parameter;
	}

	@Override
	public String toString() {
		return "Point{" +
				"m_x=" + m_x +
				", m_y=" + m_y +
				", m_parameter=" + m_parameter +
				'}';
	}

	private Point(Double _x, Double _y, Double _time) {
		m_x = _x;
		m_y = _y;
		m_parameter = _time;
	}

	private final Double m_x;
	private final Double m_y;
	private final Double m_parameter;
}
