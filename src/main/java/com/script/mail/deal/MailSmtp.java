package com.script.mail.deal;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.config.load.ConfigProviderFactory;
import com.config.load.IConfigLoader;
import com.config.load.property.CommonConfig;
import com.config.load.property.CommonConfigLoader;

/**
 * https://yq.aliyun.com/articles/604862
 * https://javaee.github.io/javamail
 * https://github.com/javaee/javamail
 * @author zhaoj
 * 
 */
public class MailSmtp {
	
    private final String MAIL_SMTP_HOST = "mail.smtp.host";
    private final String MAIL_SMTP_PORT = "mail.smtp.port";
    private final String MAIL_SENDER_MAIL = "mail.sender.mail";
    private final String MAIL_SENDER_PASS = "mail.sender.password";
    private final String MAIL_SNEDER_NICKNAME = "mail.sender.nickname";
    //是否需要认证
    private final String AUTH_VALIDATE = "mail.smtp.auth";
    //是否开启debug模式
    private final String MAIL_DEBUG = "mail.debug";
    
    private CommonConfig config = null;
    //邮件服务配置文件（发件人及邮件服务地址信息）
    private String configPath = "mail-sender.properties";
    
	public MailSmtp() {
		IConfigLoader<CommonConfig> loader = new CommonConfigLoader(ConfigProviderFactory.createPropertiesProvider(configPath));
		config = loader.load();
		Objects.requireNonNull(config, "mail-sender.properties cannot load!");
	}
    
    private Session getMailSession() {
    	Properties props = new Properties();
    	props.put("mail.smtp.host", config.getValue(MAIL_SMTP_HOST));
    	props.put("mail.smtp.port", config.getValue(MAIL_SMTP_PORT));
    	props.put("mail.smtp.auth", config.getValue(AUTH_VALIDATE, "fasle"));
    	//javax.net.ssl.SSLSocketFactory 
    	//配置端口为加密端口时会需要用到，比如465
    	props.put("mail.smtp.socketFactory.class", "com.sun.mail.util.MailSSLSocketFactory");
    	
    	if("true".equals(config.getValue(AUTH_VALIDATE, "fasle"))) {
    		return Session.getDefaultInstance(props, new Authenticator() {
    			@Override
    			protected PasswordAuthentication getPasswordAuthentication() {
    				return new PasswordAuthentication(config.getValue(MAIL_SENDER_MAIL), config.getValue(MAIL_SENDER_PASS));
    			}
			});
    	}
    	return Session.getDefaultInstance(props);
	}
    
    /**
     * 
     * @param subject 邮件标题
     * @param recipients 多个需要以“,”分割
     * @param copyToRecipients 抄送
     * @param secretCopyToRecipients 密送
     * @return MimeMessage
     * @throws AddressException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private MimeMessage getMimeMessage(String subject, String recipients, String copyToRecipients, String secretCopyToRecipients) throws AddressException, MessagingException, UnsupportedEncodingException {
		Session session = getMailSession();
		if("true".equals(config.getValue(MAIL_DEBUG, "fasle"))) {
			// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
			session.setDebug(true);
		}
		
		// 设置邮件发送者，标题等基础信息
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(config.getValue(MAIL_SENDER_MAIL), config.getValue(MAIL_SNEDER_NICKNAME, config.getValue(MAIL_SENDER_MAIL)), "UTF-8"));
		message.setSubject(subject, "UTF-8");
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients, false));
		message.setRecipients(Message.RecipientType.CC, copyToRecipients == null ? null : InternetAddress.parse(copyToRecipients, false));
		message.setRecipients(Message.RecipientType.BCC, secretCopyToRecipients == null ? null : InternetAddress.parse(secretCopyToRecipients, false));
		
		return message;
	}
    
    /**
     * 
     * @param subject 邮件标题
     * @param content 编写txt内容时，缩进只能使用空格，换行需要在txt加载时替换为\n
     * @param recipients 多个需要以“,”分割
     * @param copyToRecipients 抄送
     * @param secretCopyToRecipients 密送
     * @return
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public boolean sendTextMail(String subject, String content, String recipients, String copyToRecipients, String secretCopyToRecipients) throws UnsupportedEncodingException, MessagingException {
		// 设置邮件内容
    	MimeMessage message = getMimeMessage(subject, recipients, copyToRecipients, secretCopyToRecipients);
		message.setText(content, "UTF-8");
		
		// 保存邮件修改并发送
		Transport.send(message);
		return true;
	}
    
    public boolean sendHtmlMail(String subject, String content, String recipients, String copyToRecipients, String secretCopyToRecipients) throws UnsupportedEncodingException, MessagingException {
		 //设置邮件内容
		 MimeMessage message = getMimeMessage(subject, recipients, copyToRecipients, secretCopyToRecipients);
		//创建邮件正文，为了避免邮件正文中文乱码问题，需要使用charset=UTF-8指明字符编码
//		 message.setContent(content, "text/html; charset=utf-8");
		 
		 MimeBodyPart html = new MimeBodyPart();
		 html.setContent(content, "text/html;charset=utf-8");
		 MimeMultipart parts = new MimeMultipart();
		 parts.addBodyPart(html);
		 message.setContent(parts);
		 
		 //保存邮件修改并发送
		 Transport.send(message);
		 return true;
	}
    
    public boolean sendMultMail(String subject, String content, String[] attachs, String recipients, String copyToRecipients, String secretCopyToRecipients) throws UnsupportedEncodingException, MessagingException {
    	MimeMessage message = getMimeMessage(subject, recipients, copyToRecipients, secretCopyToRecipients);
    	//文本内容
		MimeBodyPart text = new MimeBodyPart();
//		 text.setContent(content, "text/html;charset=UTF-8");
		text.setText(content, "UTF-8");
		
		MimeMultipart parts = new MimeMultipart();
		parts.addBodyPart(text);
		parts.setSubType("mixed");// 混合关系

		// 创建邮件附件
		for (String attach : attachs) {
			MimeBodyPart attachment = new MimeBodyPart();
			DataHandler dh = new DataHandler(new FileDataSource(attach));
			attachment.setDataHandler(dh);
			attachment.setFileName(MimeUtility.encodeText(dh.getName()));
			// 创建容器描述数据关系
			parts.addBodyPart(attachment);
		}
		message.setContent(parts);

		Transport.send(message);
		return true;
	}
    
}
