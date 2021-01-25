package packagem;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class OperationDate {
	
	private OperationDate() {
		throw new UnsupportedOperationException();
	}

	public static Date convertData(String dateInString) {
		/* mi permette di convertire una stringa in formato yyyy-MM-dd in formata Date*/
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Logger logger = Logger.getAnonymousLogger();
	    try {
	        return formatter.parse(dateInString);
	    } catch (ParseException e) {
	        logger.info("Errore nel parsiong");
	    }
		return null;
	}
	
	public static List<Date> calcoloDate(String version,String projectName){
		/* Trovo le due date che mi indicano 
		 * l'intervallo di competenza della versione indicata
		 */
		Date dataFile=convertData(takeDataVersion(version,projectName));
		int versionS=Integer.parseInt(version);
		versionS++;
		ArrayList<Date> values= new ArrayList<>();
		String versionSucc=Integer.toString(versionS);
		values.add(dataFile);
		if(takeDataVersion(versionSucc,projectName).equalsIgnoreCase(".")) {
			values.add(null);
		}else {
			values.add(convertData(takeDataVersion(versionSucc,projectName)));
		}
		return values;
		
	}
	
	public static String takeDataVersion(String version,String projectName) {
		/* Metodo per ottenere la data di una determianta versione di Progetto */
		String csvFile = "C:\\Users\\gabri\\OneDrive\\Desktop\\"+projectName+"VersionInfo.csv";
	    String line = "";
	    String cvsSplitBy = ",";
	    Integer salta=0;
	    String dateVersion =".";
		Logger logger = Logger.getAnonymousLogger();
	    try (BufferedReader br= new BufferedReader(new FileReader(csvFile))){

	        while ((line = br.readLine()) != null) {

	            // use comma as separator
	            String[] country = line.split(cvsSplitBy);
	            if(salta != 0 && country[0].equalsIgnoreCase(version) ) {
	            	dateVersion=country[3];
	            	break;
	            }
	            salta++;
	        }

	    } catch (FileNotFoundException e) {
	        logger.info("File non trovato");
	    } catch (IOException e) {
	        logger.info("Errore IO");
	    }
	    return dateVersion;
	}
}