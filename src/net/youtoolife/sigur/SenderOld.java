package net.youtoolife.sigur;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.youtoolife.sigur.view.MainController;

public class SenderOld /*implements Runnable*/ {
	
	
	private MainController main = null;
	public String msg = "";
	
	//@Override
	public String run(Personal pers) {
		//while (true) {
			
			/*if (!main.isRun())
				return;
			*/
			if (pers != null)
				try {
					if (pers.phone.equalsIgnoreCase("")||pers.phone.isEmpty()||pers.phone == null)
					{
						//main.addText("Error! "+pers.name+" - сообщение не отправлено - номер не задан!");
						msg = "!!!"+getDate()+" Для "+pers.name+" не задан номер. Сообщение не отправлено!";
						//main.val0.add(1);
						//continue;
						return msg;
					}
					sendMessage(pers);
					//Thread.sleep(main.getDelay());
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return msg;
	}
	
	private String getDate() {
		  Date dateNow = new Date();
	      SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	      return formatForDateNow.format(dateNow);
	}
	
	public void post(String url, String param, Personal pers) throws Exception{
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true);
		  connection.setRequestProperty("Accept-Charset", charset);
		  connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(param.getBytes(charset));
		  }
		  catch (Exception e) {
			  /*
			  main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
			  msg = getDate()+" Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]";
		  }

		  String data = "";
		  InputStream iStream = connection.getInputStream();
		  BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "utf8"));
		  StringBuffer sb = new StringBuffer();
		  String line = "";

		  while ((line = br.readLine()) != null) {
		      sb.append(line);
		  }
		  data = sb.toString();
		  
		  System.out.println(data);
		  
		  JsonParser jp = new JsonParser();
		  JsonObject jo = jp.parse(data).getAsJsonObject();
		  if (jo.get("sent").getAsBoolean()) {
			  //main.addText("Messager: ("+pers.name+") Сообщение успешно отпралено на номер: '"+pers.phone+"'");
			  msg = getDate()+" Сообщение успешно отпралено на номер: '"+pers.phone+"' ("+pers.name+")";
			  main.val0.set(main.val0.get()+1);
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Error! "+pers.name+" - "
				  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
		  				+ "["+jo.get("message").getAsString()+"]";
		  }
		}
	
	private void sendMessage(Personal pers) {
		
		//String head = "SMS-INFO ГГКЭИТ "; 
		String head = main.settings.title;//"SMS-INFO Медколледж ";

		if (main.getToken().equals(""))
			main.setToken("ybh3buw58y");
		String url = main.settings.url+
				(main.settings.url.charAt(main.settings.url.length()-1) != '/'?"/":"")
				+"message?token="+main.getToken();	
		//String sb = "{\"phone\":\"79315427605\",\"body\":\""+body+"\"}";
		
		String body = head+" "+pers.dir+":\n"+pers.name+"\n"+pers.date;
		 JsonObject rootObject = new JsonObject();
	        rootObject.addProperty("phone", pers.phone);
	        rootObject.addProperty("body", body);
	        Gson gson = new Gson();
	        String json = gson.toJson(rootObject);
	      
	        try {
				post(url, json, pers);
			} catch (Exception e) {
				/*
				main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
				msg = getDate()+" Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]";
				e.printStackTrace();
			}
			  

	}
	
	public SenderOld(MainController main) {
		this.main = main;
	}

}
