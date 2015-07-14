package com.forloexample.facedetect;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FaceDetectMainActivity extends Activity {

	public static int screenHeight,screenWidth;
	public MyView faceview;
	
	public Bitmap defaultBitmap;
	
	public Button gallery,detect;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_detect);
		
		
		// we use Display metrics to get the screen width and height.
		//we need the screen size for later use to set the image from gallery on the screen.
		
		 DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			screenHeight = displaymetrics.heightPixels;  
			screenWidth = displaymetrics.widthPixels; 
		
		// Declaring our custom view ( I named it faceview).
			
			faceview = (MyView)findViewById(R.id.faceview);
			
		//Declaring our two buttons.
			
			gallery=(Button)findViewById(R.id.gallery);
			detect=(Button)findViewById(R.id.detect);
			
		
			
			 BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
				bitmapFatoryOptions.inPreferredConfig=Bitmap.Config.RGB_565;
		
			defaultBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.face4,bitmapFatoryOptions);
			// we are gicing a default bitmap to show up when the app starts.
			// this is important. To detect the faces in the bitmap, the bitmap should be in the format RGB_565.
			// so we convert the bitmap to RGB format using BitmapFactory.Options.
			
			
			//setting the default bitmap to our customview.(A method setImage in view class)
			faceview.setImage(defaultBitmap);
			
			
			gallery.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
			
					// onclick we are having onactivity for result. To select an image from our gallery and return an image.
					
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				    intent.setType("image/*");
					startActivityForResult(intent, 0 );
					
				}
			});
		
			
				detect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					
					// a method to detect faces ( in view class).
					faceview.facedetect();
					
				}
			});
		
	}
	
	
	// onActivityresult for getting an image from gallery.
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
        	
        	if(requestCode==0){
        	
        // We get the image selected in gallery in the form of image Uri. 
        		
           Uri imageURI = data.getData(); 
            
            try {
            	
            	//converting the image uri to the bitmap of RGB format.( for our needs)
            	
            	 BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
         		bitmapFatoryOptions.inPreferredConfig=Bitmap.Config.RGB_565;
            	
            	Bitmap b =  
            			   BitmapFactory.decodeStream(getContentResolver().openInputStream(imageURI), null,  
            					   bitmapFatoryOptions);

            	// we need to display the selected image in our view. So we are setting the bitmap as follows
            	faceview.myBitmap=b;

            	
			} catch (Exception e) {
//				
				e.printStackTrace();
			} 
            faceview.invalidate(); 
 
        	}
        	//invalidate the view for changes to take effect.
        	faceview.invalidate(); 
        } else {
            System.exit(0);
            Log.e("result", "BAD");
        }
    }
	
	
	
	
}
