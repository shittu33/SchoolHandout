package com.example.abumuhsin.udusmini_library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.abumuhsin.udusmini_library.Enums.Cover_type;

import java.util.Arrays;
import java.util.Random;

public class CoverSuplier {

    public static final String SIMPLE_2 = "Simple2";
    public static final String SIMPLE_COVER = "simple cover";
    public static final String SIMLE_COVER_2 = "simle cover2";
    private static Cover_type type;

    public static Cover_type getType() {
        return type;
    }

    @SuppressLint("SetTextI18n")
    public static View getCoverOfType(Context context, String book_name, String course_code) {
//        Cover_type type = getRandomType();
        type = getRandomType();
        switch (type) {
            case SIMPLE2:
                return getSimple2(context, book_name, course_code);
            case SIMPLE_COVER:
                return getSimpleCover(context, book_name, course_code);
            case SIMPLE_COVER2:
                return getSampleCover2(context, book_name, course_code);
            default:
                return getSimple2(context, book_name, course_code);

        }
    }

    private static View getSimple2(Context context, String book_name, String course_code) {
        View view2 = LayoutInflater.from(context).inflate(R.layout.book_cover_lay2, null);
        TextView title2 = view2.findViewById(R.id.title_tv);
        TextView sub_title = view2.findViewById(R.id.sub_title);
        title2.setText(book_name);
        sub_title.setText(course_code);
        return view2;
    }
    private static View getSimpleCover(Context context, String book_name, String course_code) {
        View view4 = LayoutInflater.from(context).inflate(R.layout.simple_cover, null);
        view4.findViewById(R.id.cover_bac).setBackgroundColor(getRandomColor(context));
        TextView title4 = view4.findViewById(R.id.title_tv);
        TextView dept4 = view4.findViewById(R.id.dept);
        title4.setText(book_name);
        dept4.setText("Depatment of ");
        String dept_code4 = course_code.substring(0, 3);
        dept4.append(getDeptFromAbrv(dept_code4));
        return view4;
    }

    public static String getCourseCodeNumber(String course_code) {
        if (course_code.length() <= 8) {
            if (course_code.length() == 6) {
                return course_code.substring(3, 6);
            } else if (course_code.length() == 7) {
                if (course_code.contains(" ")) {
                    return course_code.split(" ")[1];
                } else {
                    return course_code.substring(3, 7);
                }
            } else if (course_code.length() == 8 && course_code.contains(" ")) {
//                abrv = course_code.split(" ")[0];
                return course_code.split(" ")[1];
            }
        }
        return "";
    }
    public static String getLevelFromCode(String code){
        char first_char = code.charAt(0);
        switch (first_char){
            case '1':
                return "100";
            case '2':
                return "200";
            case '3':
                return "300";
            case '4':
                return "400";
            case '5':
                return "500";
            case '6':
                return "600";
        }
        return "200";
    }
    public static String getAbrvFromCourseCode(String course_code){
        return course_code.substring(0,3);
    }
    public static String getDeptFromAbrv(String course_abrv){

        switch (course_abrv) {
            case "CMP":
                return "Mathematics/CMP";
            case "STA":
                return ("Mathematics/STA");
            case "MAT":
                return ("Mathematics");
            case "GLG":
                return ("Geology");
            case "BCH":
                return ("Biochemistry");
            case "MCB":
                return ("Micro Biology");
            default:
                return (course_abrv);
        }
    }

    public static int getIntTypeFrom_Enum(Cover_type cover_type){
        return Arrays.binarySearch(Cover_type.values(),cover_type);
    }
    private static View getSampleCover2(Context context, String book_name, String course_code) {
        View view4 = LayoutInflater.from(context).inflate(R.layout.simple_cover2, null);
        TextView title4 = view4.findViewById(R.id.title_tv);
        TextView code1 = view4.findViewById(R.id.course_code1);
        TextView code2 = view4.findViewById(R.id.course_code_2);
        title4.setText(book_name);
        code1.setText(course_code.substring(0,3));
        code2.setText(getCourseCodeNumber(course_code));
        return view4;
    }

    private static int getRandomColor(Context context) {
        Random random = new Random();
        Integer[] colors = {
                context.getResources().getColor(R.color.cover_color_dark)
                , context.getResources().getColor(R.color.colorPrimary)
                , context.getResources().getColor(R.color.cover_purple)
                , context.getResources().getColor(R.color.cover_color1)
                , context.getResources().getColor(R.color.cover_color2)
                , context.getResources().getColor(R.color.cover_color3)
                , context.getResources().getColor(R.color.cover_color4)
                , context.getResources().getColor(R.color.cover_color5)
                , context.getResources().getColor(R.color.cover_color6)
                , context.getResources().getColor(R.color.cover_color7)
                , context.getResources().getColor(R.color.cover_color8)
        };
        return colors[random.nextInt(11)];
    }

    public static Cover_type getRandomType() {
        Random random = new Random();
        return Cover_type.values()[random.nextInt(5)];
    }
}
