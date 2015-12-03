package com.videocapture.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OpenDraw extends SurfaceView implements SurfaceHolder.Callback {
	private final String TAG = "OpenDraw";
	private Bitmap bmp;
	private String imgPath = "";
	protected SurfaceHolder sh; // 专门用于控制surfaceView的
	private int w;
	private int h;
	private static OpenDraw instance = null;

	public static OpenDraw getInstance(){
		return instance;
	}
	
	public OpenDraw(Context context) {
		super(context);
		instance = this;
		
		Log.d(TAG, "OpenDraw");
		initUI();
		
	}	

	// XML文件解析需要调用View的构造函数View(Context , AttributeSet)
	// 因此自定义SurfaceView中也需要该构造函数
	public OpenDraw(Context context, AttributeSet attrs) {
		super(context);
		instance = this;
		
		Log.d(TAG, "OpenDraw attrs");
		initUI();
		
		
	}

	private void initUI() {
		sh = getHolder();
		sh.addCallback(this);
		sh.setFormat(PixelFormat.TRANSPARENT); // 设置为透明
		setZOrderOnTop(true);// 设置为顶端
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "w=" + width + " h=" + height);
		w = width;
		h = height;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		instance = null;
	}

	public void clearDraw() {
		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT);// 清除画布
		sh.unlockCanvasAndPost(canvas);
	}

	/**
	 * 绘制
	 */
	public void doDraw() {
		if (bmp != null) {
			Canvas canvas = sh.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景
			Paint p = new Paint(); // 笔触
			p.setAntiAlias(true); // 反锯齿
			p.setColor(Color.RED);
			p.setStyle(Style.STROKE);
			canvas.drawBitmap(bmp, 0, 0, p);
			canvas.drawLine(w / 2 - 100, 0, h / 2 - 100, h, p);
			canvas.drawLine(w / 2 + 100, 0, h / 2 + 100, h, p);
			// ------------------------ 画边框---------------------
			Rect rec = canvas.getClipBounds();
			rec.bottom--;
			rec.right--;
			p.setColor(Color.GRAY); // 颜色
			p.setStrokeWidth(5);
			canvas.drawRect(rec, p);
			// 提交绘制
			sh.unlockCanvasAndPost(canvas);
		}

	}

	public void drawLine() {
		Log.d(TAG, "drawLine");

		Canvas canvas = sh.lockCanvas();

		canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景
		Paint p = new Paint(); // 笔触
		p.setAntiAlias(true); // 反锯齿
		p.setColor(Color.RED);
		p.setStyle(Style.STROKE);
		canvas.drawLine(w / 2 - 100, 0, w / 2 - 100, h, p);
		canvas.drawLine(w / 2 + 100, 0, w / 2 + 100, h, p);
		canvas.drawLine(0, h / 2 - 100, w, h / 2 - 100, p);
		canvas.drawLine(0, h / 2 + 100, w, h / 2 + 100, p);
		// 提交绘制
		sh.unlockCanvasAndPost(canvas);
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
		// 根据路径载入目标图像
		bmp = BitmapFactory.decodeFile(imgPath);
	}
}
