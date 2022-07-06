package jp.sagalab;

/**
 * Transformインターフェースのスケーリング変換での実装.
 * 任意の中心点と各軸方向の拡大率を設定し、指定した点をスケーリングさせる機能を提供する.
 */
public class Scaling implements Transform{

	/**
	 * 指定した中心点と拡大率で点をスケーリングするオブジェクトを生成する.
	 * @param _sx x軸方向の拡大率
	 * @param _sy y軸方向の拡大率
	 * @param _pivot 中心点
	 * @return Scalingクラスのインスタンス
	 */
	public static Scaling of(Double _sx, Double _sy, Point _pivot){
		return new Scaling(_sx, _sy, _pivot);
	}

	/**
	 * 指定した点に対してスケーリング変換を行う.
	 * @param _p スケーリング変換をする点
	 * @return スケーリング変換後の点
	 */
	@Override
	public Point apply(Point _p){
		Double x = _p.getX()-m_pivot.getX();
		Double y = _p.getY()-m_pivot.getY();
		Double x_prime = x*m_sx;
		Double y_prime = y*m_sy;
		return Point.create(x_prime + m_pivot.getX(), y_prime + m_pivot.getY());
	}

	/** x軸方向の拡大率 */
	private Double m_sx;
	/** y軸方向の拡大率 */
	private Double m_sy;
	/** 中心点 */
	private Point m_pivot;

	private Scaling(Double _sx, Double _sy, Point _pivot){
		m_sx = _sx;
		m_sy = _sy;
		m_pivot = _pivot;
	}

}
