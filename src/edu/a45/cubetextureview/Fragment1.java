package edu.a45.cubetextureview;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import edu.a45.cubetextureview.utils.DragConfig;

@SuppressLint({ "NewApi", "InflateParams", "ClickableViewAccessibility" }) 
public class Fragment1 extends Fragment  implements TextureView.SurfaceTextureListener, OnTouchListener{
	
	private TextureView surface1;
	private CubeRenderer renderer;
	  
	private int surfaceWidth;
	private int surfaceHeight;
	
	private final DragState dragState = new DragState();
	private boolean yDragEnabled = true;
	
	public final DragConfig dragConfig = new DragConfig();
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v= inflater.inflate(R.layout.screen_fragment, null);
		
		 surface1 = (TextureView)v.findViewById(R.id.surface);
	     surface1.setSurfaceTextureListener(this);   
	     surface1.setRotationY(180);
	     surface1.setOnTouchListener(this);
	        
		return v;
	}

	private void startRender() {
    	renderer = new CubeRenderer((MainActivity)getActivity(), surface1.getSurfaceTexture(), surfaceWidth, surfaceHeight);
    	renderer.setCubeSize(surfaceWidth, surfaceHeight);
    }

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height){
		surfaceWidth = width;
	    surfaceHeight = height;
	    startRender();
	}
	
	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface){
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface){
	}
	
	
	public void enableYDrag(boolean flag) {
		yDragEnabled = flag;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// make sure already calibrated
		if (!dragConfig.calibrated) {
			// calibration is done here because at this point
			// view.getWidth() or .getHeight() already
			// return a valid result
			dragConfig.calibrate(surfaceWidth, surfaceHeight);
		}
		switch (event.getAction()) {
		// end conditions
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
			dragState.started = false;
			renderer.dragEnd();
			break;
		// pre-start condition
		case MotionEvent.ACTION_DOWN:
			dragState.xStart = (int) event.getX();
			dragState.yStart = (int) event.getY();
			break;
		// handle start and drag
		case MotionEvent.ACTION_MOVE:
			int x = (int) event.getX();
			int y = (int) event.getY();
			if (!dragState.started) {
				// handle start condition
				int dx = Math.abs(x - dragState.xStart);
				int dy = Math.abs(y - dragState.yStart);
				if (dx + dy > dragConfig.threshold) {
					// fix direction
					dragState.xDirection = dy < dx;
					if (!yDragEnabled) {
						dragState.xDirection = true;
					}
					// start dragging
					if (renderer.dragStart(dragState.xDirection)) {
						dragState.started = true;
						dragState.xStart = x;
						dragState.yStart = y;
					}
				}
			} else {
				// do the dragging
				float angle = (x - dragState.xStart) * dragConfig.xMultiplier;
				if (!dragState.xDirection) {
					angle = (y - dragState.yStart) * dragConfig.yMultiplier;
				}
				renderer.drag(angle);
			}
			break;
		}
		return true;
	}

	
	static class DragState {
		public int xStart = 0, yStart = 0;
		public boolean started = false;
		public boolean xDirection = true;
	}
	
}
