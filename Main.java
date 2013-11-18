import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException 
	{
		
		//Classify Digits
		Digits digit_classification = new Digits();
	
		digit_classification.updateLikelihoods();
		
		digit_classification.getNewImage();
		
		
		
		//Classify Faces
		Faces face_classification = new Faces();
		
		face_classification.updateLikelihoods();
		
		face_classification.getNewFace();
		

	
	}
}