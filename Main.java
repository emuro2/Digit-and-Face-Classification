import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException 
	{
		
		//Classify Digits
		Digits digit_classification = new Digits();
	
		digit_classification.updateLikelihoods();
		
		//help to debug
		//digit_classification.printLikelihoods();
		
		digit_classification.getNewImage();
		
		
		
		//Classify Faces
		Faces face_classification = new Faces();
		

	
	}
}