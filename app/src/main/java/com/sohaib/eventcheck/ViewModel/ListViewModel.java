package com.sohaib.eventcheck.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sohaib.eventcheck.Model.ListModel;

import java.util.List;

public class ListViewModel extends ViewModel {

    private MutableLiveData<List<ListModel>> listMutableLiveData;

    public void init()
    {
        if(listMutableLiveData == null)
        {
            listMutableLiveData = new MutableLiveData<>();
        }
    }




    public LiveData<List<ListModel>> getListModels()
    {
        return listMutableLiveData;
    }


}
