package jp.sagalab;

import java.util.List;

/**
 * 対称軸となる直線を取り扱う.
 */
public class Axis {

	/**
	 * 軸のインスタンス生成を行う.
	 * @param _angle x軸となす角度(θ)
	 * @param _distance 直線の原点からの最短距離(ρ)
	 * @return 軸のインスタンス
	 */
	public static Axis create(double _angle, double _distance, double _grade) {
		return new Axis(_angle, _distance, _grade);
	}

	/**
	 * Axisリストのソート(降順ソート)を行う.
	 * @param _axisList 軸のリスト
	 */
	public static void sort(List<Axis> _axisList) {
		_axisList.sort((o1, o2) -> Double.compare(o2.getGrade(), o1.getGrade()));
	}

	public static void printAxis(Axis _axis) {
		System.out.printf("θ: %5.1f",Math.toDegrees(_axis.getAngle()));
		System.out.printf(" ρ: %7.2f", _axis.getDistance());
		System.out.printf(" grade: %f\n", _axis.getGrade());
	}

	/**
	 * x軸となす角度を取得する.
	 * @return x軸となす角度(θ)
	 */
	public double getAngle() {
		return m_angle;
	}

	/**
	 * 直線の原点からの最短距離を取得する.
	 * @return 直線の原点からの最短距離(ρ)
	 */
	public double getDistance() {
		return m_distance;
	}

	/**
	 * グレードを取得する.
	 * @return 軸のグレード
	 */
	public double getGrade() {
		return m_grade;
	}

	/**
	 * コンストラクタ
	 * @param _angle x軸となす角度(θ)
	 * @param _distance 直線の原点からの最短距離(ρ)
	 */
	private Axis(double _angle, double _distance, double _grade) {
		m_angle = _angle;
		m_distance = _distance;
		m_grade = _grade;
	}

	/** x軸となす角度(θ) */
	private final double m_angle;
	/** 直線の原点からの最短距離(ρ) */
	private final double m_distance;
	/** 軸のグレード */
	private final double m_grade;
}