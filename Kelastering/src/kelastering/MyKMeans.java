/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelastering;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.RandomizableClusterer;
import weka.core.*;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author ryanyonata
 */
public class MyKMeans extends RandomizableClusterer {

    private Instances centroids;
    private int numCluster;
    protected DistanceFunction distanceFunction = new EuclideanDistance();
    private double [] squaredErrors;
    private int[] assignment;
    private int maxIterations = 500;
    private int iterations;
    private int[] clusterSize;

    public MyKMeans(){
        super();

        setSeed(10);
        numCluster = 2; // Default = 2
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }


    public void setNumClusters(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("Number of clusters must be > 0");
        }
        numCluster = n;
    }

    @Override
    public void buildClusterer(Instances data) throws Exception {
        Instances instances = new Instances(data);

        if(instances.numInstances() == 0){
            throw new RuntimeException("The dataset should not be empty");
        }
        if(numCluster == 0){
            throw new RuntimeException("Number of clusters must be > 0");
        }

        distanceFunction.setInstances(instances);
        squaredErrors = new double[numCluster];

        // Init Centroids
        centroids = new Instances(instances, numCluster);
        int instanceSize = instances.numInstances();
        ArrayList<Integer> chosenIndex = new ArrayList<>(numCluster);
        Random rg = new Random(getSeed());
        for (int i=0; i<numCluster;i++){
            int chosen = rg.nextInt(instanceSize);
            while (chosenIndex.contains(chosen)){
                chosen = rg.nextInt(instanceSize);
            }
            chosenIndex.add(chosen);
            centroids.add(instances.instance(chosen));
        }

        assignment = new int[instanceSize];
        Instances [] tempClusters = new Instances[numCluster];

        boolean isConverged = false;

        iterations = 0;
        while (!isConverged) {
            isConverged = true;
            iterations++;
            System.out.println(iterations);
            for (int i = 0; i < instanceSize; i++){
                Instance toBeClustered = instances.instance(i);
                int newCluster = clusterProcessedInstance(toBeClustered,true);
                if (newCluster != assignment[i]){ // Cluster berbeda / berubah
                    isConverged = false;
                }
                assignment[i] = newCluster;
            }

            // Update Centroids
            centroids = new Instances(instances, numCluster);
            for (int i = 0; i < numCluster; i++){
                tempClusters[i] = new Instances(instances, 0); // Membuat set of instances kosong
            }
            for (int i = 0; i < instanceSize; i++){
                tempClusters[assignment[i]].add(instances.instance(i));
            }
            for (int i = 0; i< numCluster; i++){
                moveCentroid(tempClusters[i]);
            }

            if (!isConverged){
                squaredErrors = new double[numCluster];
            }

            if (iterations == maxIterations){
                isConverged = true;
            }
            clusterSize = new int [numCluster];
            for (int i = 0; i < numCluster; i++){
                clusterSize[i] = tempClusters[i].numInstances();
            }
        }

    }

    @Override
    public int numberOfClusters() throws Exception {
        return numCluster;
    }

    public int clusterInstance(Instance instance) {
        return clusterProcessedInstance(instance, false);
    }

    public int clusterProcessedInstance(Instance instance, boolean updateErrors) {
        double minDist = Integer.MAX_VALUE;
        int bestCluster = 0;
        for (int i = 0; i < numCluster; i++) {
            double dist = distanceFunction.distance(instance, centroids.instance(i));
            if (dist < minDist) {
                minDist = dist;
                bestCluster = i;
            }
        }
        if (updateErrors) {
            if(distanceFunction instanceof EuclideanDistance){
                //Euclidean distance to Squared Euclidean distance
                minDist *= minDist;
            }
            squaredErrors[bestCluster] += minDist;
        }
        return bestCluster;
    }

    protected double[] moveCentroid(Instances members){
        double [] vals = new double[members.numAttributes()];

        for (int j = 0; j < members.numAttributes(); j++) {
            vals[j] = members.meanOrMode(j);
        }
        centroids.add(new Instance(1.0, vals));

        return vals;
    }

    public String toString() {
        if (centroids == null) {
            return "No clusterer built yet!";
        }

        int maxWidth = 0;
        int maxAttWidth = 0;
        boolean containsNumeric = false;
        for (int i = 0; i < numCluster; i++) {
            for (int j = 0 ;j < centroids.numAttributes(); j++) {
                if (centroids.attribute(j).name().length() > maxAttWidth) {
                    maxAttWidth = centroids.attribute(j).name().length();
                }
                if (centroids.attribute(j).isNumeric()) {
                    containsNumeric = true;
                    double width = Math.log(Math.abs(centroids.instance(i).value(j))) /
                            Math.log(10.0);
                    //          System.err.println(m_ClusterCentroids.instance(i).value(j)+" "+width);
                    if (width < 0) {
                        width = 1;
                    }
                    // decimal + # decimal places + 1
                    width += 6.0;
                    if ((int)width > maxWidth) {
                        maxWidth = (int)width;
                    }
                }
            }
        }

        for (int i = 0; i < centroids.numAttributes(); i++) {
            if (centroids.attribute(i).isNominal()) {
                Attribute a = centroids.attribute(i);
                for (int j = 0; j < centroids.numInstances(); j++) {
                    String val = a.value((int)centroids.instance(j).value(i));
                    if (val.length() > maxWidth) {
                        maxWidth = val.length();
                    }
                }
                for (int j = 0; j < a.numValues(); j++) {
                    String val = a.value(j) + " ";
                    if (val.length() > maxAttWidth) {
                        maxAttWidth = val.length();
                    }
                }
            }
        }

        // check for size of cluster sizes
        for (int i = 0; i < clusterSize.length; i++) {
            String size = "(" + clusterSize[i] + ")";
            if (size.length() > maxWidth) {
                maxWidth = size.length();
            }
        }

        String plusMinus = "+/-";
        maxAttWidth += 2;
        if (maxAttWidth < "Attribute".length() + 2) {
            maxAttWidth = "Attribute".length() + 2;
        }

        if (maxWidth < "Full Data".length()) {
            maxWidth = "Full Data".length() + 1;
        }

        if (maxWidth < "missing".length()) {
            maxWidth = "missing".length() + 1;
        }



        StringBuffer temp = new StringBuffer();
        //    String naString = "N/A";


    /*    for (int i = 0; i < maxWidth+2; i++) {
          naString += " ";
          } */
        temp.append("\nkMeans\n======\n");
        temp.append("\nNumber of iterations: " + iterations+"\n");

        if(distanceFunction instanceof EuclideanDistance){
            temp.append("Within cluster sum of squared errors: " + Utils.sum(squaredErrors));
        }else{
            temp.append("Sum of within cluster distances: " + Utils.sum(squaredErrors));
        }

        temp.append("\n\nCluster centroids:\n");
        temp.append(pad("Cluster#", " ", (maxAttWidth + (maxWidth * 2 + 2)) - "Cluster#".length(), true));

        temp.append("\n");
        temp.append(pad("Attribute", " ", maxAttWidth - "Attribute".length(), false));


//        temp.append(pad("Full Data", " ", maxWidth + 1 - "Full Data".length(), true));

        // cluster numbers
        for (int i = 0; i < numCluster; i++) {
            String clustNum = "" + i;
            temp.append(pad(clustNum, " ", maxWidth + 1 - clustNum.length(), true));
        }
        temp.append("\n");

        // cluster sizes
        String cSize = "";
        temp.append(pad(cSize, " ", maxAttWidth - cSize.length(), true));
        for (int i = 0; i < numCluster; i++) {
            cSize = "(" + clusterSize[i] + ")";
            temp.append(pad(cSize, " ",maxWidth + 1 - cSize.length(), true));
        }
        temp.append("\n");

        temp.append(pad("", "=", maxAttWidth +
                (maxWidth * (centroids.numInstances())
                        + centroids.numInstances()), true));
        temp.append("\n");

        for (int i = 0; i < centroids.numAttributes(); i++) {
            String attName = centroids.attribute(i).name();
            temp.append(attName);
            for (int j = 0; j < maxAttWidth - attName.length(); j++) {
                temp.append(" ");
            }

            String strVal;
            String valMeanMode;
            // full data
//            if (centroids.attribute(i).isNominal()) {
//                if (m_FullMeansOrMediansOrModes[i] == -1) { // missing
//                    valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
//                } else {
//                    valMeanMode =
//                            pad((strVal = centroids.attribute(i).value((int)m_FullMeansOrMediansOrModes[i])),
//                                    " ", maxWidth + 1 - strVal.length(), true);
//                }
//            } else {
//                if (Double.isNaN(m_FullMeansOrMediansOrModes[i])) {
//                    valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
//                } else {
//                    valMeanMode =  pad((strVal = Utils.doubleToString(m_FullMeansOrMediansOrModes[i],
//                            maxWidth,4).trim()),
//                            " ", maxWidth + 1 - strVal.length(), true);
//                }
//            }
//            temp.append(valMeanMode);

            for (int j = 0; j < numCluster; j++) {
                if (centroids.attribute(i).isNominal()) {
                    if (centroids.instance(j).isMissing(i)) {
                        valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
                    } else {
                        valMeanMode =
                                pad((strVal = centroids.attribute(i).value((int)centroids.instance(j).value(i))),
                                        " ", maxWidth + 1 - strVal.length(), true);
                    }
                } else {
                    if (centroids.instance(j).isMissing(i)) {
                        valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
                    } else {
                        valMeanMode = pad((strVal = Utils.doubleToString(centroids.instance(j).value(i),
                                maxWidth,4).trim()),
                                " ", maxWidth + 1 - strVal.length(), true);
                    }
                }
                temp.append(valMeanMode);
            }
            temp.append("\n");
        }

        temp.append("\n\n");
        return temp.toString();
    }

    private String pad(String source, String padChar,
                       int length, boolean leftPad) {
        StringBuffer temp = new StringBuffer();

        if (leftPad) {
            for (int i = 0; i< length; i++) {
                temp.append(padChar);
            }
            temp.append(source);
        } else {
            temp.append(source);
            for (int i = 0; i< length; i++) {
                temp.append(padChar);
            }
        }
        return temp.toString();
    }
}
