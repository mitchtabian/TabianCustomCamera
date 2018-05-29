package codingwithmitch.com.tabiancustomcamera;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by User on 5/24/2018.
 */

public class StickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "StickerAdapter";

    private ArrayList<Drawable> mStickers = new ArrayList<>();
    private RecyclerViewClickListener mClickListener;
    private Context mContext;


    public StickerAdapter(Context context, ArrayList<Drawable> stickers, RecyclerViewClickListener clickListener) {
        mStickers = stickers;
        mClickListener = clickListener;
        mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mStickers.get(position))
                .into(((ViewHolder)holder).image);
    }

    @Override
    public int getItemCount() {
        return mStickers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        RecyclerViewClickListener clickListener;

        public ViewHolder(View itemView, RecyclerViewClickListener clickListener) {
            super(itemView);
            image = itemView.findViewById(R.id.sticker_image);
            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null){
                Log.d(TAG, "onClick: clicked.");
                clickListener.onStickerClicked(getAdapterPosition());
            }
        }
    }

    public interface RecyclerViewClickListener{
        void onStickerClicked(int position);
    }
}














