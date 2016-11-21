/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelastering;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.RandomizableClusterer;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

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

    public MyKMeans(){
        super();

        setSeed(10);
        numCluster = 2; // Default = 2
    }

    public MyKMeans(int numCluster){
        this.numCluster = numCluster;
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
        while (!isConverged) {
            isConverged = true;
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
}
