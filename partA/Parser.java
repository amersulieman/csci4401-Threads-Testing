/***
 * @author Amer Sulieman
 * @Version 10/12/2018
 *
 * The class is made to a parse a file that contains two matrices
 * It will parse the string data and return two 2D array matrices of double type value
 */

import java.util.*;
import java.io.File;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;

class Parser{


    private double matrix1[][];                     //matrix1 after creation
    private double matrix2[][];                     //matrix2 after creation

    private int m1row=1;                            //matrix 1, how many rows! initially at 1 because last row counted!
    private int m1column=0;                         //matrix 1, how many columns! initially 0

    private int m2row=1;                            //matrix 2, how many rows!  initially 1 because last row counted!
    private int m2column=0;                         //matrix 2, how many columns! initially 0

    /**
     * After I get the right format for each matrix , they will be in a string each and then i use split() to
     * split the matrix by commas which will return values in an array, I use the following arrays to store
     * each matrix after being split by commas
     */
    private String matrix1StringSplitted[];
    private String matrix2StringSplitted[];

    /**
     * Constructor which will handle prsing the file and at the end, when the object is created,
     * Two 2D matrices are created with doubl type values in them.
     * @param File The file to parse
     * @throws FileNotFoundException Exception if the file not found.
     */
    public Parser(File file)throws FileNotFoundException{

        //The following array holds the array that fileParser returns, file parser is explained at the bottom
        String fileDataArray[] =fileParser(file);
        //String array holds matrix 1 splitted by comma
        matrix1StringSplitted = fileDataArray[0].split(",");
        //String array holds matrix 2 splitted by comma
        matrix2StringSplitted = fileDataArray[1].split(",");
        //measure deminsions of matrix 1 and update instance variables
        matrixDeminsions(1);
        //measure deminsions of matrix 1 and update instance variables
        matrixDeminsions(2);

        /**
         * The core of everything is here, after parsing everything, we need now to create the matrices
         * So they can contain values as double type. So mathematics can be applied later
         */
        matrix1= createMatrix(matrix1StringSplitted,m1row,m1column);// matrix 1 created finally
        matrix2 =createMatrix(matrix2StringSplitted,m2row,m2column);//matrix 2 created finally

    }//End of constructor

    /**
     * A method to return how many rows matrix1 has
     * @return number of rows matrix1 has
     */
    public int getM1RowSize(){
        return m1row;
    }

    /**
     * A method to return how many columns matrix1 has
     * @return  number of columns matrix 1 has
     */
    public int getM1ColumnSize(){
        return m1column;
    }
    /**
     * A method to return how many rows matrix2 has
     * @return number of rows matrix2 has
     */
    public int getM2RowSize(){
        return m2row;
    }
    /**
     * A method to return how many columns matrix2 has
     * @return  number of columns matrix 2 has
     */
    public int getM2ColumnSize(){
        return m2column;
    }
    /**
     * Method to return matrix1 created
     * @return matrix1
     */
    public double[][] getMatrix1(){
        return matrix1;
    }

    /**
     * Method to return matrix2 created
     * @return matrix2
     */
    public double[][] getMatrix2(){
        return matrix2;
    }

    /**
     * This method is responsible to parse the array i created with matrices. In the array i created, index 0 and index 1
     * contain matrix 1 and 2 as a string as a whole. So this method will parse each string to calculate how many
     * rows and columns each matrix needs to create the 2D array later to hold their values.
     * The process goes as follow:
     *
     * The method takes an input, 1 or 2 which will identify which matrix I want to calculate its deminsions needed
     * The instance variables for each matrix will be used such an example : variables -> m1row=1 m1column=0
     * so since each line feed was replaced by "$" sign then every time the parser find that sign it means there
     * was a new line and it means a new row needed to make the matrix 2D. the row for each matrix is initially 1
     * becasue there is not dollar sign at the end to count the last row.
     * So after getting the rows needed, while parsing if the dollar sign isnt found, i keep incrementing the
     * column value. Which at the end the column number will be equal to how many items the matrix has, if I divide
     * the number of items by the number of rows then i get my columns number because each row has same amount of elements.
     *
     * @param whichMatrix The decision which matrix to calculate.
     */
    private void matrixDeminsions(int whichMatrix){
        //If the method called with 1 then calculate for matrix 1 and update instance variables for matrix 1
        if(whichMatrix==1){
            //Since the array that has matrix 1 as a string splitted by commas, i use it to parse its values
            for(int i=0;i<matrix1StringSplitted.length;i++){
                //look for dollar sign which measn there was a new line there
                if(matrix1StringSplitted[i].equals("$")){
                    m1row+=1;//increment number of rows
                }
                //increment number of columns but its incrementing for every value
                else{m1column+=1;}
            }
            //because at this point m1column has number of items in the matrix, divide by the number of rows to get # of columns needed
            m1column/= m1row;
        }
        //Same steps as above happen but for matrix 2 if choice 2 was given.
        else if(whichMatrix==2){
            for(int i=0;i<matrix2StringSplitted.length;i++){
                if(matrix2StringSplitted[i].equals("$")){
                    m2row+=1;
                }
                else{m2column+=1;}
            }
            m2column/= m2row;
        }
        //check the option given.
        else{
            System.out.println("Enter 1 or 2 !!");
        }

    }//End of matrixDemisnions method

    /**
     * This method is responsible to grab the file and concatonate all of its data into a string variable
     * Then, since the file is sat up in a specific way such:
     * Two matrices separated by empty line AND each matrix's values are sepearated by commas except the
     * last value in each line. I take that string that holds all the data and split it by Empty line regex
     * which then i will have an array of values: (matrix 1) (matrix 2) and other data if the file has.
     * My only significance are the first two spots of the array which will contain a string of the whole matrices 1 and 2
     * as a whole. Then in each one of those strings that contain the whole matrix, i replace the "\n" line feed regex
     * with ",$," --> The reason is becasue i want to calculate how many of dollar signs occur, which will give me
     * how many rows i will have since dollar sign means new line in my case. and the commas between "$" is because when
     * I split the string by commas, i want the dollar signs to stay and the last value of each line will be separted from next line
     * since in the file format there was no comma to separate them.
     * @param file  The file to parse
     * @return  Returns a string essay which will contain matrix1 and 2 as string each foramted specific way
     * @throws FileNotFoundException    Exception if file not found
     */
    private String[] fileParser(File file)throws FileNotFoundException{
        //Scanner to read the file.
        Scanner input = new Scanner(file);
        //String variable that will hold the entire data of the file
        String fileData = "";
        //while loop concatenate each line in the file to the string variable.
        while(input.hasNextLine()) {
            //I add a new line feed because i want to keep the same format as the data was in the file.
            fileData += input.nextLine() + "\n";
        }

        /**
         * I split the data by Empty line becasue i want to have each matrix by it self.
         * The array will hold each matrix in string form as a whole
         */
        String dataSplit[] = fileData.split("\n\n");
        /**
         * I chose i<2 because if the file has other text beside the matrices then it will also be in the array
         * So i only care about index 0 and index 1 which will contain matrix 1 and matrix 2
         */
        for (int i =0; i<2;i++){
            /**
             * As i explained in the beginning of the method that i change each line feed to ",$," and also
             * delete the extra space between number.
             */
            dataSplit[i]=dataSplit[i].replaceAll("\n",",\\$,").replaceAll(" ","");
        }
        //Return the array which in index 0 will contain matrix1 as a string with "$" formated to and same to matrix 2
        return dataSplit;
    }//end of file parser method

    /***
     * Method to create a 2d matrix from a 1d array of strings. Each string is a digit string which gets converted to
     * a double value in the process of creating the 2D matrix.
     * @param source 1D string array with digits as strings
     * @param rows Number of rows for the 2D area to be created
     * @param columns   Number of Columns for the 2D array to be created
     * @return  2D matrix
     */
    private double[][] createMatrix(String[] source, int rows,int columns){

        /**
         * create 2D array that will hold the matrix values.
         * Giving it 2 instance variables of the class that holds
         * How much the rows and columns should be!!!
         */
        double dest[][] = new double[rows][columns];

        //This variable will be used as index value to index the 1D array
        int iter=0;
        /***
         * Nested loops are to copy the data from 1D array to 2D array
         */
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                /**
                 * Since the string array contains "$" That replaces the new line feed, if I reach that value break the
                 * loop which means it is time to increment 'i' which is the row number. Becuase if "$" is reached
                 * it means it is a new line which means time to insert at new row in the array
                 */
                if(source[iter].equals("$")){
                    iter++;//The index for 1D array still incremented because when the loop comes back I want it to be after the "$" sign
                    break;
                }
                /**
                 * If the value at the string array was not "$" then parse that number as a double in place it in
                 * appropriate value  in the 2D array.
                 */
                else{
                    dest[i][j]=Double.parseDouble(source[iter]);
                    iter++;//Index still incremented so it points to the next spot in the String array.
                }
            }
            /***
             * If i reach the max column number, the loop should break to start copying the next row, but if that happens
             * the 1D array index value isn't incremented because loop breaks automatically, so I increment it here to
             * point to the next spot in that array.
             */
            iter++;
        }
        //Return the matrix filled up.
        return dest;
    }//End of FILLARRAY method().
}