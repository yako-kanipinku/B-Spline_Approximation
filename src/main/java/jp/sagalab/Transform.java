package jp.sagalab;

/**
 * アフィン変換を表すインターフェース.
 * このインターフェースを実装することで、複数の種類のアフィン変換を統一的に扱うことができる.
 */
public interface Transform {
	/**
	 * 指定した点に対してアフィン変換を行う.
	 * @param _p アフィン変換する点
	 * @return 変換後の点
	 */
	Point apply(Point _p);
}
