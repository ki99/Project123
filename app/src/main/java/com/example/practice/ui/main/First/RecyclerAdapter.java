package com.example.practice.ui.main.First;

import android.app.AlertDialog;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.practice.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Dictionary> mData;
    Context context;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView index;
        protected TextView name;
        protected TextView group;
        protected String number;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조. (hold strong reference)
            index = itemView.findViewById(R.id.text1);
            name = itemView.findViewById(R.id.text2);
            group = itemView.findViewById(R.id.text3);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("연락처").setMessage(number);

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();
                }
            });
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public RecyclerAdapter(ArrayList<Dictionary> list, Context context) {
        this.mData = list;
        this.context = context;
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.first_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.index.setText(mData.get(position).getIndex());
        holder.name.setText(mData.get(position).getName());
        holder.group.setText(mData.get(position).getGroup());
        holder.number = mData.get(position).getNumber();
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        if (mData !=null)
        return mData.size();
        else return 0;
    }
}
