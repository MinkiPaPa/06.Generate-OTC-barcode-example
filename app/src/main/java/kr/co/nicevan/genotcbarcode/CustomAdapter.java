package kr.co.nicevan.genotcbarcode;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Dictionary> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView date;
        protected TextView name;
        protected TextView amount;
        protected TextView balance;

        public CustomViewHolder(View view) {
            super(view);
            this.date = (TextView) view.findViewById(R.id.date_listitem);
            this.name = (TextView) view.findViewById(R.id.name_listitem);
            this.amount = (TextView) view.findViewById(R.id.amount_listitem);
        }
    }


    public CustomAdapter(ArrayList<Dictionary> list) {
        this.mList = list;
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }





    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        viewholder.date.setGravity(Gravity.CENTER);
        viewholder.name.setGravity(Gravity.CENTER);
        viewholder.amount.setGravity(Gravity.CENTER);



        viewholder.date.setText(mList.get(position).getDate());
        viewholder.name.setText(mList.get(position).getName());
        viewholder.amount.setText(mList.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
