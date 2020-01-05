package com.example.practice.ui.main.Second;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.practice.MainActivity;
import com.example.practice.R;


public class SecondFragment extends Fragment {


    public static SecondFragment newInstance() {
        return new SecondFragment();
    }

    private SecondViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    //fragment_image를 화면에 띄워줌, 이의 참조 획득
    View view = inflater.inflate(R.layout.second_fragment, container, false);

    //위에서 참조한 fragment_image에서 gridview에 대한 참조 획득
    GridView gridView = (GridView) view.findViewById(R.id.gridView);

    //각각의 gridview를 관리해줄 adapter 연결
        gridView.setAdapter(new ImageAdapter(getActivity()));


    //gridview를 클릭했을 떄 나타나는 효과에 대한 메소드
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Sending image id to FullScreenActivity를 하기 위한 intent의 참조 획득
            Intent i = new Intent(getActivity(), FullImageActivity.class);
            // 누른 이미지의 position 기억
            i.putExtra("id", position);
            //Activity 전환
            startActivity(i);

        }
    });
        return view;
}

}
