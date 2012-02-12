package com.mezcode.wikiwidgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;

public class MapPoint extends Drawable {
	      
	     private Paint numberPaint = new Paint(); 
	     private static final Paint blackPaint = new Paint(); 
/*	     private static final int HEIGHT = 100; 
	     private static final int WIDTH = 100; */
	     private int mPointNumber;
	     private static int mPointTracker = 0;
	     
	     //multiple calls to the LocationActivity increment unnecessarily
	     //need to reset in onResume
	     public static void resetMapNumbers() {
	    	 mPointTracker = 0;
	     }
	     
	     public MapPoint( ) { 
	    	 setBounds(0, 0, 25, 25);
	          numberPaint.setColor(Color.GREEN);
	          numberPaint.setTextSize(25.0f);
	          numberPaint.setTextAlign(Align.CENTER);
	          blackPaint.setColor(Color.DKGRAY);
	          blackPaint.setAlpha(150);
	          mPointTracker++;
	          mPointNumber = mPointTracker-1;

	     } 

	     // Draws a colored circle with a radius of 40px inside a 
	     // black circle with a radius of 50 px.
	     @Override 
	     public void draw(Canvas canvas) { 	           
	          //canvas.setViewport(WIDTH, HEIGHT); 
	          canvas.drawCircle(0, 0, 25, blackPaint); 
	          canvas.drawText(mPointNumber+"", 0.0f, 5.0f, numberPaint); 
	     } 

	     @Override 
	     public int getOpacity() { 
	          return 0; 
	     } 

	     @Override 
	     public void setAlpha(int alpha) { 
	     } 

	     @Override 
	     public void setColorFilter(ColorFilter cf) { 
	     }

	} 
