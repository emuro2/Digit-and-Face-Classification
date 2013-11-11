import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Digits {
	
	//array [][] of F_ij (pixel) probabilities
	//for all 0-9 digits
	
	public Digits() throws IOException {
	FileReader inputStream = null;
    
    try {
        inputStream = new FileReader("digitdata/testimages");
       
        int c;
        

       
        outerloop:
        while(true)
        {
			for(int l =0; l < 29; l++)
			{
				c =  inputStream.read();
				
				if(c == -1)
				{
					break outerloop;
				}
				
				if(c != -1)
				{	
    				if(c == 32)
    					System.out.print( " " );
    				else if(c == 35)
    					System.out.print("#");
    				else if(c == 43)
    					System.out.print("+");
    				
				}	
						
			}
			System.out.println();
			
			//new letter
			
			
			
		}

	} 
	catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	
	
	
}
	

}
