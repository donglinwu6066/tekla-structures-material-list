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
	 * �w�]�c�y��k�A�ϥιw�]���_
	 */
	public Crypto() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * ���w���_�c�y��k
	 * 
	 * @param strKey
	 *            ���w�����_
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

	/** �r��w�]��� */
	private static String strDefaultKey = "delicate";
	/** �[�K�u�� */
	private Cipher encryptCipher = null;
	/** �ѱK�u�� */
	private Cipher decryptCipher = null;

	/**
	 * �Nbyte�}�C�ഫ�����16�i���Ȫ��r��A �p�Gbyte[]{8,18}�ഫ���G0813�A �Mpublic static byte[]
	 * hexStr2ByteArr(String strIn) �����i�f���ഫ�L�{
	 * 
	 * @param arrB
	 *            �ݭn�ഫ��byte�}�C
	 * @return �ഫ�᪺�r��
	 * @throws Exception����k���B�z���󲧱`�A�Ҧ����`�������Y
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// �C��byte�Ψ�Ӧr���~���ܡA�ҥH�r�ꪺ���׬O�}�C���ת��⭿
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// ��t���ഫ������
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// �p��0F���ƻݭn�b�e����0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * �N���16�i���Ȫ��r���ഫ��byte�}�C�A �Mpublic static String byteArr2HexStr(byte[] arrB)
	 * �����i�f���ഫ�L�{
	 * 
	 * @param strIn
	 *            �ݭn�ഫ���r��
	 * @return �ഫ�᪺byte�}�C
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// ��Ӧr����ܤ@�Ӧ줸�աA�ҥH�줸�հ}�C���׬O�r����װ��H2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
			System.out.println("arrOut[i / 2] " + arrOut[i / 2]);
		}
		return arrOut;
	}

	/**
	 * �[�K�줸�հ}�C
	 * 
	 * @param arrB
	 *            �ݥ[�K���줸�հ}�C
	 * @return �[�K�᪺�줸�հ}�C
	 */
	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * �[�K�r��
	 * 
	 * @param strIn
	 *            �ݥ[�K���r��
	 * @return �[�K�᪺�r��
	 */
	public String encrypt(String strIn) throws Exception {
		return byteArr2HexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * �ѱK�줸�հ}�C
	 * 
	 * @param arrB
	 *            �ݸѱK���줸�հ}�C
	 * @return �ѱK�᪺�줸�հ}�C
	 */
	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * �ѱK�r��
	 * 
	 * @param strIn
	 *            �ݸѱK���r��
	 * @return �ѱK�᪺�r��
	 */
	public String decrypt(String strIn) throws Exception {
		return new String(decrypt(hexStr2ByteArr(strIn)));
	}

	/**
	 * �q���w�r��ͦ����_�A���_�һݪ��줸�հ}�C���׬�8�� ����8��ɫ᭱��0�A�W�X8��u���e8��
	 * @param arrBTmp�c���Ӧr�ꪺ�줸�հ}�C
	 * @return �ͦ������_
	 */
	private Key getKey(byte[] arrBTmp) throws Exception {
		// �إߤ@�ӪŪ�8��줸�հ}�C�]�w�]�Ȭ�0�^
		byte[] arrB = new byte[8];
		// �N��l�줸�հ}�C�ഫ��8��
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		// �ͦ����_
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
