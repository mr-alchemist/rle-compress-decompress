import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

public class Program {

	public Program() {
		
	}

	public static void main(String[] args) {
		Program program = new Program();
		program.run(args);
		
	}
	void run(String[] args) {
		compressFileRLE("C:\\1.txt", "C:\\2.txt");
	}
	
	byte[] compressRLE(byte[] data) {
		return null;
	}
	
	void compressFileRLE(String inputFileName, String outputFileName) {
		
		try {
			FileInputStream input = new FileInputStream(inputFileName);
			FileOutputStream output = new FileOutputStream(outputFileName);
			
			boolean start = true;
			byte lastB = 0;
			int readBufCnt = 0;
			int cntRLE = 0;
			byte[] buffer = new byte[256];
			
			do {
				readBufCnt = input.read(buffer);
				for(int i = 0; i < readBufCnt; i++) {
					byte b = buffer[i];
					if(start) {
						start = false;
						lastB = b;
						cntRLE = 1;
						continue;
					}
					if(lastB == b) {
						cntRLE++;
					}
					else {
						output.write(cntRLE);
						output.write(lastB);
						cntRLE = 1;
						lastB = b;
					}
					
				}
				
			}while(readBufCnt > 0);
			if(!start) {
				output.write(cntRLE);
				output.write(lastB);
			}
			
			input.close();
			output.close();
			
		}
		catch(Exception ex) {
			System.out.println(ex.getClass() + ": "+ ex.getMessage());
			return;
		}
		
		
		
		
	}
	
	boolean doesFileExist(String fileName) {
		return false;
	}
	

}
