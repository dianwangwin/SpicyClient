package info.spicyclient.files;

import java.io.IOException;
import java.util.ArrayList;

public class AltInfo {
	
	public ArrayList<Alt> alts = new ArrayList<AltInfo.Alt>();
	
	public String API_Key = "api-xxxx-xxxx-xxxx";
	
	public void addAlt(String email, String password, boolean premium) {
		
		if (premium) {
			AltInfo.Alt a = new Alt(email, password, premium);
			this.alts.add(a);
			
			try {
				FileManager.saveAltInfo(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			AltInfo.Alt a = new Alt(email, password, premium);
			a.username = email;
			this.alts.add(a);
			
			try {
				FileManager.saveAltInfo(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void addCreatedAlt(Alt a) {
		
		if (a.premium) {
			
			this.alts.add(a);
			
			try {
				FileManager.saveAltInfo(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			
			this.alts.add(a);
			
			try {
				FileManager.saveAltInfo(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static class Alt{
		
		public String username = "Log in to view the username";
		public String email;
		public String password;
		public boolean premium;
		public int status = 0;
		public long unbannedAt = 0;
		
		
		public Alt(String email, String password, boolean premium) {
			
			this.email = email;
			this.password = password;
			this.premium = premium;
			
		}
		
	}
	
}
