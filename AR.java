/*Alex Pickering
 *12/15/2017
 *
 *Alex's Reader Library 1.2
 *An alternative to EasyReader with more methods with more functionality, while keeping everything we've used EasyReader for
 *In 1.0 its the same as EER but you don't need to initialize and EasyReader, or include EasyReader
 *
 *Changelog:
 *1.3
 *Added readers for longs - Same methods as ints but with longs
 *Added readers for shorts - Same methods as ints but with shorts
 *Added readers for bytes - Same methods as ints but with bytes
 *Added readers for floats - Same methods as doubles but with floats
 *Minor formatting change
 *
 *1.2
 *Added IllegalArgumentExceptions when min and max are equal
 *Changed methods to call the base instead of copying the base
 *Made a minor syntax change with if statements
 *
 *1.1
 *Added readCharS
 *Added readCharSI
 *
 *1.0
 *First Release
 *
 *0.3
 *Started using a different method for modified readers
 *
 *0.2
 *Changed some minor things so that repeated code is a method, for example parsing odd/even
 *
 *0.1
 *Started calling something with max and min something with range
 */
 
 import java.io.*;
 
 public class AR{
 	protected static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 	
 	//Integers
 	//Int
 	public static int readInt(String prompt, String NFE){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
 				System.out.println(prompt);
 			
 			try{
 				s = reader.readLine();
 				
 				return Integer.parseInt(s);
 			} catch(IOException e){
 				System.out.println("IO Exception.");
 			} catch(NumberFormatException e){
 				if(!NFE.equals(""))
 					System.out.println(NFE);
 			}
 		}
 	}
 	
 	//Int with min
 	public static int readIntMin(int min, String prompt, String NFE, String LM){
 		int i = 0;
 		
 		while(true){
 			i = readInt(prompt, NFE);
 			
 			if(i < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return i;
 		}
 	}
 	
 	//Int with max
 	public static int readIntMax(int max, String prompt, String NFE, String GM){
 		int i = 0;
 		
 		while(true){
 			i = readInt(prompt, NFE);
 			
 			if(i > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return i;
 		}
 	}
 	
 	//Int with range
 	public static int readIntRange(int mi, int ma, String prompt, String NFE, String LM, String GM){
 		int i = 0, min = 0, max = 0;
 		
 		if(mi > ma){
 			min = ma;
 			max = mi;
 		} else if(ma > mi){
 			min = mi;
 			max = ma;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			i = readInt(prompt, NFE);
 			
 			if(i < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			} else if(i > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return i;
 		}
 	}
 	
 	//Int with odd/even
 	public static int readIntOE(String oe, String prompt, String NFE, String NOE){
 		int i = 0;
 		
 		boolean even = parseOE(oe);
 		
 		while(true){
 			i = readInt(prompt, NFE);
 			
 			if((i % 2 == 0) == even)
 				return i;
 			
 			if(!NOE.equals(""))
 				System.out.println(NOE);
 		}
 	}
 	
 	//Int with odd/even and min
 	public static int readIntOEMin(String oe, int min, String prompt, String NFE, String NOE, String LM){
 		int i = 0;
 		
 		while(true){
 			i = readIntOE(oe, prompt, NFE, NOE);
 			
 			if(i < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return i;
 		}
 	}
 	
 	//Int with odd/even and max
 	public static int readIntOEMax(String oe, int max, String prompt, String NFE, String NOE, String GM){
 		int i = 0;
 		
 		while(true){
 			i = readIntOE(oe, prompt, NFE, NOE);
 			
 			if(i > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return i;
 		}
 	}
 	
 	//Int with odd/even and range
 	public static int readIntOERange(String oe, int mi, int ma, String prompt, String NFE, String NOE, String LM, String GM){
 		int i = 0, max = 0, min = 0;
 		
 		if(mi > ma){
 			max = mi;
 			min = ma;
 		} else if(ma > mi){
 			max = ma;
 			min = mi;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			i = readIntOE(oe, prompt, NFE, NOE);
 			
 			if(i > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			} else if(i < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return i;
 		}
 	}
 	
 	//Longs
 	//Long
 	public static long readLong(String prompt, String NFE){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
 				System.out.println(prompt);
 			
 			try{
 				s = reader.readLine();
 				
 				return Long.parseLong(s);
 			} catch(NumberFormatException e){
 				if(!NFE.equals(""))
 					System.out.println("NFE");
 			} catch(IOException e){
 				System.out.println("IO Exception");
 			}
 		}
 	}
 	
 	//Long with min
 	public static Long readLongMin(long min, String prompt, String NFE, String LM){
 		long l = 0;
 		
 		while(true){
 			l = readLong(prompt, NFE);
 			
 			if(l < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return l;
 		}
 	}
 	
 	//Long with max
 	public static long readLongMax(long max, String prompt, String NFE, String GM){
 		long l = 0;
 		
 		while(true){
 			l = readLong(prompt, NFE);
 			
 			if(l > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return l;
 		}
 	}
 	
 	//Long with range
 	public static long readLongRange(long mi, long ma, String prompt, String NFE, String LM, String GM){
 		long l = 0, min = 0, max = 0;
 		
 		if(mi > ma){
 			min = ma;
 			max = mi;
 		} else if(ma > mi){
 			min = mi;
 			max = ma;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			l = readLong(prompt, NFE);
 			
 			if(l < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			} else if(l > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return l;
 		}
 	}
 	
 	//Long with odd/even
 	public static long readLongOE(String oe, String prompt, String NFE, String NOE){
 		long l = 0;
 		
 		boolean even = parseOE(oe);
 		
 		while(true){
 			l = readLong(prompt, NFE);
 			
 			if((l % 2 == 0) == even)
 				return l;
 			
 			if(!NOE.equals(""))
 				System.out.println(NOE);
 		}
 	}
 	
 	//Long with odd/even and min
 	public static long readLongOEMin(String oe, long min, String prompt, String NFE, String NOE, String LM){
 		long l = 0;
 		
 		while(true){
 			l = readLongOE(oe, prompt, NFE, NOE);
 			
 			if(l < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return l;
 		}
 	}
 	
 	//Long with odd/even and max
 	public static long readLongOEMax(String oe, long max, String prompt, String NFE, String NOE, String GM){
 		long l = 0;
 		
 		while(true){
 			l = readLongOE(oe, prompt, NFE, NOE);
 			
 			if(l > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return l;
 		}
 	}
 	
 	//Long with odd/even and range
 	public static long readLongOERange(String oe, long mi, long ma, String prompt, String NFE, String NOE, String LM, String GM){
 		long l = 0, max = 0, min = 0;
 		
 		if(mi > ma){
 			max = mi;
 			min = ma;
 		} else if(ma > mi){
 			max = ma;
 			min = mi;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			l = readLongOE(oe, prompt, NFE, NOE);
 			
 			if(l > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			} else if(l < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return l;
 		}
 	}
 	
 	//Shorts
 	//Short
 	public static short readShort(String prompt, String NFE){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
 				System.out.println(prompt);
 			
 			try{
 				s = reader.readLine();
 				
 				return Short.parseShort(s);
 			} catch(IOException e){
 				System.out.println("IO Exception.");
 			} catch(NumberFormatException e){
 				if(!NFE.equals(""))
 					System.out.println(NFE);
 			}
 		}
 	}
 	
 	//Short with min
 	public static short readShortMin(short min, String prompt, String NFE, String LM){
 		short s = 0;
 		
 		while(true){
 			s = readShort(prompt, NFE);
 			
 			if(s < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return s;
 		}
 	}
 	
 	//Short with max
 	public static short readShortMax(short max, String prompt, String NFE, String GM){
 		short s = 0;
 		
 		while(true){
 			s = readShort(prompt, NFE);
 			
 			if(s > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return s;
 		}
 	}
 	
 	//Short with range
 	public static short readShortRange(short mi, short ma, String prompt, String NFE, String LM, String GM){
 		short s = 0, min = 0, max = 0;
 		
 		if(mi > ma){
 			min = ma;
 			max = mi;
 		} else if(ma > mi){
 			min = mi;
 			max = ma;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			s = readShort(prompt, NFE);
 			
 			if(s < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			} else if(s > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return s;
 		}
 	}
 	
 	//Short with odd/even
 	public static short readShortOE(String oe, String prompt, String NFE, String NOE){
 		short s = 0;
 		
 		boolean even = parseOE(oe);
 		
 		while(true){
 			s = readShort(prompt, NFE);
 			
 			if((s % 2 == 0) == even)
 				return s;
 			
 			if(!NOE.equals(""))
 				System.out.println(NOE);
 		}
 	}
 	
 	//Short with odd/even and min
 	public static short readShortOEMin(String oe, short min, String prompt, String NFE, String NOE, String LM){
 		short s = 0;
 		
 		while(true){
 			s = readShortOE(oe, prompt, NFE, NOE);
 			
 			if(s < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return s;
 		}
 	}
 	
 	//Short with odd/even and max
 	public static short readShortOEMax(String oe, short max, String prompt, String NFE, String NOE, String GM){
 		short s = 0;
 		
 		while(true){
 			s = readShortOE(oe, prompt, NFE, NOE);
 			
 			if(s > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return s;
 		}
 	}
 	
 	//Short with odd/even and range
 	public static short readShortOERange(String oe, short mi, short ma, String prompt, String NFE, String NOE, String LM, String GM){
 		short s = 0, max = 0, min = 0;
 		
 		if(mi > ma){
 			max = mi;
 			min = ma;
 		} else if(ma > mi){
 			max = ma;
 			min = mi;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			s = readShortOE(oe, prompt, NFE, NOE);
 			
 			if(s > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			} else if(s < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return s;
 		}
 	}
 	
 	//Bytes
 	//Byte
 	public static byte readByte(String prompt, String NFE){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
 				System.out.println(prompt);
 			
 			try{
 				s = reader.readLine();
 				
 				return Byte.parseByte(s);
 			} catch(IOException e){
 				System.out.println("IO Exception.");
 			} catch(NumberFormatException e){
 				if(!NFE.equals(""))
 					System.out.println(NFE);
 			}
 		}
 	}
 	
 	//Byte with min
 	public static byte readByteMin(byte min, String prompt, String NFE, String LM){
 		byte b = 0;
 		
 		while(true){
 			b = readByte(prompt, NFE);
 			
 			if(b < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return b;
 		}
 	}
 	
 	//Byte with max
 	public static byte readByteMax(byte max, String prompt, String NFE, String GM){
 		byte b = 0;
 		
 		while(true){
 			b = readByte(prompt, NFE);
 			
 			if(b > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return b;
 		}
 	}
 	
 	//Byte with range
 	public static byte readByteRange(byte mi, byte ma, String prompt, String NFE, String LM, String GM){
 		byte b = 0, min = 0, max = 0;
 		
 		if(mi > ma){
 			min = ma;
 			max = mi;
 		} else if(ma > mi){
 			min = mi;
 			max = ma;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			b = readByte(prompt, NFE);
 			
 			if(b < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			} else if(b > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return b;
 		}
 	}
 	
 	//Byte with odd/even
 	public static byte readByteOE(String oe, String prompt, String NFE, String NOE){
 		byte b = 0;
 		
 		boolean even = parseOE(oe);
 		
 		while(true){
 			b = readByte(prompt, NFE);
 			
 			if((b % 2 == 0) == even)
 				return b;
 			
 			if(!NOE.equals(""))
 				System.out.println(NOE);
 		}
 	}
 	
 	//Byte with odd/even and min
 	public static byte readByteOEMin(String oe, byte min, String prompt, String NFE, String NOE, String LM){
 		byte b = 0;
 		
 		while(true){
 			b = readByteOE(oe, prompt, NFE, NOE);
 			
 			if(b < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return b;
 		}
 	}
 	
 	//Byte with odd/even and max
 	public static byte readByteOEMax(String oe, byte max, String prompt, String NFE, String NOE, String GM){
 		byte b = 0;
 		
 		while(true){
 			b = readByteOE(oe, prompt, NFE, NOE);
 			
 			if(b > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return b;
 		}
 	}
 	
 	//Byte with odd/even and range
 	public static byte readByteOERange(String oe, byte mi, byte ma, String prompt, String NFE, String NOE, String LM, String GM){
 		byte b = 0, max = 0, min = 0;
 		
 		if(mi > ma){
 			max = mi;
 			min = ma;
 		} else if(ma > mi){
 			max = ma;
 			min = mi;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			b = readByteOE(oe, prompt, NFE, NOE);
 			
 			if(b > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			} else if(b < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return b;
 		}
 	}
 	
 	//Doubles
 	//Double
 	public static double readDouble(String prompt, String NFE){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
 				System.out.println(prompt);
 			
 			try{
 				s = reader.readLine();
 				
 				return Double.parseDouble(s);
 			} catch(NumberFormatException e){
 				if(!NFE.equals(""))
 					System.out.println(NFE);
 			} catch(IOException e){
 				System.out.println("IO Exception");
 			}
 		}
 	}
 	
 	//Double with min
 	public static double readDoubleMin(double min, String prompt, String NFE, String LM){
 		double d = 0;
 		
 		while(true){
 			d = readDouble(prompt, NFE);
 			
 			if(d < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			}
 			
 			return d;
 		}
 	}
 	
 	//Double with max
 	public static double readDoubleMax(double max, String prompt, String NFE, String GM){
 		double d = 0;
 		
 		while(true){
 			d = readDouble(prompt, NFE);
 			
 			if(d > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return d;
 		}
 	}
 	
 	//Double with range
 	public static double readDoubleRange(double mi, double ma, String prompt, String NFE, String LM, String GM){
 		double d = 0, min = 0, max = 0;
 		
 		if(mi > ma){
 			min = ma;
 			max = mi;
 		} else if(ma > mi){
 			min = mi;
 			max = ma;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			d = readDouble(prompt, NFE);
 			
 			if(d < min){
 				if(!LM.equals(""))
 					System.out.println(LM);
 				continue;
 			} else if(d > max){
 				if(!GM.equals(""))
 					System.out.println(GM);
 				continue;
 			}
 			
 			return d;
 		}
 	}
 	
 	//Strings
 	//String
 	public static String readString(String prompt, String NS){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
 				System.out.println(prompt);
 			
 			try{
 				s = reader.readLine();
 				
 				if(s.equals("") || s.equals(null)){
 					if(!NS.equals(""))
 						System.out.println(NS);
 					continue;
 				}
 				
 				return s;
 			} catch(IOException e){
 				System.out.println("IO Exception");
 			}
 		}
 	}
 	
 	//String specific, only given values are valid, returns the value
 	public static String readStringS(String[] possible, String prompt, String NS, String NA){
 		String s = null;
 		
 		while(true){
 			s = readString(prompt, NS);
 			
 			for(int i = 0; i < possible.length; i++)
 				if(s.equalsIgnoreCase(possible[i]))
 					return s;
 			
 			if(!NA.equals(""))
 				System.out.println(NA);
 		}
 	}
 	
 	//String specific, returns the index from the input
 	public static int readStringSI(String[] possible, String prompt, String NS, String NA){
 		String s = readStringS(possible, prompt, NS, NA);
 		
 		for(int i = 0; i < possible.length; i++)
 			if(possible[i].equals(s))
 				return i;
 		
 		return 0;
 	}
 	
 	
 	//String with max length
 	public static String readStringMax(int max, String prompt, String NS, String TL){
 		String s = null;
 		
 		while(true){
 			s = readString(prompt, NS);
 			
 			try{
 				char tmp = s.charAt(max);
 				
 				if(!TL.equals(""))
 					System.out.println(TL);
 			} catch(StringIndexOutOfBoundsException e){
 				return s;
 			}
 		}
 	}
 	
 	//String with min length
 	public static String readStringMin(int min, String prompt, String NS, String TS){
 		String s = null;
 		
 		while(true){
 			s = readString(prompt, NS);
 			
 			try{
 				char tmp = s.charAt(min - 1);
 				
 				return s;
 			} catch(StringIndexOutOfBoundsException e){
 				if(!TS.equals(""))
 					System.out.println(TS);
 			}
 		}
 	}
 	
 	//String with length range
 	public static String readStringRange(int mi, int ma, String prompt, String NS, String TS, String TL){
 		String s = null;
 		int max = 0, min = 0;
 		
 		if(mi > ma){
 			max = mi;
 			min = ma;
 		} else if(ma > mi){
 			max = ma;
 			min = mi;
 		} else {
 			throw new IllegalArgumentException();
 		}
 		
 		while(true){
 			s = readString(prompt, NS);
 			
 			try{
 				char tmp1 = s.charAt(max);
 				
 				if(!TL.equals(""))
 					System.out.println(TL);
 			} catch(StringIndexOutOfBoundsException e){
 				try{
 					char tmp2 = s.charAt(min - 1);
 					
 					return s;
 				} catch(StringIndexOutOfBoundsException f){
 					if(!TS.equals(""))
 						System.out.println(TS);
 				}
 			}
 		}
 	}
 	
 	//Booleans
 	//Boolean
 	public static boolean readBoolean(String prompt, String NB){
 		return readBooleanCI(prompt, new String[]{"true", "tru", "tr", "t"}, new String[]{"false", "fals", "fal", "fa", "f"}, NB);
 	}
 	
 	//Boolean with yes/no inputs
 	public static boolean readBooleanYN(String prompt, String NB){
 		return readBooleanCI(prompt, new String[]{"yes", "y", "ye", "yas", "ya"}, new String[]{"no", "n", "nah", "na", "neh", "ne"}, NB);
 	}
 	
 	//Boolean with custom inputs (Base Method)
 	public static boolean readBooleanCI(String prompt, String[] trueInputs, String[] falseInputs, String NB){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
				System.out.println(prompt);
			
 			try{
 				s = reader.readLine();
				
				for(int i = 0; i < trueInputs.length; i++)
					if(s.equalsIgnoreCase(trueInputs[i]))
						return true;
				
				for(int i = 0; i < falseInputs.length; i++)
					if(s.equalsIgnoreCase(falseInputs[i]))
						return false;
				
				if(!NB.equals(""))
					System.out.println(NB);
 			} catch(IOException e){
 				System.out.println("IO Exception");
 			}
 		}
 	}
 	
 	//Chars
 	//Char
 	public static char readChar(String prompt, String TMC, String NC){
 		String s = null;
 		
 		while(true){
 			if(!prompt.equals(""))
 				System.out.println(prompt);
 			
 			try{
 				s = reader.readLine();
 				
 				try{
 					char tmp = s.charAt(1);
 					
 					if(!TMC.equals(""))
 						System.out.println(TMC);
 				} catch(StringIndexOutOfBoundsException e){
 					try{
 						char c = s.charAt(0);
 						
 						return c;
 					} catch(StringIndexOutOfBoundsException f){
 						if(!NC.equals(""))
 							System.out.println(NC);
 					}
 				}
 			} catch(IOException e){
 				System.out.println("IO Exception");
 			}
 		}
 	}
 	
 	//Char with specific inputs
 	public static char readCharS(char[] possible, String prompt, String TMC, String NC, String NA){
 		char c = ' ';
 		
 		while(true){
 			c = readChar(prompt, TMC, NC);
 			
 			for(int i = 0; i < possible.length; i++)
 				if(c == possible[i])
 					return c;
 			
 			if(!NA.equals(""))
 				System.out.println(NA);
 		}
 	}
 	
 	//Char with specific inputs, returns index
 	public static int readCharSI(char[] possible, String prompt, String TMC, String NC, String NA){
 		char c = readCharS(possible, prompt, TMC, NC, NA);
 		
 		for(int i = 0; i < possible.length; i++)
 			if(c == possible[i])
 				return i;
 		
 		return -1; //Shouldn't happen but necessary for compilation
 	}
 	
 	//Support Methods
 	//Parse Odd/Even
 	public static boolean parseOE(String oe){
 		if(oe.equalsIgnoreCase("odd") || oe.equalsIgnoreCase("o") || oe.equalsIgnoreCase("od")){
 			return false;
 		} else if(oe.equalsIgnoreCase("even") || oe.equalsIgnoreCase("e") || oe.equalsIgnoreCase("ev") || oe.equalsIgnoreCase("eve")){
 			return true;
 		} else {
 			throw new IllegalArgumentException();
 		}
 	}
 }