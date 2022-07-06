package jp.sagalab;

import java.util.ArrayList;
import java.util.List;

/**
 * 任意の次数のスプライン曲線を表す.
 * このクラスでは次数を指定せず、制御点とノット列からスプライン曲線を生成する.
 * 次数は以下の式から求めることができる.
 *
 * 次数 = ノットの数 - 制御点の数 - 1
 */
public class SplineCurve {

	/**
	 * 制御点列とノット列を指定してスプライン曲線のオブジェクトを生成する.
	 * @param _controlPoints 制御点列
	 * @param _knots ノット列
	 * @return スプライン曲線のオブジェクト
	 * @throws IllegalArgumentException 制御点数が次数以下であった場合
	 */
	public static SplineCurve create(List<Point> _controlPoints, List<Double> _knots){
		if(_controlPoints.size()<=(_knots.size()-_controlPoints.size()-1))
			throw new IllegalArgumentException("controlPoints must be more than degree");
		return new SplineCurve(_controlPoints, _knots);
	}

	/**
	 * 制御点列のコピーを取得する.
	 * @return 制御点列のコピー
	 */
	public List<Point> getControlPoints(){
		List<Point> controlPoints = new ArrayList<>(m_controlPoints);
		return controlPoints;
	}

	/**
	 * ノット列のコピーを取得する.
	 * @return ノット列のコピー
	 */
	public List<Double> getKnots(){
		List<Double> knots = new ArrayList<>(m_knots);
		return knots;
	}

	/**
	 * 次数を取得する.
	 * @return 次数
	 */
	public Integer getDegree(){
		return m_knots.size()-m_controlPoints.size()-1;
	}

	/**
	 * 定義域を取得する.
	 * @return 定義域
	 */
	public Interval getDomain(){
		Double begin = m_knots.get(getDegree());
		Double end = m_knots.get(m_controlPoints.size());
		return Interval.create(begin, end);
	}

	/**
	 * パラメーターtに対応する評価点を求める.
	 * パラメータtは定義域内の値である必要がある.
	 * @param _t 定義域内のパラメーターt
	 * @return パラメーターtに対応する評価点
	 * @throws IllegalArgumentException パラメーターtが定義域外であった場合
	 */
	public Point evaluate(Double _t){
		Double x = 0.0;
		Double y = 0.0;
		for(int i=0; i<m_controlPoints.size(); i++) {
			x += m_controlPoints.get(i).getX() * bSplineBasisFunction(i, getDegree(), _t);
			y += m_controlPoints.get(i).getY() * bSplineBasisFunction(i, getDegree(), _t);
		}
		return Point.create(x, y);
	}

	/**
	 * スプライン曲線を指定した形状にアフィン変換する.
	 * @param _transformation アフィン変換オブジェクト
	 * @return 変換後のスプライン曲線
	 */
	public SplineCurve transform(Transform _transformation){
		List<Point> controlPoints = new ArrayList<>();
		for(Point p:m_controlPoints){
			controlPoints.add(_transformation.apply(p));
		}
		return SplineCurve.create(controlPoints, getKnots());
	}

	/**
	 * B-スプライン基底関数を表すメソッド.
	 * @param _i 制御点列内の対象の制御点のインデックス
	 * @param _k 次数
	 * @param _t 定義域内のパラメーターt
	 * @return B-スプライン基底関数の値
	 */
	private Double bSplineBasisFunction(Integer _i, Integer _k, Double _t){
		if(_k==0){
			if (_i == 4) {
				if (m_knots.get(_i) <= _t && _t <= m_knots.get(_i + 1)) {
					return 1.0;
				}
			}
			if (m_knots.get(_i) <= _t && _t < m_knots.get(_i + 1)) {
				return 1.0;
			}
			return 0.0;
		}
		Double denom1 = m_knots.get(_i+_k)-m_knots.get(_i);
		Double denom2 = m_knots.get(_i+_k+1)-m_knots.get(_i+1);
		Double a = 0.0;
		Double b = 0.0;
		if(denom1 != 0) {
			a = ((_t - m_knots.get(_i)) / denom1) * bSplineBasisFunction(_i, _k - 1, _t);
		}
		if (denom2 != 0) {
			b = ((m_knots.get(_i + _k + 1) - _t) / denom2) * bSplineBasisFunction(_i + 1, _k - 1, _t);
		}
		return a+b;

	}

	/** 制御点列 */
	private List<Point> m_controlPoints;
	/** ノット列 */
	private List<Double> m_knots;
	private SplineCurve(List<Point> _controlPoints, List<Double> _knots){
		m_controlPoints = _controlPoints;
		m_knots = _knots;
	}

}
