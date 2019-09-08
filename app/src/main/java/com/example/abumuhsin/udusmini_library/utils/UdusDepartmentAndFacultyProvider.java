package com.example.abumuhsin.udusmini_library.utils;

import java.util.Arrays;

public class UdusDepartmentAndFacultyProvider {
    private static String[] faculties;
    private static String[][] fact_dept_array;

    static {
        faculties = new String[]{
                "Select your faculty", "Science", "Health Science", "Agriculture", "Art&Islamic"
                , "Education&Extension", "Engineering", "Law", "Management Science"
                , "Social Science", "Veterinary Medicine", "Pharmaceutical Science"};
        fact_dept_array = new String[][]{
                //none
                new String[]{
                        "Select your Department"
                },
                //science
                new String[]{
                        "Select your Department"
                        , "Biochemistry"
                        , "Biological Sciences"
                        , "Geology"
                        , "Mathematics"
                        , "Microbiology"
                        , "Physics"
                        , "Pure and Applied Chemistry"
                },
                //Health science
                new String[]{
                        "Select your Department"
                        ,
                },
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
                        "Select your Department"
                        , "Arabic"
                        , "Islamic studies"
                        , "History"
                        , "Modern European Language and Linguistics"
                        , "Nigerian Languages"
                },
                //Education
                new String[]{

                        "Select your Department"
                        ,
                        "Adult Education and Extension Services"
                        , "Curriculum Studies & Educational Tech"
                        , "Educational Foundation"
                        , "Science and Vocational Education"
                },
                //Engineering
                new String[]{

                        "Select your Department"
                        ,
                        "Civil Engineering"
                        , "Electrical and Electronics Engineering"
                        , "Environmental Resources Management"
                        , "Information and Communication Technology"
                        , "Mechanical Enigineering"
                },
                //Law
                new String[]{
                        "Select your Department"
                        ,
                        "Islamic Law"
                        , "Public Law and Jurisprudence"
                        , "Private and Business Law"

                },
                //Management
                new String[]{
                        "Select your Department"
                        ,
                        "Accounting"
                        , "Business Administration"
                        , "Public Administration"
                },
                //Social science
                new String[]{
                        "Select your Department"
                        ,
                        "Eocnomics"
                        , "Geography"
                        , "Political Science"
                        , "Sociology"
                },
                //Vet med
                new String[]{
                        "Select your Department"
                        ,
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
