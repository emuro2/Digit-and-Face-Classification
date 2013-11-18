	
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.*;


public class Faces {
		
		//Likelihoods for each class (0-9)
		double [][][] likelihoods = new double [1][70][60];
		
		//keep track of how many times a face comes up in the training labels file
		int numberOfFaces;
		
		//priors P(class). The empirical frequencies of the classes
		double frequencies;
		
		//total number of numbers in the traininglabels 
		int total = 451;
		
		
		//new image from test images (new number)
		int [][] image = new int[70][60];
		
		int actualNum = 0;
		
		double percent = 0;
		double count = 0;
		
		
		
		
		public void initLikelihoods()
		{


			for(int j =0; j < 28; j++)
			{
				for(int k =0; k < 28; k++)
				{
					//smooth the likelihoods to ensure that there are no zero counts.
					//adding a constant K to each pixel
					likelihoods[0][j][k] = 1;
				}
			}			
	
			
		}
		
		
		
		//initializes the likelihood array that contains the likelihoods for each class (i.e. classes 0-9)
		public Faces() throws IOException 
		{
			initLikelihoods();
			
			FileReader inputStream = null;
			FileReader labels = null;
		    
		    try {
		        inputStream = new FileReader("facedata/facedatatrain");
		        labels = new FileReader("facedata/facedatatrainlabels");

		        int c;
		        int face = 0;
		        
		        outerloop:
		        for(int i = 0; i < 500; i++)
		        {
		        	//we need to read in the first number to be updated
		        	if(i == 0)
		        	{
		        		face = labels.read();
		
			        	//update the index, because read() returns an int (ascii format)
			        	face = face-'0';
			        	//total++;
		        	}
			        	
					for(int j =0; j < 61; j++)
					{
						c =  inputStream.read();
						
						//invalid read or end of the file
						if(c == -1)
						{
							//update number of times this number has showed up
							numberOfFaces++;
							break outerloop;
						}	
						
		    			//foreground
						else if(c == 35 )
		    			{
		    					 likelihoods[0][i%60][j]= likelihoods[0][i%60][j]+1; 
		    					 System.out.print("#");
		    			}
						//background, else its a background		
						else
						{
							System.out.print(" ");
						}
					}
					System.out.println();
					//new letter, need a new index
					if(i%70 == 0 && i != 0)
					{
						System.out.println();
						System.out.println();
						System.out.println("new face");
						//update how many times this number has showed up
						numberOfFaces++;
						
		        		//new number
						
			        	//invalid read, or reached the end
			        	if(face == -1)
			        	{
			        		break outerloop;
			        	}
						
						
						face = labels.read();
						while (face-'0' != 1)
		        		{	
		        			//skip that image
		        			for(int i1 = 0; i1 < 70; i1++)
		        			{
		        				for(int j = 0; j < 61; j++)
		        				{
		        					//skip the non-face image
		        					inputStream.read();
		        				}
		        			}
		        			
		        			face = labels.read();
		        		}
						
		
			        	//index is in ascii format...subtract the offset of 0
			        	face = face-'0';
			        	//total++;
					}
		
				}
		
			}//end of reading in from files, finished calculating likelihoods
		    
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
		
		
		}//end of Faces Constructor
		
		
		
		
		
		//estimate the likelihoods P(F_ij | class), and adds smoothing. As well as calculates the frequencies 
		public void updateLikelihoods()
		{
			//calculate the frequencies of each number(Prior P(class))
			frequencies= (double)(numberOfFaces)/(total);
			
			//i = the number(class) we are going to update
			for(int i =0; i < 10; i++)
			{


				//updates the likelihoods
				for(int j =0; j < 28; j++)
				{
					for(int k =0; k < 28; k++)
					{
						
						//likelihoods[i][j][k] = (double)(likelihoods[i][j][k])/( numbers[i] );
						//System.out.print(likelihoods[i][j][k]);
					}
					//System.out.println();
				}
				//System.out.println();
				//System.out.println();
				
			}
		}
		
		
		
		//help debug likelihoods 
		public void printLikelihoods()
		{

			for(int j =0; j < 28; j++)
			{
				for(int k =0; k < 28; k++)
				{
					//a threshold to see what the likelihoods look like
					if(likelihoods[0][j][k] > .5)
						System.out.print("#");
					else 
						System.out.print(" ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
			
					
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
							pickFace();
							break outerloop;
							
						}	
						
		    			//foreground
						else if(c == 35 )
		    			{
							System.out.print("#");
		    				image[i%28][j] = 1;
		    			}
						//background, else its a background	
						else if(c == 32)
						{
							System.out.print(" ");
							image[i%28][j]= 0;
						}
					}
					System.out.println();
					
					//we have a complete number, we should decide what number it is
					if(i %28==0 && i !=0)
						pickFace();
					
		
				}
		
			}//end of reading in from testimages
		    
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
		
		
		
		
		
		
		
		//picks a class for a particular test case. (estimates)
		public void pickFace()
		{
			double[] probabilities = new double[10];
			int best = 0;
			
			//i = the number(class) we are going to estimate
			for(int i =0; i < 10; i++)
			{
				probabilities[i]= probabilities[i] + (Math.log(frequencies) );
				
				for(int j =0; j < 28; j++)
				{
					for(int k =0; k < 28; k++)
					{
						probabilities[i] = probabilities[i] + ( image[j][k] * Math.log(likelihoods[i][j][k]) );
					}
				}
				
				System.out.println(i+": "+ probabilities[i]);
				if(probabilities[i] > probabilities[best])
					best = i;
				
			}
			
			System.out.println("Best = "+ best + "  actual: "+actualNum);
			if(best == actualNum)
			{
				percent++;
				
			}
			count++;
			System.out.println("Count: " +percent+" / "+count);
			
			System.out.println("percent: "+ (double)(percent/count)*100);
			
		}
		
		
		
		
		

}//end of faces class
