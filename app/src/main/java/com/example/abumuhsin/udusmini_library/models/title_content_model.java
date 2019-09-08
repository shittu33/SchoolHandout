package com.example.abumuhsin.udusmini_library.models;

import java.io.Serializable;

/**
 * Created by Abu Muhsin on 03/10/2018.
 */

public class title_content_model implements Serializable {
    private String title = null;
    private String content = null;

    public title_content_model(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
