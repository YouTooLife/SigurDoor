package net.youtoolife.sigur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBHandler /*implements Runnable*/ {
	
	//private MainController main = null;
	Connection conn = null;
	boolean connected = false;
	

	int lastDBID = -1;
	//int size = 0;
	//@Override
	public ArrayList<Personal> run(int lastSentID) {

        //while (true) {
		//System.out.println(lastSentID + "_db___"+lastDBID);
	
		if (!connected) {
			System.out.println("no connect");
			return null;
		}
		
		int lastID = lastDBID;
		
		if (lastID == -1)
			lastID = lastSentID;
		
		//System.out.println("from db _pre_lastID: "+lastID);
		
  
        ArrayList<Personal> arr = new ArrayList<>();
        try (Statement statement = conn.createStatement()) {
        	
                	//System.out.println("yo");
                    String str =
                    		"select logs.id as id, logs.logtime as time, logs.devhint as door,"
                    		+ " logs.emphint as pid, "
                    		+ "ord(substr(logs.logdata, 5, 1)) as dir, "
                    		+ "personal.id as ppid, "
                    		+ "personal.name as name, personal.sms_targetnumber as num "
                    		+ "from `tc-db-log`.logs inner join `tc-db-main`.personal on logs.emphint = personal.id "
                    		+ "where logs.id > '"+Integer.toString(lastID)+"' AND substr(logs.logdata,1,2)=0xFE06  order by logs.id asc";
                    
                    ResultSet resultSet = statement.executeQuery(str);     

                    while (resultSet.next()) {
                    Personal pers = new Personal();
                    pers.doorID = resultSet.getInt("door");
                    pers.date = resultSet.getString("time");
                    pers.dir = resultSet.getString("dir").equalsIgnoreCase("1")?"Выход":"Вход";
                    pers.id = resultSet.getInt("pid");
                    pers.idAction = resultSet.getInt("id");
                    pers.name = resultSet.getString("name");
                    pers.phone = resultSet.getString("num");
                    arr.add(pers);
                    
                    
                    //if (arr.size() < 300)
                    	//System.out.println("from DB: "+pers.idAction+"; "+pers.id);
                    }
                    
                    resultSet.close();
                    
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        //Collections.reverse(arr);
        if (arr.size() > 0) {
        	lastDBID = arr.get(arr.size()-1).idAction;
        }
        //System.out.println("from DB _last: "+lastDBID);
		return arr;
	}
	
	public int getLastRecord() {
		if (!connected) {
			System.out.println("no connect");
			return -1;
		}
		
		int result = -1;
		
	        try (Statement statement = conn.createStatement()) {
	                    String str =
	                    		"select id, logdata from logs "
	                    		+ "where substr(logdata,1,2)=0xFE06  order by logs.id desc limit 1";
	                    ResultSet resultSet = statement.executeQuery(str);     
	                    while (resultSet.next()) {
	                    	result = resultSet.getInt("id");
	                    }
	                    resultSet.close();
	                    
	        } catch (SQLException e) {
	        	e.printStackTrace();
	        }
	        return result;
	}
	
	public DBHandler() {
		//this.main = main;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
	        //String url = "jdbc:mysql://localhost:3305/tc-db-log";
			String url = "jdbc:mysql://localhost:3306/tc-db-log";
	        conn = DriverManager.getConnection(url,"root",""); 
	        
	        System.out.println("Connection to mysql has been established.");
	        connected = true;
	            
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
	            
	        
	}

}
