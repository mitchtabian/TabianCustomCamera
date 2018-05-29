package codingwithmitch.com.customcameratest;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

public class MainActivity extends AppCompatActivity implements IMainActivity{

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1234;
    public static String CAMERA_POSITION_FRONT;
    public static String CAMERA_POSITION_BACK;
    public static String MAX_ASPECT_RATIO;

    //widgets

    //vars
    private boolean mPermissions;
    public String mCameraOrientation = "10"; // Front-facing or back-facing


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMaxAspectRatio();
        init();
    }

    private void setMaxAspectRatio(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        double aspectRatio = (double)height / (double)width;

        Log.d(TAG, "setMaxAspectRatio: height: " + String.valueOf((double)height));
        Log.d(TAG, "setMaxAspectRatio: width: " + String.valueOf((double)width));
        Log.d(TAG, "setMaxAspectRatio: aspect ratio: " + aspectRatio);


        // Standard 16/9 aspect ratio
        if((aspectRatio * 9 > 15 && aspectRatio * 9 < 17)){
            MAX_ASPECT_RATIO = "16/9";
        }
        // Small aspect ratio
        else if(aspectRatio * 3 > 3 && aspectRatio * 3 < 5){
            MAX_ASPECT_RATIO = "4/3";
        }
        else{
            MAX_ASPECT_RATIO = "other"; // OR is for longer screens (ex Samsung s8)
        }

        Log.d(TAG, "setMaxAspectRatio: max aspect ratio: " + MAX_ASPECT_RATIO);
    }

    private void init(){
        if(mPermissions){
            if(checkCameraHardware(this)){

                // ... CHECK SAVED INSTANCE STATE??
                startCamera2();
            }
        }
        else{
            verifyPermissions();
        }
    }

    private void startCamera2(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_container, Camera2Fragment.newInstance(), getString(R.string.fragment_camera2))
                .commit();

    }

    @Override
    public void toggleViewStickersFragment(){

        ViewStickersFragment viewStickersFragment
                = (ViewStickersFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_view_stickers));
        if (viewStickersFragment != null) {
            if(viewStickersFragment.isVisible()){
                hideViewStickersFragment(viewStickersFragment);
            }
            else{
                showViewStickersFragment(viewStickersFragment);
            }
        }
        else{
            inflateViewStickersFragment();
        }
    }

    private void hideViewStickersFragment(ViewStickersFragment fragment){

        showStillshotWidgets();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
        transaction.hide(fragment);
        transaction.commit();
    }

    private void showViewStickersFragment(ViewStickersFragment fragment){

        hideStillshotWidgets();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
        transaction.show(fragment);
        transaction.commit();
    }

    private void inflateViewStickersFragment(){

        hideStillshotWidgets();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
        transaction.add(R.id.camera_container, ViewStickersFragment.newInstance(), getString(R.string.fragment_view_stickers));
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        ViewStickersFragment viewStickersFragment
                = (ViewStickersFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_view_stickers));
        if (viewStickersFragment != null) {
            if(viewStickersFragment.isVisible()){
                hideViewStickersFragment(viewStickersFragment);
            }
            else{
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    /**
     * Generalized method for asking permission. Can pass any array of permissions
     */
    public void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permissions = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1] ) == PackageManager.PERMISSION_GRANTED) {
            mPermissions = true;
            init();
        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    permissions,
                    REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: called.");

        if(requestCode == REQUEST_CODE){
            Log.d(TAG, "onRequestPermissionsResult: request code detected.");
            if(mPermissions){
                Log.d(TAG, "onRequestPermissionsResult: storage permission is granted.");
                init();
            }
            else{
                verifyPermissions();
            }
        }
    }

    @Override
    public String getMaxAspectRatio(){
        return MAX_ASPECT_RATIO;
    }

    @Override
    public void hideStillshotWidgets() {
        Camera2Fragment camera2Fragment = (Camera2Fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_camera2));
        if (camera2Fragment != null) {
            if(camera2Fragment.isVisible()){
//                Log.d(TAG, "drawingStarted: started drawing.");
                camera2Fragment.drawingStarted();
            }
        }
    }

    @Override
    public void showStillshotWidgets() {
        Camera2Fragment camera2Fragment = (Camera2Fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_camera2));
        if (camera2Fragment != null) {
            if(camera2Fragment.isVisible()){
//                Log.d(TAG, "drawingStopped: stopped drawing.");
                camera2Fragment.drawingStopped();
            }
        }
    }

    public void dragStickerStarted(){
        Camera2Fragment camera2Fragment = (Camera2Fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_camera2));
        if (camera2Fragment != null) {
            if(camera2Fragment.isVisible()){
                camera2Fragment.dragStickerStarted();
            }
        }
    }

    public void dragStickerStopped(){
        Camera2Fragment camera2Fragment = (Camera2Fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_camera2));
        if (camera2Fragment != null) {
            if(camera2Fragment.isVisible()){
                camera2Fragment.dragStickerStopped();
            }
        }
    }

    @Override
    public void onCloseViewStillshot() {
        startCamera2();
    }

    @Override
    public void hideStatusBar() {

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void showStatusBar() {

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void setCameraFrontFacing() {
        mCameraOrientation = CAMERA_POSITION_FRONT;
    }

    @Override
    public void setFrontCameraId(String cameraId){
        CAMERA_POSITION_FRONT = cameraId;
    }


    @Override
    public void setCameraBackFacing() {
        mCameraOrientation = CAMERA_POSITION_BACK;
    }

    @Override
    public void setBackCameraId(String cameraId){
        CAMERA_POSITION_BACK = cameraId;
    }

    @Override
    public String getBackCameraId(){
        return CAMERA_POSITION_BACK;
    }

    @Override
    public String getFrontCameraId(){
        return CAMERA_POSITION_FRONT;
    }

    @Override
    public boolean isCameraFrontFacing() {
        if(mCameraOrientation.equals(CAMERA_POSITION_FRONT)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isCameraBackFacing() {
        if(mCameraOrientation.equals(CAMERA_POSITION_BACK)){
            return true;
        }
        return false;
    }

    @Override
    public void addSticker(Drawable sticker){
        Camera2Fragment camera2Fragment = (Camera2Fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_camera2));
        if (camera2Fragment != null) {
            if(camera2Fragment.isVisible()){
                camera2Fragment.addSticker(sticker);
            }
        }
    }

    @Override
    public void setTrashIconSize(int width, int height){
        Camera2Fragment camera2Fragment = (Camera2Fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_camera2));
        if (camera2Fragment != null) {
            if(camera2Fragment.isVisible()){
                camera2Fragment.setTrashIconSize(width, height);
            }
        }
    }

}








