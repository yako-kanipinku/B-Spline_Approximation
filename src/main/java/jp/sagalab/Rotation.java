package jp.sagalab;

/**
 * Transformインターフェースの回転変換での実装.
 * 任意の中心点と角度を設定し、指定した点を回転させる機能を提供する.
 */
public class Rotation implements Transform {

	/**
	 * 指定した中心点と角度で点を回転させるオブジェクトを生成する.
	 * @param _degree 回転角度(弧度法)
	 * @param _pivot 中心点
	 * @return Rotationクラスのインスタンス
	 */
	public static Rotation of(Double _degree, Point _pivot){
		return new Rotation(_degree, _pivot);
	}

	/**
	 * 指定した点に対して回転変換を行う.
	 * @param _p 回転変換する点
	 * @return 回転変換された点
	 */
	@Override
	public final Point apply(Point _p){
		Double radian = Math.toRadians(m_degree);
		Double x = _p.getX()-m_pivot.getX();
		Double y = _p.getY()-m_pivot.getY();
		Double x_prime = x*Math.cos(radian)-y*Math.sin(radian);
		Double y_prime = x*Math.sin(radian)+y*Math.cos(radian);
		return Point.create(x_prime + m_pivot.getX(), y_prime + m_pivot.getY());
	}

	/** 回転角度(弧度法) */
	private Double m_degree;
	/** 中心点 */
	private Point m_pivot;

	private Rotation(Double _degree, Point _pivot){
		m_degree = _degree;
		m_pivot = _pivot;
	}

}
