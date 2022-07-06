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
import java.util.ArrayList;
import java.util.List;

public class Painter extends JFrame {

	static void create(){
		new Painter();
	}

	class WindowClosing extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			int ans = JOptionPane.showConfirmDialog(Painter.this,  "本当に閉じちゃうの?泣");
			if(ans == JOptionPane.YES_OPTION)
				System.exit(0);
		}
	}

	public void drawPoint(Point _p) {
		Graphics g = canvas.getGraphics();
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

	private Painter(){
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		addWindowListener(new WindowClosing());
		setState(JFrame.ICONIFIED);
		setIconImage(new ImageIcon("icon2.jpg").getImage());
		canvas.setSize(800, 600);
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
						System.out.println(m_startTime);

						m_points.clear();
						cleanCanvas();
						Point point = Point.create((double) e.getX(), (double) e.getY(), 0.0);
						m_points.add( point );
						drawPoint( point );
						m_previousPoint = point;
					}

					@Override
					public void mouseReleased(MouseEvent e){

						Point point = Point.create((double) e.getX(), (double) e.getY(),(double)(getTime()-m_startTime)/1000);
						m_points.add( point );
						drawPoint( point );
						drawLine(m_previousPoint, point, Color.BLACK);

						BSplineApprox sci = BSplineApprox.create(m_points, 3);
						List<Point> controlPoints = new ArrayList<>(sci.getControlPoints());

						SplineCurve sc = SplineCurve.create(controlPoints, sci.getKnots());

						Interval domain = sc.getDomain();
						Point tmp = sc.evaluate(domain.getBegin());
						double interval = 0.01;
						double loopCount = (domain.getEnd() - domain.getBegin()) / interval;

						for(int i=0; i <= loopCount; i++){
							double t = domain.getBegin() + interval * i;

							Point p = sc.evaluate(t);

							drawLine(tmp, p, Color.RED);

							tmp = p;
						}

						m_points.clear();
					}

				}

		);

		canvas.addMouseMotionListener(
				new MouseAdapter() {

					@Override
					public void mouseDragged(MouseEvent e){
						Point point = Point.create((double)e.getX(), (double)e.getY(), (double)(getTime()-m_startTime)/1000);
						m_points.add( point );
						drawLine(m_previousPoint, point, Color.BLACK);
						m_previousPoint = point;
					}
				}
		);

	}


	private long m_startTime = 0;
	private Point m_previousPoint;
	private List<Point> m_points = new ArrayList<>();
	private final Canvas canvas = new Canvas();
}
