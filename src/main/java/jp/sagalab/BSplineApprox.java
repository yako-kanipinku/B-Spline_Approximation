package jp.sagalab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;

/**
 * B-Spline近似を行うクラス.
 */
public class BSplineApprox {

	public static BSplineApprox create(List<Point> _points, int _degree){
		return new BSplineApprox(_points, _degree);
	}

	/**
	 * B-Splineの基底関数を取得するメソッド.
	 * @param _i 制御点列内の対象の制御点のインデックス
	 * @param _k 次数
	 * @param _t 定義域内のパラメータt
	 * @return B-スプライン基底関数の値
	 */
	private Double basisFunction(Integer _i, Integer _k, Double _t){
		{
			int knotsSize = m_knots.size();
			int controlPointsSize = m_knots.size() - m_degree + 1;
			int n = m_degree;

			if (_i == 0) {
				double coeff = (m_knots.get(_i + _k) - _t) / (m_knots.get(_i + _k) - m_knots.get(_i));
				return coeff * basisFunction(_i + 1, _k - 1, _t);
			}

			if (_i + _k == knotsSize) {
				double coeff = (_t - m_knots.get(_i - 1)) / (m_knots.get(_i + _k - 1) - m_knots.get(_i - 1));
				return coeff * basisFunction(_i, _k - 1, _t);
			}

			if (_k == 0) {
				boolean isN_Overlapped = m_knots.get(m_knots.size() - 1).equals(m_knots.get(m_knots.size() - n));

				if (_i == controlPointsSize - 1 && m_knots.get(knotsSize - 1).equals(_t) && isN_Overlapped) {
					return 1.0;
				}

				return (m_knots.get(_i - 1) <= _t && _t < m_knots.get(_i)) ? 1.0 : 0.0;
			}
		}

		double denom1 = m_knots.get(_i + _k - 1) - m_knots.get(_i - 1);
		double denom2 = m_knots.get(_i + _k) - m_knots.get(_i);
		double coeff1 = (denom1 != 0.0) ? (_t - m_knots.get(_i - 1)) / denom1 : 0.0;
		double coeff2 = (denom2 != 0.0) ? (m_knots.get(_i + _k) - _t) / denom2 : 0.0;

		return coeff1 * basisFunction(_i, _k - 1, _t) + coeff2 * basisFunction(_i + 1, _k - 1, _t);
	}

	/**
	 * ノット列を作成するメソッド.
	 * @return ノット列
	 */
	public List<Double> generateKnots(){
		double firstParameter = m_normalizedParameter.get(0);
		double lastParameter = m_normalizedParameter.get(m_normalizedParameter.size() - 1);
		int knotIntervalNum = (int)Math.ceil((lastParameter-firstParameter)/PARAMETER_NUMBER); // 区間数の決め方.
		// Math.ceil(); は引数として与えた数以上の最小の整数を返す.
		int knotSize = knotIntervalNum + 2 * m_degree - 1; // 節点の数.

		// System.out.println("区間数: "+ knotIntervalNum);

		List<Double> result = new ArrayList<>();

		for (int i = 0; i < knotSize; ++i){
			double w = (i - m_degree + 1) / (double) knotIntervalNum;
			result.add((1.0 - w) * firstParameter + w * lastParameter);
		}

		// System.out.println("接点: "+result);
		return result;
	}

	/**
	 * 正規化を行うメソッド.
	 *
	 * p0 = 点列の最初の点.
	 * pn = 点列の最後の点.
	 * kを 1 から m_points.size() までとすると、
	 * (pn - pk)/(pn - p0)
	 * という式で正規化できる.
	 * @return 正規化したリスト.
	 */
	public List<Double> normalizedParameter(){
		double pn = m_points.get(m_points.size()-1).getParameter();
		double p0 = m_points.get(0).getParameter();

		List<Double> pointParameter = new ArrayList<>();

		for (Point m_point : m_points) {
			double denominator = pn - p0;
			double numerator = m_point.getParameter() - p0;
			double result = numerator/denominator;
			pointParameter.add(result);
		}

		return pointParameter;

//		double timeLast = m_points.get(m_points.size()-1).getParameter();
//		// ex.) timeLast = 3.12
//		double lastFloored = (Math.floor(timeLast*10))/10;
//		// ex.) 3.12*10 = 31.2 floor→ 31.0 *10 = 3.1
//		double ratio = lastFloored / timeLast;
//		// ex.) 3.1/3.12 = 0.9935...
//		List<Double> result = new ArrayList<>();
//
//		for (Point p : m_points){
//			result.add(p.getParameter()*ratio); // 節点区間の時間間隔を狭める.
//
//			System.out.println("second: "+p.getParameter()*ratio);
//		}
//
//		return result;
	}

	/**
	 * 制御点を取得するメソッド.
	 * LU分解を使用.
	 * @return 制御点
	 */
	public List<Point> getControlPoints(){
		int size = m_points.size();
		int controlPointsSize = m_knots.size() - m_degree + 1;

		double[][] passMatrixRaw = new double[size*2][1];

		for (int i=0; i < size; ++i){
			passMatrixRaw[i][0] = m_points.get(i).getX();
			passMatrixRaw[i+size][0] = m_points.get(i).getY();
		}

		Matrix passMatrix = Matrix.create(passMatrixRaw);

//		showMatrix(passMatrix, "passMatrix");

		double[][] basisMatrixRaw = new double[size*2][controlPointsSize*2];

		for(int i=0; i < size; ++i){
			for(int j=0; j < controlPointsSize; ++j){
				double basis = basisFunction(j,m_degree, m_normalizedParameter.get(i));
				basisMatrixRaw[i][j] = basis;
				basisMatrixRaw[i+size][j+controlPointsSize] = basis;
			}
		}

		/** 二行のC,dをとりあえず作成 */
		double[][] c = new double[2][controlPointsSize*2];

		/** 最初の制御点と最後の制御点だけをあるxで対称化 */
		c[0][0] = 1;
		c[0][controlPointsSize-1] = 1;
		c[1][controlPointsSize] = 1;
		c[1][controlPointsSize*2-1] = -1;

		double[][] d = new double[2][1];
		d[0][0] = 2*500; // 2*x_c
		d[1][0] = 0;

		Matrix D = Matrix.create(d);
		showMatrix(D,"D");

		Matrix C = Matrix.create(c);
		showMatrix(C,"C");

		Matrix N = Matrix.create(basisMatrixRaw);
		Matrix N_T = N.transpose();
		Matrix N_TN = N_T.product(N);

//		writeMatrixToCSV(N);

		showMatrix(N, "N");
//		showMatrix(N_T, "N_T");
//		showMatrix(N_TN, "N_TN");

		Matrix N_Tp = N_T.product(passMatrix);

//		showMatrix(N_Tp, "N_Tp");

//		Matrix resultMatrix = N_TN.solve(N_Tp);
		Matrix resultMatrix = LeastSquares.solveConstrained(N, passMatrix, C, D);

		List<Point> result = new ArrayList<>();

		for(int i=0; i < controlPointsSize; ++i){
			double x = resultMatrix.get(i, 0);
			double y = resultMatrix.get(i+controlPointsSize, 0);

			result.add(Point.create(x,y));
		}

		return result;
	}

	/**
	 * 行列を表示するメソッド.
	 * @param m 行列
	 * @param name 行列の名前
	 */
	public void showMatrix(Matrix m, String name) {
		System.out.println("  ------------------ " + name);
		for (int i = 0; i < m.rowSize(); i++) {
			System.out.print("  {");
			for (int j = 0; j < m.columnSize(); j++) {
				System.out.printf("%.2f",m.get(i, j));
				if (j < m.columnSize()-1) {
					System.out.print(", ");
				}
			}
			System.out.println("}");
		}
		System.out.println();
	}

	/**
	 * 行列をcsvファイルに出力.
	 * @param m 行列
	 */
	public void writeMatrixToCSV(RealMatrix m) {

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int milliSecond = calendar.get(Calendar.MILLISECOND);

		String fileName = year + "_" + month + "_" + day + "_" + hour + "_" + minute + "_" + second + "_" + milliSecond + ".csv";

		try {

			FileWriter fw = new FileWriter("files/matrix/" + fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			for (int i = 0; i < m.getRowDimension(); i++) {
				for (int j = 0; j < m.getColumnDimension(); j++) {
					pw.printf("%.2f",m.getEntry(i, j));
					if (j < m.getColumnDimension()-1) {
						pw.print(",");
					}
				}
				pw.println();
			}

			pw.close();

			System.out.println("行列の出力が正常に終わりました");
			System.out.println("ファイル名:" + fileName);

		} catch (IOException ex) {
		}

	}

	/**
	 * ノット列のコピーを取得する.
	 * @return ノット列のコピー
	 */
	public List<Double> getKnots(){
		return new ArrayList<>(m_knots);
	}

	public BSplineApprox(List<Point> _points, int _degree){
		m_degree = _degree;
		m_points = new ArrayList<>(_points);
		m_normalizedParameter = normalizedParameter();
		m_knots = generateKnots();
	}

	/** 次数 */
	private final int m_degree;
	/** 点列 */
	private final List<Point> m_points;
	/** パラメータの列 */
	private final List<Double> m_normalizedParameter;
	/** ノット列 */
	private final List<Double> m_knots;
	/** 1区間分のパラメータ */
	private static final double PARAMETER_NUMBER = 1.0/20; // 最初は0.05. 間隔を広くすることで書き始めを区間内に入れる.
	// 20等分するため、1区間を1/20とする.
}
