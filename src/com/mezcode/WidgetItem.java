package com.mezcode;

public class WidgetItem { //extends View implements OnTouchListener {
    public String text;
    public String summary;
    public String url;
    private static final String TAG = "WidgetItem";
    
	/*public WidgetItem(Context ctx, AttributeSet attrs, int defStyle){
		super(ctx, attrs, defStyle);
		init(ctx);		
	}
	
	public WidgetItem(Context ctx, AttributeSet attrs)  {
		super(ctx, attrs);
		init(ctx);		
	}	

	public WidgetItem(Context ctx) {
		super(ctx);
		init(ctx);		
	}	
	
	void init(Context ctx) {
		Log.d(TAG, "init called?");
	}
*/

    public WidgetItem(String text) {
    	//super(ctx);    
        this.text = text;
    }
    
    public WidgetItem(String text, String summary, String url) {
    	//super(ctx);    
        this.text = text;
        this.summary = summary;
        this.url = url;
    }
    
    /*public WidgetItem(Context ctx, String text, String url, String summary) {
    	//super(ctx);    
        this.text = text;
        this.url = url;
        this.summary = summary;
        //this.setOnTouchListener(this);
    }*/    
    
    
}
