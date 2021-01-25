package packagem;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

public class GetMetrics {
	public static final String FIELDS="fields";
	public static final String VERSIONS="versions";
	public static final String COMMIT="commit";
	public static final String MESSAGE="message";
	public static final String AUTHOR="author";
	public static final String FIXVERSIONS="fixVersions";
	public static final String FILES="files";
	public static final String ADDITIONS="additions";
	public static final String DELETIONS="deletions";
	public static final String PATH1="C:";
	public static final String PATH2="\\Users\\gabri\\OneDrive\\Desktop\\";
	public static final String PATH3=" Semestre 1\\ISW2\\Falessi\\20200407 Falessi Deliverable 2 Milestone 1 V2\\GetReleaseInfo\\";
	public static final String INFO="VersionInfo.csv";
	
	private GetMetrics() {
		throw new UnsupportedOperationException();
	}
	
	public static String getSize(String code) {
		/* Metodo necessario per convertire il contenuto di una classe Criptato in Stringa
		 * e calcolarne il LOC
		 */
		String classe=new String(Base64.getMimeDecoder().decode(code));
		int size=0;
		for(int i=0; i<classe.length() ; i++) {
			if(classe.charAt(i) == '\n') {
				size++;
			}
		}
		return Integer.toString(size);
	}
	
	public static int foundVersion(Date data,String projectName) throws IOException {
	/* Mi permette di individuare da una Data la versione a cui fa riferimento */
		 String csvFile =PATH1+PATH2+projectName+INFO;
	     String line = "";
	     String cvsSplitBy = ",";
	     Integer salta=0;
	     int numVersions=0;
	     Date versionData=null;
	     Logger logger = Logger.getAnonymousLogger();
	     try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

	         while ((line = br.readLine()) != null) {

	             // use comma as separator
	             String[] country = line.split(cvsSplitBy);
	             if(salta != 0) {
	            	 versionData=OperationDate.convertData(country[3]);
	            	 if(data.after(versionData)) {
	            		 numVersions=Integer.parseInt(country[0]);
	            	 }else {
	            		 break;
	            	 }
	            	 
	             }
	             salta++;
	         }

	     } catch (FileNotFoundException e) {
	         logger.info("File non trovato");
	     } catch (IOException e) {
	         logger.info("Errore IO");
	     }	
		 return numVersions;
		
	}

	
	public static Map<String, String> readFileName(String nameFile) {
		System.out.println(nameFile);
		/* Metodo per ottenere il numero di versione associato al rispettivo nome (Es. 0.2.0) */ 
		 String csvFile = nameFile;
	     String line = "";
	     String cvsSplitBy = ",";
	     Integer salta=0;
	     Logger logger = Logger.getAnonymousLogger();
	     Map<String,String> versionsName= new HashMap<>();
	     try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

	         while ((line = br.readLine()) != null) {

	             // use comma as separator
	             String[] country = line.split(cvsSplitBy);
	             if(salta != 0) {
	            	 versionsName.put(country[0], country[2]);
	             }
	             salta++;
	         }

	     } catch (FileNotFoundException e) {
	         logger.info("File non trovato");
	     } catch (IOException e) {
	         logger.info("Errore IO");
	     }
		return versionsName;
	}
	
	public static List<ArrayList<String>> notHaveAv(JSONArray object,JSONArray object1,int k,String projectName,int p) throws JSONException, IOException {
		/* Metodo per verificare se un determinato ticket di jira che non ha l'AV è o meno Buggy */
		List<ArrayList<String>> ticket=new ArrayList<>();
		for(int i = 0;i<object.length();i++) {
			Boolean prima=verifiCorrisp(object.getJSONObject(i).getJSONObject(COMMIT).getString(MESSAGE),object1.getJSONObject(k).get("key").toString());
			if(Boolean.TRUE.equals(prima)) {
				String nameReleaseOv = object.getJSONObject(i).getJSONObject(COMMIT).getJSONObject(AUTHOR).getString("date");
				Date dataOv = OperationDate.convertData(nameReleaseOv);
				int ov=foundVersion(dataOv,projectName);
				if(object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(FIXVERSIONS).length()==0) {
					break;
				}
				String nameReleaseFv = object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(FIXVERSIONS).getJSONObject(0).getString("name").toString();
			    int fv=calculateFvIv(nameReleaseFv,projectName);
			    int iv=fv-(fv-ov)*p;
			    if(ov>=iv && ov<fv) {
			    	ArrayList<String> riferimento=new ArrayList<>();
			    	riferimento.add(Integer.toString(iv));
			    	riferimento.add(object1.getJSONObject(k).get("key").toString());
			    	ticket.add(riferimento);
			    }
			}
		}
		return ticket;
	}
	
	public static List<ArrayList<String>> haveAv1(int k,JSONArray object1,Map<String,String> numberVersions,JSONArray object,String projectName,int[] p) throws JSONException, IOException{
		/* Metodo che mi permette di individuare i ticket di Jira che hanno l'AV 
		 * aggiungendo alla lista il nome dei ticket che contengono classi Buggy
		 */
		List<ArrayList<String>> ticket=new ArrayList<>();
		for(int i = 0;i<object.length();i++) {
			Boolean prima=verifiCorrisp(object.getJSONObject(i).getJSONObject(COMMIT).getString(MESSAGE),object1.getJSONObject(k).get("key").toString());
			if(Boolean.TRUE.equals(prima)) {
				String nameReleaseIv = object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(VERSIONS).getJSONObject(0).getString("name").toString();
				if(object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(FIXVERSIONS).length()==0) {
					break;
				}
				String nameReleaseFv = object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(FIXVERSIONS).getJSONObject(0).getString("name").toString();
				String nameReleaseOv = object.getJSONObject(i).getJSONObject(COMMIT).getJSONObject(AUTHOR).getString("date");
				Date dataOv = OperationDate.convertData(nameReleaseOv);
				int ov=foundVersion(dataOv,projectName);
			    int fv=calculateFvIv(nameReleaseFv,projectName);
			    int iv=calculateFvIv(nameReleaseIv,projectName);
				
				calculateP(fv,ov,iv,p);
				ticket.addAll(haveAv(k,object1,numberVersions));

			}
		}
		return ticket;
	}
	
	public static List<ArrayList<String>> haveAv(int k,JSONArray object1,Map<String,String> numberVersions) throws JSONException{
		/* Metodo che mi permette di individuare i ticket di Jira che hanno l'AV 
		 * aggiungendo alla lista il nome dei ticket che contengono classi Buggy
		 */
		List<ArrayList<String>> ticket=new ArrayList<>();
		for(int z = 0;z<object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(VERSIONS).length();z++) {
			for (Entry<String, String> key : numberVersions.entrySet()) {
	            String value = key.getValue();
	            String nomeav=object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(VERSIONS).getJSONObject(z).getString("name");
	            if(value.compareTo(nomeav)==0) {
	            	int iv=Integer.parseInt(key.getKey());
	            	ArrayList<String> riferimento=new ArrayList<>();
			    	riferimento.add(Integer.toString(iv));
			    	riferimento.add(object1.getJSONObject(k).get("key").toString());
			    	ticket.add(riferimento);
	            }
		    }
		}
		return ticket;
	}
	
	public static List<ArrayList<String>> foundBuggy(String projectName) throws  JSONException, IOException {
		String token = new String(Files.readAllBytes(Paths.get("C:\\Users\\gabri\\OneDrive\\Desktop\\"+projectName+"Commit.json")));
	    JSONArray object = new JSONArray(token);
	    String token1 =new String(Files.readAllBytes(Paths.get("C:\\Users\\gabri\\OneDrive\\Desktop\\"+projectName+"Jira.json")));
	    JSONArray object1 =new JSONArray(token1);
		List<ArrayList<String>> ticket=new ArrayList<>();
	    int[] p=new int[1];
	    p[0]=0;
		HashMap<String,String> numberVersions= (HashMap<String, String>) readFileName(PATH1+PATH2+projectName+INFO);
	    for(int k = object1.length()-1;k>=0;k--) {
	    	if(object1.getJSONObject(k).getJSONObject(FIELDS).getJSONArray(VERSIONS).length()==0) {
	    		ticket.addAll(notHaveAv(object,object1,k,projectName,p[0]));
	    	}else {
	    		ticket.addAll(haveAv1(k,object1,numberVersions,object,projectName,p));
	    	}
	    }
	    return ticket;
	}
	public static boolean verifiCorrisp(String str1,String str2) {
		/* Metodo per vedere se c'è corrispondenza tra due ticket */
		Boolean first=false;
		Boolean second =false;
		first=str1.contains(str2+":");
		second=str2.contains("["+str2+"]");
		if(Boolean.TRUE.equals(first)) {
			return first;
		}else if(Boolean.TRUE.equals(second)) {
			return second;
		}
		return false;
	}
	
	public static String subCalculateMetrics(List<ArrayList<String>> ticketBuggy,String version,String fileName) {
		/*Metodo introdotto per ridurre la complessità del metodo che calcola le metriche */
		String buggy="NO";
		for(int k=0;k<ticketBuggy.size();k++) {
			if(ticketBuggy.get(k).get(0).compareTo(version)==0 && ticketBuggy.get(k).get(1).compareTo(fileName)==0) {
				buggy="YES";
			}
		}
		return buggy;
	}
	
	public static List<String> createMetrics(int[] loc,int maxLocAdd,int count,int nr,int churn,int maxChurn,String buggy){
		/* Metodo per raccogliere tutte le informazioni di una classe di una determinata release
		 * e raggrupparle in un unico Array
		 */
		List<String> values=new ArrayList<>();
		values.add(Integer.toString(loc[0]));
		values.add(Integer.toString(loc[1]));
		values.add(Integer.toString(maxLocAdd));
		if(count==0) {
			count=1;
		}
		values.add(Float.toString((float)loc[1]/count));
		values.add(Integer.toString(churn));
		if(nr!=0) {
			values.add(Integer.toString(maxChurn));
		}else {
			values.add("0");
		}
		values.add(Float.toString((float)churn/count));
		values.add(Integer.toString(nr));
		values.add(buggy);
		return values;
	}
	public static List<String> calculateMetrics(String fileName,String version,List<Integer> codicisha,JSONArray json,List<ArrayList<String>> ticketBuggy) throws IOException, JSONException {
		/* Metodo per calcolare le metriche scelte per questo progetto esclusa SIZE */
		int locT=0;
		int locAdd=0;
		int maxLocAdd=0;
		int churn=0;
		int maxChurn=-9999999;
		int count=0;
		int nr=0;
		String buggy="NO";
		
		List<String> values= new ArrayList<>();
		for(int i=0; i<codicisha.size(); i++) {
			JSONArray json1=json.getJSONObject(codicisha.get(i)).getJSONArray(FILES);
			buggy=subCalculateMetrics(ticketBuggy,version,fileName);
			for(int j=0; j<json1.length();j++) {
				if(json1.getJSONObject(j).getString("filename").compareToIgnoreCase(fileName)==0) {
					if(json1.getJSONObject(j).getInt(ADDITIONS)>maxLocAdd) {
						maxLocAdd=json1.getJSONObject(j).getInt(ADDITIONS);
					}
					count++;
					nr=count;
					churn=churn+(json1.getJSONObject(j).getInt(ADDITIONS)-json1.getJSONObject(j).getInt(DELETIONS));
					if(json1.getJSONObject(j).getInt(ADDITIONS)-json1.getJSONObject(j).getInt(DELETIONS)>maxChurn) {
						maxChurn=json1.getJSONObject(j).getInt(ADDITIONS)-json1.getJSONObject(j).getInt(DELETIONS);
					}
					locAdd=locAdd+json1.getJSONObject(j).getInt(ADDITIONS);
					locT=locT +locAdd;
					locT=locT +json1.getJSONObject(j).getInt(DELETIONS);
					locT=locT +json1.getJSONObject(j).getInt("changes");
					break;
					}
			}
			
		}
		int[] loc=new int[2];
		loc[0]=locT;
		loc[1]=locAdd;
		values.addAll(createMetrics(loc,maxLocAdd,count,nr,churn,maxChurn,buggy));
		return values;

	}
	
	public static int calculateFvIv(String fv,String projectName){
		/* Metodo per calcolare l'indice FV e IV */
		HashMap<String,String> numberVersions= (HashMap<String, String>) GetMetrics.readFileName(PATH1+PATH2+projectName+INFO);
		int v = 0;
		for (Entry<String, String> key : numberVersions.entrySet()) {
            String value = key.getValue();
            if(value.compareTo(fv)==0) {
            	v=Integer.parseInt(key.getKey());
            	break;
            }
	    }
		return v;
	}
	

	public static List<ArrayList<String>> subFoundClassBuggy(Boolean prima,JSONArray object,int k,List<ArrayList<String>> ticketBuggy) throws JSONException{
		/* Metodo per ridurre la complessità di foundClassBuggy */
		List<ArrayList<String>> fileBuggy= new ArrayList<>();
		if(Boolean.TRUE.equals(prima)) {
			for(int z = 0;z<object.getJSONObject(k).getJSONArray(FILES).length();z++) {
				ArrayList<String> fb = new ArrayList<>();
				String version =ticketBuggy.get(k).get(0);
				String file = object.getJSONObject(k).getJSONArray(FILES).getJSONObject(z).getString("filename").toString();
				fb.add(version);
				fb.add(file);
				fileBuggy.add(fb);
			}
		}
		return fileBuggy;
	}
	
	
	public static List<ArrayList<String>> foundClassBuggy(List<ArrayList<String>> ticketBuggy,JSONArray object) throws JSONException{
		/*Metodo per individuare le classi che hanno molto probabilmente un Bug */
		Boolean prima;
	    List<ArrayList<String>> fileBuggy= new ArrayList<>();
		for(int k = 0;k<ticketBuggy.size();k++) {
			if(Integer.parseInt(ticketBuggy.get(k).get(0))!=0 && Integer.parseInt(ticketBuggy.get(k).get(0))>0) {
				for(int i=0; i<object.length(); i++) {
					prima=GetMetrics.verifiCorrisp(object.getJSONObject(i).getJSONObject(COMMIT).getString(MESSAGE),ticketBuggy.get(k).get(1));
					fileBuggy.addAll(subFoundClassBuggy(prima,object,k,ticketBuggy));
				}
					
			}
		}
		return fileBuggy;
	}
	public static void calculateP(int fv,int ov,int iv,int[] p) {
		/* Metodo per caloclare P utilizzato per il metodo Propotion */
		if((fv-ov)!=0) {
			p[0]=(fv-iv)/(fv-ov);
        }
	}
}