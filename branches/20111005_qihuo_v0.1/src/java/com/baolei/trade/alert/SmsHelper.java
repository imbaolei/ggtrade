package com.baolei.trade.alert;

import java.io.UnsupportedEncodingException;

import com.baolei.trade.util.HttpHelper;

public class SmsHelper {
	public String send(String msg){
		String url = "http://sms.api.bz/fetion.php?username=13777419821&password=123qweQWE123&sendto=13456769908&message=";
		url = url + msg;
		HttpHelper hh = new HttpHelper();
		try {
			return hh.getUrl(new String(url.getBytes(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
