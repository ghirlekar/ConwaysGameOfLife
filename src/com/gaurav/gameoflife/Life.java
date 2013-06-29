package com.gaurav.gameoflife;

import java.util.Arrays;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Life extends SurfaceView {

	private final static float RATIO_CELL_TO_SPACING = 5;
	final static int MAX_SIZE = 200;

	private Paint paintAlive, paintBackground;
	// private final static String[] colorsAlive = { "#FFFFC30D", "#FFE89B0C",
	// "#FFFF8800", "#FFE8610C", "#FFDB3D0B" };
	// private final static String[] colorsAlive = { "#10B24D", "#FFFFFD",
	// "#FFD200", "#0300B2", "#0400FF" };
	private boolean antiAliased;
	private int xPos = 100, yPos = 100, xWidth = 50, yWidth;
	boolean[][] state;
	private boolean[] ruleSurvive;
	private boolean[] ruleBirth;
	private byte[][] numNeighbours;
	private DrawRunnable drawRunnable;
	private RecomputeNeighboursRunnable recomputeNeighboursRunnable;
	private ComputeRunnable computeRunnable;
	private RandomizeRunnable randomizeRunnable;
	private GestureDetectorCompat mDetector;
	private ScaleGestureDetector mScaleDetector;
	private boolean canvasLocked;
	private boolean paused;
	private int fps = 5;
	private float randomPercentFill = (float) 0.3;
	private int swipeTrail = 1; // swipeTrail 0 is circular, 1 is rectangular

	public Life(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			state = new boolean[MAX_SIZE][MAX_SIZE];
			numNeighbours = new byte[MAX_SIZE][MAX_SIZE];
			SurfaceHolder holder = getHolder();
			holder.addCallback(callbackListener);
			recomputeNeighboursRunnable = new RecomputeNeighboursRunnable();
			randomizeRunnable = new RandomizeRunnable();
			mDetector = new GestureDetectorCompat(context,
					new MyGestureListener());
			mScaleDetector = new ScaleGestureDetector(context,
					new MyScaleGestureListener());
		}
		paintAlive = new Paint();
		paintBackground = new Paint();
		if (antiAliased)
			paintAlive.setAntiAlias(true);
		paintAlive.setColor(Color.parseColor("#FF8800"));
		paintBackground.setColor(Color.BLACK);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		yWidth = Math.round((float) height / width * xWidth);
	}

	private SurfaceHolder.Callback callbackListener = new SurfaceHolder.Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			drawRunnable = new DrawRunnable(holder);
			post(drawRunnable);
			computeRunnable = new ComputeRunnable();
			if (!paused)
				post(computeRunnable);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}
	};

	public class RecomputeNeighboursRunnable implements Runnable {

		@Override
		public void run() {
			byte[][] tempNeighbourCount = new byte[MAX_SIZE][MAX_SIZE];
			for (int i = MAX_SIZE; i < 2 * MAX_SIZE; i++)
				for (int j = MAX_SIZE; j < 2 * MAX_SIZE; j++)
					if (state[i % MAX_SIZE][j % MAX_SIZE]) {
						tempNeighbourCount[(i - 1) % MAX_SIZE][(j - 1)
								% MAX_SIZE]++;
						tempNeighbourCount[(i - 1) % MAX_SIZE][(j) % MAX_SIZE]++;
						tempNeighbourCount[(i - 1) % MAX_SIZE][(j + 1)
								% MAX_SIZE]++;
						tempNeighbourCount[(i) % MAX_SIZE][(j - 1) % MAX_SIZE]++;
						tempNeighbourCount[(i) % MAX_SIZE][(j + 1) % MAX_SIZE]++;
						tempNeighbourCount[(i + 1) % MAX_SIZE][(j - 1)
								% MAX_SIZE]++;
						tempNeighbourCount[(i + 1) % MAX_SIZE][(j) % MAX_SIZE]++;
						tempNeighbourCount[(i + 1) % MAX_SIZE][(j + 1)
								% MAX_SIZE]++;
					}
			numNeighbours = tempNeighbourCount;
		}
	}

	private class DrawRunnable implements Runnable {
		private boolean isDrawing;
		private SurfaceHolder surfHolder;
		boolean[][] stateSnap;

		public DrawRunnable(SurfaceHolder surfHolder) {
			this.surfHolder = surfHolder;
			stateSnap = new boolean[MAX_SIZE][MAX_SIZE];
		}

		@Override
		public void run() {
			isDrawing = true;
			for (int i = 0; i < MAX_SIZE; i++)
				System.arraycopy(state[i], 0, stateSnap[i], 0, MAX_SIZE);
			Canvas canvas = surfHolder.lockCanvas();
			if (canvas != null) {
				int xWidthSnap = xWidth;
				int yWidthSnap = yWidth;
				int xPosSnap = xPos;
				while (xPosSnap < 0)
					xPosSnap += MAX_SIZE;
				int yPosSnap = yPos;
				while (yPosSnap < 0)
					yPosSnap += MAX_SIZE;
				float adjacentDist = (getWidth())
						/ (xWidthSnap * (1 + 1 / RATIO_CELL_TO_SPACING) - 1 / RATIO_CELL_TO_SPACING);
				canvas.drawPaint(paintBackground);
				float offset = adjacentDist / 2;
				for (int i = 0; i < xWidthSnap; i++) {
					for (int j = 0; j < yWidthSnap; j++) {
						if (stateSnap[(i + xPosSnap) % MAX_SIZE][(j + yPosSnap)
								% MAX_SIZE]) {
							float[] center = {
									offset + i * adjacentDist
											* (1 + 1 / RATIO_CELL_TO_SPACING),
									offset + j * adjacentDist
											* (1 + 1 / RATIO_CELL_TO_SPACING) };
							canvas.drawCircle(center[0] - adjacentDist / 2,
									center[1] - adjacentDist / 2,
									adjacentDist / 2, paintAlive);
						}
					}
				}
				surfHolder.unlockCanvasAndPost(canvas);
			}
			isDrawing = false;
		}
	}

	private class ComputeRunnable implements Runnable {
		boolean[][] stateSnap;
		byte[][] numNeighboursSnap;
		HelperRunnable r1, r2, r3, r4;
		boolean isComputing;

		public ComputeRunnable() {
			stateSnap = new boolean[MAX_SIZE][MAX_SIZE];
			numNeighboursSnap = new byte[MAX_SIZE][MAX_SIZE];
			r1 = new HelperRunnable(0, MAX_SIZE / 2, 0, MAX_SIZE / 2);
			r2 = new HelperRunnable(0, MAX_SIZE / 2, MAX_SIZE / 2, MAX_SIZE);
			r3 = new HelperRunnable(MAX_SIZE / 2, MAX_SIZE, 0, MAX_SIZE / 2);
			r4 = new HelperRunnable(MAX_SIZE / 2, MAX_SIZE, MAX_SIZE / 2,
					MAX_SIZE);
		}

		@Override
		public void run() {
			isComputing = true;
			if (!paused)
				postDelayed(this, 1000 / fps);
			for (int i = 0; i < MAX_SIZE; i++) {
				System.arraycopy(state[i], 0, stateSnap[i], 0, MAX_SIZE);
				System.arraycopy(numNeighbours[i], 0, numNeighboursSnap[i], 0,
						MAX_SIZE);
			}
			Thread[] threadArray = new Thread[] { new Thread(r1),
					new Thread(r2), new Thread(r3), new Thread(r4) };
			for (Thread thread : threadArray)
				thread.start();
			for (Thread thread : threadArray)
				while (true) {
					try {
						thread.join();
					} catch (InterruptedException e) {
					}
					break;
				}
			refresh();
			new Thread(recomputeNeighboursRunnable).start();
			isComputing = false;
		}

		private class HelperRunnable implements Runnable {

			private int xMin, xMax, yMin, yMax;

			public HelperRunnable(int xMin, int xMax, int yMin, int yMax) {
				this.xMin = xMin;
				this.xMax = xMax;
				this.yMin = yMin;
				this.yMax = yMax;
			}

			@Override
			public void run() {
				for (int i = xMin; i < xMax; i++)
					for (int j = yMin; j < yMax; j++)
						if (stateSnap[i][j])
							state[i][j] = ruleSurvive[numNeighboursSnap[i][j]] ? true
									: false;
						else
							state[i][j] = ruleBirth[numNeighboursSnap[i][j]] ? true
									: false;
			}
		}
	}

	public class RandomizeRunnable implements Runnable {

		@Override
		public void run() {
			removeCallbacks(computeRunnable);
			while (computeRunnable.isComputing) {
			}
			Random rand = new Random();
			int threshold = (int) (randomPercentFill * 1000);
			for (int i = 0; i < MAX_SIZE; i++)
				for (int j = 0; j < MAX_SIZE; j++)
					state[i][j] = (rand.nextInt(1000) < threshold) ? true
							: false;
			refresh();
			byte[][] tempNeighbourCount = new byte[MAX_SIZE][MAX_SIZE];
			for (int i = MAX_SIZE; i < 2 * MAX_SIZE; i++)
				for (int j = MAX_SIZE; j < 2 * MAX_SIZE; j++)
					if (state[i % MAX_SIZE][j % MAX_SIZE]) {
						tempNeighbourCount[(i - 1) % MAX_SIZE][(j - 1)
								% MAX_SIZE]++;
						tempNeighbourCount[(i - 1) % MAX_SIZE][(j) % MAX_SIZE]++;
						tempNeighbourCount[(i - 1) % MAX_SIZE][(j + 1)
								% MAX_SIZE]++;
						tempNeighbourCount[(i) % MAX_SIZE][(j - 1) % MAX_SIZE]++;
						tempNeighbourCount[(i) % MAX_SIZE][(j + 1) % MAX_SIZE]++;
						tempNeighbourCount[(i + 1) % MAX_SIZE][(j - 1)
								% MAX_SIZE]++;
						tempNeighbourCount[(i + 1) % MAX_SIZE][(j) % MAX_SIZE]++;
						tempNeighbourCount[(i + 1) % MAX_SIZE][(j + 1)
								% MAX_SIZE]++;
					}
			numNeighbours = tempNeighbourCount;
			if (!isPaused())
				post(computeRunnable);
		}

	}

	public void randomize(double percentFill) {
		new Thread(randomizeRunnable).start();
	}

	public void refresh() {
		removeCallbacks(drawRunnable);
		post(drawRunnable);
	}

	public boolean isCanvasLock() {
		return canvasLocked;
	}

	public void setCanvasLock(boolean canvasLock) {
		this.canvasLocked = canvasLock;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getxWidth() {
		return xWidth;
	}

	public void setxWidth(int xWidth) {
		xPos += (this.xWidth - xWidth) / 2;
		this.xWidth = xWidth;
	}

	public int getyWidth() {
		return yWidth;
	}

	public void setyWidth(int yWidth) {
		yPos += (this.yWidth - yWidth) / 2;
		this.yWidth = yWidth;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (canvasLocked) {
			int action = MotionEventCompat.getActionMasked(event);

			switch (action) {
			case (MotionEvent.ACTION_DOWN):
				return true;
			case (MotionEvent.ACTION_POINTER_DOWN):
				return true;
			case (MotionEvent.ACTION_MOVE):
				for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++) {
					int size = (int) Math.round(getResources()
							.getDisplayMetrics().xdpi / 12);
					int xMin = Math.round(xPos
							+ (MotionEventCompat.getX(event, i) - size / 2)
							/ getWidth() * xWidth);
					int xMax = Math.round(xPos
							+ (MotionEventCompat.getX(event, i) + size / 2)
							/ getWidth() * xWidth);
					int yMin = Math.round(yPos
							+ (MotionEventCompat.getY(event, i) - size / 2)
							/ getHeight() * yWidth);
					int yMax = Math.round(yPos
							+ (MotionEventCompat.getY(event, i) + size / 2)
							/ getHeight() * yWidth);
					while (xMax < 0) {
						xMin += MAX_SIZE;
						xMax += MAX_SIZE;
					}
					while (yMax < 0) {
						yMin += MAX_SIZE;
						yMax += MAX_SIZE;
					}
					for (int j = xMin; j < xMax; j++)
						for (int k = yMin; k < yMax; k++) {
							if (swipeTrail == 0) {
								if (Math.pow(j - (xMin + xMax) / 2, 2)
										+ Math.pow(k - (yMin + yMax) / 2, 2) <= Math
											.pow((xMin - xMax) / 2, 2))
									state[j % MAX_SIZE][k % MAX_SIZE] = true;
							} else
								state[j % MAX_SIZE][k % MAX_SIZE] = true;
						}
				}
				refresh();
				new Thread(recomputeNeighboursRunnable).start();
				return true;
			}
		}
		boolean retVal = mScaleDetector.onTouchEvent(event);
		retVal = mDetector.onTouchEvent(event) || retVal;
		return retVal || super.onTouchEvent(event);
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		Flinger flinger;
		PointF prevPointPan;

		@Override
		public boolean onDown(MotionEvent e) {
			if (!canvasLocked) {
				prevPointPan = new PointF(getxPos(), getyPos());
				removeCallbacks(flinger);
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (!canvasLocked) {
				flinger = new Flinger(velocityX / getWidth(), velocityY
						/ getWidth());
				new Thread(flinger).start();
				return true;
			} else
				return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (e2.getPointerCount() == 1) {
				prevPointPan.offset(distanceX / getWidth() * xWidth, distanceY
						/ getHeight() * yWidth);
				setxPos(Math.round(prevPointPan.x));
				setyPos(Math.round(prevPointPan.y));
				refresh();
				return true;
			} else
				return false;
		}
	}

	private class MyScaleGestureListener implements
			ScaleGestureDetector.OnScaleGestureListener {
		float xWidth, yWidth;

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (!canvasLocked) {
				xWidth /= Math.pow(detector.getScaleFactor(), 2);
				yWidth /= Math.pow(detector.getScaleFactor(), 2);
				if (xWidth < 100 && xWidth > 20
						&& yWidth < 100 * getHeight() / getWidth()
						&& yWidth > 20 * getHeight() / getWidth()) {
					setxWidth(Math.round(xWidth));
					setyWidth(Math.round(yWidth));
				}
				refresh();
				return true;
			} else
				return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			xWidth = getxWidth();
			yWidth = getyWidth();
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
		}

	}

	public class Flinger implements Runnable {

		private float xVel, yVel, xPos, yPos;

		public Flinger(float velocityX, float velocityY) {
			xVel = velocityX * 4;
			yVel = velocityY * 4;
			xPos = getxPos();
			yPos = getyPos();
		}

		@Override
		public void run() {
			xPos -= xVel;
			yPos -= yVel;
			xVel *= 0.9;
			yVel *= 0.9;
			if (Math.abs(xVel) > 0.1 || Math.abs(yVel) > 0.1)
				postDelayed(this, 25);
			setxPos(Math.round(xPos));
			setyPos(Math.round(yPos));
			refresh();
		}

	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
		if (!paused && computeRunnable != null)
			post(computeRunnable);
	}

	public boolean[][] getState() {
		return state;
	}

	public void setState(boolean[][] state) {
		boolean wasPaused = isPaused();
		setPaused(true);
		this.state = state;
		refresh();
		byte[][] tempNeighbourCount = new byte[MAX_SIZE][MAX_SIZE];
		for (int i = MAX_SIZE; i < 2 * MAX_SIZE; i++)
			for (int j = MAX_SIZE; j < 2 * MAX_SIZE; j++)
				if (state[i % MAX_SIZE][j % MAX_SIZE]) {
					tempNeighbourCount[(i - 1) % MAX_SIZE][(j - 1) % MAX_SIZE]++;
					tempNeighbourCount[(i - 1) % MAX_SIZE][(j) % MAX_SIZE]++;
					tempNeighbourCount[(i - 1) % MAX_SIZE][(j + 1) % MAX_SIZE]++;
					tempNeighbourCount[(i) % MAX_SIZE][(j - 1) % MAX_SIZE]++;
					tempNeighbourCount[(i) % MAX_SIZE][(j + 1) % MAX_SIZE]++;
					tempNeighbourCount[(i + 1) % MAX_SIZE][(j - 1) % MAX_SIZE]++;
					tempNeighbourCount[(i + 1) % MAX_SIZE][(j) % MAX_SIZE]++;
					tempNeighbourCount[(i + 1) % MAX_SIZE][(j + 1) % MAX_SIZE]++;
				}
		numNeighbours = tempNeighbourCount;
		if (!wasPaused)
			setPaused(false);
	}

	public void clear() {
		for (int i = 0; i < MAX_SIZE; i++) {
			Arrays.fill(state[i], false);
			Arrays.fill(numNeighbours[i], (byte) 0);
		}
		refresh();
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public boolean[] getRuleSurvive() {
		return ruleSurvive;
	}

	public void setRuleSurvive(boolean[] ruleSurvive) {
		assert ruleSurvive.length == 9;
		this.ruleSurvive = ruleSurvive;
	}

	public boolean[] getRuleBirth() {
		return ruleBirth;
	}

	public void setRuleBirth(boolean[] ruleBirth) {
		assert ruleBirth.length == 9;
		this.ruleBirth = ruleBirth;
	}

	public boolean isAntiAliased() {
		return antiAliased;
	}

	public void setAntiAliased(boolean antiAliased) {
		this.antiAliased = antiAliased;
		paintAlive.setAntiAlias(antiAliased);
	}

	public int getSwipeTrail() {
		return swipeTrail;
	}

	public void setSwipeTrail(int i) {
		this.swipeTrail = i;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (isInEditMode()) {
			canvas.drawARGB(255, 0, 0, 0);
		}
	}

}
