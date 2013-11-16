import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException 
	{
		Digits digit_classification = new Digits();
	
		digit_classification.updateLikelihoods();
		
		
		digit_classification.getNewImage();
		
		//digit_classification.pickClass();
	
	}
}