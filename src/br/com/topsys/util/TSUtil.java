/*
 * Created on 08/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package br.com.topsys.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;

import br.com.topsys.exception.TSSystemException;

/**
 * @author andre
 * 
 */
public final class TSUtil {

	private static final int[] pesoCPF = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final int[] pesoCNPJ = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static final int TAM_CNPJ = 14;

	private static final int TAM_CPF = 11;

	private static final String STRING_DEFAULT = "0";


	public static final String FILE_ENCODING = getSystemProperty("file.encoding");

	
	public static final String FILE_SEPARATOR = getSystemProperty("file.separator");

	
	public static final String JAVA_CLASS_PATH = getSystemProperty("java.class.path");

	
	public static final String JAVA_CLASS_VERSION = getSystemProperty("java.class.version");

	
	public static final String JAVA_COMPILER = getSystemProperty("java.compiler");

	
	public static final String JAVA_EXT_DIRS = getSystemProperty("java.ext.dirs");

	
	public static final String JAVA_HOME = getSystemProperty("java.home");

	
	public static final String JAVA_IO_TMPDIR = getSystemProperty("java.io.tmpdir");

	
	public static final String JAVA_LIBRARY_PATH = getSystemProperty("java.library.path");

	
	public static final String JAVA_RUNTIME_NAME = getSystemProperty("java.runtime.name");

	
	public static final String JAVA_RUNTIME_VERSION = getSystemProperty("java.runtime.version");

	
	public static final String JAVA_SPECIFICATION_NAME = getSystemProperty("java.specification.name");

	
	public static final String JAVA_SPECIFICATION_VENDOR = getSystemProperty("java.specification.vendor");

	
	public static final String JAVA_SPECIFICATION_VERSION = getSystemProperty("java.specification.version");

	
	public static final String JAVA_VENDOR = getSystemProperty("java.vendor");

	
	public static final String JAVA_VENDOR_URL = getSystemProperty("java.vendor.url");


	public static final String JAVA_VERSION = getSystemProperty("java.version");

	
	public static final String JAVA_VM_INFO = getSystemProperty("java.vm.info");

	
	public static final String JAVA_VM_NAME = getSystemProperty("java.vm.name");

	
	public static final String JAVA_VM_SPECIFICATION_NAME = getSystemProperty("java.vm.specification.name");

	
	public static final String JAVA_VM_SPECIFICATION_VENDOR = getSystemProperty("java.vm.specification.vendor");

	
	public static final String JAVA_VM_SPECIFICATION_VERSION = getSystemProperty("java.vm.specification.version");

	
	public static final String JAVA_VM_VENDOR = getSystemProperty("java.vm.vendor");

	
	public static final String JAVA_VM_VERSION = getSystemProperty("java.vm.version");

	
	public static final String LINE_SEPARATOR = getSystemProperty("line.separator");

	
	public static final String OS_ARCH = getSystemProperty("os.arch");

	
	public static final String OS_NAME = getSystemProperty("os.name");

	
	public static final String OS_VERSION = getSystemProperty("os.version");

	
	public static final String PATH_SEPARATOR = getSystemProperty("path.separator");

	
	public static final String USER_COUNTRY = (getSystemProperty("user.country") == null ? getSystemProperty("user.region") : getSystemProperty("user.country"));

	
	public static final String USER_DIR = getSystemProperty("user.dir");

	
	public static final String USER_HOME = getSystemProperty("user.home");

	
	public static final String USER_LANGUAGE = getSystemProperty("user.language");

	
	public static final String USER_NAME = getSystemProperty("user.name");


	public static final boolean IS_JAVA_1_1 = getJavaVersionMatches("1.1");


	public static final boolean IS_JAVA_1_2 = getJavaVersionMatches("1.2");

	public static final boolean IS_JAVA_1_3 = getJavaVersionMatches("1.3");

	
	public static final boolean IS_JAVA_1_4 = getJavaVersionMatches("1.4");

	
	public static final boolean IS_JAVA_1_5 = getJavaVersionMatches("1.5");

	
	public static final boolean IS_OS_AIX = getOSMatches("AIX");

	
	public static final boolean IS_OS_HP_UX = getOSMatches("HP-UX");

	
	public static final boolean IS_OS_IRIX = getOSMatches("Irix");

	
	public static final boolean IS_OS_LINUX = getOSMatches("Linux") || getOSMatches("LINUX");

	
	public static final boolean IS_OS_MAC = getOSMatches("Mac");

	
	public static final boolean IS_OS_MAC_OSX = getOSMatches("Mac OS X");

	
	public static final boolean IS_OS_OS2 = getOSMatches("OS/2");

	
	public static final boolean IS_OS_SOLARIS = getOSMatches("Solaris");

	
	public static final boolean IS_OS_SUN_OS = getOSMatches("SunOS");

	
	public static final boolean IS_OS_WINDOWS = getOSMatches("Windows");

	
	public static final boolean IS_OS_WINDOWS_2000 = getOSMatches("Windows", "5.0");

	
	public static final boolean IS_OS_WINDOWS_95 = getOSMatches("Windows 9", "4.0");

	
	public static final boolean IS_OS_WINDOWS_98 = getOSMatches("Windows 9", "4.1");

	
	public static final boolean IS_OS_WINDOWS_ME = getOSMatches("Windows", "4.9");


	public static final boolean IS_OS_WINDOWS_NT = getOSMatches("Windows NT");

	
	public static final boolean IS_OS_WINDOWS_XP = getOSMatches("Windows", "5.1");

	
	private static boolean getJavaVersionMatches(String versionPrefix) {
		if (JAVA_VERSION == null) {
			return false;
		}
		return JAVA_VERSION.startsWith(versionPrefix);
	}


	private static boolean getOSMatches(String osNamePrefix) {
		if (OS_NAME == null) {
			return false;
		}
		return OS_NAME.startsWith(osNamePrefix);
	}


	private static boolean getOSMatches(String osNamePrefix, String osVersionPrefix) {
		if (OS_NAME == null || OS_VERSION == null) {
			return false;
		}
		return OS_NAME.startsWith(osNamePrefix) && OS_VERSION.startsWith(osVersionPrefix);
	}

	
	private static String getSystemProperty(String property) {
		try {
			return System.getProperty(property);
		} catch (SecurityException ex) {
			// we are not allowed to look at this property
			System.err.println("Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
			return null;
		}
	}

	

	
	public static void sincronizarBeans(Object bean, Object form) {
		try {
			BeanUtils.copyProperties(bean, form);

		} catch (Exception e) {
			throw new TSSystemException(e);
		}
	}

	
	public static String cpfComMascara(String cpf) {
		if (cpf == null || cpf.equals("")) {
			return null;
		}

		cpf = removerCaracter(cpf);

		cpf = preencheString(cpf, TAM_CPF);

		return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}

	
	public static String cnpjComMascara(String cnpj) {
		if (cnpj == null || cnpj.equals("")) {
			cnpj = new String();
		}
		cnpj = removerCaracter(cnpj);

		cnpj = preencheString(cnpj, TAM_CNPJ);

		return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
	}

	
	public static String removerCaracter(String param) {

		if (param == null) {
			return null;
		}

		return param.replaceAll("\\D", "");
	}


	public static String removerNaoDigitos(String param) {

		if (param == null) {
			return null;
		}

		return param.replaceAll("\\D", "");
	}


	private static String preencheString(String param, int tamanho) {

		if (param == null) {
			param = new String();
		}
		int dif = tamanho - param.length();

		for (; dif > 0; dif--) {
			param = STRING_DEFAULT + param;
		}
		return param;
	}

	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}

	public static boolean isNotEmpty(String str) {
		return (str != null && str.length() > 0);
	}

	
	public static boolean isEmpty(Object value) {

		if (value == null) {
			return true;
		} else if (value instanceof Collection) {
			return ((Collection) value).isEmpty();
		} else if (value instanceof String) {
			return isEmpty((String) value);
		}

		return false;
	}


	public static boolean isEmailValid(String email) {
		Pattern patterns = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher matcher = patterns.matcher(email);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	
	public static boolean isNumeric(String numero) {
		return TSStringUtil.isNumeric(numero);

	}

	public static String gerarId() {

		Calendar calendario = Calendar.getInstance();

		int diaAtual = calendario.get(Calendar.DAY_OF_MONTH);
		int mesAtual = calendario.get(Calendar.MONTH) + 1;
		int anoAtual = calendario.get(Calendar.YEAR);
		int horaAtual = calendario.get(Calendar.HOUR_OF_DAY);
		int minutoAtual = calendario.get(Calendar.MINUTE);
		int segAtual = calendario.get(Calendar.SECOND);
		int miliAtual = calendario.get(Calendar.MILLISECOND);

		return String.valueOf(anoAtual) + String.valueOf(mesAtual) + String.valueOf(diaAtual) + String.valueOf(horaAtual) + String.valueOf(minutoAtual) + String.valueOf(segAtual) + String.valueOf(miliAtual);

	}

	public static String calcularPorcetagem(Double total, Double valorParcial) {

		if (!TSUtil.isEmpty(total) && total.equals(0L)) {

			return "0";

		} else {

			DecimalFormat df = new DecimalFormat("0.00");

			Double parcial = valorParcial * 100;

			Double porcetagem = parcial.doubleValue() / total;

			return df.format(porcetagem).replaceAll(",00", "");

		}

	}

	private static int calcularDigito(String str, int[] peso) {

		int soma = 0;

		for (int indice = str.length() - 1, digito; indice >= 0; indice--) {

			digito = Integer.parseInt(str.substring(indice, indice + 1));

			soma += digito * peso[peso.length - str.length() + indice];

		}

		soma = 11 - soma % 11;

		return soma > 9 ? 0 : soma;

	}

	public static boolean isValidCPF(String cpf) {

		if ((cpf == null) || (cpf.length() != 11))
			return false;

		Integer digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF);

		Integer digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF);

		return cpf.equals(cpf.substring(0, 9) + digito1.toString() + digito2.toString());

	}

	public static boolean isValidCNPJ(String cnpj) {

		if ((cnpj == null) || (cnpj.length() != 14))
			return false;

		Integer digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ);

		Integer digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ);

		return cnpj.equals(cnpj.substring(0, 12) + digito1.toString() + digito2.toString());

	}

	public static String formatarMoeda(Double valor) {

		String valorFormatado = "";

		if (!TSUtil.isEmpty(valor)) {

			NumberFormat nf = new DecimalFormat("###,###,##0.00");

			valorFormatado = nf.format(valor);

		}

		return valorFormatado;

	}

	public static Double desformatarMoeda(String valor) {

		Number n = 0;

		if (!TSUtil.isEmpty(valor)) {

			NumberFormat nf = new DecimalFormat("###,###,###,##0.00");

			try {

				n = nf.parse(valor);

			} catch (ParseException e) {

				e.printStackTrace();

			}

		}

		return new Double(n.doubleValue());

	}

	public static String getAnoMes(Date data) {

		if (!TSUtil.isEmpty(data)) {

			return TSParseUtil.dateToString(data, TSDateUtil.YYYY) + "/" + TSParseUtil.dateToString(data, TSDateUtil.MM) + "/";

		} else {

			return null;

		}

	}

	public static String msgResultado(List<?> lista) {

		String resultado = "";

		if (TSUtil.isEmpty(lista)) {

			resultado = "A pesquisa não retornou nenhuma ocorrência";

		} else {

			Integer tamanho = lista.size();

			if (tamanho.equals(1)) {

				resultado = "A pesquisa retornou 1 ocorr�ncia";

			} else {

				resultado = "A pesquisa retornou " + tamanho + " ocorrências";

			}

		}

		return resultado;

	}

	public static String getSituacao(Boolean flagAtivo) {

		if (!TSUtil.isEmpty(flagAtivo) && flagAtivo) {

			return "Ativo";

		} else {

			return "Inativo";

		}

	}

	public static String gerarSenha() {

		try {

			return TSCryptoUtil.criptografar(gerarId());

		} catch (Exception e) {

			e.printStackTrace();

			return null;

		}

	}

	public static String tratarString(String valor) {

		if (!TSUtil.isEmpty(valor)) {
			valor = valor.trim().replaceAll("  ", " ");
		}else{
			valor = null;
		}

		return valor;

	}

	public static Long tratarLong(Long valor) {

		if (!TSUtil.isEmpty(valor) && valor.equals(0L)) {

			valor = null;

		}

		return valor;

	}

	public static Integer tratarInteger(Integer valor) {

		if (!TSUtil.isEmpty(valor) && valor.equals(0)) {

			valor = null;

		}

		return valor;

	}
	
	
	
	
	public static String gerarSenha(int tamanho) {

        final char[] ALL_CHARS = new char[62];

        final Random RANDOM = new Random();

        for (int i = 48, j = 0; i < 123; i++) {
                if (Character.isLetterOrDigit(i)) {
                        ALL_CHARS[j] = (char) i;
                        j++;
                }
        }

        final char[] result = new char[tamanho];
        for (int i = 0; i < tamanho; i++) {
                result[i] = ALL_CHARS[RANDOM.nextInt(ALL_CHARS.length)];
        }

        return new String(result);

	}



}