package net.youtoolife.sigur.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.youtoolife.sigur.DBHandler;
import net.youtoolife.sigur.Main;
import net.youtoolife.sigur.Personal;
import net.youtoolife.sigur.Sender;
import net.youtoolife.sigur.Settings;

import org.apache.http.client.ClientProtocolException;
import com.google.gson.Gson;

public class MainController {
	
	
	@FXML
	private TextArea area;
	
	@FXML
	private ListView<String> listView;
	
	
	
	@FXML
	private Button startBtn;
	
	@FXML
	private Button testBtn;
	@FXML
	private Button showLastSent;
	
	
	@FXML
	private Button showBtn;
	
	@FXML
	private Button removeBtn;
	
	
	
	@FXML
	private TextField titleField;
	@FXML
	private TextField urlField;
	@FXML
	private TextField tokenField;
	@FXML
	private TextField sleepField;
	
	@FXML
	private Label qLabel;
	@FXML
	private Label sendsLabel;
	@FXML
	private Label lastSent;
	
	
	@FXML
	private AnchorPane paneRoot;
	@FXML
	private AnchorPane pane1;
	@FXML
	private AnchorPane pane2;
	@FXML
	private ImageView imgView;

	
	private Executor exec;
	private DBHandler dbHandler;
	private Sender senderHandler;
	public Task<Void> dbTask;
	public Task<Void> sendTask;
	
	private StringProperty value1 = new SimpleStringProperty("Очередь: null");
	private StringProperty value2 = new SimpleStringProperty("Отправлено: null");
	public  IntegerProperty val0 = new SimpleIntegerProperty(0);
	public volatile long ttime = -777;
	
	volatile ArrayList<Personal> arr = null;
	
	private int delay = 3000;
	private boolean run = false;
	private volatile int lastID = -1;
	
	private int lastDBID = -1;
	//private volatile int lastDBID = -1;
	
	public Settings settings = null;
	
	public Main main;

   
    public MainController() {
    	
    }
    
    
    
    private String setStrToGson(String str) {
    	String result = "";
    	for (int i = 0; i < str.length(); i++) {
    		char c = str.charAt(i);
    		if (c == ' ')
    			result+="<;s>";
    		else
    			result += c;
    	}
    	return result;
    }
    
    private String getStrFromGson(String str) {
    	String result = "";
    	String[] s = str.split("<;s>");
    	for (int i = 0; i < s.length; i++) {
    		result += s[i];
    		if (i != s.length-1)
    			result += " ";
    	}
    	return result;
    }
    
	@FXML
    private void initialize() {
		System.out.println("Init");
		arr = new ArrayList<>();
		
		String dir0 = System.getProperty("user.home")+"/YouTooLife/SigurDoorCHBMK/";
		
		File dir = new File(dir0);
		try {
		if (!dir.exists()) 
			dir.mkdirs();
		File setnFile = new File(dir0 + "settings.conf");
		if (setnFile.exists()) {
			Scanner sc = new Scanner(setnFile);
			String json = sc.next();
			sc.close();
			Gson gson = new Gson();
			settings = gson.fromJson(json, Settings.class);
		}
		else {
			settings = new Settings();
			
			String dir00 = System.getProperty("user.home")+"/YouTooLife/SigurDoor/";
			File setnFile0 = new File(dir00 + "settings.conf");
			if (setnFile0.exists()) {
				Scanner sc = new Scanner(setnFile0);
				String json = sc.next();
				sc.close();
				Gson gson = new Gson();
				Settings settings2 = gson.fromJson(json, Settings.class);
				
				settings.lastID = settings2.lastID;
			}
		}
		/*
		File arrFile = new File(dir0 + "queue.arr");
		if (arrFile.exists()) {
			Scanner sc = new Scanner(arrFile);
			String json = sc.next();
			sc.close();
			Gson gson = new Gson();
			Type itemsListType = new TypeToken<ArrayList<Personal>>() {}.getType();
			arr = gson.fromJson(json, itemsListType);
		}
		*/
		lastID = settings.lastID;
		//lastDBID = settings.lastDBID;
		
		System.out.println("____"+lastID);
		//setToken(getStrFromGson(settings.token));
		sleepField.setText(String.valueOf(settings.delay));
		titleField.setText(getStrFromGson(settings.title));
		urlField.setText(getStrFromGson(settings.url));
		delay = settings.delay;
		tokenField.setVisible(false);
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		
		exec = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t ;
        });
		
		listView.setVisible(false);
		listView.setDisable(true);
		removeBtn.setVisible(false);
		removeBtn.setDisable(true);
		
		///
		testBtn.setVisible(false);
		testBtn.setDisable(true);
		///
		
		qLabel.textProperty().bind(value1);
		sendsLabel.textProperty().bind(value2);	
		
		dbHandler = new DBHandler();
		if (dbHandler != null) {
			lastDBID = dbHandler.getLastRecord();
			lastSent.setText("посл. отправ.: "+lastID+" / посл. запись.: "+lastDBID);
		}
	}
	
	public void setBG() {
		
		//File file = new File(getClass().getResource(main.dir2+"/view/bg-img.jpg").getPath());
		/*File file = new File(main.dir2+"/view/bg-img.jpg");
		System.out.println("isExists: "+file.exists());
		String localUrl = null;
		try {
			localUrl = file.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}*/
		Image img = new Image(getClass().getResource("bg-img.jpg").toExternalForm());
		imgView.setImage(img);
		/*imgView.setFitWidth(230);
		imgView.setFitHeight(230);*/
		imgView.setScaleX(1.5);
		imgView.setScaleY(1.5);
		imgView.setTranslateX(50);
	}
	
	
	
	private String getDateDay() {
		  Date dateNow = new Date();
	      SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd-MM-yyyy_HHmm");
	      return formatForDateNow.format(dateNow);
	}
	
	public void saveLogs(boolean clearArea, boolean newCount) {
		//System.out.println("\nPPPpath:"+ MainController.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		
		//System.out.println("path:"+getClass().getResource("../../../../").getPath());
		if (!area.getText().isEmpty())
		try {
		File file = new File(main.dir2+"/"+getDateDay()+".log");
		
		PrintWriter pw = new PrintWriter(file);
		pw.write(area.getText());
		pw.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (clearArea)
			area.clear();
		if (newCount)
			val0.set(0);
	}
	
	
	public void saveSettings() throws FileNotFoundException {
			System.out.println("Save settings...");
			if (dbTask != null && dbTask.isRunning())
				dbTask.cancel();
			if (sendTask != null && sendTask.isRunning())
				sendTask.cancel();
			
		String dir0 = System.getProperty("user.home")+"/YouTooLife/SigurDoorCHBMK/";
		
		settings.delay = Integer.valueOf(sleepField.getText());
		settings.lastID = lastID;
		//settings.lastDBID = lastDBID;
		//settings.token = setStrToGson(tokenField.getText());
		settings.url = setStrToGson(urlField.getText());
		settings.title = setStrToGson(titleField.getText());
		
		Gson gson = new Gson();
		String str = gson.toJson(settings, Settings.class);
		PrintWriter pw = new PrintWriter(new File(dir0+"settings.conf"));
		pw.write(str);
		pw.close();
		
		/*
		if (arr.size() > 0) {
		pw = new PrintWriter(new File(dir0+"queue.arr"));
		str = gson.toJson(arr);
		pw.write(str);
		pw.close();
		}
		*/
		saveLogs(true, true);
		
	}
	
	
	boolean save_logs_at12 = false;
	@SuppressWarnings("deprecation")
	public int getCurrentHours() {
		Date date = new Date();
		return date.getHours();
	}
	
	int size = 0;
	
	
	volatile ArrayList<Personal> pers2 = null;
	
	@FXML
    private void startBtn() {
		System.out.println("Start");
		if (run) {
			
			if (pers2 != null && pers2.size() > 0 && arr != null) {
				if (pers2.get(pers2.size()-1).idAction > arr.get(arr.size()-1).idAction)
					arr.addAll(pers2);
				else
					arr.addAll(0, pers2);
				System.out.println("================::::SIZE: "+pers2.size());
				size -= pers2.size();
			}
				
			
			run = false;
			startBtn.setText("Start");
			dbTask.cancel();
			sendTask.cancel();
			saveLogs(false, false);
			
			lastDBID = dbHandler.getLastRecord();
			lastSent.setText("посл. отправ.: "+lastID+" / посл. запись.: "+lastDBID);
			lastSent.setVisible(true);
		}
		else {
			
			lastSent.setVisible(false);
			
			run = true;
			
			dbHandler = new DBHandler();
			senderHandler = new Sender(this);
			
			settings.title = titleField.getText();
			settings.url = urlField.getText();
			
			final int lastDB = (arr != null & arr.size() > 0)?arr.get(arr.size()-1).idAction:-1;
			
			dbTask = new Task<Void>() {
	        	int lSentID = -1;
	        	//int lDBID = -1;
	        	
	        	volatile boolean stop_flag = false;
	        	
	            @Override
	            public Void call() throws Exception {
	            	
	            	while (true) {
	            		
	            		//System.out.println("___new iter___");
	            		
	            		if (isCancelled()) {
	            			System.out.println("___dbhandle__stop!___");
	            			break;
	            		}
	            		/*Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            	lSentID = lastID;
                            	lDBID = lastDBID;
                            	
                            	System.out.println("two");
                            	System.out.println("reads__"+lastID + "_db___"+lastDBID);
                            }
                        });*/
	            		
	            		lSentID = lastID;
                    	//lDBID = lastDBID;
                    	
	            		ArrayList<Personal> pers = (lastDB > -1) ?
	            				dbHandler.run(lastDB):dbHandler.run(lSentID);
	            				
	            		stop_flag = true;
	            		Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            	
                            	if (senderHandler != null && senderHandler.ttime != -1) {
                            	Date dateNow = new Date();
                            	long taskTime =  dateNow.getTime() - senderHandler.ttime;
                            	//System.out.println("task time:" +taskTime);
                            	
                            	if (taskTime > 1000 * (60+30)) {
                            		System.out.println(".....Repair......");
                            		System.out.println("Stoping......");
                            		startBtn();
                            		System.out.println("Stoped");
                            		System.out.println("Starting.....");
                            		startBtn();
                            		System.out.println("Started");
                            		return;
                            	}
                            	}
                            	
                            	if (pers.size() > 0) {
                            		//lastDBID = pers.get(pers.size()-1).idAction;
                            		//System.out.println("read last DBID = "+lastDBID);
                            		arr.addAll(pers);
                            		value1.setValue(String.format("Очередь: %d", arr.size()));
                            	}
                            	stop_flag = false;
                            }
                        });
	            		
	            		while (stop_flag) {}
	            		
	            		Thread.sleep(500);
	            		//System.out.println("___iter___end");
	            	}
					return null;
	            }
	            
	            @Override 
	            protected void succeeded() {
	                super.succeeded();
	            }

	            @Override 
	            protected void cancelled() {
	                super.cancelled();
	            }

	        @Override 
	        protected void failed() {
	            super.failed();
	            }
	        };
	        
	        
	        sendTask = new Task<Void>() {
	        	
	        	volatile boolean flag_stop = false;
	        	
	        	//Personal pers = null;
	            @Override 
	            public Void call() throws Exception {
	            	delay = Integer.parseInt(sleepField.getText());
	            	
	            	pers2 = new ArrayList<>();
	            	while (true) {
	            	
	            		/*
	            	System.out.println("db Runn: "+dbTask.isRunning()+
	            			" db canc"+dbTask.isCancelled()+
	            			" db done"+dbTask.isDone());
	            			*/
	            		
	            		if (isCancelled())
	            			break;
	            		
	            		if (getCurrentHours() == 0) {
	            			if (!save_logs_at12) {
	            				flag_stop = true;
	            				Platform.runLater(new Runnable() {
	                                @Override
	                                public void run() {
	                                	saveLogs(true, true);
	                                	flag_stop = false;
	                             }});
	            				while (flag_stop) {} 
	            				save_logs_at12 = true;
	            			}
	            		}
	            		else {
	            			save_logs_at12 = false;
	            		}
	            		
	            		/*Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            	if (arr.size() > 0)
                            		pers = arr.get(0);
                            	else
                            		pers = null;
                            }
                        });
	            		*/
	            		
	            		/*
	            		if (arr.size() > 0)
                    		pers = arr.get(0);
                    	else
                    		pers = null;
                    		*/
	            		
	         		
	            		if (arr != null && arr.size() > 0) {
	            		
	            			//System.out.println("__FIRST__");
	            			

	                            	
	       
	                            	ArrayList<Personal> arr2 = new ArrayList<>(arr);
	                            	if (arr.size() < 1600)
	                            		arr.clear();
	                            	else {
	                            		//System.out.println("arr before del: "+arr.size());
	                            		arr2 = new ArrayList<>(arr2.subList(0, 1600));
	                            		arr.removeAll(arr2);
	                            		//System.out.println("arr after del: "+arr.size());
	                            	}
	        		            	for (Personal per:arr2) {
	        		            		if (per != null)
	        		            		if (per.phone == null|| per.phone.isEmpty()) {
	        		            			area.setText(area.getText()+"\n"+
	        		            					"!!!"+getDate()+" Для "+per.name+" не задан номер. Сообщение не отправлено!");
	        		            		}
	        		            		else {
	        		            			//System.out.println(per.name);
	        		            			pers2.add(per);
	        		            			setLastID(per.idAction);
	        		            			
	        		            			size++;
	        		            			//System.out.println("==================size: "+size);
	        		            		}
	        		            	}
	        		            	Platform.runLater(new Runnable() {	
	                                    @Override
	                                    public void run() {
	                                    	//System.out.println("__SECOND__");
	                                    	value1.setValue(String.format("Очередь: %d", arr.size()));
	                                    	area.setScrollTop(Double.MAX_VALUE);
	                                    }
	        		            	});
	                            }
		            		
	            		
	            			
	            		//System.out.println("__0__");
	            		
	            		String msg = senderHandler.run(pers2);
	            		
	            		//System.out.println("__2__");
	            		
	            		if (!msg.isEmpty()) {
	            		flag_stop = true;
	            		Platform.runLater(new Runnable() {	
                            @Override
                            public void run() {
                            	//System.out.println("__3__");
                            	area.setText(area.getText()+"\n"+ msg);
                                value2.setValue(String.format("Отправлено: %d", val0.get()));
                                //setLastID(pers.idAction);
                                //arr.remove(0);
                                //value1.setValue(String.format("Очередь: %d", arr.size()));
                                area.setScrollTop(Double.MAX_VALUE);
                                
                                String[] amsg = msg.split("\n");
                                if (!amsg[0].equalsIgnoreCase("Error! 0xF2") 
                                		&& !amsg[amsg.length-1].contains("response code: 502"))
                                	pers2.clear();
                                else
                                	System.out.println("repeat");
                                if (area.getText().split("\n").length > 1000)
                                	saveLogs(true, false);
                                flag_stop = false;
                            }
                        });
	            		}
	            		
	            		while (flag_stop) { }
	            		
	            		//System.out.println("__4__");

	            		Thread.sleep(delay);
	            	}
					return null;
	            }

	            @Override 
	            protected void succeeded() {
	                super.succeeded();
	            }

	            @Override 
	            protected void cancelled() {
	                super.cancelled();
	            }

	        @Override 
	        protected void failed() {
	            super.failed();
	            }
	        };
	        exec.execute(dbTask);
	        exec.execute(sendTask);
	        
			startBtn.setText("Stop");
		}
		
	}
	
	private String getDate() {
		  Date dateNow = new Date();
	      SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	      return formatForDateNow.format(dateNow);
	}
	
	public boolean isRun() {
		return run;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public String getToken() {
		return tokenField.getText();
	}
	
	public int getLastID() {
		return lastID;
	}
	
	public void setLastID(int id) {
		lastID = id;
	}
	
	public void setToken(String s) {
		tokenField.setText(s);
	}
	
	
	
	
	@FXML
    private void removeBtn() {
		int id = listView.getSelectionModel().getSelectedIndex();
		//System.out.println(listView.getSelectionModel().getSelectedIndex());
		arr.remove(id);
		ArrayList<String> itms = new ArrayList<>();
		for (Personal pers:arr)
			 itms.add(pers.date+" "+pers.dir+": "+pers.name);
		ObservableList<String> items = FXCollections.observableArrayList(itms);
		listView.setItems(items);
	}
	
	@FXML
    private void showBtn() {
		if (removeBtn.isDisable()) {
			System.out.println("en");
		ArrayList<String> itms = new ArrayList<>();
		
		/*
		if (lastDBID == -1) {
			arr.addAll(dbHandler.run(lastID, ));
			if (arr.size() > 0) 
			lastDBID = arr.get(arr.size()-1).idAction;
			value1.setValue(String.format("Очередь: %d", arr.size()));
		}
		*/
		for (Personal pers:arr)
		 itms.add(pers.date+" "+pers.dir+": "+pers.name);
		ObservableList<String> items = FXCollections.observableArrayList(itms);
		listView.setItems(items);
		
		listView.setVisible(true);
		listView.setDisable(false);
		removeBtn.setVisible(true);
		removeBtn.setDisable(false);
		area.setVisible(false);
		area.setDisable(true);
		showBtn.setText("Скрыть очередь");
		}
		else {
			System.out.println("dis");
			listView.setVisible(false);
			listView.setDisable(true);
			removeBtn.setVisible(false);
			removeBtn.setDisable(true);
			area.setVisible(true);
			area.setDisable(false);
			showBtn.setText("Показать очередь");
		}
	}
	
	public void setMainApp(Main mainApp) {
		this.main = mainApp;
	}
	
	@FXML
    private void lastSentBtn() {
		if (dbHandler == null)
			dbHandler = new DBHandler();
		lastDBID = dbHandler.getLastRecord();
		lastID = lastDBID;
		lastSent.setText("посл. отправ.: "+lastID+" / посл. запись.: "+lastDBID);
	}
	
	
	@FXML
    private void testBtn() throws ClientProtocolException, IOException {
		
		/*System.out.println("Test0");
		
		if (getToken().equals(""))
			tokenField.setText("ybh3buw58y");
		String url = "https://eu1.whatsapp.chat-api.com/instance114/message?token="+getToken();	
		String body = "Test! SuperTest Приветб Мир!";
		//String sb = "{\"phone\":\"79315427605\",\"body\":\""+body+"\"}";
		 	JsonObject rootObject = new JsonObject();
	        rootObject.addProperty("phone", "79389891188");
	        rootObject.addProperty("body", body);
	        Gson gson = new Gson();
	        String json = gson.toJson(rootObject);

	        try {
				post(url, json);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	}
	
	
}



/*
public void post(String url, String param ) throws Exception{
	  String charset = "UTF-8"; 
	  URLConnection connection = new URL(url).openConnection();
	  connection.setDoOutput(true); // Triggers POST.
	  connection.setRequestProperty("Accept-Charset", charset);
	  connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

	  try (OutputStream output = connection.getOutputStream()) {
	    output.write(param.getBytes(charset));
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
		  System.out.println("Good!");
	  }

	}
*/