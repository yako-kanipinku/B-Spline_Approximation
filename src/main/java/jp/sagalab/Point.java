package jp.sagalab;

/**
 * 平面上の点を表す. 平面上の点のx座標とy座標とパラメータを保持する.
 * 今回は、弦長パラメータ.
 * @author yako
 */
public class Point {

	/**
	 * 指定した座標にある点を生成する.
	 * @param _x x座標
	 * @param _y y座標
	 * @param _parameter パラメータ
	 * @return 点とパラメータ
	 */
	public static Point create(Double _x, Double _y, Double _parameter){
		return new Point(_x, _y, _parameter);
	}

	/**
	 * 指定した座標にある点を生成する.
	 * @param _x x座標
	 * @param _y y座標
	 * @return 点
	 */
	public static Point create(Double _x, Double _y){
		return new Point(_x, _y, 0.0);
	}

	/**
	 * 点のx座標を取得する.
	 * @return x座標
	 */
	public double getX(){
		return m_x;
	}

	/**
	 * 点のy座標を取得する.
	 * @return y座標
	 */
	public double getY(){
		return m_y;
	}

	/**
	 * パラメータを取得する.
	 * @return パラメータ
	 */
	public double getParameter(){
		return m_parameter;
	}

	/**
	 * 点のx座標, y座標, パラメータを表示する.
	 * 既存のtoStringクラス.
	 * @return 点のx座標, y座標, パラメータの表示.
	 */
	@Override
	public String toString() {
		return "  Point{" +
				"m_x=" + m_x +
				", m_y=" + m_y +
				", m_parameter=" + m_parameter +
				'}';
	}


	private Point(Double _x, Double _y, Double _parameter) {
		m_x = _x;
		m_y = _y;
		m_parameter = _parameter;
	}

	/** x座標 */
	private final Double m_x;
	/** y座標 */
	private final Double m_y;
	/** パラメータ */
	private final Double m_parameter;
}
