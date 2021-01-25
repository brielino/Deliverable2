package packagemm;

import weka.core.Instances;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.classifiers.lazy.IBk;


public class TestWekaEasy{
	
	public static final String PATH0="\\Users\\gabri\\OneDrive\\Desktop\\Metriche";
	public static final String PATH1="C:";
	public static final String PROJECTNAME="TAJO";
	static int numberofAttribute=0;
	static Boolean writeFilter=false;
	
	private TestWekaEasy() {
		throw new UnsupportedOperationException();
	}
	
	public static void writeFileCsv(String classifier,int i,double precision,double recall,double auc,double kappa,FileWriter fileWriter) throws IOException {
		fileWriter.append(PROJECTNAME);
		fileWriter.append(",");
		fileWriter.append(Integer.toString(i));
		fileWriter.append(",");
		fileWriter.append(classifier);
		fileWriter.append(",");
		fileWriter.append(Double.toString(precision));
		fileWriter.append(",");
		fileWriter.append(Double.toString(recall));
		fileWriter.append(",");
		fileWriter.append(Double.toString(auc));
		fileWriter.append(",");
		fileWriter.append(Double.toString(kappa));
		if(Boolean.TRUE.equals(writeFilter)) {
			fileWriter.append(",");
			fileWriter.append(Integer.toString(numberofAttribute));
		}
		fileWriter.append("\n");
		
		
	}
	
	public static void main(String[] args) throws Exception{
		//load datasets
		ArrayList<Integer> version=(ArrayList<Integer>) WekaCreateFileArff.foundVersion(PROJECTNAME);
		try(FileWriter fileWriter = new FileWriter(PATH1+"\\Users\\gabri\\OneDrive\\Desktop\\"+PROJECTNAME+"WekaInfo.csv");
				FileWriter fileWriterFilter= new FileWriter(PATH1+"\\Users\\gabri\\OneDrive\\Desktop\\"+PROJECTNAME+"WekaInfoFilter.csv")){
			fileWriter.append("DataSet,#TrainingR,Classifier,Precision,Recall,AUC,Kappa\n");
			fileWriterFilter.append("DataSet,#TrainingR,Classifier,Precision,Recall,AUC,Kappa,#Attribute\n");
			for(int i = 0;i<version.size();i++) {
				if(version.get(i)!=1) {
					DataSource source1 = new DataSource(PATH1+"\\Users\\gabri\\OneDrive\\Desktop\\WekaFiles\\Metriche"+PROJECTNAME+version.get(i)+"Training.arff");
					Instances training = source1.getDataSet();
					DataSource source2 = new DataSource(PATH1+"\\Users\\gabri\\OneDrive\\Desktop\\WekaFiles\\Metriche"+PROJECTNAME+version.get(i)+"Testing.arff");
					Instances testing = source2.getDataSet();
					int numAttr = training.numAttributes();
					training.setClassIndex(numAttr - 1);
					testing.setClassIndex(numAttr - 1);
					NaiveBayes classifier = new NaiveBayes();
					RandomForest classifier1 =new RandomForest();
					IBk classifier2 = new IBk();
					classifier.buildClassifier(training);
					classifier1.buildClassifier(training);
					classifier2.buildClassifier(training);
					Evaluation eval = new Evaluation(testing);
					eval.evaluateModel(classifier, testing);
					writeFilter=false;
					writeFileCsv("NaiveBayes",i,eval.precision(1),eval.recall(1),eval.areaUnderROC(1),eval.kappa(),fileWriter);
					eval.evaluateModel(classifier1, testing);
					writeFileCsv("RandomForest",i,eval.precision(1),eval.recall(1),eval.areaUnderROC(1),eval.kappa(),fileWriter);
					eval.evaluateModel(classifier2, testing);
					writeFileCsv("IBk",i,eval.precision(1),eval.recall(1),eval.areaUnderROC(1),eval.kappa(),fileWriter);
					
					//create AttributeSelection object
					AttributeSelection filter = new AttributeSelection();
					//create evaluator and search algorithm objects
					CfsSubsetEval eval1 = new CfsSubsetEval();
					GreedyStepwise search = new GreedyStepwise();
					//set the algorithm to search backward
					search.setSearchBackwards(true);
					//set the filter to use the evaluator and search algorithm
					filter.setEvaluator(eval1);
					filter.setSearch(search);
					//specify the dataset
					filter.setInputFormat(training);
					//apply
					//evaluation with filtered
					Instances filteredTraining = Filter.useFilter(training, filter);
					int numAttrFiltered = filteredTraining.numAttributes();
					numberofAttribute=numAttrFiltered;
					filteredTraining.setClassIndex(numAttrFiltered - 1);
					Instances testingFiltered = Filter.useFilter(testing, filter);
					testingFiltered.setClassIndex(numAttrFiltered - 1);
					classifier.buildClassifier(filteredTraining);
				    eval.evaluateModel(classifier, testingFiltered);
				    writeFilter=true;
				    writeFileCsv("NaiveBayes",i,eval.precision(1),eval.recall(1),eval.areaUnderROC(1),eval.kappa(),fileWriterFilter);
					classifier1.buildClassifier(filteredTraining);
				    eval.evaluateModel(classifier1, testingFiltered);
				    writeFileCsv("RandomForest",i,eval.precision(1),eval.recall(1),eval.areaUnderROC(1),eval.kappa(),fileWriterFilter);
				    classifier2.buildClassifier(filteredTraining);
				    eval.evaluateModel(classifier2, testingFiltered);
				    writeFileCsv("IBk",i,eval.precision(1),eval.recall(1),eval.areaUnderROC(1),eval.kappa(),fileWriterFilter);
				}
			}
		}
	}
}