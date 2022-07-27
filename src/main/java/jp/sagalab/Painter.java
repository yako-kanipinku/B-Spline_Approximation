package jp.sagalab;

import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

public class Painter extends JFrame {

	static void create(){
		new Painter();
	}

	class WindowClosing extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			int ans = JOptionPane.showConfirmDialog(Painter.this,  "本当に閉じますか ?");
			if(ans == JOptionPane.YES_OPTION)
				System.exit(0);
		}
	}

	public void drawPoint(Point _p, Color _color) {
		Graphics g = canvas.getGraphics();
		g.setColor(_color);
		int diameter = 8;
		g.drawOval((int)_p.getX()-diameter/2, (int)_p.getY()-diameter/2, diameter, diameter);
	}

	public void drawLine(Point _previousPoint, Point _currentPoint, Color _color) {
		Graphics g = canvas.getGraphics();
		g.setColor(_color);
		g.drawLine((int) _previousPoint.getX(), (int) _previousPoint.getY(), (int) _currentPoint.getX(), (int) _currentPoint.getY());
	}

	public void cleanCanvas(){
		Graphics g = canvas.getGraphics();
		g.clearRect(0, 0, 800, 600);
	}

	public long getTime(){
		long time = System.currentTimeMillis();
		return time;
	}

	/**
	 * _previousPointと_currentPointの距離を求める関数.
	 * @param _previousPoint 過去の点.
	 * @param _currentPoint 現在の点.
	 * @return 距離.
	 */
	public static double getDistance(Point _previousPoint, Point _currentPoint){

		double x = _currentPoint.getX() - _previousPoint.getX();
		double y = _currentPoint.getY() - _previousPoint.getY();

		// 三平方の定理
		double nowDistance = Math.sqrt(x*x + y*y);
		System.out.println("nowDistance: "+nowDistance);

		return nowDistance;
	}

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

						m_startTime = getTime();

						m_points.clear();
						cleanCanvas();
						Point point = Point.create((double) e.getX(), (double) e.getY(), 0.0);
						// 最初の点は追加しない.
						//m_points.add( point );
						drawPoint(point, Color.BLACK);
						m_previousPoint = point;
					}

					@Override
					public void mouseReleased(MouseEvent e){

						Point point = Point.create((double) e.getX(), (double) e.getY(),(double)(getTime()-m_startTime)/1000);
						// 最後の点も追加しない.
						//m_points.add( point );
						drawPoint(point, Color.BLACK);
						drawLine(m_previousPoint, point, Color.BLACK);

//						for (Point m_point : m_points) {
//							System.out.println(m_point);
//						}

						BSplineApprox sci = BSplineApprox.create(m_points, 3);
						List<Point> controlPoints = new ArrayList<>(sci.getControlPoints());

						for(int i=0; i < controlPoints.size(); i++){
							Point y = Point.create(controlPoints.get(i).getX(), controlPoints.get(i).getY());
							drawPoint(y, Color.BLUE);
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

					}

					// Output.writeToCSV(m_points);
				}

		);

		canvas.addMouseMotionListener(
				new MouseAdapter() {

					@Override
					public void mouseDragged(MouseEvent e){
						Point point = Point.create((double)e.getX(), (double)e.getY(), 0.0);

						double distance = getDistance(m_previousPoint, point);

						Point point1 = Point.create(point.getX(), point.getY(), distance+m_previousPoint.getParameter());

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
//						// test
//						System.out.println("getDistance: "+getDistance(m_previousPoint, point));

						drawLine(m_previousPoint, point1, Color.BLACK);
						m_previousPoint = point1;
					}
				}
		);

	}


	private final List<Double> m_distance = new ArrayList<>();
	private long m_startTime = 0;
	private Point m_previousPoint;
	private List<Point> m_points = new ArrayList<>();
	private final Canvas canvas = new Canvas();
}
