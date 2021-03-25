package br.com.topsys.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * @author andre.topazio
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public final class TSEmailUtil {

	public static final String TEXT_PLAIN = "text/plain";


	private TSEmailUtil() {
	}

	public static boolean enviar(String remetente, String assunto, String corpo, String emailTO, String mimeType, String smtpServer) {
		String smtp = smtpServer;
		String destinatario = emailTO;

		try {
			Properties propriedades = System.getProperties();
			propriedades.put("mail.smtp.host", smtp);

			Session sessao = Session.getInstance(propriedades, null);

			Message mensagem = new MimeMessage(sessao);
			mensagem.setSentDate(new Date());
			mensagem.setFrom(new InternetAddress(remetente));
			mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario, false));

			mensagem.setSubject(assunto);

			if (mimeType == null) {
				mensagem.setText(corpo);
			} else {
				mensagem.setContent(corpo, mimeType);
			}

			/**
			 * Envia a mensagem
			 */
			Transport.send(mensagem);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

}