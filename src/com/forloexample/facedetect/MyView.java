package com.forloexample.facedetect;


import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.AttributeSet;

import android.view.View;
import android.widget.Toast;

public class MyView extends View {

	//Mang ket qua cac mat nhan duoc
	private FaceDetector.Face[] detectedFaces;
	//So luong mat toi da co the duoc nhan dien trong anh
	private final int MAX_FACES = 10;
	
	private FaceDetector faceDetector;
	//So luong khuon mat da duoc nhan dien
	int NUMBER_OF_FACE_DETECTED;

	//Toa do goc tren, trai cua anh
	int imageLeft, imageTop;
	
	//myBitmap la anh goc, resultBmp la anh goc sau khi duoc zoom in/out cho w=100%
	//Neu ko muon zoom in/out thi cu gan resultBmp=myBitmap
	public Bitmap myBitmap, resultBmp;

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Chon image, tinh cac gia tri imageTop, imageLeft truoc khi thuc hien nhan dien khuon mat
	 * imageTop duoc tinh theo toa do TopLeft
	 * custom 2 gia tri imageLeft, imageTop de phu hop voi he toa do lon nguoc cui bap
	 * @param bitmap (= BitmapFactory.decodeResource(getResources(), R.drawable.face6,bitmapFatoryOptions);)
	 */
	
	public void setImage(Bitmap bitmap) {
		myBitmap = bitmap;
		invalidate();
		if (myBitmap != null) {

			int w = myBitmap.getWidth();
			int h = myBitmap.getHeight();
			resultBmp = null;
			
			//Lay chieu dai man hinh cua Activity goi doi tuong cua lop nay
			//Day la lenh set gia tri screenHeight, screenWidth o Activity do
			
//			DisplayMetrics displaymetrics = new DisplayMetrics();
//			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//			screenHeight = displaymetrics.heightPixels;  
//			screenWidth = displaymetrics.widthPixels; 
			
			int widthofBitMap = FaceDetectMainActivity.screenWidth;
			
			int heightofBitMap = widthofBitMap * h / w;

		
			//Co keo hinh cho chieu dai full man hinh
			resultBmp = Bitmap.createScaledBitmap(myBitmap, widthofBitMap,
					heightofBitMap, true);
			
			//Ve hinh image o giua man hinh
			imageLeft = (FaceDetectMainActivity.screenWidth - widthofBitMap) / 2;
			imageTop = (FaceDetectMainActivity.screenHeight - heightofBitMap)/2 ;
			
		}

	}

	//Nhan dien khuon mat
	public void facedetect() {

		detectedFaces = new FaceDetector.Face[MAX_FACES];
		faceDetector = new FaceDetector(resultBmp.getWidth(),
				resultBmp.getHeight(), MAX_FACES);
		
		//Thuc hien nhan dien khuon mat, luu vao mang detectedFaces
		NUMBER_OF_FACE_DETECTED = faceDetector.findFaces(resultBmp,
				detectedFaces);

		invalidate();

	

		if (NUMBER_OF_FACE_DETECTED == 0) {

			Toast.makeText(getContext(), "no faces detected", Toast.LENGTH_LONG)
					.show();

		} else if (NUMBER_OF_FACE_DETECTED != 0) {

			Toast.makeText(getContext(),
					NUMBER_OF_FACE_DETECTED + " face(s) detected ",
					Toast.LENGTH_LONG).show();

		}
	}
	
	//Bien nay dung de luu diem giua 2 mat cua mot khuon mat cu the
	private PointF midPoint = new PointF();
	
	//Chieu cao khuon mat
	float getFaceHeight (FaceDetector.Face face){
		return (float) ((face.eyesDistance() / 2) * 2.5)*50/17;
	}
	
	 // Chieu dai khuon mat
	float getFaceWidth (FaceDetector.Face face){
		return (float) (face.eyesDistance()*2.5);
	}
	
	//Toa do mieng
	PointF getMouthPoint(FaceDetector.Face face){
		PointF point=new PointF(midPoint.x + imageLeft,(float) (midPoint.y+ getFaceHeight(face)*0.5*0.65 + imageTop));
		return point;
	}
	
	//Toa do mat trai 
	PointF getLeftEyePoint(FaceDetector.Face face){
		face.getMidPoint(midPoint);
		PointF point=new PointF(midPoint.x-face.eyesDistance()/2+imageLeft,midPoint.y+imageTop); 
		return point;
	}
	
	//Toa do mat phai
	PointF getRightEyePoint(FaceDetector.Face face){
		face.getMidPoint(midPoint);
		PointF point=new PointF(midPoint.x+face.eyesDistance()/2+imageLeft,midPoint.y+imageTop);
		return point;
	}
	
	/**
	 * Demo ve 
	 * 
	 */
	protected void onDraw(Canvas canvas) {
		if (myBitmap != null) {
			canvas.drawBitmap(resultBmp, imageLeft, imageTop, null);

			for (int i = 0; i < NUMBER_OF_FACE_DETECTED; i++) {
				FaceDetector.Face face = detectedFaces[i];
				if (face.confidence() < 0.4)
					continue;
//				myPaint.setColor(Color.BLACK);
//					canvas.drawText(String.valueOf(face.confidence()), getLeftEyePoint(face).x, getRightEyePoint(face).y, myPaint);
				drawEyesLine(canvas, face, Color.GREEN);

				drawFaceBorder(canvas, face, Color.RED);
				
				drawMouthPosition(canvas, face, Color.BLUE);
			}

		}

	}
	 Paint myPaint = new Paint();
	void drawFaceBorder(Canvas canvas, FaceDetector.Face face, int strokeColor) {
		
		myPaint.setColor(strokeColor);
		
		
		
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(2);
		
		face.getMidPoint(midPoint);
		//
		// canvas.drawCircle(midPoint.x + imageLeft,
		// midPoint.y + imageTop, (float) (face.eyesDistance() * 1.25),
		// myPaint);

		float halfOfFaceWidth = (float) ((face.eyesDistance() / 2) * 2.5);
		float faceHeight=halfOfFaceWidth*50/17;
		
		canvas.drawRect(midPoint.x - halfOfFaceWidth + imageLeft,
				midPoint.y- faceHeight/2 + imageTop  ,
				midPoint.x + halfOfFaceWidth + imageLeft,
				midPoint.y+ faceHeight/2 + imageTop  ,
				myPaint);

	}

	void  drawEyesLine(Canvas canvas, FaceDetector.Face face, int strokeColor) {

		myPaint.setColor(strokeColor);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(2);

		PointF l=getLeftEyePoint(face);
		PointF r=getRightEyePoint(face);
//		
		canvas.drawLine(l.x, l.y, r.x, r.y, myPaint);
		
		

	}
	
	void drawMouthPosition (Canvas canvas, FaceDetector.Face face, int strokeColor) {

		myPaint.setColor(strokeColor);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(2);
		
		PointF mouthPoint=getMouthPoint(face);
		
		 canvas.drawCircle(mouthPoint.x,mouthPoint.y, face.eyesDistance()/4,
		 myPaint);

	}

	
	
	
}
