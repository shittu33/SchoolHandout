package com.example.abumuhsin.udusmini_library.activities;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;

import java.io.File;

public interface OnlineLocalHandoutListener {
    void onHandoutDownloadFromOnline(Handout handout, File zip_file, File cover_file);
}
