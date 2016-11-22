/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kelastering;

import java.io.BufferedReader;
import java.io.FileReader;
import kelastering.Model.WekaAccessor;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.clusterers.*;
import weka.core.Instances;

/**
 *
 * @author ryanyonata
 */
public class Main {
    
    public Instances dataset;
    public Instances testset;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        //WekaAccessor accessor = new WekaAccessor();
        Instances trainset;
        ClusterEvaluation eval;
        String path = "D:\\Kuliah\\Semester VII\\ML\\Kelastering\\Kelastering\\resources\\weather.nominal.arff";
        BufferedReader data = new BufferedReader(new FileReader(path));
        trainset = new Instances(data);
        MyAgnes wekaKmeans = new MyAgnes(2,0,trainset);
        //wekaKmeans.setSeed(3);
        //wekaKmeans.setPreserveInstancesOrder(true);
        //wekaKmeans.setNumClusters(2);
        //wekaKmeans.buildClusterer(trainset);
        //eval = new ClusterEvaluation();
        //eval.setClusterer(wekaKmeans);
        //eval.evaluateClusterer(trainset);
        
        /*
        HierarchicalClusterer wekaAgnes = new HierarchicalClusterer();
        wekaAgnes.setNumClusters(2);
        wekaAgnes.buildClusterer(trainset);
        eval = new ClusterEvaluation();
        eval.setClusterer(wekaAgnes);
        eval.evaluateClusterer(trainset);
        */

        //System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());
       
    }
}
