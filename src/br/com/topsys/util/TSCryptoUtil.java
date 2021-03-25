package br.com.topsys.util;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import br.com.topsys.exception.TSSystemException;


public final class TSCryptoUtil {

	private static final byte[] CHAVE = "top10sysSistemas".getBytes();

	
	private TSCryptoUtil() {
	}

	
	public static String criptografar(Object texto) {

		return String.valueOf(hexDump(criptografarByte(texto)));

	}

	public static byte[] criptografarByte(Object texto) {

		byte[] cipherText = null;
		byte[] plainText = null;

		try {
			if (texto == null) {
				return null;
			}
			if (texto instanceof String || texto instanceof Long) {
				plainText = (texto.toString()).getBytes("UTF8");
			} else {
				return null;
			}

			Key key = new SecretKeySpec(CHAVE, "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

			cipher.init(Cipher.ENCRYPT_MODE, key);

			cipherText = cipher.doFinal(plainText);

		} catch (Exception e) {
			throw new TSSystemException(e);
		}
		return cipherText;
	}

	public static String desCriptografar(String texto) {

		String retorno = null;

		try {
			byte[] newPlainText = null;

			if (texto == null) {
				return null;
			}
			Key chave = new SecretKeySpec(CHAVE, "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

			cipher.init(Cipher.DECRYPT_MODE, chave);

			newPlainText = cipher.doFinal(DatatypeConverter.parseHexBinary(texto.replace("\\x", "")));

			retorno = new String(newPlainText, "UTF8");

		} catch (Exception e) {
			throw new TSSystemException(e);
		}

		return retorno;
	}

	public static String desCriptografar(byte[] texto) {

		String retorno = null;

		try {
			byte[] newPlainText = null;

			if (texto == null) {
				return null;
			}
			Key chave = new SecretKeySpec(CHAVE, "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

			cipher.init(Cipher.DECRYPT_MODE, chave);

			newPlainText = cipher.doFinal(texto);

			retorno = new String(newPlainText, "UTF8");

		} catch (Exception e) {
			throw new TSSystemException(e);
		}

		return retorno;
	}

	public static void main(String[] args) {
		System.out.println(TSCryptoUtil.desCriptografar(TSCryptoUtil.criptografar("andre")));
	}

	
	private static char[] hexDump(byte[] entrada) {
		char[] buf = new char[entrada.length * 2];

		for (int b = 0; b < entrada.length; b++) {
			String hexByte = Integer.toHexString((int) entrada[b] & 0xff);

			if (hexByte.length() < 2) {
				buf[(b * 2) + 0] = '0';
				buf[(b * 2) + 1] = hexByte.charAt(0);
			} else {
				buf[(b * 2) + 0] = hexByte.charAt(0);
				buf[(b * 2) + 1] = hexByte.charAt(1);
			}
		}

		return buf;
	}


	private static byte[] byteDump(String hexTexto) {
		byte[] byteRetorno = new byte[hexTexto.length() / 2];

		for (int i = 0, j = 0; i <= (hexTexto.length() - 2); i += 2, j++) {
			byteRetorno[j] = (byte) (Integer.parseInt(hexTexto.substring(i, i + 2), 16));
		}

		return byteRetorno;
	}


	public static boolean isCriptografado(String hexTexto) {

		if (hexTexto == null) {
			return false;
		}

		try {
			Key chave = new SecretKeySpec(CHAVE, "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

			cipher.init(Cipher.DECRYPT_MODE, chave);

			byte[] newPlainText = cipher.doFinal(byteDump(hexTexto));
		} catch (RuntimeException e) {
			TSLogUtil.getInstance().severe(e.getMessage());

			return false;
		} catch (Exception e) {
			TSLogUtil.getInstance().severe(e.getMessage());

			return false;
		}

		return true;
	}

	
	public static String gerarHash(String plainText, String algorithm) {

		MessageDigest mdAlgorithm;

		StringBuffer hexString = new StringBuffer();

		try {

			mdAlgorithm = MessageDigest.getInstance(algorithm);

			mdAlgorithm.update(plainText.getBytes());

			byte[] digest = mdAlgorithm.digest();

			for (int i = 0; i < digest.length; i++) {

				plainText = Integer.toHexString(0xFF & digest[i]);

				if (plainText.length() < 2) {

					plainText = "0" + plainText;
				}

				hexString.append(plainText);
			}

		} catch (Exception e) {

			throw new TSSystemException(e);
		}

		return hexString.toString();
	}

	private static String toHex(byte[] hash) {
		final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		char strHash[] = new char[hash.length * 2];
		for (int i = 0, x = 0; i < hash.length; i++) {
			strHash[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
			strHash[x++] = HEX_CHARS[hash[i] & 0xf];
		}
		return new String(strHash);

		// BASE64Encoder b64 = new BASE64Encoder();
		// return b64.encode(hash);
	}

	private static String strHexVal = "0123456789abcdef";

	public static byte[] convertStringToByteArray(String strInput) {

		strInput = strInput.toLowerCase();
		byte[] byteConverted = new byte[(strInput.length() + 1) / 2];
		int j = 0;
		int interimVal;
		int nibble = -1;

		for (int i = 0; i < strInput.length(); ++i) {
			interimVal = strHexVal.indexOf(strInput.charAt(i));
			if (interimVal >= 0) {
				if (nibble < 0) {
					nibble = interimVal;
				} else {
					byteConverted[j++] = (byte) ((nibble << 4) + interimVal);
					nibble = -1;
				}
			}
		}

		if (nibble >= 0) {
			byteConverted[j++] = (byte) (nibble << 4);
		}

		if (j < byteConverted.length) {
			byte[] byteTemp = new byte[j];
			System.arraycopy(byteConverted, 0, byteTemp, 0, j);
			byteConverted = byteTemp;
		}

		return byteConverted;
		/*
		 * BASE64Decoder decoder = new BASE64Decoder(); try { return
		 * decoder.decodeBuffer(strInput); } catch (Exception e) { throw new
		 * TSSystemException(e); }
		 */
	}

}
