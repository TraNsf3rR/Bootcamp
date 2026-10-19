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

        printAllStudentAvgGrades(studentGrades);
    }

//    Methods must be outside of main program running function.
//    Most important method whose job is to show all info in console.
    static void printAllStudentAvgGrades(int[][] grades)
    {
        int len_i = grades.length;
        for (int i = 0; i < len_i; i++)
        {
            System.out.println("Student " + i);
            System.out.printf("Grades [ ");

            int len_j = grades[i].length;

            for (int j = 0; j < len_j; j++)
            {
                System.out.printf("%d ", grades[i][j]);
            }

            System.out.printf("] - Average: %.2f", getAvg(grades[i]));
            System.out.println("");
        }
    }

//    Calculates avg from array of simple ints
    static float getAvg(int[] grades)
    {
        int count = grades.length;
        int sum = 0;
        for (int grade : grades)
        {
            sum += grade;
        }

        return (float) sum / count;
    }
}