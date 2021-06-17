package com.example.abumuhsin.udusmini_library.fragments;

import org.junit.Test;

public class MyBook_fragmentTest {

    @Test
    public void isCourseCodeValid() {
        String course_code = "cmpA324";
        MyBook_fragment myBook_fragment = new MyBook_fragment(zip_uri);
        if (myBook_fragment.isCourseCodeValid("cmp324")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
        if (myBook_fragment.isCourseCodeValid("cmp 324")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
        if (myBook_fragment.isCourseCodeValid("cmpA324")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
        if (myBook_fragment.isCourseCodeValid("cmpA 324")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
        if (myBook_fragment.isCourseCodeValid("cmp324B")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
        if (myBook_fragment.isCourseCodeValid("cmp 324B")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
        if (myBook_fragment.isCourseCodeValid("cmpA324B")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
        if (myBook_fragment.isCourseCodeValid("cmpA 324B")) {
            System.out.println(myBook_fragment.getCourse_code());
        }
    }
}