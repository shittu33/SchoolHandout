package com.example.abumuhsin.udusmini_library.firebaseStuff.model;

public class FcmMessage {
    String to;
    Data data;

    public FcmMessage(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public FcmMessage() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FcmMessage{" +
                "to='" + to + '\'' +
                ", data=" + data +
                '}';
    }
}
