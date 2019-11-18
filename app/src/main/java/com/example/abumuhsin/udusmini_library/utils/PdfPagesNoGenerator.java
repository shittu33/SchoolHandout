package com.example.abumuhsin.udusmini_library.utils;

import android.util.Log;

import java.util.LinkedList;

public class PdfPagesNoGenerator {
    public static final String TAG = "PdfPagesNoGenerator";
    private LinkedList<Integer> bufferedPages = new LinkedList<>();
    private int bufferIndex = -1;
    public int adapterIndex = -1;
    private int adapterDataCount = 0;
    private final int sideBufferSize = 1;
    private LinkedList<Integer> releasedPages = new LinkedList<>();
    private static final int MAX_RELEASED_VIEW_SIZE = 1;

    public PdfPagesNoGenerator() {
        InitLoad();
    }

    public LinkedList<Integer> getBufferedPages() {
        return bufferedPages;
    }

    private void releaseView(int page) {
        addReleasedView(page);
    }

    private void InitLoad() {
        bufferedPages.add(0);
        bufferedPages.add(1);
        bufferedPages.add(2);
    }

    private void addReleasedView(int page) {
        if (releasedPages.size() < MAX_RELEASED_VIEW_SIZE) {
            releasedPages.add(page);
        }
    }

    public void flippedToView(int indexInAdapter) {
        Log.i(TAG, String.format("flippedToView: %d, isPost %s", indexInAdapter, null));
        if (indexInAdapter >= 0 && indexInAdapter < adapterDataCount) { //let say count is 5

            if (indexInAdapter == adapterIndex + 1) { //forward one page e.g from 3 to 4
                if (adapterIndex < adapterDataCount - 1) {// 3<4, proceed...
                    adapterIndex++; //e.g 3 -> 4
                    //get the old view
                    int old = bufferedPages.get(bufferIndex);//e.g previous buffer index 1 has view 3
                    if (bufferIndex + 1 > sideBufferSize) { //e.g 1+1 > 1(greater), continue...
                        releaseView(bufferedPages.removeFirst());//e.g release view at first of buffer
                    }
                    if (adapterIndex + sideBufferSize < adapterDataCount) {//e.g 5  == 5 (not less than 5),skip...
                        bufferedPages.addLast(adapterIndex + sideBufferSize); //e.g add view of index 4 at last of buffer
                    }
                    bufferIndex = bufferedPages.indexOf(old) + 1; //e.g 0+1, buffer index of old view shifted from 0 to 1
                }
            } else if (indexInAdapter == adapterIndex - 1) {//Backward e.g from 2 to 1 ....Size 5
                if (adapterIndex > 0) {//if old adapter index is greater than 0
                    adapterIndex--; //e.g from 2 to 1

                    int old = bufferedPages.get(bufferIndex); // e.g previous buffer index 1 has view 2
                    if (bufferIndex - 1 + sideBufferSize < bufferedPages.size() - 1) { // e.g 1<2, continue...
                        releaseView(bufferedPages.removeLast()); //remove the last view in buffer
                    }
                    if (adapterIndex - sideBufferSize >= 0) { // e.g 0 == 0, continue...

                        bufferedPages.addFirst(adapterIndex - sideBufferSize);
                    }
                    bufferIndex = bufferedPages.indexOf(old) - 1; //e.g the current index-1 of view 4 is 1
                } else {

                    Log.i(TAG, "index is less than 0");
                }
            } else {
                Log.i(TAG, "Should not happen," + adapterIndex + "," + indexInAdapter + "," + adapterIndex);
            }
        }
    }

}
