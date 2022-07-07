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
		final int cpsSize = _controlPoints.size();
		final int knotsSize = _knots.size();

		if (!(cpsSize <= knotsSize && knotsSize <= (2 * cpsSize - 2))) {
			throw new IllegalArgumentException("controlPoints must be more than degree");
		}

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
		return m_knots.size() - m_controlPoints.size() + 1;
	}

	/**
	 * 定義域を取得する.
	 * @return 定義域
	 */
	public Interval getDomain() {
		final Integer k = getDegree();
		final Integer l = numberOfSections();
		final Integer startIndex = k - 1;
		final Integer endIndex = k + l - 1;

		return Interval.create(m_knots.get(startIndex), m_knots.get(endIndex));
	}


	public Integer numberOfSections() {
		Integer cpsSize = m_controlPoints.size();
		Integer knotsSize = m_knots.size();

		return 2 * cpsSize - knotsSize - 1;
	}

	/**
	 * パラメーターtに対応する評価点を求める.
	 * パラメータtは定義域内の値である必要がある.
	 * @param _t 定義域内のパラメーターt
	 * @return パラメーターtに対応する評価点
	 * @throws IllegalArgumentException パラメーターtが定義域外であった場合
	 */
	public Point evaluate(Double _t){
		if (!getDomain().contains(_t)) {
			throw new IllegalArgumentException("_t out of domain.");
		}

		double x = 0.0;
		double y = 0.0;
		final Integer degree = getDegree();

		for(int i=0; i < m_controlPoints.size(); i++) {
			final Double basis = bSplineBasisFunction(i, degree, _t);
			x += m_controlPoints.get(i).getX() * basis;
			y += m_controlPoints.get(i).getY() * basis;
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
		int controlPointsSize = m_controlPoints.size();

		{
			int knotsSize = m_knots.size();
			int n = knotsSize - controlPointsSize + 1;

			if (_i == 0) {
				double coeff = (m_knots.get(_i + _k) - _t) / (m_knots.get(_i + _k) - m_knots.get(_i));
				return coeff * bSplineBasisFunction(_i + 1, _k - 1, _t);
			}

			// 右端では u(knotsSize) は考慮しない（p.48 図3.14、p.50 図3.16、図3.17）
			if (_i + _k == knotsSize) {
				double coeff = (_t - m_knots.get(_i - 1)) / (m_knots.get(_i + _k - 1) - m_knots.get(_i - 1));
				return coeff * bSplineBasisFunction(_i, _k - 1, _t);
			}

			if (_k == 0) {
				// 節点列の終端がn重節点になっているか
				boolean isN_Overlapped = m_knots.get(m_knots.size() - 1).equals(m_knots.get(m_knots.size() - n));

				if (_i == controlPointsSize - 1 && m_knots.get(knotsSize - 1).equals(_t) && isN_Overlapped) {
					return 1.0;
				}

				return (m_knots.get(_i - 1) <= _t && _t < m_knots.get(_i)) ? 1.0 : 0.0;
			}
		}
		Double denom1 = m_knots.get(_i + _k - 1) - m_knots.get(_i - 1);
		Double denom2 = m_knots.get(_i + _k) - m_knots.get(_i);
		double coeff1 = (denom1 != 0.0) ? (_t - m_knots.get(_i - 1)) / denom1 : 0.0;
		double coeff2 = (denom2 != 0.0) ? (m_knots.get(_i + _k) - _t) / denom2 : 0.0;

		return coeff1* bSplineBasisFunction(_i, _k - 1, _t) + coeff2 * bSplineBasisFunction(_i + 1, _k - 1, _t);

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
