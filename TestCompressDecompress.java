import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import storage.FactorArray;

import java.io.*;
import java.util.Arrays;

class TestCompressDecompress {
	
	FactorArray<byte[]> dataList = new FactorArray<byte[]>();
	
	@BeforeEach
	void setUp() throws Exception {
		dataList.add(new byte[]{});
		dataList.add(new byte[]{25, 31, 85, 17, 01, 05, 05, 05, 05, 05, 05, 12, -128, 127, 100, 88, 88, 77, 77, 15});
		dataList.add(new byte[]{55});
		dataList.add(new byte[]{75, 62, 12, 15, 75, 95, 100, 111, 111, 111, 111, 111});
		dataList.add(new byte[]{1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2});
		
		byte[] bArr = new byte[1000];
		Arrays.fill(bArr, (byte)0);
		for(int i = 0; i < 127; i++)
			bArr[i] = 55;
		for(int i = 127; i < (127 + 129); i++)
			bArr[i] = 55;
		dataList.add(bArr);
		
		bArr = new byte[500];
		Arrays.fill(bArr, (byte)0);
		bArr[0] = 55;
		bArr[1] = 55;
		bArr[2] = 55;
		for(int i = 3; i < (3 + 126); i++)
			bArr[i] = (byte)i;
		for(int i = (3 + 126); i < (3 + 126 + 2); i++)
			bArr[i] = 99;
		for(int i = (3 + 126 + 2); i < (3 + 126 + 2 + 10); i++)
			bArr[i] = 99;
		dataList.add(bArr);
		
		bArr = new byte[500];
		Arrays.fill(bArr, (byte)0);
		bArr[0] = 55;
		bArr[1] = 55;
		bArr[2] = 55;
		for(int i = 3; i < (3 + 126); i++)
			bArr[i] = (byte)i;
		for(int i = (3 + 126); i < (3 + 126 + 2); i++)
			bArr[i] = 77;
		for(int i = (3 + 126 + 2); i < (3 + 126 + 2 + 10); i++)
			bArr[i] = 99;
		dataList.add(bArr);
		
		bArr = new byte[500];
		Arrays.fill(bArr, (byte)0);
		bArr[0] = 55;
		for(int i = 1; i < (1 + 128); i++)
			bArr[i] = (byte)(i/2);
		dataList.add(bArr);
		
		bArr = new byte[500];
		Arrays.fill(bArr, (byte)0);
		bArr[0] = 55;
		for(int i = 1; i < (1 + 127); i++)
			bArr[i] = (byte)i;
		dataList.add(bArr);
		
		for(int i = 0; i < dataList.size(); i++) {
			FileOutputStream fos = new FileOutputStream(i + ".bin");
			fos.write(dataList.get(i));
			fos.close();
		}
	}

	@AfterEach
	void tearDown() throws Exception {
		
		for(int i = 0; i < dataList.size(); i++) {
			File file = new File(i + ".bin");
			file.delete();
			file = new File(i + ".bin.rle");
			file.delete();
			file = new File(i + ".bin.dc");
			file.delete();
		}
		dataList = null;
		
	}

	@Test
	void test() {
		Rle pr = new Rle();
		for(int i = 0; i < dataList.size(); i++) {
			String fileName = i + ".bin";
			String comprFileName = fileName + ".rle";
			String decomprFileName = fileName + ".dc";
			System.out.print("Testing compressing/decompressing '" + fileName + "' ...");
			pr.compressFileRLE(fileName, comprFileName);
			pr.decompressFileRLE(comprFileName, decomprFileName);
			boolean filesEqual = areFilesEqual(fileName, decomprFileName);
			System.out.println(filesEqual?"OK":"FAIL");
			assertTrue(filesEqual);
		}
	}
	
	boolean areFilesEqual(String fileName1, String fileName2) {
		File file1 = new File(fileName1);
		File file2 = new File(fileName2);
		if(file1.length() != file2.length())
			return false;
		
		FileInputStream inf1 = null;
		FileInputStream inf2 = null;
		
		try {
			inf1 = new FileInputStream(fileName1);
			inf2 = new FileInputStream(fileName2);
			int b1 = 0;
			int b2 = 0;
			while((b1 = inf1.read()) >=0 ) {
				b2 = inf2.read();
				if(b1 != b2) {
					inf1.close();
					inf2.close();
					return false;
				}
			}
			
			inf1.close();
			inf2.close();
		}
		catch(Exception e) {
			System.out.println(e.getClass()+ ": "+e.getMessage());
			return false;
		}
		
		return true;
	}

}
