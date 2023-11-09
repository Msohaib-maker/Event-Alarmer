package com.sohaib.eventcheck.Repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sohaib.eventcheck.Model.ListModel;

import java.util.List;

@Dao
public interface ListModelDao {

    @Query("select * from ListModel")
    List<ListModel> getAllListModels();

    @Insert
    void addListModel(ListModel listModel);

    @Update
    void UpdateListModel(ListModel listModel);

    @Delete
    void DeleteListModel(ListModel listModel);

    @Query("select * from ListModel where id = :Id")
    ListModel getListModel(int Id);

}
