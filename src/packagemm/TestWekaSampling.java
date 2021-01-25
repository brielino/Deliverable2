package packagemm;

import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.lazy.IBk;
import weka.filters.supervised.instance.SMOTE;
/*Classe che mi permettee e di applicare Sampling su un Data Set 66/33 
 * applicando 3 tipi di Classificatori e diverse metodologie di Sampling 
 */
public class TestWekaSampling {
	
	public static final String PATH="\\Users\\gabri\\OneDrive\\Desktop\\Metriche";
	public static final String PATH0="\\Users\\gabri\\OneDrive\\Desktop\\SamplingFiles\\";
	public static final String PATH1="C:";
	public static final String PROJECTNAME="TAJO";
	public static final String TRAININGSMOTE="TrainingSmote.csv";
	public static final String RANDOMFOREST="RandomForest";
	public static final String NAIVEBAYES="NaiveBayes";
	public static final String IBK="IBk";
	public static final String SMOTE="SMOTE";
	public static final String NS="NoSampling";
	public static final String OVS="OverSampling";
	public static final String UNS="UnderSampling";
	protected static final String[]  OPTS={ "-B", "1.0", "-Z", "130.3"};
	
	private TestWekaSampling() {
		throw new UnsupportedOperationException();
	}

	public static int foundPartition() throws IOException {
		/* Calcolo il numero di istanze per una partizione 66/33% (Training/Testing) */
		String line="";
		int count=0;
		try(BufferedReader filecsv= new BufferedReader(new FileReader(PATH1+PATH+PROJECTNAME+".csv"))){
			line=filecsv.readLine();
			count++;
			while(line!=null) {
				count++;
				line=filecsv.readLine();
				
			}
		}
		return count*66/100;
	}
	
	public static void doNothing() {
		// Questo metodo è stato introdotto per fare nulla
	}
	
	public static void createFileCsvTraining(int numberSplit) throws IOException {
		/* Creo 2 file Csv uno per il Testing e un per Training utilizzando numSplit come delimitatore per 
		 * la partizione del Data Set
		 */
		String line="";
		int count=1;
		try(BufferedReader filecsv= new BufferedReader(new FileReader(PATH1+PATH+PROJECTNAME+".csv"))){
			try(FileWriter fileTraining=new FileWriter(PATH1+PATH0+PROJECTNAME+TRAININGSMOTE);
					FileWriter fileTesting =new FileWriter(PATH1+PATH0+PROJECTNAME+"TestingSmote.csv")){
				line=filecsv.readLine();
				fileTraining.write(line);
				fileTraining.write("\n");
				fileTesting.write(line);
				fileTesting.write("\n");
				line=filecsv.readLine();
				while(count!=numberSplit) {
					line=filecsv.readLine();
					fileTraining.write(line);
					fileTraining.write("\n");
					count++;
				}
				line=filecsv.readLine();
				while(line!=null) {
					fileTesting.write(line);
					fileTesting.write("\n");
					line=filecsv.readLine();
				}
			}
		}	
	}
	public static double takeYforOverSampling() throws IOException {
		/* mi permette di calcolare la percentuale della classe maggioritara del Data Set,
		 * informaazione necessarie per poter implementare OverSampling 
		 */
		String line="";
		int count=1;
		int y=0;
		int n=0;
		try(BufferedReader fileTraining =new BufferedReader(new FileReader(PATH1+PATH0+PROJECTNAME+TRAININGSMOTE))){
			line=fileTraining.readLine();
			while(line!=null) {
				String[] z= line.split(",");
				if(z[11].compareTo("YES")==0) {
					y++;
				}
				if(z[11].compareTo("NO")==0) {
					n++;
				}
				count++;
				line=fileTraining.readLine();
			}
		}
		if(y>n) {
			return ((double)y/count);
		}else {
			return ((double)n/count);
		}
	}
	/* Insieme di metodi che implementano le diverse tipologie di Sampling (SMOTE,OverSampling,UnderSampling) 
	 * applicati a i diversi classificatori 
	 */
	public static Evaluation overSamplingRandomForest(Instances trainingORF,Instances testingORF){
		try{
			Resample resample = new Resample();
			
			resample.setInputFormat(trainingORF);
			
			resample.setNoReplacement(false);
			resample.setSampleSizePercent(2*takeYforOverSampling());
			resample.setOptions(OPTS);
			
			FilteredClassifier fcORF = new FilteredClassifier();

			RandomForest rf = new RandomForest();
			fcORF.setClassifier(rf);
			
			fcORF.setFilter(resample);
			fcORF.buildClassifier(trainingORF);
			Evaluation evalORF = new Evaluation(testingORF);	
			evalORF.evaluateModel(fcORF, testingORF);
			return evalORF;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation overSamplingNaiveBayes(Instances treaningONB,Instances testingONB){
		try{
			Resample resample = new Resample();
			resample.setInputFormat(treaningONB);
			resample.setNoReplacement(false);
			resample.setSampleSizePercent(2*takeYforOverSampling());
			resample.setOptions(OPTS);
			
			FilteredClassifier fcONB = new FilteredClassifier();

			NaiveBayes naiveB = new NaiveBayes();
			fcONB.setClassifier(naiveB);
			
			fcONB.setFilter(resample);
			fcONB.buildClassifier(treaningONB);
			Evaluation evalONB = new Evaluation(testingONB);	
			evalONB.evaluateModel(fcONB, testingONB);
			return evalONB;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation overSamplingIBk(Instances trainingOIBK,Instances testingOIBK){
			
		try{
			Resample resample = new Resample();
			resample.setInputFormat(trainingOIBK);
			resample.setNoReplacement(false);
			resample.setSampleSizePercent(2*takeYforOverSampling());
			resample.setOptions(OPTS);
			
			FilteredClassifier fcOIBK = new FilteredClassifier();
	
			IBk ibk = new IBk();
			fcOIBK.setClassifier(ibk);
			
			fcOIBK.setFilter(resample);
			fcOIBK.buildClassifier(trainingOIBK);
			Evaluation evaOIBK = new Evaluation(testingOIBK);	
			evaOIBK.evaluateModel(fcOIBK, testingOIBK);
			return evaOIBK;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation smoteSamplingRandomForest(Instances trainingSRF,Instances testingSFR){
			
		try {
			FilteredClassifier fcSRF = new FilteredClassifier();
			RandomForest rfSRF = new RandomForest();
			fcSRF.setClassifier(rfSRF);
			
			SMOTE smote = new SMOTE();
			smote.setInputFormat(trainingSRF);
			fcSRF.setFilter(smote);
			
			fcSRF.buildClassifier(trainingSRF);
			Evaluation evaSFR = new Evaluation(testingSFR);	
			evaSFR.evaluateModel(fcSRF, testingSFR);
			return evaSFR;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation smoteSamplingNaiveBayes(Instances trainingSNB,Instances testingSNB){
		try {	
			FilteredClassifier fcSNB = new FilteredClassifier();
			NaiveBayes naiveB = new NaiveBayes();
			fcSNB.setClassifier(naiveB);
			
			SMOTE smote = new SMOTE();
			smote.setInputFormat(trainingSNB);
			fcSNB.setFilter(smote);
			
			fcSNB.buildClassifier(trainingSNB);
			Evaluation evalSNB = new Evaluation(testingSNB);	
			evalSNB.evaluateModel(fcSNB, testingSNB);
			return evalSNB;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation smoteSamplingIBk(Instances trainingSIBK,Instances testingSIBK){
		try {
			FilteredClassifier fcSIBK = new FilteredClassifier();
			IBk ibk = new IBk();
			fcSIBK.setClassifier(ibk);
			
			SMOTE smote = new SMOTE();
			smote.setInputFormat(trainingSIBK);
			fcSIBK.setFilter(smote);
			
			fcSIBK.buildClassifier(trainingSIBK);
			Evaluation evaSIBK = new Evaluation(testingSIBK);	
			evaSIBK.evaluateModel(fcSIBK, testingSIBK);
			return evaSIBK;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation underSamplingRandomForest(Instances trainingURF,Instances testingURF){
		try {	
			FilteredClassifier fcURF = new FilteredClassifier();
			RandomForest rfURF = new RandomForest();
			fcURF.setClassifier(rfURF);
			
			SpreadSubsample  spreadSubsample = new SpreadSubsample();
			String[] opts = new String[]{ "-M", "1.0"};
			spreadSubsample.setOptions(opts);
			fcURF.setFilter(spreadSubsample);
			
			fcURF.buildClassifier(trainingURF);
			Evaluation evaURF = new Evaluation(testingURF);	
			evaURF.evaluateModel(fcURF, testingURF);
			return evaURF;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation underSamplingNaiveBayes(Instances trainingUNB,Instances testingUNB){
		try {	
			FilteredClassifier fcUNB = new FilteredClassifier();
			NaiveBayes nbUNB = new NaiveBayes();
			fcUNB.setClassifier(nbUNB);
			
			SpreadSubsample  spreadSubsample = new SpreadSubsample();
			String[] opts = new String[]{ "-M", "1.0"};
			spreadSubsample.setOptions(opts);
			fcUNB.setFilter(spreadSubsample);
			
			fcUNB.buildClassifier(trainingUNB);
			Evaluation evaUNB = new Evaluation(testingUNB);	
			evaUNB.evaluateModel(fcUNB, testingUNB);
			return evaUNB;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Evaluation underSamplingIBk(Instances trainingUIBK,Instances testingUIBK){
		try {	
			FilteredClassifier fcUIBK = new FilteredClassifier();
			IBk ibkU = new IBk();
			fcUIBK.setClassifier(ibkU);
			
			SpreadSubsample  spreadSubsample = new SpreadSubsample();
			String[] opts = new String[]{ "-M", "1.0"};
			spreadSubsample.setOptions(opts);
			fcUIBK.setFilter(spreadSubsample);
			
			fcUIBK.buildClassifier(trainingUIBK);
			Evaluation evalUIBK = new Evaluation(testingUIBK);	
			evalUIBK.evaluateModel(fcUIBK, testingUIBK);
			return evalUIBK;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void createFileArff() throws IOException {
		/* Metodo per la conversione di file Csv in file ARFF */
		CSVLoader loader1 = new CSVLoader();
		loader1.setSource(new File(PATH1+PATH0+PROJECTNAME+TRAININGSMOTE));
		Instances data1 = loader1.getDataSet(); 
		ArffSaver saver1 = new ArffSaver();
	    saver1.setInstances(data1);
	    saver1.setFile(new File(PATH1+PATH0+PROJECTNAME+"TrainingSmote.arff"));
	    saver1.writeBatch();
	    
	    CSVLoader loader2 = new CSVLoader();
		loader2.setSource(new File(PATH1+PATH0+PROJECTNAME+"TestingSmote.csv"));
		Instances data2 = loader1.getDataSet();
		ArffSaver saver2 = new ArffSaver();
	    saver2.setInstances(data2);
	    saver2.setFile(new File(PATH1+PATH0+PROJECTNAME+"TestingSmote.arff"));
	    saver2.writeBatch();
	}
	
	public static void writefile(FileWriter filewriter,Evaluation eval,String sampling,String classifier) throws IOException {
		/* Scrive il FileWriter con le informazioni passate come parametri */
		filewriter.append(PROJECTNAME);
		filewriter.append(",");
		filewriter.append(sampling);
		filewriter.append(",");
		filewriter.append(classifier);
		filewriter.append(",");
		filewriter.append(Double.toString(eval.precision(1)));
		filewriter.append(",");
		filewriter.append(Double.toString(eval.recall(1)));
		filewriter.append(",");
		filewriter.append(Double.toString(eval.areaUnderROC(1)));
		filewriter.append(",");
		filewriter.append(Double.toString(eval.kappa()));
		filewriter.append(",");
		filewriter.append(Double.toString(eval.pctCorrect()));
		filewriter.append("\n");		
	}
	
	public static void main(String[] args) throws Throwable{
		createFileCsvTraining(foundPartition());
		createFileArff();
		DataSource source1 = new DataSource(PATH1+PATH0+PROJECTNAME+"TrainingSmote.arff");
		Instances training = source1.getDataSet();
		DataSource source2 = new DataSource(PATH1+PATH0+PROJECTNAME+"TestingSmote.arff");
		Instances testing = source2.getDataSet();
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);
		try(FileWriter filewriter=new FileWriter(PATH1+PATH0+PROJECTNAME+"InfoSampling.csv")){
			filewriter.append("DataSet,#TipeSampling,Classifier,Precision,Recall,AUC,Kappa,Correct%\n");
			RandomForest rf = new RandomForest();
			rf.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(rf, testing);
			Evaluation urf = underSamplingRandomForest(training,testing);
			Evaluation orf = overSamplingRandomForest(training,testing);
			Evaluation srf = smoteSamplingRandomForest(training,testing);
			if(urf !=null && orf !=null && srf !=null) {
				writefile(filewriter,eval,NS,RANDOMFOREST);
				writefile(filewriter,urf,UNS,RANDOMFOREST);
				writefile(filewriter,orf,OVS,RANDOMFOREST);
				writefile(filewriter,srf,SMOTE,RANDOMFOREST);
			}
			NaiveBayes naiveB = new NaiveBayes();
			naiveB.buildClassifier(training);
			Evaluation eval1 = new Evaluation(testing);	
			eval1.evaluateModel(naiveB, testing);
			Evaluation unb = underSamplingNaiveBayes(training,testing);
			Evaluation onb = overSamplingNaiveBayes(training,testing);
			Evaluation snb = smoteSamplingNaiveBayes(training,testing);
			if(unb !=null && onb !=null && snb !=null) {
				writefile(filewriter,eval1,NS,NAIVEBAYES);
				writefile(filewriter,unb,UNS,NAIVEBAYES);
				writefile(filewriter,onb,OVS,NAIVEBAYES);
				writefile(filewriter,snb,SMOTE,NAIVEBAYES);
			}
			IBk ibk = new IBk();
			ibk.buildClassifier(training);
			Evaluation eval2 = new Evaluation(testing);
			Evaluation uib = underSamplingIBk(training,testing);
			Evaluation oib = overSamplingIBk(training,testing);
			Evaluation sib = smoteSamplingIBk(training,testing);
			if( uib != null && oib != null && sib !=null) {
				writefile(filewriter,eval2,NS,IBK);
				writefile(filewriter,uib,UNS,IBK);
				writefile(filewriter,oib,OVS,IBK);
				writefile(filewriter,sib,SMOTE,IBK);	
			}
		}			
	}
}