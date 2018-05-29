package codingwithmitch.com.tabiancustomcamera;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * Created by User on 5/22/2018.
 */

public class ViewStillshotFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ViewStillshotFragment";


    //widgets
    private ImageView mStillshotImageView;
    private RelativeLayout mClose;

    //vars
    private File mFile;
    private IMainActivity mIMainActivity;


    public static ViewStillshotFragment newInstance() {
        return new ViewStillshotFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFile = new File(getActivity().getExternalFilesDir(null), "stillshot.jpg");
        mIMainActivity.hideStatusBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_stillshot, container, false);
        mStillshotImageView = view.findViewById(R.id.stillshot_imageview);
        mClose = view.findViewById(R.id.close);

        mClose.setOnClickListener(this);

        setStillshotImage();

        return view;
    }

    private void setStillshotImage(){

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(getActivity())
                .setDefaultRequestOptions(options)
                .load(Uri.fromFile(mFile))
                .into(mStillshotImageView);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.close:{
                mIMainActivity.onCloseViewStillshot();
                break;
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mIMainActivity = (IMainActivity) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mIMainActivity.showStatusBar();
    }
}


















