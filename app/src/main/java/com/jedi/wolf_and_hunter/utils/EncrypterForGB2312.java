package com.jedi.wolf_and_hunter.utils;

import java.util.Random;


/**
 * 加密器，含加密与解密功能。实现安全中的加密接口，同时，提供其他扩展功能。

 * GB2312编码的文本进行加密时，不能超越此编码范围。因此，对其进行特殊处理。置换时，以字为单位进行置换，置换后，仍为合法的GB2312编码的文字。

 * 英文字母单独处理。同时，对密钥进行处理，将其变换值仅作为置换的移位数，而不作异或处理。liufq 20070820
 * @author 刘福强 20070820
 */
public class EncrypterForGB2312{ 
	private byte[] cipherKey = "30SAN_MOBILE_KEY".getBytes();
	/**
	 * ASCII码置换表。

	 */
	private static final int[] encryptTableAscii = { 
		117, 109, 51,  111, 79,  50,  61,  99,  70,  89,  56,  68,  100, 86,  35,  27,  
		49,  38,  85,  23,  81,  118, 83,  28,  47,  26,  55,  20,  127, 78,  42,  18,  
		92,  84,  124, 58,  123, 65,  67,  36,  8,   77,  71,  14,  17,  93,  22,  66,  
		102, 0,   32,  107, 72,  52,  30,  76,  64,  25,  69,  16,  53,  5,   59,  82,  
		101, 74,  41,  34,  40,  120, 91,  63,  10,  13,  95,  119, 80,  87,  37,  6,   
		15,  73,  90,  110, 126, 105, 44,  33,  98,  3,   29,  94,  57,  39,  113, 75,  
		43,  97,  31,  116, 62,  106, 96,  104, 46,  54,  4,   24,  112, 19,  21,  1,   
		125, 45,  103, 114, 9,   7,   121, 88,  12,  122, 48,  60,  11,  115, 2,   108
	};
	private static final int[] encryptTableAsciiIndex = { 
		49,  111, 126, 89,  106, 61,  79,  117, 40,  116, 72,  124, 120, 73,  43,  80,  
		59,  44,  31,  109, 27,  110, 46,  19,  107, 57,  25,  15,  23,  90,  54,  98,  
		50,  87,  67,  14,  39,  78,  17,  93,  68,  66,  30,  96,  86,  113, 104, 24,  
		122, 16,  5,   2,   53,  60,  105, 26,  10,  92,  35,  62,  123, 6,   100, 71,  
		56,  37,  47,  38,  11,  58,  8,   42,  52,  81,  65,  95,  55,  41,  29,  4,   
		76,  20,  63,  22,  33,  18,  13,  77,  119, 9,   82,  70,  32,  45,  91,  74,  
		102, 97,  88,  7,   12,  64,  48,  114, 103, 85,  101, 51,  127, 1,   83,  3,   
		108, 94,  115, 125, 99,  0,   21,  75,  69,  118, 121, 36,  34,  112, 84,  28
	};
	/**
	 * 汉字置换表，首字节

	 */
	private static final int[] encryptTableFirst = { 
		109, 75,  27,  39,  40,  102, 86,  13,  78,  93,  1,   5,   8,   42,  9,   83,  
		14,  71,  69,  115, 65,  52,  108, 82,  34,  81,  24,  38,  77,  30,  60,  2,   
		15,  51,  92,  0,   11,  90,  117, 44,  87,  58,  64,  113, 67,  97,  121, 20,  
		99,  19,  79,  12,  22,  110, 94,  53,  59,  76,  16,  4,   21,  73,  88,  105, 
		125, 18,  28,  6,   47,  10,  95,  70,  123, 26,  45,  80,  103, 85,  91,  56,  
		66,  50,  111, 84,  98,  37,  72,  41,  114, 32,  17,  63,  33,  36,  119, 100, 
		23,  57,  96,  116, 3,   31,  106, 68,  62,  49,  124, 55,  54,  118, 29,  120, 
		112, 48,  101, 122, 89,  74,  107, 43,  35,  61,  25,  7,   104, 46
	};
	private static final int[] encryptTableFirstIndex = { 
		35,  10,  31,  100, 59,  11,  67,  123, 12,  14,  69,  36,  51,  7,   16,  32,  
		58,  90,  65,  49,  47,  60,  52,  96,  26,  122, 73,  2,   66,  110, 29,  101, 
		89,  92,  24,  120, 93,  85,  27,  3,   4,   87,  13,  119, 39,  74,  125, 68,  
		113, 105, 81,  33,  21,  55,  108, 107, 79,  97,  41,  56,  30,  121, 104, 91,  
		42,  20,  80,  44,  103, 18,  71,  17,  86,  61,  117, 1,   57,  28,  8,   50,  
		75,  25,  23,  15,  83,  77,  6,   40,  62,  116, 37,  78,  34,  9,   54,  70,  
		98,  45,  84,  48,  95,  114, 5,   76,  124, 63,  102, 118, 22,  0,   53,  82,  
		112, 43,  88,  19,  99,  38,  109, 94,  111, 46,  115, 72,  106, 64
	};
	/**
	 * 汉字置换表，尾字节，第一个取值范围

	 */
	private static final int[] encryptTableSecond1 = { 
		46,  12,  8,   25,  6,   23,  58,  42,  13,  10,  51,  18,  37,  52,  48,  32,  
		50,  47,  41,  31,  56,  15,  0,   28,  29,  57,  16,  5,   33,  39,  40,  30,  
		20,  43,  2,   19,  24,  21,  60,  36,  27,  26,  7,   17,  59,  14,  3,   53,  
		38,  22,  35,  54,  11,  34,  49,  1,   61,  9,   55,  4,   62,  44,  45
	};
	private static final int[] encryptTableSecond1Index = { 
		22,  55,  34,  46,  59,  27,  4,   42,  2,   57,  9,   52,  1,   8,   45,  21,  
		26,  43,  11,  35,  32,  37,  49,  5,   36,  3,   41,  40,  23,  24,  31,  19,  
		15,  28,  53,  50,  39,  12,  48,  29,  30,  18,  7,   33,  61,  62,  0,   17,  
		14,  54,  16,  10,  13,  47,  51,  58,  20,  25,  6,   44,  38,  56,  60
	};
	/**
	 * 汉字置换表，尾字节，第二个取值范围

	 */
	private static final int[] encryptTableSecond2 = { 
		107, 26,  98,  9,   17,  18,  73,  74,  83,  60,  4,   62,  59,  124, 47,  37,  
		66,  64,  120, 53,  106, 1,   69,  81,  61,  31,  104, 68,  101, 80,  25,  36,  
		108, 38,  41,  72,  48,  125, 33,  115, 119, 102, 88,  95,  49,  55,  86,  27,  
		91,  0,   111, 79,  12,  32,  29,  82,  113, 112, 94,  110, 114, 65,  3,   123, 
		70,  126, 16,  15,  50,  71,  44,  20,  121, 63,  58,  24,  11,  76,  10,  39,  
		109, 14,  96,  40,  13,  84,  45,  87,  57,  97,  99,  77,  85,  89,  90,  117, 
		8,   103, 116, 5,   2,   56,  6,   21,  100, 42,  51,  52,  7,   22,  93,  75,  
		122, 54,  118, 92,  30,  43,  34,  28,  19,  78,  46,  23,  105, 35,  67
	};
	private static final int[] encryptTableSecond2Index = { 
		49,  21,  100, 62,  10,  99,  102, 108, 96,  3,   78,  76,  52,  84,  81,  67,  
		66,  4,   5,   120, 71,  103, 109, 123, 75,  30,  1,   47,  119, 54,  116, 25,  
		53,  38,  118, 125, 31,  15,  33,  79,  83,  34,  105, 117, 70,  86,  122, 14,  
		36,  44,  68,  106, 107, 19,  113, 45,  101, 88,  74,  12,  9,   24,  11,  73,  
		17,  61,  16,  126, 27,  22,  64,  69,  35,  6,   7,   111, 77,  91,  121, 51,  
		29,  23,  55,  8,   85,  92,  46,  87,  42,  93,  94,  48,  115, 110, 58,  43,  
		82,  89,  2,   90,  104, 28,  41,  97,  26,  124, 20,  0,   32,  80,  59,  50,  
		57,  56,  60,  39,  98,  95,  114, 40,  18,  72,  112, 63,  13,  37,  65
	};
	public EncrypterForGB2312(){
		setCipherKey("30SAN_MOBILE_KEY");
	}
	public EncrypterForGB2312(String key){
		setCipherKey(key);
	}
	public void setCipherKey(String key){
		setCipherKey(key.getBytes());
	}
	public void setCipherKey(byte[] key){
		cipherKey = key;
	}
	public String encrypt(String content) throws Exception {
		if (content == null) return null;
		return new String(encrypt(content.getBytes("GB18030")),"GB18030");
	} 
	public String decrypt(String content) throws Exception {
		if (content == null) return null;
		return new String(decrypt(content.getBytes("GB18030")),"GB2312");
	}
	/**
	 * 对提交的数据进行加密，并返回加密后的数据。

	 * @param plainText byte[] 明文
	 * @return byte[] 密文
	 */
	public byte[] encrypt(byte[] plainText){
		// 对输入的字节流按GB2312编码规则进行解码，并分别使用相应的置换表对其进行置换。

		// 置换时，使用密钥对明文置换后的值进行增值处理，如果超过，则循环。

		// 单字节部分采用GB/T 11383的编码结构与规则，使用0×00至0×7F码位(对应于ASCII码的相应码位)。

		// 双字节部分，首字节码位从0×81至0×FE，尾字节码位分别是0×40至0×7E和0×80至0×FE。

		// 对于
		byte[] cipherText = plainText; // 增加一个变量，便于理解。

		// 加密。做变换
		//System.out.println("变换前*" + new String(plainText) + "*");
		for(int i=0, j=0; i<plainText.length; i++, j++){
			if (j >= cipherKey.length) j = 0; // 循环使用密钥。

			int byteValue = plainText[i] & 0xff ;
			if (byteValue < 128){ // 判断首位是0还是1，从而确定当前是一个ASCII码，还是汉字的开始。

				// 计算置换的位置。同时，使用密钥。

				int newIndex = (byteValue) + (cipherKey[j] & 0xff);
				while (newIndex >= encryptTableAscii.length) newIndex -= encryptTableAscii.length; // 循环使用置换表。

				cipherText[i] = (byte)encryptTableAscii[newIndex]; // 置换。

				
			}else{ // 不是ASCII码，则必然是汉字的开始。同时处理两个字节。

				// 计算置换的位置。同时，使用密钥。

				int newIndex = (byteValue - 0x81) + (cipherKey[j] & 0xff);
				while (newIndex >= encryptTableFirst.length) newIndex -= encryptTableFirst.length; // 循环使用置换表。

				cipherText[i] = (byte)(encryptTableFirst[newIndex] + 0x81);
				i++; // 加1，以便处理第二个字节。

				j++;
				if (j >= cipherKey.length) j = 0; // 循环使用密钥。

				byteValue = plainText[i] & 0xff; // 计算第二个字节的值。

				if (byteValue <= 0x7E) {
					// 计算置换的位置。同时，使用密钥。

					newIndex = (byteValue - 0x40) + (cipherKey[j] & 0xff);
					while (newIndex >= encryptTableSecond1.length) newIndex -= encryptTableSecond1.length; // 循环使用置换表。

					cipherText[i] = (byte)(encryptTableSecond1[newIndex] + 0x40);
				}else{
					// 计算置换的位置。同时，使用密钥。

					newIndex = (byteValue - 0x80) + (cipherKey[j] & 0xff);
					while (newIndex >= encryptTableSecond2.length) newIndex -= encryptTableSecond2.length; // 循环使用置换表。

					cipherText[i] = (byte)(encryptTableSecond2[newIndex] + 0x80);
				}
			}
		}
		//System.out.println("变换后*" + new String(cipherText) + "*");
		return cipherText;
	}
	public byte[] decrypt(byte[] cipherText, byte[] key){
		setCipherKey(key);
		return decrypt(cipherText);
	}
	
	
	
	public byte[] decrypt(byte[] cipherText){
		// 加密的逆运算。找到位置，反向使用密钥，得到明文。

		//System.out.println("使用密钥前*" + new String(cipherText) + "*");
		byte[] plainText = cipherText;
		for(int i=0, j=0; i<plainText.length; i++, j++){
			
			if (j >= cipherKey.length) j = 0; // 循环使用密钥。

			// 判断当前是ASCII码，还是汉字
			int byteValue = plainText[i] & 0xff ;
			if (byteValue < 128){ // 判断首位是0还是1，从而确定当前是一个ASCII码，还是汉字的开始。

				int index = encryptTableAsciiIndex[byteValue]; // 找到位置
				index = index - (cipherKey[j] & 0xff); // 应用密钥
				while (index < 0) index += encryptTableAscii.length; // 循环使用置换表。

				cipherText[i] = (byte)index; // 当前的位置就是对应的明文
			}else{ // 不是ASCII码，则必然是汉字的开始。同时处理两个字节。

				int index = encryptTableFirstIndex[byteValue - 0x81]; // 找到位置
				index = index - (cipherKey[j] & 0xff); // 应用密钥
				while (index < 0) index += encryptTableFirst.length; // 循环使用置换表。

				cipherText[i] = (byte)(index + 0x81); // 当前的位置就是对应的明文
				i++; // 加1，以便处理第二个字节。

				j++;
				
				if (j >= cipherKey.length) j = 0; // 循环使用密钥。

				byteValue = plainText[i] & 0xff; // 计算第二个字节的值。

				if (byteValue <= 0x7E) {
					index = encryptTableSecond1Index[byteValue - 0x40]; // 找到位置
					index = index - (cipherKey[j] & 0xff); // 应用密钥
					while (index < 0) index += encryptTableSecond1.length; // 循环使用置换表。

					cipherText[i] = (byte)(index + 0x40); // 当前的位置就是对应的明文
				}else{
					index = encryptTableSecond2Index[byteValue - 0x80]; // 找到位置
					index = index - (cipherKey[j] & 0xff); // 应用密钥
					while (index < 0) index += encryptTableSecond2.length; // 循环使用置换表。

					cipherText[i] = (byte)(index + 0x80); // 当前的位置就是对应的明文
				}
			}
		}
		//System.out.println("变换后*" + new String(plainText) + "*");
		return plainText;
	}
	/**
	 * 置换表生成工具。按照GB2312的编码规则生成各段的置换表。

	 * 单字节部分采用GB/T 11383的编码结构与规则，使用0×00至0×7F码位(对应于ASCII码的相应码位)。

     * 双字节部分，首字节码位从0×81至0×FE，尾字节码位分别是0×40至0×7E和0×80至0×FE。

	 */
	private static void cipherTableCreator(){
		// 生成127以下的置换表
		int n[] = createTableValue(128);
		System.out.print("\n\n置换表　128");
		printTableValue(n);
		// 生成逆置换表。将置换后的数据换回原位。

		int m[] = createReserveTableValue(n);
		System.out.print("\n逆置换表");
		printTableValue(m);
		
		// 生成首字节置换表
		n = createTableValue(0xFE - 0x81 + 1);
		System.out.print("\n\n置换表　0xFE - 0x81");
		printTableValue(n);
		// 生成逆置换表。将置换后的数据换回原位。

		m = createReserveTableValue(n);
		System.out.print("\n逆置换表");
		printTableValue(m);
		
		// 生成首字节置换表
		n = createTableValue(0x7E - 0x40 + 1);
		System.out.print("\n\n置换表　0x7E - 0x40");
		printTableValue(n);
		// 生成逆置换表。将置换后的数据换回原位。

		m = createReserveTableValue(n);
		System.out.print("\n逆置换表");
		printTableValue(m);
		
		// 生成首字节置换表
		n = createTableValue(0xFE - 0x80 + 1);
		System.out.print("\n\n置换表　0xFE - 0x80");
		printTableValue(n);
		// 生成逆置换表。将置换后的数据换回原位。

		m = createReserveTableValue(n);
		System.out.print("\n逆置换表");
		printTableValue(m);
		
	}
	private static int[] createTableValue(int maxValue){
		int n[] = new int[maxValue];
		for(int i=0; i<n.length; i++){
			n[i] = -1;
		}
		Random a = new Random(System.currentTimeMillis());
		for(int i=0; i<n.length; i++){
			// 产生随机数，形成128个位置的乱序
			for(int count=0; count < 1000; count ++){
				int j = a.nextInt(n.length);
				boolean exist = false;
				for(int k = 0; k<i; k++){
					if (n[k] == j){
						exist = true; 
						break;
					}
				}
				if (exist == false){
					n[i] = j;
					break;
				}
			}
		}
		return n;
	}
	private static int[] createReserveTableValue(int[] n){
		int m[] = new int[n.length];
		for(int i=0; i<n.length; i++){
			m[n[i]] = i;
		}
		return m;
	}
	private static int getIndex(int[] table, int value){
		for(int i=0; i<table.length; i++){
			if (table[i] == value){
				return i;
			}
		}
		return -1;
	}
	private static int[] createTableValueIndex(int[] n){
		int m[] = new int[n.length];
		for(int j=0; j<n.length; j++){
			m[j] = getIndex(n, j);
		}
		return m;
	}
	private static void printTableValue(int[] table){
		for(int i=0; i<table.length; i++){
			String nn = String.valueOf(table[i]);
			nn = nn + ",";
			while(nn.length() < 5) nn += " ";
			if ((i - ((i / 16) * 16)) == 0){
				System.out.println();
			}
			System.out.print(nn);
		}
	}
//	public static void main(String[] argv) throws Exception{
//		//cipherTableCreator();
//		/*int[] m;
//		System.out.print("\nASCII码置换表 索引");
//		m = createTableValueIndex(encryptTableAscii);
//		printTableValue(m);
//		System.out.println();
//
//		System.out.print("\n汉字置换表，首字节 索引");
//		m = createTableValueIndex(encryptTableFirst);
//		printTableValue(m);
//		System.out.println();
//
//		System.out.print("\n汉字置换表，尾字节，第一个取值范围 索引");
//		m = createTableValueIndex(encryptTableSecond1);
//		printTableValue(m);
//		System.out.println();
//
//		System.out.print("\n汉字置换表，尾字节，第二个取值范围 索引");
//		m = createTableValueIndex(encryptTableSecond2);
//		printTableValue(m);
//		System.out.println();*/
//
//		
//		EncrypterForGB2312 en = new EncrypterForGB2312();
//		/*String a = "獉獊兀獌獎獏獑獓獔獕獖獘獙獚獛獜獝獞獟獡獢獣獤獥獦獧獨獩獪獫獮獰獱";
//		byte[] aa = a.getBytes();
//		for(int i=0; i<aa.length; i++){
//			if ((aa[i] & 0xff) < 128) {
//				Tools.printDebugMsg(String.valueOf((aa[i] & 0xff)), i);
//			}
//		}
//		String b = en.encrypt("aa", a); 
//		Tools.printDebugMsg(a, b);
//		Tools.printDebugMsg("长度", String.valueOf(a.length() == b.length()));
//		aa = b.getBytes();
//		for(int i=0; i<aa.length; i++){
//			if ((aa[i] & 0xff) < 128) {
//				Tools.printDebugMsg(String.valueOf((aa[i] & 0xff)), i);
//			}
//		}
//		String c = en.decrypt("aa", b);
//		Tools.printDebugMsg(a, c);
//		Tools.printDebugMsg("是否相等", String.valueOf(a.equals(c)));
//		//cipherTableCreator();
//		byte[] aaa = {(byte)254, (byte)63};
//		Tools.printDebugMsg(new String(aaa));*/
//		//String a = FileTools.getFileContent("d:\\a.txt");//"你好！獉獊兀獌獎獏獑獓獔3獕獖獘獙獚獛獜獝獞獟獡獢7獣獤獥獦獧1獨獩獪獫獮獰獱";
//		// 进行性能测试
//		Date startTime = new Date();
//		for(int i=0; i<1; i++){
//			
//			for(int c = 0 ; c < en.cipherKey.length ; c++)
//			{
//				System.out.println("cipherKey["+c+"]:"+ en.cipherKey[c]);
//			}
//			
//			
//			//原文
//			//String ori = "男，195308(57)，中央党校研究生，哲学硕士，山东惠民，市委省委常委，市委书记兼广州警备区党委第一书记";
//			
//			String ori = "王国如";
//			byte[] oriByte = ori.getBytes();
//			for(int j=0; j<oriByte.length; j++)
//			{
//				System.out.println("plainByte["+j+"]:"+oriByte[j]);
//			}
//			
//			//加密
//			String cipherText = en.encrypt(ori);
//			byte[] cipherByte = cipherText.getBytes("GB18030");
//			for(int k=0; k<cipherByte.length; k++)
//			{
//				System.out.println("cipherText["+k+"]:"+cipherByte[k]);
//			}
//			
//			//解密
//			String plainText= en.decrypt(cipherText);
//			//Tools.printDebugMsg(i + " " + a.equals(c), a.length() + " " + b.length() + " " + c.length());
//			System.out.println(ori);
//			System.out.println(cipherText);
//			System.out.println(plainText);
//		}
//		//Date endTime = new Date();
//		//System.out.println(startTime.getTime() + " " + endTime.getTime() + " " + (endTime.getTime() - startTime.getTime()));
//	}
	
}



