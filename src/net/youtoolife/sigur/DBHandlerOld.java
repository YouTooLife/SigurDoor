package net.youtoolife.sigur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class DBHandlerOld /*implements Runnable*/ {
	
	//private MainController main = null;
	Connection conn = null;
	boolean connected = false;
	
	int lastID = -1;

	//@Override
	public ArrayList<Personal> run(int lastSentID, int lastDBID) {

        //while (true) {
		//System.out.println(lastSentID + "_db___"+lastDBID);
	
		if (!connected)
			return null;
		
		if (lastID == -1)
			lastID = lastDBID;
		if (lastID == -1)
			lastID = lastSentID;
		
        boolean flag = false;
        ArrayList<Personal> arr = new ArrayList<>();
        try (Statement statement = conn.createStatement()) {
        	int limit = 1;
                String str0 =
                		"select id, logtime as time, devhint as door,"
                		+ " emphint as pid, "
                		+ "ord(substr(logdata, 5, 1)) as dir "
                		+ "from logs where substr(logdata,1,2)=0xFE06  order by id desc limit 1";
                ResultSet resultSet = statement.executeQuery(str0);
                int nLastID = -1;
                while (resultSet.next()) {
                	nLastID = resultSet.getInt("id");
                	System.out.println("DBHandler: "+nLastID);
                	
                if (nLastID == lastID) {
                	//System.out.println(nLastID);
                	flag = true;
                	continue;
                }
                
                if (lastID == -1) 
                	limit = 1;
                else 
                	limit = nLastID - lastID;
                //System.out.println(lastID == -1? "initDB":(limit+" = "+nLastID+" - "+lastID));
                }
                resultSet.close();
            	
                if (!flag) {
                	//System.out.println("yo");
                    String str =
                    		"select id, logtime as time, devhint as door,"
                    		+ " emphint as pid, "
                    		+ "ord(substr(logdata, 5, 1)) as dir "
                    		+ "from logs where substr(logdata,1,2)=0xFE06  order by id desc limit "+limit;
                    resultSet = statement.executeQuery(str);
                    while (resultSet.next()) {
                    Personal pers = new Personal();
                    pers.doorID = resultSet.getInt("door");
                    pers.date = resultSet.getString("time");
                    pers.dir = resultSet.getString("dir").equalsIgnoreCase("1")?"Выход":"Вход";
                    pers.id = resultSet.getInt("pid");
                    pers.idAction = resultSet.getInt("id");
                    arr.add(pers);
                    }
                    resultSet.close();
                    
                //////    
                for (Personal pers:arr) {
                str = "select name, sms_targetnumber as num from `tc-db-main`.personal where id="+pers.id;
                resultSet = statement.executeQuery(str);
                while (resultSet.next()) {
                    pers.name = resultSet.getString("name");
                    pers.phone = resultSet.getString("num");
                 }
                resultSet.close();
                }
               }
               lastID = nLastID;
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
        //ArrayList<String> arr2 = new ArrayList<>();
        Collections.reverse(arr);
		return arr;
	}
	
	public DBHandlerOld() {
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
