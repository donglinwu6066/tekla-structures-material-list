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
	 * 箇砞篶硑よ猭ㄏノ箇砞芲
	 */
	public Crypto() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * ﹚芲篶硑よ猭
	 * 
	 * @param strKey
	 *            ﹚芲
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

	/** ﹃箇砞龄 */
	private static String strDefaultKey = "delicate";
	/** 盞ㄣ */
	private Cipher encryptCipher = null;
	/** 秆盞ㄣ */
	private Cipher decryptCipher = null;

	/**
	 * 盢byte皚锣传ボ16秈﹃ byte[]{8,18}锣传0813 ㎝public static byte[]
	 * hexStr2ByteArr(String strIn) が癴锣传筁祘
	 * 
	 * @param arrB
	 *            惠璶锣传byte皚
	 * @return 锣传﹃
	 * @throws Exceptionセよ猭ぃ矪瞶ヴ钵盽┮Τ钵盽场メ耏
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// –byteノㄢじボ┮﹃琌皚ㄢ
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// р璽计锣传タ计
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 0F计惠璶玡干0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 盢ボ16秈﹃锣传byte皚 ㎝public static String byteArr2HexStr(byte[] arrB)
	 * が癴锣传筁祘
	 * 
	 * @param strIn
	 *            惠璶锣传﹃
	 * @return 锣传byte皚
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// ㄢじボじ舱┮じ舱皚琌﹃埃2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * 盞じ舱皚
	 * 
	 * @param arrB
	 *            惠盞じ舱皚
	 * @return 盞じ舱皚
	 */
	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * 盞﹃
	 * 
	 * @param strIn
	 *            惠盞﹃
	 * @return 盞﹃
	 */
	public String encrypt(String strIn) throws Exception {
		return byteArr2HexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * 秆盞じ舱皚
	 * 
	 * @param arrB
	 *            惠秆盞じ舱皚
	 * @return 秆盞じ舱皚
	 */
	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * 秆盞﹃
	 * 
	 * @param strIn
	 *            惠秆盞﹃
	 * @return 秆盞﹃
	 */
	public String decrypt(String strIn) throws Exception {
		return new String(decrypt(hexStr2ByteArr(strIn)));
	}

	/**
	 * 眖﹚﹃ネΘ芲芲┮惠じ舱皚8 ぃì8干0禬8玡8
	 * @param arrBTmp篶Θ赣﹃じ舱皚
	 * @return ネΘ芲
	 */
	private Key getKey(byte[] arrBTmp) throws Exception {
		// ミ8じ舱皚箇砞0
		byte[] arrB = new byte[8];
		// 盢﹍じ舱皚锣传8
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		// ネΘ芲
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
