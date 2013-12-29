/*
 *	Digits Class	
 *	Calculates the likelihoods using the training labels and training images
 *	 
 * 	Estimates whether an image is a digit (0-9)
 * 
 * @author  Erik Muro, William Hempy
 * @netid   emuro2, hempy2
 * 
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Digits {
	
	
	//Likelihoods for each class (0-9)
	double [][][] likelihoods = new double [10][28][28];
	
	//Number of occurences of a class in the traininglabels file
	double [] numbers = new double [10];
	
	//priors P(class). The empirical frequencies of the classes
	double [] frequencies = new double[10];
	
	//total number of numbers in the traininglabels 
	double total = 5000;
	
	//This is a 10x10 matrix whose entry in row r and column c 
	//is the percentage of test images from class r that are classified as class c.
	//Indexed by [row][column] 
	double [][] confusion_matrix = new double [10][10];
	
	//new image from test images (new number)
	double [][] image = new double[28][28];
	
	double actualNum = 0; // The actual class of the current digit
	double percent = 0; // The counter for the number of correct classifications
	double count = 0; // A counter for the number of digits
	double smooth = 1;
	
	public static void main(String[] args) throws IOException 
	{
		//Classify Digits
		Digits digit_classification = new Digits();
		digit_classification.updateLikelihoods();
		digit_classification.getNewImage();
	}
	
	/**
	 * Smooth the likelihoods to ensure that there are no zero count.
	 * Adding a constant K to each pixel.
	 */
	public void smoothLikelihoods() {

		for(int i =0; i < 10; i++) {
			for(int j =0; j < 28; j++) {
				for(int k =0; k < 28; k++) {
					likelihoods[i][j][k] = smooth;
				}
			}			
		}
	}
	
	/**
	 * A function to calculate the likelihoods using the training
	 * data in the specified files.
	 * 
	 * The images are 28x28, so we need to read the image textfile
	 * 28 lines at a time.
	 * 
	 * This function simply counts the number of occurrences of
	 * non-blank symbols for each image, and adds them to the likelihoods.
	 * 
	 * It also counts the number of occurrences of each class, storing 
	 * them into numbers[].
	 * 
	 * @throws IOException
	 */
	public void initLikelihoods() throws IOException {
        FileReader inputStream = new FileReader("digitdata/trainingimages");
        Scanner labels = new Scanner(new File("digitdata/traininglabels"));
        
        int c;
        int index = labels.nextInt();
        
        outerloop:
        for(int i = 0; true; i++){	        	
			for(int j =0; j < 29; j++) {
				c =  inputStream.read();
				
				switch (c) {
				case -1:
					numbers[index]++;
					break outerloop;
				case 35:
				case 43:
					likelihoods[index][i%28][j]++;
				}
			}

			if(i%28 == 0 && i != 0) {
				numbers[index]++;

				if (!labels.hasNextInt())
					break outerloop;
				else
					index = labels.nextInt();
			}
		}	
	}
	
	
	/**
	 * Initializes the likelihood array that contains that 
	 * contains the likelihoods for each class (i.e. classes 0-9)
	 */
	public Digits() throws IOException {
		smoothLikelihoods();
		initLikelihoods();
	}
	
	
	/**
	 * Estimate the likelihoods P(F_ij | class), and adds smoothing. 
	 * As well as calculates the frequencies.
	 * 
	 *  - i = the number(class) we are going to update
	 *  
	 *  Calculate the frequencies of each number(Prior P(class))
	 *  Simply equal to (# of occurrences of each number)/(# total numbers)
	 *  
	 *  Updates the likelihoods by dividing each F_{i,j} by the number of 
	 *  occurrences of that class to get P(F_{i,j} | class)
	 */
	public void updateLikelihoods() {
		for(int i =0; i < 10; i++) {
			frequencies[i]= (double)(numbers[i])/(total);

			for(int j =0; j < 28; j++) {
				for(int k =0; k < 28; k++) {		
					likelihoods[i][j][k] = (double)(likelihoods[i][j][k])/(numbers[i]+(2*smooth));					
				}
			}
			System.out.println("Num "+ i+ ": "+numbers[i]);
		}
	}
	
	/**
	 * Gets new image from testimages file.
	 * Each image is a 28x28, so we load from 
	 * the file one at a time.
	 * 
	 * If we have a complete number, we should decide what number it is.
	 * This occurs when i%28 and i != 0.
	 * 
	 * @throws IOException
	 */
	public void getNewImage() throws IOException
	{
		FileReader inputNumber = new FileReader("digitdata/testimages");
		Scanner actualNumber = new Scanner(new File("digitdata/testlabels"));

        int c;
			
        outerloop:
        for(int i = 0; true; i++) {		 
			if(i %28 == 0 && i != 0) {
				if (!actualNumber.hasNextInt())
					break outerloop;
				else
					actualNum = actualNumber.nextInt();
			}
			
			for(int j =0; j < 29; j++) {

				c =  inputNumber.read();
				
				switch (c) {
				case -1:
					pickClass(c);
					break outerloop;
				case 35:
				case 43:
					image[i%28][j] = 1;
					break;
				case 32:
					image[i%28][j] = 0;
					break;
				}
			}

			if(i %28==0 && i !=0)
				pickClass(1);
		}	
	}
	

	
	/**
	 * Picks a class for a particular test case. (estimates)
	 * - i = the number(class) we are going to estimate
	 */
	public void pickClass( int input)
	{
		double[] probabilities = new double[10];
		int best = 0;
		
		for(int i =0; i < 10; i++) {
			probabilities[i]= probabilities[i] + (Math.log(frequencies[i]) );
			
			for(int j =0; j < 28; j++) {
				for(int k =0; k < 28; k++) {
					if(image[j][k]==0)
					probabilities[i] = probabilities[i] + ( Math.log(1-likelihoods[i][j][k]));
					else
						probabilities[i] = probabilities[i] + ( Math.log(likelihoods[i][j][k]));
				}
			}
			
			if(probabilities[i] > probabilities[best])
				best = i;
			
		}

		if(best == actualNum) {
			percent++;	
		}
		
		confusion_matrix[best][(int)actualNum]++;
		count++;
		
		if(input ==-1) {
			printResults();
		}
	}
	
	/**
	 * Computes the odds ratio for two given classes and 
	 * prints out a graphical representation of the odds.
	 * 
	 * This file will appear in the form of a .png file with the
	 * format (c1,c2)ratio.png.
	 * 
	 * @throws IOException 
	 * 
	 * @input c1 the first class
	 * @input c2 the second class
	 */
	public void oddsRatioMap(int c1, int c2) throws IOException {
		BufferedImage img = new BufferedImage(84, 28, BufferedImage.TYPE_INT_RGB);
		BufferedImage img_c1 = likelihoodMap(c1);
		BufferedImage img_c2 = likelihoodMap(c2);
		
		double [][] odds_ratio = new double[28][28];
		
		// Calculate the minimum and maximum values that x can take.
		double x,min = 100, max = -100;
		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 28; j++) {
				x = Math.log(likelihoods[c1][j][i]/likelihoods[c2][j][i]);
				if (x < min) min = x;
				if (x > max) max = x;
			}
		}
		
		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 28; j++) {
				odds_ratio[j][i] = likelihoods[c1][j][i]/likelihoods[c2][j][i];
				x = Math.log(odds_ratio[j][i]);
				img.setRGB(i,j,getColor(x,min,max));
				img.setRGB(i+28,j,img_c1.getRGB(i,j));
				img.setRGB(i+56,j,img_c2.getRGB(i,j));
			}
		}

		
		File output = new File("(" + c1 + "," + c2 + ")ratio.png");
		ImageIO.write(img, "PNG", output);
	}
	
	/**
	 * Prints out a graphical representation for a given class's
	 * probability likelihood array.
	 * 
	 * 
	 * @throw IOException
	 * 
	 * @input c the class number
	 * @return img the likelihood image
	 */
	public BufferedImage likelihoodMap(int c) throws IOException {
		BufferedImage img = new BufferedImage(28,28, BufferedImage.TYPE_INT_RGB);
		
		// Calculate the minimum and maximum values that x can take.
		double x,min = 100, max = -100;
		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 28; j++) {
				x = Math.log(likelihoods[c][j][i]);
				if (x < min) min = x;
				if (x > max) max = x;
			}
		}

		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 28; j++) {
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
	 * The logs are in the range [-6.4,0]
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
	
	/**
	 * A wrapper function to print the image
	 * representations for the odds ratio output.
	 * 
	 * It prints the four highest odds ratio pairs,
	 * which are the following:
	 * 
	 * (8,3) (9,7) (5,3) (9,4)
	 */
	public void printOddsRatios() {
		try {
			oddsRatioMap(8,3);
			oddsRatioMap(9,7);
			oddsRatioMap(5,3);
			oddsRatioMap(9,4);
		}
		catch ( IOException e) {}
	}
	
	/**
	 * Prints all of the likelihood maps for visual comparison.
	 */
	public void printLikelihoodMaps() {
		try {
			for (int i = 0; i < 10; i++) {
				BufferedImage img = likelihoodMap(i);
				File output = new File("(" + i + ")likelihood.png");
				ImageIO.write(img, "PNG", output);
			}
		} catch ( IOException e ) {}
	}
	
	/**
	 * Prints the resulting statistics of the
	 * digit classification. 
	 */
	public void printResults() {
		System.out.println("Digit Classification: ");
		System.out.println("Count: " +percent+" / "+count);
		System.out.println("Percent: "+ (float)(percent/count)*100);
		System.out.println("Confusion Matrix:");
		printConfusionMatrix();
		//printLikelihoodMaps();
		printOddsRatios();
	}
	
	/**
	 * Converts each entry of the matrix to a percentage.
	 * Prints the confusion matrix.
	 */
	public void printConfusionMatrix() {
		int sum = 0;
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 10; i++) {
				sum += confusion_matrix[i][j];
			}
			for (int i = 0; i < 10; i++) {
				confusion_matrix[i][j] = confusion_matrix[i][j]/sum;
			}
			sum = 0;
		}
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if ((100*(confusion_matrix[i][j])) >= 10)
					System.out.printf("%.1f ",(float)(100*(confusion_matrix[i][j])));
				else
					System.out.printf("%.1f  ",(float)(100*(confusion_matrix[i][j])));
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/** 
	 * (DEBUGGING FUNCTION)
	 * Prints the array of likelihoods.
	 */
	public void printLikelihoods() {
		
		for(int i =0; i < 10; i++) {
			for(int j =0; j < 28; j++) {
				for(int k =0; k < 28; k++) {
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
}//end of class Digits
