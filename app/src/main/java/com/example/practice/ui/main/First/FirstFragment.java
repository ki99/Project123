package com.example.practice.ui.main.First;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.practice.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FirstFragment extends Fragment {
    RecyclerView rv;
    RecyclerAdapter ra;
    View view;
    FloatingActionButton fab;
    String json;
    String ContactAddress;
    int k;
/*    static int cnum = 0;
    static int cvnum = 0;
    static int acnum = 0;
    static int startnum = 0;
    static int rnum = 0;
    static int panum = 0;
    static int stopnum = 0;
    static int devnum = 0;
    static int desnum = 0;
    static int detanum = 0;*/

    private FirstViewModel mViewModel;
    private ArrayList<Dictionary> Items;
    private int count = 1;

    public static FirstFragment newInstance() {
        return new FirstFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Items = new ArrayList<>();

        json = getJsonString();
        jsonParsing(json, Items);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.first_fragment, container, false);
        rv = view.findViewById(R.id.recycler);
        rv.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));

        ra = new RecyclerAdapter(Items);
        rv.setAdapter(ra);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                Dictionary date;
                date = new Dictionary(count + "", "name", "group", "number");
                Items.add(date); // RecyclerView의 마지막 줄에 삽입
                ra.notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FirstViewModel.class);
/*        mViewModel.getList().observe(this, new Observer<List<Dictionary>>() {
            @Override
            public void onChanged(List<Dictionary> newdict) {
            }
        });

        final Observer<Dictionary> listObserver = new Observer<Dictionary>() {
            @Override
            public void onChanged(@Nullable final Dictionary newcontact){
                (newcontact);
            }
        }
        mViewModel.getList().observe(this, contactList->{

        })*/
    }

    private String getJsonString() {
        String json = "";

        try {
            InputStream is = getResources().getAssets().open("data.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private void jsonParsing(String json, ArrayList<Dictionary> items) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            ContactAddress = jsonObject.getString("ContactAddress");
            JSONArray dictionaryArray = new JSONArray(ContactAddress);

            for (int i = 0; i < dictionaryArray.length(); i++) {
                JSONObject dictObject = dictionaryArray.getJSONObject(i);

                Dictionary dict = new Dictionary();
                dict.setIndex(count+"");
                dict.setName(dictObject.getString("name"));
                dict.setGroup(dictObject.getString("group"));
                dict.setNumber(dictObject.getString("number"));
                count++;
                items.add(dict);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
