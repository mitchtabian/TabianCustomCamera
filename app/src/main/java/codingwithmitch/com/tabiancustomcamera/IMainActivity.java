package codingwithmitch.com.tabiancustomcamera;

import android.graphics.drawable.Drawable;

/**
 * Created by User on 6/5/2018.
 */

public interface IMainActivity {

    void setCameraFrontFacing();

    void setCameraBackFacing();

    boolean isCameraFrontFacing();

    boolean isCameraBackFacing();

    void setFrontCameraId(String cameraId);

    void setBackCameraId(String cameraId);

    String getFrontCameraId();

    String getBackCameraId();

    void hideStatusBar();

    void showStatusBar();

    void hideStillshotWidgets();

    void showStillshotWidgets();

    void toggleViewStickersFragment();

    void addSticker(Drawable sticker);
}
