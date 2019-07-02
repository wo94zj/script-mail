package com.script.mail.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.config.load.command.CommandArgs;

public class RegExUtil {

	private final static Pattern BRACES_PATTERN = Pattern.compile("(\\$\\{[^{}]+\\})");
	
	/**
	 * 将${}内的信息替换成配置的内容
	 * @param input
	 * @param args
	 * @return
	 */
	public static String transform(String input, CommandArgs args) {
		if(input == null) {
			return input;
		}
		StringBuffer sb = new StringBuffer();
		Matcher matcher = BRACES_PATTERN.matcher(input);
		while(matcher.find()) {
			String key = matcher.group();
			matcher.appendReplacement(sb, args.getValue(key.substring(2, key.length()-1), ""));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
}
