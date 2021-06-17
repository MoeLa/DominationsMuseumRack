package bhg.sucks;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;


public class OverlayIconService extends Service {

    private boolean mCapturing = false;
    private OverlayData overlayData;
    private Intent screenCaptureData;

    @Override
    public void onCreate() {
        super.onCreate();

        this.overlayData = new OverlayData();
        overlayData.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.screenCaptureData = intent.getParcelableExtra(MainActivity.SCREENCAPTURE_DATA);

        return super.onStartCommand(intent, flags, startId);
    }

    public void toogleCapturing(View view) {
        Button b = (Button) view;

        if (mCapturing) {
            startService(com.mtsahakis.mediaprojectiondemo.ScreenCaptureService.getStopIntent(this));
            mCapturing = false;
            b.setText(R.string.button_start);
        } else {
            startService(com.mtsahakis.mediaprojectiondemo.ScreenCaptureService.getStartIntent(this, Activity.RESULT_OK, (Intent) screenCaptureData.clone()));
            mCapturing = true;
            b.setText(R.string.button_stop);
        }
    }

    @Override
    public void onDestroy() {
        overlayData.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
