package com.script.mail.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import com.config.load.command.CommandArgs;
import com.script.mail.util.RegExUtil;

public class TemplateFactory {
	
	/**
	 * 
	 * @param args 命令行参数（邮件模板，及一些参数信息——命令行中的参数值会覆盖邮件模板中的数据）
	 * @return
	 * @throws IOException
	 */
	public Template textTemplate(CommandArgs args) throws IOException {
		String mailPath = args.getValue("mailPath", args.getArgs().length > 0?args.getArgs()[0]:null);
		Objects.requireNonNull(mailPath, "usage: mail path need non null");
		
		Template mail = mailTemplate(mailPath, args);
		return mail;
	}
	
	/**
	 * 带附件的邮件模板
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public Template attachTemplate(CommandArgs args) throws IOException {
		String mailPath = args.getValue("mailPath", args.getArgs().length > 0?args.getArgs()[0]:null);
		Objects.requireNonNull(mailPath, "usage: mail path need non null");
		
		String attachs = args.getValue("attachs", args.getArgs().length > 1?args.getArgs()[1]:null);
		Objects.requireNonNull(mailPath, "usage: attachs need non null");
		
		Template mail = mailTemplate(mailPath, args);
		mail.setAttachs(attachs.split(","));
		
		return mail;
	}
	
	private Template mailTemplate(String mailPath, CommandArgs args) throws IOException {
		InputStream in = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
        	in = cl.getResourceAsStream(mailPath);
        } else {
        	in = ClassLoader.getSystemResourceAsStream(mailPath);
        }
		if(in == null){
    		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    		in = new FileInputStream(new File(path.substring(0, path.lastIndexOf(File.separator)+1)+mailPath));
    	}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		
		Template mail = new Template();
		StringBuilder content = new StringBuilder();
		String line = null;
		
		boolean isFirst = true;
		while((line = reader.readLine())!=null) {
			if(isFirst) {//首字符问题处理
				isFirst = false;
				char s =line.charAt(0); 
				if(s == 65279 && line.length() > 1) {
					line = line.substring(1);
				}
			}
			
			if(line.startsWith("subject")) {
				mail.setSubject(args.getValue("subject", value(line)));
			}else if(line.startsWith("toUser")){
				mail.setToUser(args.getValue("toUser", value(line)));
			}else if(line.startsWith("ccUser")){
				mail.setCcUser(args.getValue("ccUser", value(line)));
			}else if(line.startsWith("sccUser")){
				mail.setSccUser(args.getValue("sccUser", value(line)));
			}else if(line.startsWith("content")){
				continue;
			}else {
				content.append(line).append("\n");
			}
		}
		mail.setContent(RegExUtil.transform(args.getValue("content", content.toString()), args));
		
		return mail;
	}
	
	private String value(String str) {
		String[] split = str.split("=");
		if(split.length == 2) {
			return split[1];
		}
		return null;
	}
}
