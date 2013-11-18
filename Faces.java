	
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.*;


public class Faces {
		
		//Likelihoods for each class (0-9)
		double [][] likelihoods = new double [70][60];
		
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
					likelihoods[j][k] = 1;
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
		        for(int i = 0; true; i++)
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
		    					 likelihoods[i%60][j]= likelihoods[i%60][j]+1; 
		    					 //System.out.print("#");
		    			}
						//background, else its a background		
					}
					
					//new letter, need a new index
					if(i%70 == 0 && i != 0)
					{
					
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
							skipface(face, inputStream);
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
		
		
		
		
		
		
		//skips the image of a non-face
		/*
		 * input = the label read from facedatatrainlabels
		 * inputstream = the file reader of the faces
		 */
		public void skipface(int input, FileReader inputStream ) throws IOException
		{
			//new line character
			if(input == 10)
			{
				return;
			}
			
			//skip that image
			for(int i1 = 0; i1 < 70; i1++)
			{
				for(int j = 0; j < 61; j++)
				{
					//skip the non-face image
					inputStream.read();
				}	
			}			
		}//end of skipface
		
		
		//estimate the likelihoods P(F_ij | class), and adds smoothing. As well as calculates the frequencies 
		public void updateLikelihoods()
		{
			//calculate the frequencies of each number(Prior P(class))
			frequencies= (double)(numberOfFaces)/(total);
			
			//updates the likelihoods
			for(int j =0; j < 28; j++)
			{
				for(int k =0; k < 28; k++)
				{
					
					//likelihoods[j][k] = (double)(likelihoods[j][k])/( numberOfFaces );
					//System.out.print(likelihoods[i][j][k]);
				}
				//System.out.println();
			}
			//System.out.println();
			//System.out.println();
			
			
		}
		
		
		
		
		
		//gets new image from testimages file
		public void getNewFace() throws IOException
		{
			FileReader inputNumber = null;
			FileReader actualNumber = null;
		    
		    try {
		        inputNumber = new FileReader("facedata/facedatatest");
		        actualNumber = new FileReader("facedata/facedatatestlabels");

	
		
			}//end of reading in from testimages
		    
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
		
		
		
		
		
		
		
		//picks a class for a particular test case. (estimates)
		public void pickFace()
		{
			
			
		}
		
		
		
		
		

}//end of faces class
