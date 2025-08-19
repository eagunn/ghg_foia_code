//Christopher Wendling
//June 3rd, 2011

//Used to download KML data from Google Fusion Tables
//and then Insert into Oracle tables

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

import java.awt.List;
import java.io.*;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.sql.*;


public class UpdateQuery {

	  private static final String SERVICE_URL =
	      "https://www.google.com/fusiontables/api/query";
	  
	  private static final Pattern CSV_VALUE_PATTERN =
	      Pattern.compile("([^,\\r\\n\"]*|\"(([^\"]*\"\")*[^\"]*)\")(,|\\r?\\n)");

	  
	  private GoogleService service;
	  
	  public UpdateQuery(String email, String password)
      throws AuthenticationException {
	    service = new GoogleService("fusiontables", "fusiontables.ApiExample");
	    service.setUserCredentials(email, password, ClientLoginAccountType.GOOGLE);
	  }
	  
	  public UpdateQuery(String authToken) throws AuthenticationException {
		  service = new GoogleService("fusiontables", "fusiontables.ApiExample");
		  service.setUserToken(authToken);
	  }
	  
	  public List runSelect(String selectQuery) throws IOException,
      ServiceException {
		  List list = new List();
	    URL url = new URL(
	        SERVICE_URL + "?sql=" + URLEncoder.encode(selectQuery, "UTF-8"));
	    GDataRequest request = service.getRequestFactory().getRequest(
	            RequestType.QUERY, url, ContentType.TEXT_PLAIN);
	
	    request.execute();
	
	  /* Prints the results of the query.                */
	  /* No Google Fusion Tables API-specific code here. */
	
	    Scanner scanner = new Scanner(request.getResponseStream(),"UTF-8");
	    while (scanner.hasNextLine()) {
	      scanner.findWithinHorizon(CSV_VALUE_PATTERN, 0);
	      MatchResult match = scanner.match();
	      String quotedString = match.group(2);
	      String decoded = quotedString == null ? match.group(1)
	          : quotedString.replaceAll("\"\"", "\"");
	      list.add(decoded);
	    }
	    return list;
	  }
	  public static void main(String[] args) throws ServiceException, IOException, Exception {
		  Class.forName ("oracle.jdbc.driver.OracleDriver");
		    UpdateQuery example = new UpdateQuery("wendlingcm@saic.com", "GHGPTool");
		    List list;
		    
		    //Due to limits on Google Fusion Tables, you have to adjust the SKIP factor until all of the DB has been filled
		    //Skip by factors of 400... EX: 0, 400, 800, 1200
		    
		    //USED FOR COUNTY DATA
		    //list = example.runSelect("SELECT 'geometry', 'FIPS formula' FROM 210217 SKIP 0 LIMIT 400");
		    
		    //USED FOR STATE DATA
		    list = example.runSelect("SELECT 'geometry', 'id' FROM 926036");
		    
		    String data;
		    String stateAbbr;
		    String oracleState;
		    InputStream is;
		    String query;
		    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@10.44.4.113:1521:DBDEV20", "d_ggds", "d_ggds");
	        try {
	        	Statement stmt = conn.createStatement();
	        	try {
	        		for(int i=1; i < list.getItemCount()/2;i++){
				    	data = list.getItem((i*2));
				    	is = new ByteArrayInputStream(data.getBytes("UTF-8"));
				    	stateAbbr = list.getItem((i*2)+1);
				    	System.out.println(stateAbbr);
				    	try {
//				    		USED TO VERIFY THAT THE SAIC DB WAS POPULATED CORRECTLY, PRINTS IN PLAIN TEXT
//				    		ResultSet rs = stmt.executeQuery("SELECT * FROM PUB_DIM_COUNTY_GEO");
//				    		 while (rs.next()) {
//				    		        Blob b = rs.getBlob("GEO_DATA");
//				    		        byte[] bdata = b.getBytes(1, (int) b.length());
//				    		        String text = new String(bdata);
//				    		        System.out.println(text);
//				    		        break;
//				    		}
				    		
				    		//USED FOR COUNTY DATA
					    	//PreparedStatement pstmt = conn.prepareStatement("UPDATE PUB_DIM_COUNTY_GEO SET GEO_DATA = ? WHERE COUNTY_FIPS = "+stateAbbr);
						    
				    		//USED FOR STATE DATA
					    	PreparedStatement pstmt = conn.prepareStatement("UPDATE PUB_DIM_STATE_GEO SET GEO_DATA = ? WHERE STATE = "+stateAbbr);
					    	
					    	pstmt.setBlob(1, is);
						    
					    	//COMMENT OUT IF USING THE VERIFICATION CODE ABOVE
					    	pstmt.executeUpdate();
				    	} finally {
				    		
				    	}
	        		}
	        	} finally {
	        		try { stmt.close(); } catch (Exception ignore) {}
	        	}
	        } finally {
	        	try { conn.close(); } catch (Exception ignore) {}
	        }
	  }	  
}
