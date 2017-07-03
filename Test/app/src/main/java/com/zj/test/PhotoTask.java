package com.zj.test;



public class PhotoTask {
	    // Gets a handle to the object that creates the thread pools
		private PhotoManager sPhotoManager=null;
		
		public PhotoTask(){
			sPhotoManager = PhotoManager.getInstance();
		}
	    
	    public void handleDecodeState(int state) {
	        int outState = 0;
	        // Converts the decode state to the overall state.
	        switch(state) {
	            case PhotoDecodeRunnable.DECODE_STATE_COMPLETED:
	                outState = PhotoManager.TASK_COMPLETE;
	                break;
	        }
	        // Calls the generalized state method
	        handleState(outState);
	    }
	    
	    // Passes the state to PhotoManager
	    void handleState(int state) {
	        /*
	         * Passes a handle to this task and the
	         * current state to the class that created
	         * the thread pools
	         */
	        sPhotoManager.handleState(this, state);
	    }

	 // A class that decodes photo files into Bitmaps
	    class PhotoDecodeRunnable implements Runnable {
	    	public static final int DECODE_STATE_COMPLETED = 0;
	    	private PhotoTask mPhotoTask;
	        PhotoDecodeRunnable(PhotoTask downloadTask) {
	            mPhotoTask = downloadTask;
	        }

	        // Gets the downloaded byte array
	        //byte[] imageBuffer = mPhotoTask.getByteBuffer();

	        // Runs the code for this task
	        public void run() {
	               // Tries to decode the image buffer
//	            returnBitmap = BitmapFactory.decodeByteArray(
//	                    imageBuffer,
//	                    0,
//	                    imageBuffer.length,
//	                    bitmapOptions
//	            );
//	            // Sets the ImageView Bitmap
//	            mPhotoTask.setImage(returnBitmap);
	            // Reports a status of "completed"
	            mPhotoTask.handleDecodeState(DECODE_STATE_COMPLETED);

	        }
	    }
}
