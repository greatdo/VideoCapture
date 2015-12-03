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
	protected SurfaceHolder sh; // ר�����ڿ���surfaceView��
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

	// XML�ļ�������Ҫ����View�Ĺ��캯��View(Context , AttributeSet)
	// ����Զ���SurfaceView��Ҳ��Ҫ�ù��캯��
	public OpenDraw(Context context, AttributeSet attrs) {
		super(context);
		instance = this;
		
		Log.d(TAG, "OpenDraw attrs");
		initUI();
		
		
	}

	private void initUI() {
		sh = getHolder();
		sh.addCallback(this);
		sh.setFormat(PixelFormat.TRANSPARENT); // ����Ϊ͸��
		setZOrderOnTop(true);// ����Ϊ����
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
		canvas.drawColor(Color.TRANSPARENT);// �������
		sh.unlockCanvasAndPost(canvas);
	}

	/**
	 * ����
	 */
	public void doDraw() {
		if (bmp != null) {
			Canvas canvas = sh.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT);// �����ǻ��Ʊ���
			Paint p = new Paint(); // �ʴ�
			p.setAntiAlias(true); // �����
			p.setColor(Color.RED);
			p.setStyle(Style.STROKE);
			canvas.drawBitmap(bmp, 0, 0, p);
			canvas.drawLine(w / 2 - 100, 0, h / 2 - 100, h, p);
			canvas.drawLine(w / 2 + 100, 0, h / 2 + 100, h, p);
			// ------------------------ ���߿�---------------------
			Rect rec = canvas.getClipBounds();
			rec.bottom--;
			rec.right--;
			p.setColor(Color.GRAY); // ��ɫ
			p.setStrokeWidth(5);
			canvas.drawRect(rec, p);
			// �ύ����
			sh.unlockCanvasAndPost(canvas);
		}

	}

	public void drawLine() {
		Log.d(TAG, "drawLine");

		Canvas canvas = sh.lockCanvas();

		canvas.drawColor(Color.TRANSPARENT);// �����ǻ��Ʊ���
		Paint p = new Paint(); // �ʴ�
		p.setAntiAlias(true); // �����
		p.setColor(Color.RED);
		p.setStyle(Style.STROKE);
		canvas.drawLine(w / 2 - 100, 0, w / 2 - 100, h, p);
		canvas.drawLine(w / 2 + 100, 0, w / 2 + 100, h, p);
		canvas.drawLine(0, h / 2 - 100, w, h / 2 - 100, p);
		canvas.drawLine(0, h / 2 + 100, w, h / 2 + 100, p);
		// �ύ����
		sh.unlockCanvasAndPost(canvas);
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
		// ����·������Ŀ��ͼ��
		bmp = BitmapFactory.decodeFile(imgPath);
	}
}
