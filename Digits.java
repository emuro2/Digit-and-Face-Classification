import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Digits {
	
	
	//Likelihoods for each class (0-9)
	double [][][] likelihoods = new double [10][28][28];
	
	//array to keep track of how many times each number comes up in the traininglabels file
	int [] numbers = new int [10];
	
	//priors P(class). The empirical frequencies of the classes
	double [] frequencies = new double[10];
	
	//total number of numbers in the traininglabels 
	int total = 5000;
	
	
	//initializes the likelihood array that contains the likelihoods for each class (i.e. classes 0-9)
	public Digits() throws IOException 
	{
		
		FileReader inputStream = null;
		FileReader labels = null;
	    
	    try {
	        inputStream = new FileReader("digitdata/trainingimages");
	        labels = new FileReader("digitdata/traininglabels");

	        int c;
	        int index = 0;
	        
	        outerloop:
	        for(int i = 0; true; i++)
	        {
	        	//we need to read in the first number to be updated
	        	if(i == 0)
	        	{
	        		index = labels.read();
	        		
		        	//invalid read, or reached the end
		        	if(index == -1)
		        	{
		        		break outerloop;
		        	}
	
		        	
		        	//read in the new line character, skip it
		        	else if(index == 10)
		        	{
		        		index = labels.read();
		        	}
	
		        	//update the index, because read() returns an int (ascii format)
		        	index = index-'0';
		        	//total++;
	        	}
		        	
				for(int j =0; j < 29; j++)
				{
					c =  inputStream.read();
					
					//invalid read or end of the file
					if(c == -1)
					{
						//update number of times this number has showed up
						numbers[index]++;
						break outerloop;
					}	
					
	    			//foreground
					else if(c == 35 || c == 43)
	    			{
	    					 likelihoods[index][i%28][j]++; 
	    			}
					//background, else its a background		
				}
				
				//new letter, need a new index
				if(i%28 == 0 && i != 0)
				{
					//update how many times this number has showed up
					numbers[index]++;
					
	        		//new number
					index = labels.read();
	        		
		        	//invalid read, or reached the end
		        	if(index == -1)
		        	{
		        		break outerloop;
		        	}
	
		        	//read in the new line character, skip it and read again
		        	else if(index == 10)
		        	{
		        		index = labels.read();
		        	}
	
		        	//index is in ascii format...subtract the offset of 0
		        	index = index-'0';
		        	//total++;
				}
	
			}
	
		}//end of reading in from files, finished calculating likelihoods
	    
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
	
	
	}//end of Digits Constructor
	
	
	//estimate the likelihoods P(F_ij | class), and adds smoothing. As well as calculates the frequencies 
	public void updateLikelihoods()
	{
		//i = the number(class) we are going to update
		for(int i =0; i < 10; i++)
		{
			//calculate the frequencies of each number
			frequencies[i]= (double)(numbers[i])/(total);

			//updates the likelihoods
			for(int j =0; j < 28; j++)
			{
				for(int k =0; k < 28; k++)
				{
					//smooth the likelihoods to ensure that there are no zero counts.
					//adding a constant K to each pixel
					likelihoods[i][j][k] = likelihoods[i][j][k]+1;
					
					//k*V to the denominator  (where V is the number of possible values the feature can take on)
					likelihoods[i][j][k] = (double)(likelihoods[i][j][k])/(1*numbers[i]);
					
				}
			}
		}
	}
	

}//end of class Digits
