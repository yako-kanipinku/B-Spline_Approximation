package jp.sagalab;

/**
 * Transformインターフェースの平行移動での実装.
 * 任意のベクトルを設定し、指定した点を平行移動させる機能を提供する.
 */
public class Translation implements Transform{

	/**
	 * 指定したベクトルの方向に点を平行移動させるオブジェクトを生成する.
	 * @param _vx ベクトルのx座標
	 * @param _vy ベクトルのy座標
	 * @return Translationクラスのインスタンス
	 */
	public static Translation of(Double _vx, Double _vy){
		return new Translation(_vx, _vy);
	}

	/**
	 * 指定した点に対して平行移動を行う.
	 * @param _p 平行移動する点
	 * @return 平行移動された点
	 */
	@Override
	public final Point apply(Point _p){
		Double x_prime = _p.getX() + m_vx;
		Double y_prime = _p.getY() + m_vy;
		return Point.create(x_prime, y_prime);
	}

	/** ベクトルのx座標 */
	private Double m_vx;
	/** ベクトルのy座標 */
	private Double m_vy;

	private Translation(Double _vx, Double _vy){
		m_vx = _vx;
		m_vy = _vy;
	}

}
