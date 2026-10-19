package org.example;

public class Main {
    public static void main(String[] args) {

        int[][] studentGrades = {
                {4, 5, 8, 10},
                {7, 9, 10},
                {7, 9, 10, 3, 6},
                {7, 9, 3, 7},
                {8, 9, 5, 3, 3}
        };

        getAllStudentAvgGrades(studentGrades);
    }

//    Methods must be outside of main program running function.
    static void getAllStudentAvgGrades(int[][] grades)
    {
        for (int[] grade : grades)
        {
            for (int j = 0; j < grade.length; j++)
            {
                System.out.println(grade[j]);
            }
            System.out.println(" ");
        }
    }
}