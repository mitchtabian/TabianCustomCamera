package codingwithmitch.com.tabiancustomcamera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 5/29/2018.
 */

public class Camera2Fragment extends Fragment {

    private static final String TAG = "Camera2Fragment";

    public static Camera2Fragment newInstance(){
        return new Camera2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera2, container, false);


        return view;
    }
}
