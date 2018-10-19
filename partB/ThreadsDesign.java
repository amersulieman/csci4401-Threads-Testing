/***
 * @Author Amer Sulieman
 * @Version 10/16/2018
 *
 * @Description:
 *  This class is responsible for creating threads that the user specify. Each thread created will have its own queue
 *  for the rows and columns to process. Also the thread will solve the multiplication for the rows and columns it
 *  is assigned to work on.
 *
 */

import java.util.*;
class ThreadsDesign extends Thread {
    private int thread_Id;                      //Holds the thread Id
    private Queue<Integer> rowsQueue;           //Holds the rows that the thread will be working on
    private Queue<Integer> columnsQueue;        //Hold the columns that the thread will be working on corresponding to the rows
    private  double matrix1[][];                //The first matrix that the thread is working on
    private double matrix2[][];                 //The second matrix that the thread is working on
    private double result[][];                  //The matrix that will hold the result of multiplying the first and second matrix

    public ThreadsDesign(int my_id, Queue<Integer> queueForRows, Queue<Integer> queueForColumns , double[][] firstMatrix, double[][] secondMatrix, double[][] solution){
        thread_Id = my_id;                  //Thread Id
        rowsQueue = queueForRows;           //queue for rows the thread works on
        columnsQueue = queueForColumns;     //queue for the columns the thread should access
        matrix1 = firstMatrix;              //The first matrix the thread will work on
        matrix2 = secondMatrix;             //the second matrix that the thread will work on
        result = solution;                  //after multiplying both matrices, the resulting matrix is here
    }

    public void run(){

        int rowSpot;    //The row number that the thread will work on
        int columnSpot; //The column number that the thread will work on
        /**
         * Number of elements to be multiplied is the same as matrix2 number of rows. Which is the same number as
         * matrix1 number of columns, That is how much the iterator should loop for each cell it needs to solve.
         */
        int numberOfElementsToMultiply = matrix2.length;
        /**
         * A variable to keep track of the multiplication because we are multiplying a whole row with a whole column
         * so when we loop we need to add each multiplication with the next one for that row and column.
         */
        double multiplicationResult;

        /**
         * The loop will keep looping until the thread has no more cells to work on.
         * WEhich is if the thread's queue for the rows and columns are empty, it means it has no more work.
         */
        while(!(rowsQueue.isEmpty() && columnsQueue.isEmpty())) {
            rowSpot = rowsQueue.remove();       //retrieve the cell row number to work on.
            columnSpot = columnsQueue.remove(); //retrieve the cell column number to work on.
            //Print the thread thats working and the [row][column] ot is working on. thread+! is becasue the array start at index 0
            multiplicationResult = 0;   //Set the multipliaction result variable to 0 because each row*column should be its unique result.
            /**
             * The iterator will loop as long as there is numbers between the row and the column to multiply.
             * When it is done, the row*column result will be stored in the variable multiplicationResult variable.
             * So then it will be moved to the result matrix.
             * Since thr row number stays the same for each multipliaction, then we need to be changing the column
             * number when we multiply.
             */
            for (int iterator = 0; iterator < numberOfElementsToMultiply; iterator++) {
                //Row is the same for matrix 1 but loop through its values, The column is the same in matrix 2 but loop its values.
                multiplicationResult += (matrix1[rowSpot][iterator] * matrix2[iterator][columnSpot]);
            }
            //When multipliaction is done for the whole row and column, move it to the resulting matrix for that specific
            //Rowspot and column that the thread was working on.
            result[rowSpot][columnSpot] = multiplicationResult;
        }
    }
}



