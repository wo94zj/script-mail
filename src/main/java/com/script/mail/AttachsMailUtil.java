package com.script.mail;

import java.io.IOException;
import java.util.Objects;

import javax.mail.MessagingException;

import com.config.load.ConfigProviderFactory;
import com.config.load.IConfigLoader;
import com.config.load.command.CommandArgs;
import com.config.load.command.CommandArgsLoader;
import com.config.load.exception.ExceptionUtil;
import com.script.mail.common.Template;
import com.script.mail.common.TemplateFactory;
import com.script.mail.deal.MailSmtp;

/**
 * 带附件的文本邮件
 * @author zhaoj
 *
 */
public class AttachsMailUtil {
	
	public static void main(String[] args) {
		IConfigLoader<CommandArgs> loader = new CommandArgsLoader(ConfigProviderFactory.createCommandProvider(args));
		CommandArgs params = loader.load();
		
		Objects.requireNonNull(params, "usage: mail template need non null");
		
		TemplateFactory attachs = new TemplateFactory();
		Template temp = null;
		try {
			temp = attachs.attachTemplate(params);
			MailSmtp smtp = new MailSmtp();
			smtp.sendMultMail(temp.getSubject(), temp.getContent(), temp.getAttachs(), temp.getToUser(), temp.getCcUser(), temp.getSccUser());
		} catch (MessagingException | IOException e) {
			ExceptionUtil.asString(e);
			e.printStackTrace();
		}
	}
}
