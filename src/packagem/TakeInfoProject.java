package packagem;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TakeInfoProject {
	
	private TakeInfoProject() {
		throw new UnsupportedOperationException();
	}

	public static List<String> subTakeContentClass(String[] nameRelease1,String nameRelease,int[] numberOfRequest,JSONArray files) throws InterruptedException, JSONException, IOException {
		/* Metodo necessario per ridurre la complessità di takeContentClass */
		ArrayList<String> allContent = new ArrayList<>();
		if(nameRelease1[0].compareTo(nameRelease)!=0) {
			nameRelease1[0]=nameRelease;
	    	for(int j = 0; j < files.length();j++) {
				String[] v=files.getJSONObject(j).getString("path").split("/");
				if(v[v.length-1].contains(".java")) {
					numberOfRequest[0]=sleep(numberOfRequest[0])+1;
					JSONObject sizeUrl=GetConnection.readJsonFromUrl(files.getJSONObject(j).getString("url"));
					String content=sizeUrl.getString("content");
					content = content.replaceAll("\r\n|\r|\n","");
					allContent.add(content);
				}
	    	}
		}
		return allContent;
		
	}
	public static void takeContentClass(String fileUrl,String projectName) throws IOException, JSONException, InterruptedException {
		/* Metodo necessario per scaricare il contenuto delle classi e successivamente utilizzate per calcolare 
		 * la metrica size
		 */
		String st1="https://api.github.com/repos/apache/";
		String st2="/tags";
		String st3="?page=";
		int t=1;
		ArrayList<String> allContent = new ArrayList<>();
		for(;;t++){
			JSONArray releaseUrl= GetConnection.readJsonArrayFromUrl1(st1+projectName+st2+st3+t);
			if(releaseUrl.length()!=0) {
				String nameRelease="";
				String[] nameRelease1=new String[1];
				nameRelease1[1]="";
				int[] numberOfRequest=new int[1];
				numberOfRequest[0]=1;
				String currentRelease="";
				for(int i = 0 ; i < releaseUrl.length() ; i++){
					String[] release = releaseUrl.getJSONObject(i).getString("name").toString().split("-");
					currentRelease=release[1];
					if(!verificsVersion(currentRelease,projectName).isEmpty()){
						JSONObject releaseActual =GetConnection.readJsonFromUrl(releaseUrl.getJSONObject(i).getJSONObject("commit").getString("url"));
						numberOfRequest[0]=sleep(numberOfRequest[0])+2;
						JSONObject filesF =GetConnection.readJsonFromUrl(releaseActual.getJSONObject("commit").getJSONObject("tree").getString("url")+"?recursive=1");
						JSONArray files=filesF.getJSONArray("tree");
						String[] releaseN = releaseUrl.getJSONObject(i).getString("name").toString().split("-");
					    nameRelease1[0]=releaseN[1];
					    allContent.addAll(subTakeContentClass(nameRelease1,nameRelease,numberOfRequest,files));
					
					}
				}
			}else {
				break;
			}
		}
		Logger logger = Logger.getAnonymousLogger();
		try(FileWriter file= new FileWriter(fileUrl)) { 
	        // Constructs a FileWriter given a file name, using the platform's default charset
	        for(int k = 0; k< allContent.size(); k++) {
	        	file.write(allContent.get(k));
	        	file.write("\n");
	        }
	        file.flush();

	    } catch (IOException e) {
	        logger.info("Errore IO!");

	    }
	}
	
	public static void takeJiraInfo(String pathFile,String projectName) throws IOException, JSONException {
		/* Metodo per scaricare e scrivere u file le informazioni di Jira di un determinato progetto */
		JSONArray json1=new JSONArray();
	    Integer j = 0;
	    Integer i = 0; 
	    Integer total = 1;
	    //Get JSON API for closed bugs w/ AV in the project
	    do {
	       //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
	       j = i + 1000;
	       String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project%20%3D%20"+projectName+"%20AND%20issuetype%20%3D%20Bug%20AND%20status%20in%20(Resolved,%20Closed)%20AND%20resolution%20=%20Fixed%20&&fields=resolutiondate,fixVersions,versions,created&startAt="+ 
	         		i.toString() + "&maxResults=" + j.toString();
	       JSONObject json = GetConnection.readJsonFromUrl1(url);
	       JSONArray issues = json.getJSONArray("issues");
	       total = json.getInt("total");
	       for (; i < total && i < j; i++) {
	          //Iterate through each bug
	          json1.put(issues.getJSONObject(i%1000));
	       }
	    } while (i < total);
	    Logger logger = Logger.getAnonymousLogger();
		try(FileWriter file = new FileWriter(pathFile);) {
			 
	        // Constructs a FileWriter given a file name, using the platform's default charset
	        file.write(json1.toString());
	        file.flush();

	    } catch (IOException e) {
	        logger.info("Errore IO!");
	    }
	}
	
	public static void getCommit(String urlFile,String projectName) throws IOException, JSONException {
	    /* Metodo per scaricare i commit da GitHub di un progetto */
		JSONArray object = new JSONArray();
		String url1 = "https://api.github.com/repos/apache/"+projectName+"/commits";
		String url2 = "?page=";
		String url3 = "&per_page=100";
		int i=1;
		for(; ;i++) {
			//Scorro le varie pagine
			JSONArray json = GetConnection.readJsonArrayFromUrl(url1+url2+i+url3);
			Integer l = json.length();
			if(l!= 0) { // Verifico che la pagina contiene almeno un committ
				for(int k = 0 ; k< l ; k++) { // Questo scorre i vari committ
					JSONObject json1 = GetConnection.readJsonFromUrl(url1+"/"+json.getJSONObject(k).getString("sha"));
					object.put(json1);
				}
			}else {
				break;
			}
		}
		Logger logger = Logger.getAnonymousLogger();
		try(FileWriter file= new FileWriter(urlFile)) {
			 
	        // Constructs a FileWriter given a file name, using the platform's default charset
	        file.write(object.toString());
	        file.flush();

	    } catch (IOException e) {
	        logger.info("Errore IO");

	    }
	}
	
	public static List<String> verificsVersion(String version,String projectName) {
		/* Metodo per la lettura del File contenente le info delle versioni */
		HashMap<String,String> numberVersions= (HashMap<String, String>) GetMetrics.readFileName("C:\\Users\\gabri\\OneDrive\\Desktop\\"+projectName+"VersionInfo.csv");
		String corrispondenza="";
		List<String> verVersion= new ArrayList<>();
		for (Entry<String, String> key : numberVersions.entrySet()) {
            String value = key.getValue();
            if(value.compareTo(version)==0) {
            	corrispondenza= ".";
            	verVersion.add(corrispondenza);
            	verVersion.add(key.getKey());
            	break;
            }
        }
		return verVersion;		
	}
	public static int sleep(int nRequest) throws InterruptedException {
		/* Metodo necessario per evitare di fare piu' di 5000 richieste l'ora */
		if(nRequest==4500) {
			TimeUnit.HOURS.sleep(1);
			nRequest=0;
		}
		return nRequest;
	}
}