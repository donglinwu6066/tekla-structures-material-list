package bnotai.tekla.material.fileio;

import java.lang.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

public class Crypto {
	/**
	 * 預設構造方法，使用預設金鑰
	 */
	public Crypto() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * 指定金鑰構造方法
	 * 
	 * @param strKey
	 *            指定的金鑰
	 * @throws Exception
	 */
	public Crypto(String strKey) throws Exception {
		// Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = getKey(strKey.getBytes());
		encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("Fearless".getBytes());
        AlgorithmParameterSpec paramSpec = iv;
		encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
	}

	/** 字串預設鍵值 */
	private static String strDefaultKey = "delicate";
	/** 加密工具 */
	private Cipher encryptCipher = null;
	/** 解密工具 */
	private Cipher decryptCipher = null;

	/**
	 * 將byte陣列轉換為表示16進位制值的字串， 如：byte[]{8,18}轉換為：0813， 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互為可逆的轉換過程
	 * 
	 * @param arrB
	 *            需要轉換的byte陣列
	 * @return 轉換後的字串
	 * @throws Exception本方法不處理任何異常，所有異常全部丟擲
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每個byte用兩個字元才能表示，所以字串的長度是陣列長度的兩倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把負數轉換為正數
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小於0F的數需要在前面補0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 將表示16進位制值的字串轉換為byte陣列， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互為可逆的轉換過程
	 * 
	 * @param strIn
	 *            需要轉換的字串
	 * @return 轉換後的byte陣列
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// 兩個字元表示一個位元組，所以位元組陣列長度是字串長度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
			System.out.println("arrOut[i / 2] " + arrOut[i / 2]);
		}
		return arrOut;
	}

	/**
	 * 加密位元組陣列
	 * 
	 * @param arrB
	 *            需加密的位元組陣列
	 * @return 加密後的位元組陣列
	 */
	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * 加密字串
	 * 
	 * @param strIn
	 *            需加密的字串
	 * @return 加密後的字串
	 */
	public String encrypt(String strIn) throws Exception {
		return byteArr2HexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * 解密位元組陣列
	 * 
	 * @param arrB
	 *            需解密的位元組陣列
	 * @return 解密後的位元組陣列
	 */
	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * 解密字串
	 * 
	 * @param strIn
	 *            需解密的字串
	 * @return 解密後的字串
	 */
	public String decrypt(String strIn) throws Exception {
		return new String(decrypt(hexStr2ByteArr(strIn)));
	}

	/**
	 * 從指定字串生成金鑰，金鑰所需的位元組陣列長度為8位 不足8位時後面補0，超出8位只取前8位
	 * @param arrBTmp構成該字串的位元組陣列
	 * @return 生成的金鑰
	 */
	private Key getKey(byte[] arrBTmp) throws Exception {
		// 建立一個空的8位位元組陣列（預設值為0）
		byte[] arrB = new byte[8];
		// 將原始位元組陣列轉換為8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		// 生成金鑰
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
		return key;
	}
	public String gethostname() throws Exception {
		//get host name
		InetAddress ip = InetAddress.getLocalHost();
		return ip.getHostName();
	}
	public String getMachineID() throws Exception {
		String command = "wmic csproduct get UUID";
		StringBuffer output = new StringBuffer();

		Process SerNumProcess = Runtime.getRuntime().exec(command);
		BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));

		String line = "";
		while ((line = sNumReader.readLine()) != null) {
			output.append(line + "\n");
		}
		
		// get machine id
		String MachineID = output.toString().substring(output.indexOf("\n"), output.length());
		MachineID = MachineID.replace("\n", "").replace("\r", "");
		return MachineID;
	}
	
}
