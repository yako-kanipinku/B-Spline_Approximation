package jp.sagalab;

import java.util.ArrayList;
import java.util.List;

/**
 * 区間を表す.
 * @author yako
 */
public class Interval {

	/**
	 * 閉区間[begin,end]を生成する.
	 * @param _begin 閉区間の始点
	 * @param _end 閉区間の終点
	 * @return 閉区間[begin,end]
	 * @throws IllegalArgumentException
	 */
	public static Interval create(Double _begin, Double _end){
		if(_begin > _end)
			throw new IllegalArgumentException("please do _begin < _end");
		return new Interval(_begin, _end);
	}

	/**
	 * 区間の始点を取得する.
	 * @return 区間の始点
	 */
	public Double getBegin(){
		return m_begin;
	}

	/**
	 * 区間の終点を取得する.
	 * @return 区間の終点
	 */
	public Double getEnd(){
		return m_end;
	}

	/**
	 * 区間を_num-1等分するような_num個のパラメータのリスト(始点を終点を含む)を生成する.
	 * @param _num サンプル数
	 * @return サンプリングした値
	 * @throws IllegalArgumentException サンプル数が不正な値であった場合
	 */
	public List<Double> sample(Integer _num){
		List<Double> samples = new ArrayList<Double>();
		if(_num <= 1){
			if(m_begin.equals(m_end)){
				if(_num != 1){
					throw new IllegalArgumentException("_num must be positive");
				}
				samples.add(m_end);
				return samples;
			}
			throw new IllegalArgumentException("_num must be more than 1");
		}
		final Double width = (m_end-m_begin)/(_num.doubleValue()-1.0);
		for(int i=0; i<_num; i++) {
			samples.add(m_begin + i * width);
		}
		return samples;

	}

	/**
	 * 指定した値が区間に含まれているかを調べる.
	 * 区間に含まれている場合はtrue、それ以外の場合はfalseを返す.
	 * @param _t 区間内であるかを調べる値
	 * @return 区間に含まれているか
	 */
	public boolean contains(Double _t){
		return m_begin <= _t && _t <= m_end;
	}

	/** 始点 */
	private Double m_begin;
	/** 終点 */
	private Double m_end;

	private Interval(Double _begin, Double _end){
		m_begin = _begin;
		m_end = _end;
	}

}
