/**
 * @author Amer Sulieman
 * @version 10/18/2018
 * This class meaures the performance of sequential and parallel solving matrix multiplication
 */

import java.util.*;

class MatmultPerformance {
    public static void main(String[] args) {
        double sequentialperforamnce[] = new double[51];
        double parallelperformance[] = new double[51];

        /**
         * The following loop is to record the performacne for each matrices multiplication
         * of sizes n=2 to 50. the performances are inserted into an array.
         * The array values i start at 2 to match the matrix size with the performance it resulted into.
         */
        if(Integer.parseInt(args[0])==1){
            performanceTest(sequentialperforamnce,1);
        }else if(Integer.parseInt(args[0])==2){
            performanceTest(parallelperformance,2);
        }


    }

    /**
     * A method that can be ran to choose which performance to test to run and test
     * @param performance   The array that will hold the performances fro the matrices
     * @param whichTest     1 for sequential test , 2 for parallel test
     */
    public static void performanceTest(double[] performance, int whichTest){
        if(whichTest == 1){
            for(int n=2;n<=50;n++){
                //performance for the matrices multiplication of size n
                double time=solveSequential(n);
                //insert the performance of the matrix size n to the array where it match the size
                performance[n]=time/1000;
            }
            //method to print performances
            System.out.println("N-Threads, Performance");
            printPerformance(performance);
        }
        else if(whichTest==2){
            for(int n=2;n<=50;n++){
                //performance for the matrices multiplication of size n
                double time=solveparallel(n,n);
                //insert the performance of the matrix size n to the array where it match the size
                performance[n]=time/1000;
            }
            //method to print performances
            System.out.println("N-Threads, Performance");
            printPerformance(performance);
        }
        else{
            System.out.println("Enter appropriate test number:\n\t1:Sequential!    Or      2:parallel!");
            System.exit(0);
        }
    }

    /**
     * A method that solves matrices multiplication (for size n * n) parellel using threads
     * The method records the performance of the multipliaction, The multiplication of matrices
     * gets ran 1000 times to get accurate time/1000 which will be the average time for thread run
     * The performance test includes storing the result into a 2D array
     * @param size  matrix size n*n
     * @param numOfthreads That is how many threads to use
     * @return  returns the average performance of solving matrices multiplication n*n for 100 time run
     */
    public static double solveparallel(int numOfthreads,int size){
        /**
         * These 2D arrays are used to get populated which will also be used during the performance test
         * the performance test includes storing the result in the 2D array
         */
        double firstMatrix[][];             //first matrix
        double secondMatrix[][];            //second matrix
        double result[][] =new double[size][size];  //result of multiplication
        /**
         * The following queues will hold the row numbers and columns numbers each thread needs to accomplish
         * The matrices are 2D arrays so when a thread needs to calculate result of [row-x][column-y]
         * Then the row number gets pushed to the queue and the column number gets pushed to the second queue
         */
        Queue<Integer> rowsqueue;       //queue for the row that the thread needs to be on
        Queue<Integer> columnsqueue;    //queue for the column that the thread needs to be on.
        int loadWork=(size*size)/numOfthreads;  //This is the load for each thread which is the cells numbers in reuslt /num of threads

        double performance =0;                //variable to add up the performances to return the average of 1000 runs
        double startTime=0;                   //will be used to calculate the time
        double endTime=0;                     //will be used to calculate the time

        /**
         * The follwing loop will run the matrix multiplication 1000 times, each time the performance will be recorded
         * also each time I loop, the matrices get repopulated with new random values to avoid cache advantage
         * The time starts recording in nanoseconds after the matrices get populated and ends after the matrices gets
         * multiplied and stored somewhere as result, the performance is added to a variable and then at the end
         * this method return that performance variable divided by 1000 to get the average of how long it takes
         */
        for(int i=0;i<100;i++){
            firstMatrix= matrixPopulate(size);          //matrix 1 is populated with random numbers
            secondMatrix=matrixPopulate(size);          //matrix 2 gets populated with random data

            //This thread array will hold the threads that will be doing the work.
            ThreadsDesign [] mythreads = new ThreadsDesign[numOfthreads];
            //The following variables will be used to calculate each[row][column] each thread needs to work on.
            int row=0;              //start at row 0
            int column=0;           //column 0

            startTime=System.nanoTime();                //record the start time
            //loop for all the threads
            for(int id=0;id<numOfthreads;id++){
                //for each thread create rowsQueue and columnsQueue that each thread needs to do
                rowsqueue = new LinkedList<>();
                columnsqueue = new LinkedList<>();
                //loop for the number of jobs that the thread has to do
                for(int loadId=0;loadId<loadWork;loadId++){
                    //this selection statment is used in case the thread reaches end of column of the matrix, and it still needs
                    //to do the next [row][column] which is the first column in the next row
                    if(column==size){
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
                mythreads[id]=new ThreadsDesign(id,rowsqueue,columnsqueue,firstMatrix,secondMatrix,result);
                mythreads[id].start();
            }

            //Using barrier to gather all the threads after they done to stop the time
            try {
                for (int j=0; j < numOfthreads; j++) {
                    mythreads[j].join();
                }
            }
            catch (InterruptedException e){}
            //record end time
            endTime=System.nanoTime();
            //performance is endtime - start time
            performance+=(endTime-startTime);
        }

        //after 1000 runs return the value divided by 1000 to get the average performance
        return performance/100;
    }

    /**
     * A method that solves matrices multiplication (for size n * n) sequentially
     * The method records the performance of the multipliaction, The multiplication of matrices
     * gets ran 1000 times to get accurate time/100 which will be the average time for sequential run
     * The performance test includes storing the result into a 2D array
     * @param size  matrix size n*n
     * @return  returns the average performance of solving matrices multiplication n*n for 100 time run
     */
    public static double solveSequential(int size){
        /**
         * These 2D arrays are used to get populated which will also be used during the performance test
         * the performance test includes storing the result in the 2D array
         */
        double firstMatrix[][];             //first matrix
        double secondMatrix[][];            //second matrix
        double result[][] =new double[size][size];  //result of multiplication

        double performance =0;                //variable to add up the performances to return the average of 1000 runs
        double startTime=0;                   //will be used to calculate the time
        double endTime=0;                     //will be used to calculate the time

        /**
         * The follwing loop will run the matrix multiplication 1000 times, each time the performance will be recorded
         * also each time I loop, the matrices get repopulated with new random values to avoid cache advantage
         * The time starts recording in nanoseconds after the matrices get populated and ends after the matrices gets
         * multiplied and stored somewhere as result, the performance is added to a variable and then at the end
         * this method return that performance variable divided by 1000 to get the average of how long it takes
         */
        for(int i=0;i<100;i++){
            firstMatrix= matrixPopulate(size);          //matrix 1 is populated with random numbers
            secondMatrix=matrixPopulate(size);          //matrix 2 gets populated with random data

            startTime=System.nanoTime();                //record the start time
            /**
             * columnSpot variable is used to know the column spot in the resulting multiplication, this will be resat everytime the end
             * of a row's column is reached. because when we store in the next row, then the column should start at
             * the beginning of that row.
             */
            int columnSpot;
            double multiplicationResult;        //variable to hold multiplication result of row*column
            //loop for every row
            for(int matrix1row=0;matrix1row<size;matrix1row++){
                //start at column 0
                columnSpot=0;
                //loop for every column in thaat row
                for(int matrix2Column=0;matrix2Column<size;matrix2Column++){
                    //start result variable = 0
                    multiplicationResult=0;
                    //loop every number in that row
                    for(int z=0;z<size;z++){
                        /**
                         * The multiplication needs to multiply each rows number with the corresponding spot
                         * in the column of the second matrix.
                         * So loop through first row first number in matrix 1, multiply it with first column first number
                         * in matrix 2, then move to the next number in that first row and then move to the next number
                         * in that column, after all these have been looped through, we have the result for that cell.
                         */
                        multiplicationResult+=( firstMatrix[matrix1row][z] * secondMatrix[z][matrix2Column] );
                    }
                    //place the result of that cell in the appropriate spot in resulting matrix
                    result[matrix1row][columnSpot]=multiplicationResult;
                    columnSpot++;//increase column spot because next row will multiply next column
                }
            }//end of solving matrices
            //record end time
            endTime=System.nanoTime();
            //performance is endtime - start time
            performance+=(endTime-startTime);
        }
        //after 1000 runs return the value divided by 1000 to get the average performance
        return performance/100;
    }

    /***
     * A method to populate matrices randomly. The min and max will also be sat randomly.
     * The max will be be in range of 100
     * the min will be in range of 20.
     * Every time I loop, the min and max changes and the random number is chosen mathematically so
     * min can go to the negtaive. And then a double chosen and added to integer to have total random number.
     * @param size  The size of the matrix n*n
     * @return return a 2D matrix with size n*n given
     */
    public static double[][] matrixPopulate(int size){
        int max;    //Max range
        int min;    //Min range
        Random rand = new Random(); //get random object class

        int wholeNumberOnly;    //This will build the integer side of the random number
        double finalNumber;     //This will have the integr side and the decimal side
        double [][] matrix = new double[size][size];    //A matrix 2d array

        /**
         * I loop for each row and column in the 2D array to fill it with total random number.
         */
        for(int row=0;row<size;row++){
            for(int column=0;column<size;column++){
                //set max range random every time between 0 and 100
                max = rand.nextInt(100);
                //The min range will be a random number between 0 to 20 * -1 so we can reach negative
                min= -1 * rand.nextInt(20);
                //This will have the intger side
                wholeNumberOnly = rand.nextInt(max+1-min)+min;
                //This will contain the whole double number
                finalNumber= (double)rand.nextDouble()+wholeNumberOnly;
                //fill the matrix with each row-column
                matrix[row][column]=finalNumber;
            }
        }
        return matrix;
    }

    /**
     * A method that will print the perfromance array. The array contains the performance for each matrix of n
     * @param seqORpar The array of performance(sequential performance or parallelPerformance
     */
    public static void printPerformance(double[] seqORpar){
        //Loop through the array and print comma seperated values
        for(int i =2;i<seqORpar.length;i++){
            //Print the n which is size of matrix n*n and the performance comma sperated values
            System.out.printf("%d,%.2f\n",i,seqORpar[i]);
        }
    }
}