package com.example.practice.ui.main.First;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FirstViewModel extends ViewModel {
    private MutableLiveData<List<Dictionary>> contactList;
    public LiveData<List<Dictionary>> getList(){
        if (contactList == null){
            contactList = new MutableLiveData<List<Dictionary>>();
            loadList();
        }
        return contactList;
    }

    private void loadList(){
        // Do an asynchronous operation to fetch userList.
    }


}