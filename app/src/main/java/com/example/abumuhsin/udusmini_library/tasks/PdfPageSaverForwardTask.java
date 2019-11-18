package com.example.abumuhsin.udusmini_library.tasks;

import android.os.AsyncTask;
import android.util.Log;

public class PdfPageSaverForwardTask extends AsyncTask<Integer,Integer, Integer> {

    private final String pdf_path;
    private final int current_page_index;

    public PdfPageSaverForwardTask(String pdf_path, int current_page_index) {
        this.pdf_path = pdf_path;
        this.current_page_index = current_page_index;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        for (Integer integer : integers) {
            publishProgress(integer);
        }
        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values[0]);
        Log.i("PdfPageSaverForwardTask",values[0].toString());
    }

    @Override
    protected void onPostExecute(Integer integer) {
//        Log.i("PdfPageSaverForwardTask",integer.toString());
        super.onPostExecute(integer);
    }
}
