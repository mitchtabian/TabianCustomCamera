package codingwithmitch.com.tabiancustomcamera;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by User on 5/24/2018.
 */

public class ViewStickersFragment extends Fragment implements
        StickerAdapter.RecyclerViewClickListener,
        View.OnClickListener
{

    private static final String TAG = "ViewStickersFragment";
    private static final int NUM_COLUMNS = 3;

    //widgets
    private RecyclerView mRecyclerView;

    //vars
    private ArrayList<Drawable> mStickers = new ArrayList<>();
    private IMainActivity mIMainActivity;
	private StickerAdapter mStickerAdapter;

    public static ViewStickersFragment newInstance() {
        return new ViewStickersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_stickers, container, false);
        view.findViewById(R.id.init_sticker_icon).setOnClickListener(this);
        mRecyclerView = view.findViewById(R.id.recycler_view);

        getStickers();
        initRecyclerView();

        return view;
    }

    private void initRecyclerView(){
        if(mStickerAdapter == null){
            mStickerAdapter = new StickerAdapter(getActivity(), mStickers , this);
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLUMNS));
        mRecyclerView.setAdapter(mStickerAdapter);
    }

    private void getStickers(){
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.astonished_face_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.cry_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.nerd_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.sad_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.slightly_smiling_face_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.smiley_face_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.smiley_face_tightly_closed_eyes_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.smiley_smiling_eyes_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.smiley_with_sweat_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.smirking_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.sunglasses_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.tears_of_joy_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.unamused_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.upside_down_face_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.ic_undo_small));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.evil_monkey_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.penguin_emoji));
    }

    @Override
    public void onStickerClicked(int position) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.init_sticker_icon:{
                mIMainActivity.toggleViewStickersFragment();
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
}






















