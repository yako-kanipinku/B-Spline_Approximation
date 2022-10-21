package jp.sagalab;

import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 描画するためのクラス.
 */
public class Painter extends JFrame {

	static void create(){
		new Painter();
	}

	/**
	 * ウィンドウ閉じる時に出るダイアログ.
	 */
	class WindowClosing extends WindowAdapter{

		/**
		 * 本当に閉じていいのか、確認するためのダイアログ.
		 * @param e WindowEvent
		 */
		public void windowClosing(WindowEvent e){
			int ans = JOptionPane.showConfirmDialog(Painter.this,  "本当に閉じますか ?");
			if(ans == JOptionPane.YES_OPTION)
				System.exit(0);
		}
	}

	/**
	 * 点を表示するためのメソッド.
	 * @param _p 点
	 * @param _color 色
	 */
	public void drawPoint(Point _p, Color _color) {
		Graphics g = canvas.getGraphics();
		g.setColor(_color);
		int diameter = 8;
		g.drawOval((int)_p.getX()-diameter/2, (int)_p.getY()-diameter/2, diameter, diameter);
	}

	/**
	 * 線を表示するためのメソッド.
	 * @param _previousPoint 打った点の一つ前の点
	 * @param _currentPoint 打った点
	 * @param _color 色
	 */
	public void drawLine(Point _previousPoint, Point _currentPoint, Color _color) {
		Graphics g = canvas.getGraphics();
		g.setColor(_color);
		g.drawLine((int) _previousPoint.getX(), (int) _previousPoint.getY(), (int) _currentPoint.getX(), (int) _currentPoint.getY());
	}

	/**
	 * キャンバスをクリアにするメソッド.
	 */
	public void cleanCanvas(){
		Graphics g = canvas.getGraphics();
		g.clearRect(0, 0, 1000, 800);
	}

	/**
	 * _previousPointと_currentPointの距離を求める関数.
	 * @param _previousPoint 打った点の一つ前の点.
	 * @param _currentPoint 打った点.
	 * @return 距離.
	 */
	public static double getDistance(Point _previousPoint, Point _currentPoint){

		double x = _currentPoint.getX() - _previousPoint.getX();
		double y = _currentPoint.getY() - _previousPoint.getY();

		// 三平方の定理
		double nowDistance = Math.sqrt(x*x + y*y);
		// System.out.println("  nowDistance: "+nowDistance);

		return nowDistance;
	}

	/**
	 * 点列の要素をcsvファイルへと出力
	 * @param _points 点列
	 */
	public void writeToCSV(List<Point> _points) {

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

			FileWriter fw = new FileWriter("files/points/" + fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			pw.print("x");
			pw.print(",");
			pw.print("y");
			pw.print(",");
			pw.println("parameter");

			for (int i = 0; i < _points.size(); i++) {
				pw.print(_points.get(i).getX());
				pw.print(",");
				pw.print(_points.get(i).getY());
				pw.print(",");
				pw.print(_points.get(i).getParameter());
				pw.println();
			}

			pw.close();

			System.out.println("点列の出力が正常に終わりました");
			System.out.println("ファイル名:" + fileName);

		} catch (IOException ex) {
		}

	}

	/**
	 * 描画の設定.
	 */
	private Painter(){
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		addWindowListener(new WindowClosing());
		setState(JFrame.ICONIFIED);
		setIconImage(new ImageIcon("icon2.jpg").getImage());
		canvas.setSize(1000, 800);
		canvas.setBackground(Color.WHITE);
		setTitle("B-Spline Approximation");
		add(canvas);
		pack();
		setVisible( true );

		canvas.addMouseListener(
				new MouseAdapter() {

					@Override
					public void mousePressed(MouseEvent e) {

						m_points.clear();
						cleanCanvas();
						Point point = Point.create((double) e.getX(), (double) e.getY(), 0.0);
						// 最初の点は追加しない.
						m_points.add( point );
						drawPoint(point, Color.BLACK);
						m_previousPoint = point;
					}

					@Override
					public void mouseReleased(MouseEvent e){
						/** 軸となるxcを表す. */


						Point point = Point.create((double)e.getX(), (double)e.getY(), 0.0);

						double distance = getDistance(m_previousPoint, point);

						Point point1 = Point.create(point.getX(), point.getY(), distance + m_previousPoint.getParameter());

						m_points.add(point1);
						// 最後の点も追加しない.

						drawPoint(point1, Color.BLACK);
						drawLine(m_previousPoint, point, Color.BLACK);

						// 全ての点のパラメータを表示.
						for (Point m_point : m_points) {
							System.out.println(m_point);
						}

						BSplineApprox sci = BSplineApprox.create(m_points, 3);
						List<Point> controlPoints = new ArrayList<>(sci.getControlPoints());

						writeToCSV(controlPoints);

						for(int i=0; i < controlPoints.size(); i++){
							Point y = Point.create(controlPoints.get(i).getX(), controlPoints.get(i).getY());

							// 最初と最後の制御点は緑色、他は青.
							if(i==0 || i==controlPoints.size()-1){
								drawPoint(y, Color.GREEN);
							}
							else {
								drawPoint(y, Color.BLUE);
							}

						}

						SplineCurve sc = SplineCurve.create(controlPoints, sci.getKnots());

						Interval domain = sc.getDomain();
						Point tmp = sc.evaluate(domain.getBegin());
						double interval = 0.01;
						double loopCount = (domain.getEnd() - domain.getBegin()) / interval;

						for(int i = 1; i <= loopCount; i++){
							double t = domain.getBegin() + interval * i;

							Point p = sc.evaluate(t);

							drawLine(tmp, p, Color.RED);

							tmp = p;
						}

//						writeToCSV(m_points);

					}

				}

		);

		canvas.addMouseMotionListener(
				new MouseAdapter() {

					@Override
					public void mouseDragged(MouseEvent e){
						Point point = Point.create((double)e.getX(), (double)e.getY(), 0.0);

						double distance = getDistance(m_previousPoint, point);

						Point point1 = Point.create(point.getX(), point.getY(), distance + m_previousPoint.getParameter());

						m_points.add(point1);

						// マウスドラッグが止まった場合の点を打つ処理.
//						double timeDelta = point.getTime() - m_previousPoint.getTime();
//						if (timeDelta > 0.1){
//							double time = 0.01;
//							for (double i=m_previousPoint.getTime(); i < point.getTime(); i+=time){
//								Point clone = Point.create(m_previousPoint.getX(), m_previousPoint.getY(), i);
//								m_points.add(clone);
//							}
//						}

//						m_points.add( point );

						drawLine(m_previousPoint, point1, Color.BLACK);
						m_previousPoint = point1;
					}
				}
		);

	}

	/** 打った点の一つ前の点 */
	private Point m_previousPoint;

	/** 点列 */
	private List<Point> m_points = new ArrayList<>();

	/** キャンバス */
	private final Canvas canvas = new Canvas();
}
