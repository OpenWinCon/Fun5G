package mclab;

public class AP {

	public String id;
	public String ip;
	public String ssid;
	public String desc;
	public String pw;
	public String broad;
	public String channel;
	public String time;
	public String types;
	public String values;

	private static final int AP_ID = 0;
	private static final int AP_IP = 1;
	private static final int AP_SSID = 2;
	private static final int AP_DESCRIPTION = 3;
	private static final int AP_PASSWORD = 4;
	private static final int AP_HIDE = 5;
	private static final int AP_CHANNEL = 6;


	public AP() {
		this.id = null;
		this.ip = null;
		this.ssid = null;
		this.desc = null;
		this.pw = null;
		this.broad = null;
		this.channel = null;
		this.time = null;
		this.types = "";
		this.values = "";
		// TODO Auto-generated constructor stub
	}

	public AP(String[] parser) {
		for(int index = 1; parser[index].compareTo("99") != 1; index++)
		{
			switch(Integer.parseInt(parser[index]))
			{
			case AP_ID:
				this.setId(parser[++index]);
				break;
			case AP_IP:
				this.setIp(parser[++index]);
				break;
			case AP_SSID:
				this.setSsid(parser[++index]);
				break;
			case AP_DESCRIPTION:
				this.setDesc(parser[++index]);
				break;
			case AP_PASSWORD:
				this.setPw(parser[++index]);
				break;
			case AP_HIDE:
				this.setBroad(parser[++index]);
				break;
			case AP_CHANNEL:
				this.setChannel(parser[++index]);
				break;
			case 99:
				return;
			default: break;
			}
		}

	}
	
	
	public String TypeToValue(int typeNum)
	{
		switch(typeNum)
		{
		case AP_ID: return this.id;
		case AP_IP: return this.ip;
		case AP_SSID: return this.ssid;
		case AP_DESCRIPTION: return this.desc;
		case AP_PASSWORD: return this.pw;
		case AP_HIDE: return this.broad;
		case AP_CHANNEL: return this.channel;
		default: return null;
		}
	}
	
	public String ValueTypeToString(int typeNum)
	{
		switch(typeNum)
		{
		case AP_ID: return "ID";
		case AP_IP: return "IP";
		case AP_SSID: return "SSID";
		case AP_DESCRIPTION: return "Description";
		case AP_PASSWORD: return "Password";
		case AP_HIDE: return "Broadcast";
		case AP_CHANNEL: return "Channel";
		default: return null;
		}
	}
	
	public String makeInsertQuery() 
	{
		String query = "INSERT INTO AP_Information (";
		for(int i = AP_ID; i <= AP_CHANNEL; i++)
		{
			query += ValueTypeToString(i);
			query += ", ";
		}
		
		query += "time) VALUES (";
	
		for(int i = AP_ID; i <= AP_CHANNEL; i++)
		{
			query += "\"";
			query += TypeToValue(i);
			query += "\", ";
		}

		query += "CURRENT_TIMESTAMP)";
		return query;
	}
	

	public String makeUpdateQuery() 
	{
		String query = makeInsertQuery();
		query += " ON DUPLICATE KEY UPDATE ";
		
		for(int i = AP_ID; i <= AP_CHANNEL; i++)
		{
			query += ValueTypeToString(i);
			query += "=\"";
			query += TypeToValue(i);
			query += "\", ";
		}
		query += "time=CURRENT_TIMESTAMP";
		return query;
	}
	
	public String GetTypes()
	{
		return types.substring(0, types.length()-2);
	}
	
	public String GetValues()
	{
		return values.substring(0, values.length()-2);
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {

		this.types += "ID, ";
		this.values += "\"";
		this.values += id;
		this.values += "\", ";
		this.id = id;

	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.types += "IP, ";
		this.values += "\"";
		this.values += ip;
		this.values += "\", ";
		this.ip = ip;
	}


	public String getSsid() {
		return ssid;
	}


	public void setSsid(String ssid) {
		this.types += "SSID, ";
		this.values += "\"";
		this.values += ssid;
		this.values += "\", ";
		this.ssid = ssid;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.types += "Description, ";
		this.values += "\"";
		this.values += desc;
		this.values += "\", ";
		this.desc = desc;
	}


	public String getPw() {
		return pw;
	}


	public void setPw(String pw) {
		this.types += "Password, ";
		this.values += "\"";
		this.values += pw;
		this.values += "\", ";
		this.pw = pw;
	}


	public String getBroad() {
		return broad;
	}


	public void setBroad(String broad) {
		this.types += "Broadcast, ";
		this.values += "\"";
		this.values += broad;
		this.values += "\", ";
		this.broad = broad;
	}


	public String getChannel() {
		return channel;
	}


	public void setChannel(String channel) {
		this.types += "Channel, ";
		this.values += "\"";
		this.values += channel;
		this.values += "\", ";
		this.channel = channel;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}



}
