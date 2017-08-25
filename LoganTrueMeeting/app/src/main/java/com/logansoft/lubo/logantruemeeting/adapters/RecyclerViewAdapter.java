package com.logansoft.lubo.logantruemeeting.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logansoft.lubo.logantruemeeting.R;
import com.zte.ucsp.vtcoresdk.jni.conference.OrderConfInfo;

import java.util.List;

/**
 * Created by logansoft on 2017/8/24.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private List<OrderConfInfo> orderConfInfos;
    private Context context;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(List<OrderConfInfo> orderConfInfos, Context context) {
        this.orderConfInfos = orderConfInfos;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_room_info, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        OrderConfInfo orderConfInfo = orderConfInfos.get(position);
        holder.tvRoomName.setText(orderConfInfo.getConfID()+"");
        holder.tvModerator.setText(orderConfInfo.getConfLimitTime());

        if (onItemClickListener!=null){
            holder.rvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(position);
                }
            });
            holder.rvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (orderConfInfos!=null) {
            return orderConfInfos.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvModerator;
        private final TextView tvRoomNumber;
        private final TextView tvOnlineCount;
        private final TextView tvRoomName;
        private final LinearLayout rvItem;

        public MyViewHolder(View view) {
            super(view);
            tvRoomName = (TextView) view.findViewById(R.id.tv_room_name);
            tvModerator = ((TextView) view.findViewById(R.id.tv_moderator));
            tvRoomNumber = ((TextView) view.findViewById(R.id.tv_room_number));
            tvOnlineCount = ((TextView) view.findViewById(R.id.tv_online_count));
            rvItem = ((LinearLayout) view.findViewById(R.id.llRvItem));
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int postion);
    }

}
