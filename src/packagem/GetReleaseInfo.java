package packagem;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class GetReleaseInfo {
	
	protected static  HashMap<LocalDateTime, String> releaseNames;
	protected static  HashMap<LocalDateTime, String> releaseID;
	protected static ArrayList<LocalDateTime> releases;
	protected static Integer numVersions;

public static void creazione() throws IOException, JSONException {
	   String projName ="BOOKKEEPER";
	   //Fills the arraylist with releases dates and orders them
	   //Ignores releases with missing dates
	   Logger logger = Logger.getAnonymousLogger();
	   releases = new ArrayList<>();
       Integer i;
       String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
       JSONObject json = GetConnection.readJsonFromUrl1(url);
       JSONArray versions = json.getJSONArray("versions");
       releaseNames = new HashMap<>();
       releaseID = new HashMap<> ();
       for (i = 0; i < versions.length(); i++ ) {
          String name = "";
          String id = "";
          if(versions.getJSONObject(i).has("releaseDate")) {
             if (versions.getJSONObject(i).has("name"))
                name = versions.getJSONObject(i).get("name").toString();
             if (versions.getJSONObject(i).has("id"))
                id = versions.getJSONObject(i).get("id").toString();
             
             addRelease(versions.getJSONObject(i).get("releaseDate").toString(),name,id);
          }
       }
       
       // order releases by date
       releases.sort(null);
       if (releases.size() < 6)
            return;
       String outname = projName + "VersionInfo.csv";
	 
       try (FileWriter fileWriter = new FileWriter(outname);){
            //Name of CSV for output
            fileWriter.append("Index,Version ID,Version Name,Date");
            fileWriter.append("\n");
            numVersions = releases.size();
            for ( i = 0; i < releases.size(); i++) {
               Integer index = i + 1;
               fileWriter.append(index.toString());
               fileWriter.append(",");
               fileWriter.append(releaseID.get(releases.get(i)));
               fileWriter.append(",");
               fileWriter.append(releaseNames.get(releases.get(i)));
               fileWriter.append(",");
               fileWriter.append(releases.get(i).toString());
               fileWriter.append("\n");
            }
            fileWriter.flush();

         } catch (Exception e) {
            logger.info("Error in csv writer");
         } 
	}
 
	
	   public static void addRelease(String strDate, String name, String id) {
		      LocalDate date = LocalDate.parse(strDate);
		      LocalDateTime dateTime = date.atStartOfDay();
		      if (!releases.contains(dateTime)) {
		         releases.add(dateTime);
		         releaseNames.put(dateTime, name);
		         releaseID.put(dateTime, id);
		      }
	   }
   
	   public static void main(String[] args) throws IOException, JSONException, InterruptedException {
		   CreateFileCsv.main(null);
	   }
}