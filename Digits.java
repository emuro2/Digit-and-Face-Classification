import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.*;
import java.util.Arrays;

public class Digits {
	
	
	//Likelihoods for each class (0-9)
	double [][][] likelihoods = new double [10][28][28];
	
	//array to keep track of how many times each number comes up in the traininglabels file
	int [] numbers = new int [10];
	
	//priors P(class). The empirical frequencies of the classes
	double [] frequencies = new double[10];
	
	//total number of numbers in the traininglabels 
	int total = 5000;
	
	
	//new image from test images (new number)
	int [][] image = new int[28][28];
	
	int actualNum = 0;
	
	double percent = 0;
	double count = 0;
	
	public void initLikelihoods()
	{
		//smooth the likelihoods to ensure that there are no zero counts.
		//adding a constant K to each pixel
		Arrays.fill(likelihoods[0][0],1);
		Arrays.fill(likelihoods[0],likelihoods[0][0]);
		Arrays.fill(likelihoods,likelihoods[0]);		
	}
	
	//initializes the likelihood array that contains the likelihoods for each class (i.e. classes 0-9)
	public Digits() throws IOException 
	{
		initLikelihoods();
	    
		FileReader inputStream = new FileReader("digitdata/trainingimages");
        FileReader labels = new FileReader("digitdata/traininglabels");

        int c, index = 0;
        
        outerloop:
        for(int i = 0; true; i++) {
        	//we need to read in the first number to be updated
        	if(i == 0) {
        		do {
	        		index = labels.read();
		        	if (index == -1) { 
		        		break outerloop; //end of file reached
		        	}
		        	index = index-'0'; //update the index, because read() returns an int (ascii format)
        		} while (index < 0); //if we read a character that wasn't a number, re-read
        		
        		System.out.println(index);
        	}
        	
			for(int j =0; j < 28; j++) {
				c = inputStream.read();
				
				switch (c) {
				case -1: //invalid read or end of the file
					//update number of times this number has showed up
					numbers[index]++;
					break outerloop;	
				case 35: //foreground
				case 43: //foreground
					likelihoods[index][i%28][j]++;
					break;
				default: //else its the background	
					break;
				}		
			}
			
			//new letter, need a new index
			if(i%28 == 0 && i != 0) {
				//update how many times this number has showed up
				numbers[index]++;
				
        		//new number
        		do {
	        		index = labels.read();
		        	if (index == -1) { 
		        		break outerloop; //end of file reached
		        	}
		        	index = index-'0'; //update the index, because read() returns an int (ascii format)
        		} while (index < 0); //if we read a character that wasn't a number, re-read
        		System.out.println(index);
			}
		}
		//end of reading in from files, finished calculating likelihoods 
	    inputStream.close();
	    labels.close();
	}//end of Digits Constructor
	
	
	//estimate the likelihoods P(F_ij | class), and adds smoothing. As well as calculates the frequencies 
	public void updateLikelihoods()
	{
		//i = the number(class) we are going to update
		for(int i =0; i < 10; i++)
		{
			//calculate the frequencies of each number(Prior P(class))
			frequencies[i]= (double)(numbers[i])/(total);

			//updates the likelihoods
			for(int j =0; j < 28; j++)
			{
				for(int k =0; k < 28; k++)
				{		
					//each class has around 500 samples in the trainingimages file
					likelihoods[i][j][k] = (double)(likelihoods[i][j][k])/(500);					
				}
			}
		}
	}
	
	
	
	//help debug likelihoods 
	public void printLikelihoods()
	{
		
				for(int i =0; i < 10; i++)
				{

				
					for(int j =0; j < 28; j++)
					{
						for(int k =0; k < 28; k++)
						{
							//a threshold to see what the likelihoods look like
							if(likelihoods[i][j][k] > .5)
								System.out.print("#");
							else 
								System.out.print(" ");
						}
						System.out.println();
					}
					System.out.println();
					System.out.println();
					
				}
	}
	
	//gets new image from testimages file
	public void getNewImage() throws IOException
	{
		FileReader inputNumber = null;
		FileReader actualNumber = null;
	    
	    try {
	        inputNumber = new FileReader("digitdata/testimages");
	        actualNumber = new FileReader("digitdata/testlabels");

	        int c;
				
	        outerloop:
	        for(int i = 0; true; i++)
	        {		 
				if(i %28 == 0 && i != 0)
				{
					actualNum =  actualNumber.read();
					//invalid read, or reached the end
		        	if(actualNum == -1)
		        	{
		        		break outerloop;
		        	}
	
		        	//read in the new line character, skip it and read again
		        	else if(actualNum == 10)
		        	{
		        		actualNum = actualNumber.read();
		        	}
	
		        	//index is in ascii format...subtract the offset of 0
		        	actualNum = actualNum-'0';
				}
				for(int j =0; j < 29; j++)
				{
	
					c =  inputNumber.read();
		
					
					//invalid read or end of the file
					if(c == -1)
					{
						pickClass(c);
						break outerloop;
						
					}	
					
	    			//foreground
					else if(c == 35 || c == 43)
	    			{
						//System.out.print("#");
	    				image[i%28][j] = 1;
	    			}
					//background, else its a background	
					else if(c == 32)
					{
						//System.out.print(" ");
						image[i%28][j]= 0;
					}
				}
				//System.out.println();
				
				//we have a complete number, we should decide what number it is
				if(i %28==0 && i !=0)
					pickClass(1);
				
	
			}
	
		}//end of reading in from testimages
	    
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	//picks a class for a particular test case. (estimates)
	public void pickClass( int input)
	{
		double[] probabilities = new double[10];
		int best = 0;
		
		//i = the number(class) we are going to estimate
		for(int i =0; i < 10; i++)
		{
			probabilities[i]= probabilities[i] + (Math.log(frequencies[i]) );
			
			for(int j =0; j < 28; j++)
			{
				for(int k =0; k < 28; k++)
				{
					probabilities[i] = probabilities[i] + ( image[j][k] * Math.log(likelihoods[i][j][k]) );
				}
			}
			
			//System.out.println(i+": "+ probabilities[i]);
			if(probabilities[i] > probabilities[best])
				best = i;
			
		}
		
		//System.out.println("Best = "+ best + "  actual: "+actualNum);
		if(best == actualNum)
		{
			percent++;
			
		}
		count++;
		if(input ==-1)
		{
			System.out.println("Digit Classification: ");
			System.out.println("Count: " +percent+" / "+count);
		
			System.out.println("percent: "+ (double)(percent/count)*100);
			System.out.println();
		}
	}
	
	
	
	
	

}//end of class Digits
