/**
 * Copyright 2014 Jeroen Mols
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.videocapture.preview;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.videocapture.camera.CameraWrapper;
import com.videocapture.datachange.NV21ToRgb;
import com.videocapture.file.CLog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

public class CapturePreview implements SurfaceHolder.Callback {
	private final String TAG = "CapturePreview";
	private boolean						mPreviewRunning	= false;
	private final CapturePreviewInterface	mInterface;
	public final CameraWrapper				mCameraWrapper;
	private PreviewSnap mPreviewCb = new PreviewSnap();

	private class PreviewSnap implements Camera.PreviewCallback {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Log.e(TAG, "onPreviewFrame");//every 30ms
			savePreviewBmp(data, camera);
		}
	}	

	private void savePreviewBmp(byte[] data, Camera camera){
		Camera.Parameters parameters = camera.getParameters();
		int width = parameters.getPreviewSize().width;
		int height = parameters.getPreviewSize().height;
		int format = parameters.getPreviewFormat();

		//NV21:17 RGB_565:4 YUY2:20 YUV_420_888:35 JPEG :256
		Log.e(TAG,"w="+width+" h="+height+" format="+format+" nv21_len="+data.length);

		if(true){
			NV21ToRgb trans = new NV21ToRgb();
			final int rgblen = trans.getRGBarrayLen(width,height);
			byte[] rgb = new byte[rgblen];//cost 40ms
			Long time = System.currentTimeMillis();
			Log.e(TAG, "NV21 decodeByteArray start");
			trans.startChange(rgb,data,width,height);
			Log.e(TAG, "NV21 decodeByteArray end,cost time:"+(System.currentTimeMillis()-time));//cost 140ms
			return;
		}

		if(format == ImageFormat.NV21 || format == ImageFormat.YUY2) {
			YuvImage yuv = new YuvImage(data, format, width, height, null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);
			Log.e(TAG, "compressToJpeg ok"); //cost 100ms

			byte[] bytes = out.toByteArray();
			final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			Log.e(TAG, "BitmapFactory decodeByteArray ok");  //cost 70ms
		}
	}

	public CapturePreview(CapturePreviewInterface capturePreviewInterface, CameraWrapper cameraWrapper,
			SurfaceHolder holder) {
		mInterface = capturePreviewInterface;
		mCameraWrapper = cameraWrapper;

		initalizeSurfaceHolder(holder);
	}

	@SuppressWarnings("deprecation")
	private void initalizeSurfaceHolder(final SurfaceHolder surfaceHolder) {
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Necessary for older API's
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		// NOP
	}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		if (mPreviewRunning) {
			try {
				mCameraWrapper.stopPreview();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		try {
			mCameraWrapper.setPreviewCb(mPreviewCb);
			mCameraWrapper.configureForPreview(width, height);
			CLog.d(CLog.PREVIEW, "Configured camera for preview in surface of " + width + " by " + height);
		} catch (final RuntimeException e) {
			e.printStackTrace();
			CLog.d(CLog.PREVIEW, "Failed to show preview - invalid parameters set to camera preview");
			mInterface.onCapturePreviewFailed();
			return;
		}

		try {
			mCameraWrapper.enableAutoFocus();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			CLog.d(CLog.PREVIEW, "AutoFocus not available for preview");
		}

		try {
			mCameraWrapper.startPreview(holder);
			setPreviewRunning(true);
		} catch (final IOException e) {
			e.printStackTrace();
			CLog.d(CLog.PREVIEW, "Failed to show preview - unable to connect camera to preview (IOException)");
			mInterface.onCapturePreviewFailed();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			CLog.d(CLog.PREVIEW, "Failed to show preview - unable to start camera preview (RuntimeException)");
			mInterface.onCapturePreviewFailed();
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		// NOP
	}

	public void releasePreviewResources() {
		if (mPreviewRunning) {
			try {
				mCameraWrapper.stopPreview();
				setPreviewRunning(false);
			} catch (final Exception e) {
				e.printStackTrace();
				CLog.e(CLog.PREVIEW, "Failed to clean up preview resources");
			}
		}
	}

	protected void setPreviewRunning(boolean running) {
		mPreviewRunning = running;
	}

}