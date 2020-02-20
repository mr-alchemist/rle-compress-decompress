import java.io.*;

import storage.FactorArray;

public class Program {

	public Program() {
		
	}

	public static void main(String[] args) {
		Program program = new Program();
		program.run(args);
		
	}
	void showHelp() {
		System.out.println("");
		System.out.println("Использование для компрессии:   rle [-c] <имя исходного(сжимаемого) файла> [<имя файла-результата>]");
		System.out.println("Использование для декомпрессии: rle -d <имя сжатого файла> <имя файла-результата>");
	}
	void run(String[] args) {
		if(args.length == 0) {
			showHelp();
			return;
		}
		
		String arg0 = args[0];
		if(arg0.length() == 0 ) {
			System.out.println("Неверный параметр \"\".");
			showHelp();
			return;
		}
		boolean compressMode = true;
		boolean arg0IsParam = false;
		if(arg0.charAt(0) == '-') {//если первый символ -, то идентифицируем, что это за параметр
			arg0 = arg0.toLowerCase();
			if(arg0.length() != 2 || (arg0.charAt(1) != 'c' && arg0.charAt(1) != 'd') ) {
				System.out.println("Неверный параметр " + args[0] + ".");
				showHelp();
				return;
			}
			if(arg0.charAt(1) == 'd')
				compressMode = false;//т.е. режим декомпрессии
			arg0IsParam = true;
		}
		
		if(compressMode) {
			String srcFileName = null;
			if(arg0IsParam) {
				if(args.length <= 1) {
					System.out.println("Необходимо указать имя сжимаемого файла.");
					return;
				}
				else 
					srcFileName = args[1];
				
			}
			else
				srcFileName = args[0];
			if(srcFileName.isEmpty()) {
				System.out.println("Необходимо указать непустое имя файла.");
				return;
			}
			String compressedFileName = null;
			if(arg0IsParam) {
				if(args.length <= 2) 
					compressedFileName = srcFileName + ".rle";//имя сжатого файла по умолчанию
				else 
					compressedFileName = args[2];
			}
			else {
				if(args.length <= 1) 
					compressedFileName = srcFileName + ".rle";//имя сжатого файла по умолчанию
				else 
					compressedFileName = args[1];
			}
			if(compressedFileName.isEmpty()) {
				System.out.println("Необходимо указать непустое имя файла.");
				return;
			}
			compressFileRLE(srcFileName, compressedFileName);
		}
		else {//режим декомпрессии, здесь после параметра обязательно указывать 2 имени файла
			if(args.length < 3) {
				System.out.println("После параметра \"-d\" необходимо указать файл для декомпресии и имя файла-результата.");
				return;
			}
			String compressedFileName = args[1];
			String resultFileName = args[2];
			if(compressedFileName.isEmpty()) {
				System.out.println("Необходимо указать непустое имя сжатого файла.");
				return;
			}
			if(resultFileName.isEmpty()) {
				System.out.println("Необходимо указать непустое имя файла-результата.");
				return;
			}
			decompressFileRLE(compressedFileName, resultFileName);
		}
		
	}
	
	void compressFileRLE(String inputFileName, String outputFileName) {
		
		try {
			//FileInputStream input = new FileInputStream(inputFileName);
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(inputFileName));
			//FileOutputStream output = new FileOutputStream(outputFileName);
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFileName));
			
			int q = 0;
			byte lastB = 0;
			int readBufCnt = 0;
			int cntRLE = 0;
			byte[] readBuffer = new byte[4096*4096];
			FactorArray<Byte> fa = new FactorArray<Byte>();
			
			do {
				readBufCnt = input.read(readBuffer);
				for(int i = 0; i < readBufCnt; i++) {
					byte b = readBuffer[i];
					
					switch(q) {
					case 0:
						lastB = b;
						cntRLE = 1;
						q = 1;
						break;
					case 1:
						if(b == lastB) {
							cntRLE++;
							q = 2;
						}
						else {
							fa.add(lastB);
							lastB = b;
							//cntRLE = 1;
							q = 3;
						}
						break;
					case 2:
						if(b != lastB || cntRLE == 127) {
							output.write(cntRLE);
							output.write(lastB);
							lastB = b;
							cntRLE = 1;
							q = 1;
						}
						else {
							cntRLE++;
						}
						break;
					case 3:
						if(fa.size() == 127) {
							//TODO здесь можно предусмотреть развилку для ситуаций когда b == lastB и b != lastB
							//.. для b != lastB, понятно дело, нужно fa.add(lastB); и выводить fa в файл, q = 1
							//.. b == lastB можно вывести fa в файл(127символов из fa), cnt = 2, q = 2
							//.. подумать, какой вариант выгоднее с точки зрения эффективности компрессии
							fa.add(lastB);
							//write fa:
							writeFaToFile(fa, output);
							lastB = b;
							cntRLE = 1;
							q = 1;
							break;
						}
						if(b != lastB) {
							fa.add(lastB);
							lastB = b;
							//cntRLE = 1;
						}
						else {
							cntRLE = 2;
							q = 4;
						}
						break;
					case 4:
						if(b != lastB) {
							fa.add(lastB);
							fa.add(lastB);
							lastB = b;
							cntRLE = 1;
							if(fa.size() == 128) {
								writeFaToFile(fa, output);
								q = 1;
							}
							else {
								q = 3;
							}
						}
						else {
							cntRLE = 3;
							writeFaToFile(fa, output);
							q = 2;
						}
						break;
					default:
						break;
					}
					
				}
				
			}while(readBufCnt > 0);
			
			switch(q) {
			case 1:
				output.write(cntRLE);
				output.write(lastB);
				break;
			case 2:
				output.write(cntRLE);
				output.write(lastB);
				break;
			case 3:
				fa.add(lastB);
				writeFaToFile(fa, output);
				break;
			case 4:
				fa.add(lastB);
				fa.add(lastB);
				writeFaToFile(fa, output);
				break;
			default:
				break;
			}
			
			input.close();
			output.close();
			
		}
		catch(Exception ex) {
			System.out.println(ex.getClass() + ": "+ ex.getMessage());
			return;
		}
		
	}
	
	void decompressFileRLE(String inputFileName, String outputFileName) {
		try {
			//FileInputStream input = new FileInputStream(inputFileName);
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(inputFileName));
			//FileOutputStream output = new FileOutputStream(outputFileName);
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFileName));
			
			int readBufCnt = 0;
			byte[] readBuffer = new byte[4096];
			int q = 0;
			int n = 0;
			do {
				readBufCnt = input.read(readBuffer);
				for(int i = 0; i < readBufCnt && q != 3; i++) {
					byte b = readBuffer[i];
					switch(q) {
					case 0:
						if(b > 0) {
							n = b;
							q = 1;
							break;
						}
						if(b < 0) {
							n = -b;
							q = 2;
						}
						else {
							q = 3;
						}
						break;
					case 1:
						for(int j = 0; j < n; j++) 
							output.write(b);
						q = 0;
						n = 0;
						break;
					case 2:
						output.write(b);
						n--;
						if(n == 0)
							q = 0;
						break;
					default:
						break;
					}
					
				}
				
			}while(readBufCnt > 0 && q != 3);
			
			switch(q) {
			case 1://обработка ошибки, когда должен быть еще минимум один символ(для простого RLE)
				System.out.println("Ошибка формата RLE: ожидание символа, но был достигнут конец файла");
				break;
			case 2://обработка ошибки, когда пришёл конец файла, но по-идее еще должны были быть данные
				System.out.println("Ошибка формата RLE: ожидание " + n + " символа(ов), но был достигнут конец файла");
				break;
			case 3://обработка ошибки "n = 0"
				System.out.println("Ошибка формата RLE: ожидание положительного или отрицательного значения, получено 0x00");
				break;
			default:
				break;
			}
			
			input.close();
			output.close();
			if(q == 1 || q == 2 || q == 3) {
				File file = new File(outputFileName);
				if(!file.delete())
					System.out.println("Ошибка удаления файла " + outputFileName);
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getClass() + ": "+ ex.getMessage());
			return;
		}
	}
	
	void writeFaToFile(FactorArray<Byte> fa, OutputStream fOutStream) throws IOException {
		fOutStream.write(-fa.size());
		for(int j = 0; j < fa.size() ; j++) 
			fOutStream.write(fa.get(j));
		fa.clear();
	}
	

}
