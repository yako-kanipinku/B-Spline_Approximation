package jp.sagalab;

import java.util.ArrayList;
import java.util.List;

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

		// 三平方の定理
		double nowDistance = Math.sqrt(x*x + y*y);

		System.out.println("nowDistance  : "+nowDistance);

		// 最初の要素を0.0とする.
		m_distance.add(0,0.0);

		// 打った点の距離を次々足していく.
		double totalDistance = nowDistance + m_distance.get(m_distance.size()-1);
		// 要素として追加.
		m_distance.add(totalDistance);

		System.out.println("totalDistance: "+totalDistance);
		// その時点での最後の点の距離を返す.
		return m_distance.get(m_distance.size()-1);
	}


	public double getTime(){
		return m_time;
	}

	private Point(Double _x, Double _y, Double _time) {
		m_x = _x;
		m_y = _y;
		m_time = _time;
	}

	private final List<Double> m_distance = new ArrayList<>();
	private final Double m_x;
	private final Double m_y;
	private final Double m_time;
}
