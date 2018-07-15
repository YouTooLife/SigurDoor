package net.youtoolife.sigur;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.youtoolife.sigur.view.MainController;

public class Sender /*implements Runnable*/ {
	
	
	private MainController main = null;
	public String msg = "";
	
	private ArrayList<Personal> pers = null;
	
	public long ttime = -1;
	
	int size = 0;
	//@Override
	public String run(ArrayList<Personal> pers) {
		
		//while (true) {
		
		
		msg = "";
			
		//System.out.println("__1__");
			/*if (!main.isRun())
				return;
			*/
		/*
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	Date dateNow = new Date();
            	long taskTime =  dateNow.getTime();
            	main.ttime = taskTime;
            	System.out.println("task time set:" +main.ttime);
            }
        });
		*/
		Date dateNow = new Date();
    	long taskTime =  dateNow.getTime();
    	ttime = taskTime;
    	//System.out.println("task time set:" +ttime);
		
		this.pers = new ArrayList<>(pers);
			if (this.pers != null && this.pers.size() > 0)
				try {
					//if (pers.phone.equalsIgnoreCase("")||pers.phone.isEmpty()||pers.phone == null)
					//{
						//main.addText("Error! "+pers.name+" - сообщение не отправлено - номер не задан!");
						//msg = "!!!"+getDate()+" Для "+pers.name+" не задан номер. Сообщение не отправлено!";
						//main.val0.add(1);
						//continue;
					size+= this.pers.size();
					//System.out.println("____========================================___size: "+size);
					
					//System.out.println("__1__1");
					//System.out.println("Sender: "+this.pers.size());
						//return msg;
					//}
					sendMessage();
					//Thread.sleep(main.getDelay());
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg = "Error! 0xF2\n"+e.getMessage() + msg;
				}
			//if (this.pers.size() < 300)
			//System.out.println("Sender: ("+this.pers.size()+") "+msg);
			
			dateNow = new Date();
	    	taskTime =  dateNow.getTime();
	    	ttime = taskTime;
	    	//System.out.println("task time unset:" +ttime);
	    	
			return msg;
	}
	
	private String getDate() {
		  Date dateNow = new Date();
	      SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	      return formatForDateNow.format(dateNow);
	}
	
	public void post(String url, String param) throws Exception{
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true);
		  connection.setRequestProperty("Accept-Charset", charset);
		  //connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
		  connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		  connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		  
		  String urlParameters = param;//"sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		  
		  //System.out.println("len: "+param.length()/1024);
			
		  connection.setRequestProperty( "Content-Length", Integer.toString(urlParameters.getBytes(charset).length ));

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(urlParameters.getBytes(charset));
		  }
		  catch (Exception e) {
			  /*
			  main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
			  msg = "Error! 0xF2\nОшибка отправки запроса!\n";
				for (Personal per:pers)
					msg	+= " Сообщение на номер '"+per.phone+"' не отправлено!\n";
				msg+="["+e.getLocalizedMessage()+"\n"+e.getMessage()+"]\n";
				e.printStackTrace();
				return;
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
		  
		  //System.out.println(data);
		  
		  JsonParser jp = new JsonParser();
		  try {
		  JsonObject jo = jp.parse(data).getAsJsonObject();
		  if (jo.get("sent").getAsBoolean()) {
			  //main.addText("Messager: ("+pers.name+") Сообщение успешно отпралено на номер: '"+pers.phone+"'");
			  for (Personal per:pers) {
					msg	+=  getDate()+" Сообщение успешно отпралено на номер: '"+per.phone+"' ("+per.name+")\n";	
					main.val0.set(main.val0.get()+1);
			  }
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Error!\n";
				for (Personal per:pers)
					msg	+= per.name+" - Сообщение на номер '"+per.phone+"' не отправлено!\n";		
		  	msg+="["+jo.get("message").getAsString()+"]";
		  }
		  } catch (Exception e) {
			  msg = "Error! 0xF2\n";
				for (Personal per:pers)
					msg	+= per.name+" - Сообщение на номер '"+per.phone+"' не отправлено!\n";		
		  	msg+="["+e.getMessage()+"]";
		  	msg+="{"+data+"}";
		  	e.printStackTrace();
		  	
		}
		}
	
	public void post(String url, String param, String num) throws Exception{
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true);
		  connection.setRequestProperty("Accept-Charset", charset);
		  //connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
		  connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		  connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(param.getBytes(charset));
		  }
		  catch (Exception e) {
			  /*
			  main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
			  msg = "Ошибка отправки запроса!\n";
				for (Personal per:pers)
					msg	+= " Сообщение на номер '"+per.phone+"' не отправлено!\n";
				msg+="["+e.getMessage()+"]";
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
			  for (Personal per:pers) {
				msg	=  getDate()+" Сообщение успешно отпралено на номер: '"+per.phone+"' ("+per.name+")";
				main.val0.set(main.val0.get()+1);
			  }
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Error!";
				for (Personal per:pers)
					msg	+= per.name+" - Сообщение на номер '"+per.phone+"' не отправлено!\n";		
		  	msg+="["+jo.get("message").getAsString()+"]";
		  }
		}
	

	
	private void sendMessage() {
		
		//String head = "SMS-INFO ГГКЭИТ "; 
		String head = main.settings.title;//"SMS-INFO Медколледж ";

		//if (main.getToken().equals(""))
		//	main.setToken("ybh3buw58y");
		String url = main.settings.url+
				(main.settings.url.charAt(main.settings.url.length()-1) != '/'?"/":"")
				+"send_msg2.php";	
		//String sb = "{\"phone\":\"79315427605\",\"body\":\""+body+"\"}";
		
		
		JsonObject rootObject = new JsonObject();
		JsonObject rootObject2 = new JsonObject();
		
		JsonArray jarr = new JsonArray();
		for (Personal per:pers) {
			JsonObject rootObject3 = new JsonObject();
			String body = head+" "+per.dir+":\n"+per.name+"\n"+per.date;
			rootObject3.addProperty(per.phone, body);
			jarr.add(rootObject3);
		}
		rootObject2.add("msgs", jarr);

        
        rootObject.addProperty("pwd", "ytltoor_root683asdf29ggsd123udkywnxtutruernf");
        rootObject.addProperty("t", "SMS-INFO");
        
        //rootObject.addProperty("img", img);
        
        Gson gson = new Gson();
        String json = gson.toJson(rootObject);
        String json2 = gson.toJson(rootObject2);

        String d = RSAISA.rsaEncrypt(json);
        byte[] b2 = null;
		try {
			b2 = json2.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String dd = Base64.getEncoder().encodeToString(b2);
		

        try{
        		post(url, "d="+d+"&dd="+dd);
        	
		} catch (Exception e) {
			/*
			main.addText("Ошибка отправки запроса!"
					+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
					*/
			msg = "Error! 0xF2\nОшибка отправки запроса!\n";
			for (Personal per:pers)
				msg	+= " Сообщение на номер '"+per.phone+"' не отправлено!\n";
			msg+="["+e.getLocalizedMessage()+"\n"+e.getMessage()+"]\n";
			System.out.println("__e_");
			e.printStackTrace();
		}
			  

	}
	
	public Sender(MainController main) {
		this.main = main;
	}

}
