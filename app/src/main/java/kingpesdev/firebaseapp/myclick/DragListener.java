package kingpesdev.firebaseapp.myclick;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public abstract class DragListener implements View.OnTouchListener {
    public abstract void onTouch();

    public abstract void onDrag();

    private WindowManager.LayoutParams params;
    private int startDragDistance;
    private int initialX = 0;
    private int initialY = 0;
    private float initialTouchX = 0;
    private float initialTouchY = 0;
    private boolean isDrag;


    protected DragListener(WindowManager.LayoutParams params, int startDragDistance) {
        this.params = params;
        this.startDragDistance = startDragDistance;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isDrag = false;
            initialX = params.x;
            initialY = params.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!isDrag && isDragging(event))
                isDrag = true;
            if (!isDrag) return true;
            params.x = initialX + (int) (event.getRawX() - initialTouchX);
            params.y = initialY + (int) (event.getRawY() - initialTouchY);
            onDrag();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isDrag) {
                onTouch();
                return true;
            }
        }
        return false;
    }

    private boolean isDragging(MotionEvent event) {
        return ((Math.pow((double) event.getRawX() - initialTouchX, 2.0) + Math.pow((double) event.getRawY() - initialTouchY, 2.0)) > startDragDistance * startDragDistance);
    }
}
