package packagem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* Questa Classe mi permette di ootere informazioni in formato JSONArray o JsonObject */
public class GetConnection {
	
	private GetConnection() {
		throw new UnsupportedOperationException();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		/* Metodo per ottere un JSONObject ,utilizzando una connessione autenticata che mi permette di fare
		 * 5000 richieste l'ora*/
		URLConnection uc = takeUrlConnection(url); 
		try(InputStreamReader inputStreamReader= new InputStreamReader(uc.getInputStream());
		    BufferedReader rd = new BufferedReader(inputStreamReader)) {
		    return new JSONObject(readAll(rd));
		}
	}

	
	public static JSONObject readJsonFromUrl1(String url) throws IOException, JSONException {
		/* Metodo per ottere un JSONObject senza nessun tipo di autorizzazione */
	   InputStream is = new URL(url).openStream();
	   try(BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
		   String jsonText = readAll(rd);
		   return new JSONObject(jsonText);
	   } finally {
		   is.close();
	   }
	}
	
	private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	}
	public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		/* Metodo per ottere un JSONArray senza nessun tipo di autorizzazione */
	    InputStream is = new URL(url).openStream();
	    try(BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
	       String jsonText = readAll(rd);
	       return new JSONArray(jsonText);
	     } finally {
	       is.close();
	     }
	}
	
	public static JSONArray readJsonArrayFromUrl1(String url) throws IOException, JSONException {
		//Utilizzato per fare richieste autenticate che permette di fare 5000 richieste l'ora
        URLConnection uc = takeUrlConnection(url);

		try(InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
		    BufferedReader rd = new BufferedReader(inputStreamReader)) {
		    return new JSONArray(readAll(rd));
		}
		   
	}
	public static URLConnection takeUrlConnection(String url) throws IOException {
		/* mi permette ri prendere le informazioni per Creare una connessione autenticata*/
		URL url1 = new URL(url);
        URLConnection uc = url1.openConnection();
        uc.setRequestProperty("X-Requested-With", "Curl");
        String username =  "Brielino";
        try(BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\gabri\\OneDrive\\Desktop\\Token richieste autorizzate GitHub.txt"))){
        	String line = reader.readLine();
            String token =  line;
            String userpass = username + ":" + token;
            byte[] encodedBytes = Base64.getEncoder().encode(userpass.getBytes());
            String basicAuth = "Basic " + new String(encodedBytes);
            uc.setRequestProperty("Authorization", basicAuth);
            return uc;
        }
	}
}