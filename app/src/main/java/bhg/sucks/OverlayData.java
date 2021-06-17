package bhg.sucks;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;

import androidx.core.view.GestureDetectorCompat;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bhg.sucks.dao.AppDatabase;
import bhg.sucks.model.Artifact;
import bhg.sucks.model.Category;

public class OverlayData implements GestureDetector.OnGestureListener {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private OverlayIconService mService;

    private GestureDetectorCompat gestureDetector;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private View mOverlayView;

    private int mTouchSlop;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    void init(OverlayIconService s) {
        this.mService = s;

        this.mOverlayView = LayoutInflater.from(s).inflate(R.layout.overlay_view, null);
        this.gestureDetector = new GestureDetectorCompat(mOverlayView.getContext(), this);

        mOverlayView.setOnTouchListener((view, motionEvent) -> gestureDetector.onTouchEvent(motionEvent));

        Button btn = mOverlayView.findViewById(R.id.btn_overlay);
        btn.setOnClickListener(s::toogleCapturing);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        this.params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;

        this.windowManager = (WindowManager) s.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(mOverlayView, params);

        ViewConfiguration vc = ViewConfiguration.get(s);
        this.mTouchSlop = vc.getScaledTouchSlop();
    }

    public void destroy() {
        if (mOverlayView != null) {
            windowManager.removeView(mOverlayView);
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        // Remember initial location of overlay icon
        this.initialX = params.x;
        this.initialY = params.y;
        this.initialTouchX = motionEvent.getRawX();
        this.initialTouchY = motionEvent.getRawY();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // empty by design
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // Add a dummy artifact to database
        //        executorService.execute(() -> {
        //            AppDatabase.getDatabase(mService)
        //                    .artifactDao()
        //                    .insert(Artifact.builder()
        //                            .withName("ArtifactFromOverlay")
        //                            .withCategory(Category.Weapon)
        //                            .withSkills(Collections.emptyList())
        //                            .build());
        //        });

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Move overlay icon
        int deltaX = (int) (e2.getRawX() - initialTouchX); // delta to initial position
        int deltaY = (int) (e2.getRawY() - initialTouchY); // delta to initial position

        if (Math.abs(deltaX) > mTouchSlop || Math.abs(deltaY) > mTouchSlop) {
            params.x = initialX + deltaX;
            params.y = initialY + deltaY;
            windowManager.updateViewLayout(mOverlayView, params);
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // Open main activity
        Intent intent = new Intent(mService, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mService.startActivity(intent);

        // Stop overlay service
//        Intent i2 = new Intent(mService, OverlayIconService.class);
//        mService.stopService(i2);
        mService.stopSelf();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // empty by design
        return false;
    }

}
