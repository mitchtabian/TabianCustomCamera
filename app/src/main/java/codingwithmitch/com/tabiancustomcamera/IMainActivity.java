package codingwithmitch.com.tabiancustomcamera;

import android.graphics.drawable.Drawable;

/**
 * Created by User on 5/22/2018.
 */

public interface IMainActivity {

    void hideStillshotWidgets();

    void showStillshotWidgets();

    void onCloseViewStillshot();

    void hideStatusBar();

    void showStatusBar();

    void setCameraFrontFacing();

    void setCameraBackFacing();

    void setFrontCameraId(String cameraId);

    void setBackCameraId(String cameraId);

    String getFrontCameraId();

    String getBackCameraId();

    boolean isCameraFrontFacing();

    boolean isCameraBackFacing();

    String getMaxAspectRatio();

    void toggleViewStickersFragment();

    void addSticker(Drawable sticker);

    void setTrashIconSize(int width, int height);
}
