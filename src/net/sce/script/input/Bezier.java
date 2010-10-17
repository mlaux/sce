package net.sce.script.input;

import java.awt.Point;

public class Bezier {
	private Point[] control, spline;
	
	public Bezier(Point start, Point end) {
		// Seems pretty accurate, calculated based on an exponential function
		// This is so the number of points generated stays relatively constant
		// depending on the distance between points.
		this(start, end, 0.15 * Math.pow(0.9957525d, start.distance(end)));
	}

	public Bezier(Point start, Point end, double k) {
		int x1 = start.x > end.x ? start.x : end.x, x2 = start.x > end.x ? end.x : start.x;
		int y1 = start.y > end.y ? start.y : end.y, y2 = start.y > end.y ? end.y : start.y;
		control = new Point[] { start, new Point(random(x1, x2), random(y1, y2)),
				new Point(random(x1, x2), random(y1, y2)), end };
		
		double x, y, t;
		int i = 0;
		for (t = k; t <= 1 + k; t += k)
			i++;
		spline = new Point[i];
		
		i = 0;
		for (t = k; t <= 1 + k; t += k) {
			x = (control[0].x + t * (-control[0].x * 3 + t * (3 * control[0].x - control[0].x * t)))
					+ t * (3 * control[1].x + t * (-6 * control[1].x + control[1].x * 3 * t))
					+ t * t * (control[2].x * 3 - control[2].x * 3 * t) + control[3].x * t * t * t;
			y = (control[0].y + t * (-control[0].y * 3 + t * (3 * control[0].y - control[0].y * t)))
					+ t * (3 * control[1].y + t * (-6 * control[1].y + control[1].y * 3 * t))
					+ t * t * (control[2].y * 3 - control[2].y * 3 * t) + control[3].y * t * t * t;
			spline[i++] = new Point((int) x, (int) y);
		}
	}

	public Point[] getControlPoints() {
		return control;
	}

	public Point[] getPath() {
		return spline;
	}

	private int random(int high, int low) {
		return (int) ((high - low) * Math.random() + low);
	}
}
