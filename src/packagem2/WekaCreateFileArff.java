package packagem2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class WekaCreateFileArff {
	public static final String PATH0="\\Users\\gabri\\OneDrive\\Desktop\\Metriche";
	public static final String PATH1="C:";
	public static final String PATH2="\\Users\\gabri\\OneDrive\\Desktop\\";
	public static final String PATH3=" Semestre 1\\ISW2\\Falessi\\20200407 Falessi Deliverable 2 Milestone 1 V2\\GetReleaseInfo\\";
	public static final String INFO="VersionInfo.csv";
	public static final String PATH4="\\Users\\gabri\\OneDrive\\Desktop\\WekaFiles\\Metriche";
	
	private WekaCreateFileArff() {
		throw new UnsupportedOperationException();
	}

	
	public static void createFileCsvTesting(int version,String projectName) throws IOException {
		String line="";
		try(BufferedReader filecsv= new BufferedReader(new FileReader(PATH1+PATH0+projectName+".csv"))){
			try(FileWriter csvversion=new FileWriter(PATH1+PATH4+projectName+""+version+"Testing.csv")){
				line=filecsv.readLine();
				csvversion.write(line);
				csvversion.write("\n");
				line=filecsv.readLine();
				while(line!=null) {
					String[] z= line.split(",");
					if(Integer.parseInt(z[0])==version) {
						csvversion.write(line);
						csvversion.write("\n");
					}
					line=filecsv.readLine();
					
				}
			}
		}
			
	}
	
	public static void createFileCsvTraining(int version,String projectName) throws IOException {
		String line="";
		try(BufferedReader filecsv= new BufferedReader(new FileReader(PATH1+PATH0+projectName+".csv"))){
			try(FileWriter csvversion=new FileWriter(PATH1+PATH4+projectName+version+"Training.csv")){
				line=filecsv.readLine();
				csvversion.write(line);
				csvversion.write("\n");
				line=filecsv.readLine();
				while(line!=null) {
					String[] z= line.split(",");
					if(Integer.parseInt(z[0])<version) {
						csvversion.write(line);
						csvversion.write("\n");
					}
					line=filecsv.readLine();
					
				}
			}
		}	
	}
	
	public static void createFileArff(int i,String projectName) throws IOException {
		CSVLoader loader1 = new CSVLoader();
		loader1.setSource(new File(PATH1+PATH4+projectName+i+"Training.csv"));
		Instances data1 = loader1.getDataSet(); 
		ArffSaver saver1 = new ArffSaver();
	    saver1.setInstances(data1);
	    saver1.setFile(new File(PATH1+PATH4+projectName+i+"Training.arff"));
	    saver1.writeBatch();
	    
	    CSVLoader loader2 = new CSVLoader();
		loader2.setSource(new File(PATH1+PATH4+projectName+i+"Testing.csv"));
		Instances data2 = loader1.getDataSet();
		ArffSaver saver2 = new ArffSaver();
	    saver2.setInstances(data2);
	    saver2.setFile(new File(PATH1+PATH4+projectName+i+"Testing.arff"));
	    saver2.writeBatch();
	}
	
	public static List<Integer> foundVersion(String projectName) throws IOException {
		 String csvFile =PATH1+PATH2+projectName+INFO;
		 System.out.println(csvFile);
	     String line = "";
	     String cvsSplitBy = ",";
	     Integer salta=0;
	     Logger logger = Logger.getAnonymousLogger();
	     ArrayList<Integer> version= new ArrayList<>();
	     try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

	         while ((line = br.readLine()) != null) {

	             // use comma as separator
	             String[] country = line.split(cvsSplitBy);
	             if(salta != 0) {
	            	 version.add(Integer.parseInt(country[0])); 
	             }
	             salta++;
	         }

	     } catch (FileNotFoundException e) {
	         logger.info("File non trovato");
	     } catch (IOException e) {
	         logger.info("Errore IO");
	     }	
		 return version;
		
	}
	
	public static void main(String[] args) throws Exception {
	    
		String projectName ="BOOKKEEPER";

		ArrayList<Integer> version=(ArrayList<Integer>) foundVersion(projectName);
		for(int i = 0;i<version.size();i++) {
			createFileCsvTraining(version.get(i),projectName);
			createFileCsvTesting(version.get(i),projectName);
			createFileArff(version.get(i),projectName);
		}
		
	  }
}