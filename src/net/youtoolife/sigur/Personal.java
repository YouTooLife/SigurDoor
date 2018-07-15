package net.youtoolife.sigur;

public class Personal {
	
	public String dir = "";
	public String date = "";
	public int doorID = -1;
	public int id = 0;
	public int idAction = -1;
	public String name = "";
	public String phone = "";
	
	public Personal() {
		
	}
	
	public Personal(int id, String name, String phone, String date, String dir, int doorID) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.date = date;
		this.dir = dir;
		this.doorID = doorID;
	}
	

}
