package jp.sagalab;

import java.io.*;
import java.util.Calendar;
import java.util.List;
import javax.swing.JFrame;

public class Output extends JFrame {

	/**
	 * 点列の要素をcsvファイルへと出力
	 *
	 * @param _points 点列
	 */
	public static void writeToCSV(List<Point> _points) {

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

			for (int i = 0; i < _points.size(); i++) {
				pw.print(_points.get(i).getX());
				pw.print(",");
				pw.print(_points.get(i).getY());
				pw.print(",");
				pw.print(_points.get(i).getTime());
				pw.println();
			}

			pw.close();

			System.out.println("点列の出力が正常に終わりました");
			System.out.println("ファイル名:" + fileName);

		} catch (IOException ex) {
		}

	}

	/**
	 * ファイル名の通し番号
	 */
	private static int fileNo = 0;
}
