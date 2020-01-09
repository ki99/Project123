package com.example.myapp.First;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class FirstViewModel extends ViewModel {
    private MutableLiveData<List<Dictionary>> contactList;
    private List<Dictionary> items;

    public MutableLiveData<List<Dictionary>> getLiveList() {
        if (contactList == null) {
            items = new ArrayList<>();
            contactList = new MutableLiveData<>();
        }
        return contactList;
    }

    public List<Dictionary> getList() {
        if (items == null) {
            items = new ArrayList<>();
            //contactList = new MutableLiveData<>();
        }
        return items;
    }

    public int getSize() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void add(Dictionary dict) {
        items.add(dict);
        contactList.setValue(items);
    }

    public void delete(Dictionary dict) {
        items.remove(dict);
        contactList.setValue(items);
    }
}