/**
 * A class that multiplies matrices, sequentially and using user-level threads
 * @author Amer Sulieman
 * @version 10/18/2018
 */

import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

class matrixMul
{
	public static void main(String[] args)throws FileNotFoundException{
	    //Checks if the program arguments are less than 2, then throw error if they are!
        if(args.length<2){
            System.out.println("Program requires 2 arguments: < Number of Threads> < File to Parse>");
            System.exit(0);
        }
	    File file = new File(args[1]);//file to read!!
        Parser parsed  = new Parser(file);   //Parser class is used to parse the file given as an input
        /**
         * After the text file had been parsed, two matrices are created with 'double' values
         * The following 2D arrays have first and second matrix.
         */
        double matrix1[][]=parsed.getMatrix1();                     //MATRIX 1
        int matrix1NumRows =  parsed.getM1RowSize();                //Matrix 1 number of rows
        int matrix1NumColumns = parsed.getM1ColumnSize();           //Matrix 1 number of columns

        double matrix2[][]=parsed.getMatrix2();                     //Matrix 2
        int matrix2NumRows =  parsed.getM2RowSize();                //Matrix 2 number of rows
        int matrix2NumColumns = parsed.getM2ColumnSize();           //Matrix 2 number of columns

        // check if the first and second matrix inner sizes match
        if(matrix1NumColumns!=matrix2NumRows){
            System.out.println("The matrices should have equal inner params! Matrix 1 number of columns and matrix 2 number of rows.");
            System.exit(0);
        }
        System.out.println("\t\tMatrix1");
        printMatrix(matrix1);
        System.out.println("\t\tMatrix2");
        printMatrix(matrix2);
        System.out.println();

        //Array that will hold the multiplication results
        double result[][] = new double[matrix1NumRows][matrix2NumColumns];
        //Number of jobs in total will be the number of cells the result 2d array will have
        //which is matrix1 number of rows * matrix 2 number of columns
        int jobs = matrix1NumRows*matrix2NumColumns;
        boolean fairjobs=true;                                      //boolean variable if the jobs per threads will be distributed evenly
        int threads = Integer.parseInt(args[0]);                    //How many threads asked to do given by user!
        //check if the threads given more than number of jobs! It can not exceed number of jobs

        if(threads>jobs) {
            System.out.println("The threads CAN NOT exceed the number of jobs!!\n\tSome Threads will be jobless.\n\t You have " + jobs + " jobs.");
            System.exit(0);
        }
        //If invalid number of threads given
        else if(threads<=0){
            System.out.println("Give a valid number for the threads! Need more than 0 and less than the number of jobs!!\n\t Number of jobs = "+jobs);
            System.exit(0);
        }
        //If one thread given then run sequential, no jobs need to be distributed
        else if(threads==1){
            System.out.println("The job is ran sequentially because you wanted 1 thread!!!!\n");
            System.out.println("\tProcessing in sequential........");
            /**
             * columnSpot variable is used to know the column spot in the resulting multiplication, this will be resat everytime the end
             * of a row's column is reached. because when we store in the next row, then the column should start at
             * the beginning of that row.
             */
            int columnSpot;
            double multiplicationResult;        //variable to hold multiplication result of row*column
            //loop for every row
            for(int matrix1row=0;matrix1row<matrix1NumRows;matrix1row++){
                //start at column 0
                columnSpot=0;
                //loop for every column in thaat row
                for(int matrix2Column=0;matrix2Column<matrix2NumColumns;matrix2Column++){
                    System.out.printf("Multiplying for result array cell[%d][%d]..\n",matrix1row,matrix2Column);
                    //start result variable = 0
                    multiplicationResult=0;
                    //loop every number in that row
                    for(int z=0;z<matrix1NumColumns;z++){
                        /**
                         * The multiplication needs to multiply each rows number with the corresponding spot
                         * in the column of the second matrix.
                         * So loop through first row first number in matrix 1, multiply it with first column first number
                         * in matrix 2, then move to the next number in that first row and then move to the next number
                         * in that column, after all these have been looped through, we have the result for that cell.
                         */
                        multiplicationResult+=( matrix1[matrix1row][z] * matrix2[z][matrix2Column] );
                    }
                    //place the result of that cell in the appropriate spot in resulting matrix
                    result[matrix1row][columnSpot]=multiplicationResult;
                    columnSpot++;//increase column spot because next row will multiply next column
                }
            }
        }
        //Which means more than one thread was given so run parallel.
        else{
            System.out.println("There are "+threads+" Threads Allocated!\n");
            System.out.println("Processing in parallel.......");
            /**
             * The following queues will hold the row numbers and columns numbers each thread needs to accomplish
             * The matrices are 2D arrays so when a thread needs to calculate result of [row-x][column-y]
             * Then the row number gets pushed to the queue and the column number gets pushed to the second queue
             */
            Queue<Integer> rowsqueue;       //queue for the row that the thread needs to be on
            Queue<Integer> columnsqueue;    //queue for the column that the thread needs to be on.

            //This thread array will hold the threads that will be doing the work.
            ThreadsDesign [] mythreads = new ThreadsDesign[threads];
            //The following variables will be used to calculate each[row][column] each thread needs to work on.
            int row=0;              //start at row 0
            int column=0;           //column 0

            //if the jobs / threads is evenly distributed, set that variable to true so the method distribute jobs evenly
            if(jobs%threads==0){
                fairjobs=true;
                //If the jobs/ threads is not evenly distributed, set that variable to flase so the method takes care of uneven jobs distribution
            }else if(jobs%threads!=0){
                fairjobs=false;
            }

            //The following array holds each thread and how much jobs it needs to do. distribution taken care of in method call.
            int threadsJobs[] =distributeJobs(jobs,threads,fairjobs);
            for(int i=0;i<threadsJobs.length;i++){
                //Print each thread and how many jobs it has/ (i+1)because array counts start from 0
                System.out.println("Thread: "+(i+1)+" Number of Jobs = "+threadsJobs[i]);
            }
            System.out.println();

            //loop for all the threads
            for(int id=0;id<threads;id++){
                //for each thread create rowsQueue and columnsQueue that each thread needs to do
                rowsqueue = new LinkedList<>();
                columnsqueue = new LinkedList<>();
                //loop for the number of jobs that the thread has to do
                for(int loadId=0;loadId<threadsJobs[id];loadId++){
                    //this selection statment is used in case the thread reaches end of column of the matrix, and it still needs
                    //to do the next [row][column] which is the first column in the next row
                    if(column==matrix2NumColumns){
                        column=0;//reset the column to 0 because it will be the first cell in the next row
                        row+=1;//increment the row number which will be next row that the thread needs to work on
                    }
                    //add the [row][column] that the thread needs to work on to the queues
                    rowsqueue.add(row);
                    columnsqueue.add(column);
                    //increment the column number so next loop it will be pointing to next column
                    column+=1;
                }
                //After looping to get all the cells for the thread to work on, start the thread work process.
                mythreads[id]=new ThreadsDesign(id,rowsqueue,columnsqueue,matrix1,matrix2,result);
                mythreads[id].start();
            }

            //Using barrier to gather all the threads after they done to say that everything is over
            try {
                for (int i=0; i < threads; i++) {
                    mythreads[i].join();
                }
            }
            catch (InterruptedException e){}

            System.out.println("Everyone meets here after being done!!!") ;

        }
        //method call to print the resulting matrix
        System.out.println("\n\tResult of multiplying Matrix 1 * Matrix 2");
        printMatrix(result);

    }//End of main

    /**
     * This method is built to distribute the load of work for each thread.
     * If we needed more than 1 thread then we need to distribute job on them,
     * if the number of jobs is even then just create an array for each thread spot
     * and fill that number of jobs, if number of threads is not even(which is jobs/number of threads)
     * then the array will be used for each thread spot will be filled for how many jobs it needs to accomplish
     * IMPORTANT!!!!!!!!!!!!
     * The concept is simple, if the jobs arent distributed evenly:::::
     *      For the thread start by jobs/number of threads= if it needs to be rounded up, then do
     *      and make that the number of jobs. keep track of how much been rounded up by so
     *      the next thread can be rounded by that value.
     *      So on.
     * @param jobs The number of jobs in total to work on(Which is the resulting matrix number of cells)
     * @param threads The number of threads required to do the job
     * @param fairJobs  If the number of jobs will be evenly distributed or not
     * @return  Return an array for the threads and each spot for each thread has the number of jobs it will accomplish
     */
    public static int[] distributeJobs(int jobs,int threads,boolean fairJobs) {

        //An array that will be held to store each thread's number of jobs
	    int threadsJobsDistribution[] = new int[threads];
        //When this method is called, it will be told if the number of jobs is evenly distributed or not
        //If it is, then each spot in the array will be holding same number of jobs
        if(fairJobs==true){
            //Loop through the array to fill each spot with number of jobs
            for(int i=0;i<threads;i++){
                //jobs/threads = the number of jobs each thread will need to work on
                threadsJobsDistribution[i]=jobs/threads;
            }
        }

        //If the jobs are not going to be distributed evenly then some threads going to do more work than others.
        else if(fairJobs!=true){
            //create a variable that will hold the uneven job divison by threads
            double unevenJobSplit = (double) jobs / threads;
            //This variable is used to see if the job number needs to be rounded up or not
            double result = 0;
            //The decimal part of the number of job
            double decimalPart;
            //The Integer(wholeNumber side) of the numbers of jobs
            int wholeNumberPart;
            //This variables stores how much had been rounded up or how much have been rounded down by, to keep track
            // for the next threads how much to round down or round up by. This way load distributed randomly.
            double changeLeft = 0;
            //Boolean variable to say if I rounded up or not. Initially starts false.
            boolean roundUp = false;

            //Loop for every thread spot in the array
            for (int i = 0; i < threadsJobsDistribution.length; i++) {
                //If I didn't round up, then make th number of jobs = uneven jobs number + how much lost when rounded down.
                if (roundUp == false) {
                    result = unevenJobSplit + changeLeft;
                //If i rounded up then the number of jobs = the uneven number of jobs - how much used to round up.
                } else if (roundUp == true) {
                    result = unevenJobSplit - changeLeft;
                }
                //The integer side of the jobs to do
                wholeNumberPart = (int) result;
                //The decimal side of the jobs to do
                decimalPart = (double) result - wholeNumberPart;

                //The decision to round up or not is if the decimal side of the jobs is higher or equal to .5
                if (decimalPart >= 0.5) {
                    //IF I ROUND UP, Then just increase the integer side of the number of jobs
                    wholeNumberPart += 1;
                    //Make that integer side, the number of jobs that specific thread needs to do
                    threadsJobsDistribution[i] = wholeNumberPart;
                    //CHnagelEFT is how much we rounded up by
                    changeLeft = (double) wholeNumberPart - result;
                    //turn the roundup value to true because we rounded up
                    roundUp = true;
                //If the decimal side is less than .5 then don't round up and it mean round down and that will be number of jobs for that thread
                } else {
                    //that thread gets the number of jobs rounded down
                    threadsJobsDistribution[i] = wholeNumberPart;
                    //the changeleft is how much we rouned down by
                    changeLeft = decimalPart;
                    //make the round up variable to false becuase we didn't round up
                    roundUp = false;
                }
            }
        }
        return threadsJobsDistribution;

    }

    /**
     * A method that will help printing any 2D matrix given to it
     * @param matrixToPrint The matrix to print which is in 2D array
     */
    public static void printMatrix(double[][] matrixToPrint){
        //Loop each row
        for(int i=0;i<matrixToPrint.length;i++){
            //for each row, loop each column
            for(int j=0;j<matrixToPrint[0].length;j++){
                //if we reached the last value then a comma will not be needed
                //this is to help convert to csv files
                if (j == matrixToPrint[0].length - 1) {
                    System.out.printf(" %f", matrixToPrint[i][j]);

                } else {
                    System.out.printf(" %f,", matrixToPrint[i][j]);

                }
            }
            //print new line after every row increment
            System.out.println();
        }
    }
}//End of class