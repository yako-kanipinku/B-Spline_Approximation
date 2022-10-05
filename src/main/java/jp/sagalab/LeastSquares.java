package jp.sagalab;

/**
 * 最小二乗法を表すクラスです。
 *
 * @author Taiga Takashima
 */
public final class LeastSquares {
	/**
	 * Ax = b を x について解きます。
	 *
	 * @param _A
	 * @param _b
	 * @return x
	 */
	public static Matrix solve(Matrix _A, Matrix _b) {
		Matrix A_T = _A.transpose();
		Matrix A_TA = A_T.product(_A);

		return A_TA.solve(A_T.product(_b));
	}

	/**
	 * Ax = b を x について Cx = d の条件下で解きます。
	 *
	 * @param _A
	 * @param _b
	 * @param _C
	 * @param _d
	 * @return x
	 */
	public static Matrix solveConstrained(Matrix _A, Matrix _b, Matrix _C, Matrix _d) {
		Matrix A_T = _A.transpose();
		Matrix rA_T = A_T.magnify(2);
		Matrix rA_TA = rA_T.product(_A);

		Matrix lhs = Matrix.concatVertical(
				Matrix.concatHorizontal(rA_TA, _C.transpose()),
				Matrix.concatHorizontal(_C, Matrix.create(new double[_C.rowSize()][_C.rowSize()]))
		);

		Matrix rhs = Matrix.concatVertical(rA_T.product(_b), _d);

		return lhs.solve(rhs);
	}
}