package com.example.abumuhsin.udusmini_library.fragments;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyBook_fragmentTest {

    @Test
    public void isCourseCodeValid() {
        String course_code = "cmp324";
        MyBook_fragment myBook_fragment = new MyBook_fragment();
        if (myBook_fragment.isCourseCodeValid(course_code)) {
            System.out.println(myBook_fragment.getCourse_code());
        }
    }
}