/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelastering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import weka.clusterers.AbstractClusterer;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author ryanyonata
 */
public class MyAgnes extends AbstractClusterer {
    
    final static int SINGLE = 0;
    final static int COMPLETE = 1;
    
    Instances m_instances;
    int m_clusters = 2;
    int m_linkType = SINGLE;
    HashMap<ArrayList<Integer>,Double> distanceMatrix = new HashMap<>();
    ArrayList<ArrayList<Instance>> clusters = new ArrayList<>();
    //ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<>();
    EuclideanDistance distanceCounter;
    //TO DO: Struktur data untuk hirarki

    public MyAgnes (int clusters, int linkType, Instances data) throws Exception {
        m_clusters = clusters;
        m_linkType = linkType;
        buildClusterer(data);
    }
    
    @Override
    public void buildClusterer(Instances data) throws Exception {
        m_instances = data;
        int nInstances = m_instances.numInstances();
        if (nInstances == 0)
            return;
        
        ArrayList<ArrayList<Integer>> clusterID = new ArrayList<>();
        for (int i = 0; i < nInstances; i++) {
            clusterID.add(i, new ArrayList<Integer>());
        }
        
        while (nInstances > m_clusters) {
            for (int i = 0; i < clusterID.size() - 1; i++) {
                for (int j = i+1; j < clusterID.size(); j++) {
                    //TODO: find closest clusters
                    
                }
            }
            //ato gimana caranya make fungsi yg udah dibuat. hahahah
            //TODO: combine closest pair
        }
    }

    @Override
    public int numberOfClusters() throws Exception {
        
        return Math.min(m_clusters, m_instances.numInstances());
    }
    
    public double findClosestDistance(ArrayList<Instance> cluster1, ArrayList<Instance> cluster2) {
        ArrayList<Double> calculatedDistances = new ArrayList<>();
        
        for (Instance a : cluster1) {
            for (Instance b: cluster2) {
                calculatedDistances.add(distanceCounter.distance(a,b));
            }
        }
        
        double min = calculatedDistances.get(0);
        for (Double i : calculatedDistances) {
            if (i < min) {
                min = i;
            }
        }
        
        return min;
    }
    
    public double findFurthestDistance(ArrayList<Instance> cluster1, ArrayList<Instance> cluster2) {
        ArrayList<Double> calculatedDistances = new ArrayList<>();
        
        for (Instance a : cluster1) {
            for (Instance b: cluster2) {
                calculatedDistances.add(distanceCounter.distance(a,b));
            }
        }
        
        double max = calculatedDistances.get(0);
        for (Double i : calculatedDistances) {
            if (i > max) {
                max = i;
            }
        }
        
        return max;
    }
    
    public void updateDistanceMatrix(ArrayList<ArrayList<Instance>> clusters) {
        distanceMatrix.clear();
        
        for (int i = 0; i < clusters.size()-1; i++) {
            for (int j = i+1; j < clusters.size(); j++) {
                ArrayList<Integer> arr = new ArrayList<>();
                arr.set(0, i);
                arr.set(1, j);
                double newDistance;
                if (m_linkType == 0) {
                    newDistance = findClosestDistance(clusters.get(i),clusters.get(j));
                } else {
                    newDistance = findFurthestDistance(clusters.get(i),clusters.get(j));
                } 
                distanceMatrix.put(arr,newDistance);
            }
        }
    }
    
    
    
}
