/*
 *	Face Class	
 *	Calculates the likelihoods using the training labels and training images
 *	 
 * 	Estimates whether an image is a face or not a face
 * 
 * @author  Erik Muro, William Hempy
 * @netid	emuro2, hempy2
 * 
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Faces {
                
	    //Likelihoods for each class (0-1)
		//0 = face 1 = not face
	    double [][][] likelihoods = new double [2][70][60];
	    
	    //keep track of how many times a face comes up in the training labels file
	    int [] numberOfFaces = new int [2];
	    
	    //priors P(class). The empirical frequencies of the classes
	    double [] frequencies= new double[2];
	    
	    //total number of trials in the traininglabels 
	    int total = 451;
	 
	    //new image from test images (new picture)
	    int [][] image = new int[70][60];
		int actualNum = 0; // Classification of face or non-face
		double percent = 0; // The counter for the number of correct classifications
		double count = 0; // A counter for the number of digits
		double smooth = 1;
		
		double [][] confusion = new double[2][2];
	        
		public static void main(String[] args) throws IOException 
		{
			//Classify Faces
			Faces face_classification = new Faces();
			face_classification.updateLikelihoods();
			face_classification.getNewFace();
		}
		
	    //Adds a smoothing constant to all the likelihoods pixels to ensure that there are no zero counts.
	    public void initLikelihoods()
	    {
	    	for(int i =0; i < 2; i++)
	    	{
	            for(int j =0; j < 70; j++)
	            {
                    for(int k =0; k < 60; k++)
                    {
                        likelihoods[i][j][k] = smooth;
                    }
	            }                        	
	    	}  
	    }//end of initLikelihoods
	    
	    
	    
	    //initializes the likelihoods for each class (i.e. face or not a face) from the training data
	    public Faces() throws IOException 
	    {
	        initLikelihoods();	            
	        FileReader inputStream = null;
	        FileReader labels = null;	        
	        try 
	        {
	            inputStream = new FileReader("facedata/facedatatrain");
	            labels = new FileReader("facedata/facedatatrainlabels");	
	            int c;
	            int face = 0;	            
	            outerloop:
	            for(int i = 0; true; i++)
	            {
                    if(i == 0)
                    {
                        face = labels.read();
                        face = face-'0';
                    }
                    for(int j =0; j < 61; j++)
                    {
                        c =  inputStream.read();
                        if(c == -1)
                        {
                            numberOfFaces[face]++;
                            break outerloop;
                        }                               
                        //foreground
                        else if(c == 35 )
                        {
                            likelihoods[face][i%70][j]++; 
                        }
                        //else its a background                                                                                                
                    }
                    
                    if(i%70 == 0 && i != 0)
                    {
                        face = labels.read();   

                        if(face == -1)
                        {
                            break outerloop;
                        }
                        if (face-'0' != 1 || face-'0' != 0)
                        {        
                            face = labels.read();
                        }
                        face = face-'0';
                        numberOfFaces[face]++;  
                    }
                }
            } 
            catch (FileNotFoundException e) {
                    e.printStackTrace();
            }
	    }//end of Faces Constructor
	    
	    
	    
	    
	    //estimate the likelihoods P(F_ij | class), and adds smoothing. As well as calculates the frequencies 
	    public void updateLikelihoods()
	    {
            for(int i =0; i < 2; i++)
            {	
                //(Prior P(class))
            	frequencies[i]= (double)(numberOfFaces[i])/(total);
            	
	            for(int j =0; j < 70; j++)
	            {
                    for(int k =0; k < 60; k++)
                    {
                        likelihoods[i][j][k] = (double)(likelihoods[i][j][k])/( numberOfFaces[i]+(2*smooth) );
                    }
	            }
            }
	    }//end of updateLikelihoods
	    
	    
	    
	    
	    
	    //gets new image from test images file, and determines whether the image is a face
	    public void getNewFace() throws IOException
	    {
	        FileReader inputNumber = null;
	        FileReader actualNumber = null;        
	        try {
	            inputNumber = new FileReader("facedata/facedatatest");
	            actualNumber = new FileReader("facedata/facedatatestlabels");	            
	            int c;	                    
	            outerloop:
	            for(int i = 0; true; i++)
	            {                 
            		/*DO NOT DELETE*/
            		//only obtains the actual number to compare in the function call pickFace()
                    if(i%70 == 0 && i != 0)
                    {
                        actualNum =  actualNumber.read();
	                    if(actualNum  == -1)
	                    {	                            
	                            break outerloop;
	                    }
	                    if(actualNum  == 10)
	                            actualNum =  actualNumber.read();
	                    actualNum = actualNum-'0';
                    }
                    
                    for(int j =0; j < 61; j++)
                    {
                        c =  inputNumber.read();
                        if(c == -1)
                        {
                            pickFace(c);
                            break outerloop;                                                
                        }                                  
                        //foreground
                        else if(c == 35)
                        {
                            image[i%70][j] = 1;
                        }
                        //else its a background        
                        else if(c == 32)
                        {
                            image[i%70][j]= 0;
                        }
                    } 

                    if(i %70==0 && i !=0)
                        pickFace(1);    
                    
	            }	    
            }        
            catch (FileNotFoundException e) {
                    e.printStackTrace();
            }            
	    }//end of getNewFace function
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    /*Determines if image is a face or not, uses the actualNum variable from getNewFace()
	     * 
	     * 	input = if we reached the end of test file (i.e -1), print statistics 
	     */
	    public void pickFace(int input)
	    {
            double [] probabilities = new double [2];
            int best = 0;
            
            for(int i =0; i < 2; i++)
            {
            	probabilities[i]= probabilities[i] + (Math.log(frequencies[i]) );
	            for(int j =0; j < 70; j++)
	            {
                    for(int k =0; k < 60; k++)
                    {
                        if(image[j][k]==0)  
                        	probabilities[i] = probabilities[i] + ( Math.log(1-likelihoods[i][j][k]) );
                        else 	
                        	probabilities[i] = probabilities[i] + ( Math.log(likelihoods[i][j][k]) );

                    }

	            }
	            if(probabilities[i] > probabilities[best])
	                   best = i;
            }
            confusion[best][actualNum]++;
            if(best == actualNum)
            {	            	
                percent++;	                    
            }
            
            count++;
            
            if(input ==-1)
            {
                System.out.println("Face Classification: ");
                System.out.println("Count: " +percent+" / "+count);       
                System.out.println("percent: "+ (double)(percent/count)*100);
                System.out.println();
                System.out.println("Confusion Matrix:");
                printConfusionMatrix();
                printLikelihoodMaps();
                printOddsRatios();
            }
	            
	    }//end of pickFace()
	    
	    /**
	     * Prints a graphical representation of the
	     * likelihoods for all classes.
	     */
	    public void printLikelihoodMaps() {
	    	try {
		    	BufferedImage img = printLikelihoodMap(1);
				File output = new File("(face)likelihood.png");
				ImageIO.write(img, "PNG", output);
				
				img = printLikelihoodMap(0);
				output = new File("(not_face)likelihood.png");
				ImageIO.write(img,"PNG",output);
	    	}
	    	catch (IOException e) {}
	    }
	    
	    /**
	     * Prints a graphical representation of the
	     * odds ratios for the two combined classes
	     * (face and not-face).
	     */
	    public void printOddsRatios() {
	    	BufferedImage img = new BufferedImage(60,70,BufferedImage.TYPE_INT_RGB);
	    	
			double [][] odds_ratio = new double[70][60];
			
			// Calculate the minimum and maximum values that x can take.
			double x,min = 100, max = -100;
			for (int i = 0; i < 60; i++) {
				for (int j = 0; j < 70; j++) {
					x = Math.log(likelihoods[0][j][i]/likelihoods[1][j][i]);
					if (x < min) min = x;
					if (x > max) max = x;
				}
			}
			
			for (int i = 0; i < 60; i++) {
				for (int j = 0; j < 70; j++) {
					odds_ratio[j][i] = likelihoods[0][j][i]/likelihoods[1][j][i];
					x = Math.log(odds_ratio[j][i]);
					img.setRGB(i,j,getColor(x,min,max));
				}
			}
			
			File output = new File("(face,not_face)ratio.png");
			try { ImageIO.write(img, "PNG", output); }
			catch ( IOException e) {}
	    }
	    
	    /**
	     * Prints the likelihood map for
	     * a given class.
	     * 
	     * @param c the class
	     * @return the graphical representation
	     */
	    public BufferedImage printLikelihoodMap(int c) {
			BufferedImage img = new BufferedImage(60,70, BufferedImage.TYPE_INT_RGB);
			
			// Calculate the minimum and maximum values that x can take.
			double x,min = 100, max = -100;
			for (int i = 0; i < 60; i++) {
				for (int j = 0; j < 70; j++) {
					x = Math.log(likelihoods[c][j][i]);
					if (x < min) min = x;
					if (x > max) max = x;
				}
			}

			for (int i = 0; i < 60; i++) {
				for (int j = 0; j < 70; j++) {
					x = Math.log(likelihoods[c][j][i]);
					img.setRGB(i,j,getColor(x,min,max));
				}
			}

			return img;
	    }
	    
	    /**
		 * Gets the color for a particular log of the
		 * probability. This function creates a gradient by
		 * converting from the HSV color coordinate system to
		 * RGB. It makes a sweep through the hue values [0,240]
		 * to create a gradient of color in the resulting image.
		 * 
		 * 
		 * @input x the value of the log
		 * @input min the minimum value that x can take
		 * @input max the maximum value that x can take
		 * @return the RGB result
		 */
		public int getColor(double x, double min, double max) {
			double r = 0,g = 0,b = 0;

			double y = x;
			double m = (240)/(min-max);
			double a = (-240*max)/(min-max);

			y = y*(m) + a;

			int i;
			double f, p, q, t;
			double h,s,v;
			h = y;
			s = v = 100;
			// Make sure our arguments stay in-range
			h = Math.max(0, Math.min(360, h));
			s = Math.max(0, Math.min(100, s));
			v = Math.max(0, Math.min(100, v));
		 
			s /= 100;
			v /= 100;
		 
			if(s == 0) {
				// Achromatic (grey)
				r = g = b = v;
				r = Math.round(255*r);
				g = Math.round(255*g);
				b = Math.round(255*b);
			}
			else {
				h /= 60; 
				i = (int)Math.floor(h);
				f = h - i; 
				p = v * (1 - s);
				q = v * (1 - s * f);
				t = v * (1 - s * (1 - f));
			 
				switch(i) {
					case 0:
						r = v;
						g = t;
						b = p;
						break;
			 
					case 1:
						r = q;
						g = v;
						b = p;
						break;
			 
					case 2:
						r = p;
						g = v;
						b = t;
						break;
			 
					case 3:
						r = p;
						g = q;
						b = v;
						break;
			 
					case 4:
						r = t;
						g = p;
						b = v;
						break;
			 
					default: // case 5:
						r = v;
						g = p;
						b = q;
				}
				
				r = Math.round(255*r);
				g = Math.round(255*g);
				b = Math.round(255*b);
			}
			
			return ((int)r << 16) | ((int)g << 8) | (int)b;
		}
		
		public void printConfusionMatrix() {
			int sum = 0;
			for (int j = 0; j < 2; j++) {
				for (int i = 0; i < 2; i++) {
					sum += confusion[i][j];
				}
				for (int i = 0; i < 2; i++) {
					confusion[i][j] = confusion[i][j]/sum;
				}
				sum = 0;
			}
			
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if ((100*(confusion[i][j])) >= 10)
						System.out.printf("%.1f ",(float)(100*(confusion[i][j])));
					else
						System.out.printf("%.1f  ",(float)(100*(confusion[i][j])));
				}
				System.out.println();
			}
			System.out.println();
		}
	    
}//end of Face class
