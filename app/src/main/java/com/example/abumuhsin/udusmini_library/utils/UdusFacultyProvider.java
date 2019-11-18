package com.example.abumuhsin.udusmini_library.utils;

import java.util.Arrays;

public class UdusFacultyProvider {
    private static String[] faculties;
    private static String[][] fact_dept_array;
public static String getFaculty(String dept){
    for (int i = 0; i < fact_dept_array.length; i++) {
        for (int j = 0; j < fact_dept_array[i].length; j++) {
            String available_dept= fact_dept_array[i][j];
            if (dept.equals(available_dept)){
                return faculties[i];
            }
        }
    }
    return null;
}
    static {
        faculties = new String[]{
                 "Science", "Health Science", "Agriculture", "Art&Islamic"
                , "Education&Extension", "Engineering", "Law", "Management Science"
                , "Social Science", "Veterinary Medicine", "Pharmaceutical Science"};
        fact_dept_array = new String[][]{
                //science
                new String[]{
                        "Select your Department"
                        , "BCH"
                        , "BIO"
                        , "GLG"
                        , "MAT"
                        , "MCB"
                        , "PHY"
                        , "Pure and Applied Chemistry"
                },
                //Health science
                //Agric
                new String[]{
                        "Select your Department"
                        , "Agricultural Economics"
                        , "Agricultural Extension & Rural Development"
                        , "Animal Science"
                        , "Crop Science"
                        , "Fisheries and Aquaculture"
                        , "Forestry and Environment"
                        , "Soil Science and Agricultural Engineering"
                },
                //art and islam
                new String[]{
                         "Arabic"
                        , "Islamic studies"
                        , "History"
                        , "Modern European Language and Linguistics"
                        , "Nigerian Languages"
                },
                //Education
                new String[]{

                        "Adult Education and Extension Services"
                        , "Curriculum Studies & Educational Tech"
                        , "Educational Foundation"
                        , "Science and Vocational Education"
                },
                //Engineering
                new String[]{

                        "Civil Engineering"
                        , "Electrical and Electronics Engineering"
                        , "Environmental Resources Management"
                        , "Information and Communication Technology"
                        , "Mechanical Enigineering"
                },
                //Law
                new String[]{
                        "Islamic Law"
                        , "Public Law and Jurisprudence"
                        , "Private and Business Law"

                },
                //Management
                new String[]{
                        "Accounting"
                        , "Business Administration"
                        , "Public Administration"
                },
                //Social science
                new String[]{
                        "Eocnomics"
                        , "Geography"
                        , "Political Science"
                        , "Sociology"
                },
                //Vet med
                new String[]{
                        "Veterinary Anatomy"
                        , "Veterinary Microbiology"
                        , "Veterinary Parasitology and Entomology"
                        , "Veterinary Medicine"
                        , "Veterinary Physiology and Biochemistry"
                        , "Veterinary Public Health and Preventive Medicine"
                        , "Theriogenology and Animal Production"
                        , "Veterinary Pharmacology and Toxicology"
                        , "Veterinary Pathology"
                        , "Veterinary Surgery and Radiology"
                },
                //pharmacy
                new String[]{"Pharmacy"}
        };

    }

    public static String[] getFaculties() {
        return faculties;
    }

    public static String[] getFactDepartments(String faculty) {
        int fact_index = Arrays.asList(getFaculties()).indexOf(faculty);
        return fact_dept_array[fact_index];
    }
}
