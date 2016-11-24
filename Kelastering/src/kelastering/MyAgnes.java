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
import weka.core.DistanceFunction;
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
    int[] clusters;
    ArrayList<ArrayList<Integer>> clusterID = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<Integer>>> hierarchy = new ArrayList<>();
    protected DistanceFunction m_DistanceFunction = new EuclideanDistance();

    public MyAgnes () throws Exception {
        m_clusters = 2;
        m_linkType = SINGLE;
    }
    
    public void setNumClusters(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("Number of clusters must be > 0");
        }
        m_clusters = n;
    }
    
    public void setLinkType(int n) throws Exception {
        if ((n != SINGLE) && (n != COMPLETE)) {
            throw new Exception("Wrong link type");
        }
        m_linkType = n;
    }
    
    
    @Override
    public void buildClusterer(Instances data) throws Exception {
        m_instances = data;
        int nInstances = m_instances.numInstances();
        
        if(m_instances.numInstances() == 0){
            throw new RuntimeException("The dataset should not be empty");
        }
        if(m_clusters == 0){
            throw new RuntimeException("Number of clusters must be > 0");
        }
        hierarchy.add(new ArrayList<ArrayList<Integer>>());
        for (int i = 0; i < nInstances; i++) {
            clusterID.add(new ArrayList<Integer>());
            clusterID.get(i).add(i);
            hierarchy.get(0).add(new ArrayList<Integer>());
            hierarchy.get(0).get(i).add(i);
        }
        
        m_DistanceFunction.setInstances(m_instances);
        int idi, idj;
        double min, temp;
        while (nInstances > m_clusters) {
            //System.out.println("Iterasi: " + nInstances);
            min = Double.MAX_VALUE;
            idi = -1;
            idj = -1;
            for (int i = 0; i < clusterID.size()-1; i++) {
                for (int j = i+1; j < clusterID.size(); j++) {
                    if (m_linkType == SINGLE) {
                        temp = findClosestDistance(clusterID.get(i), clusterID.get(j));
                    } else {
                        temp = findFurthestDistance(clusterID.get(i), clusterID.get(j));
                    }
                    
                    if (temp < min) {
                        min = temp;
                        idi = i;
                        idj = j;
                    }
                }
            }

            if (idi > -1) {
                for (int i = 0; i < clusterID.get(idj).size(); i++) {
                    clusterID.get(idi).add(clusterID.get(idj).get(i));
                }
                clusterID.remove(idj);
            }
            nInstances--;
            hierarchy.add(new ArrayList<ArrayList<Integer>>());
            for (int i = 0; i < clusterID.size(); i++) {
                hierarchy.get(m_instances.numInstances()-nInstances).add(new ArrayList<Integer>());
                for (int j = 0; j < clusterID.get(i).size(); j++) {
                    hierarchy.get(m_instances.numInstances()-nInstances).get(i).add(clusterID.get(i).get(j));
                }
            }
        }
        
        //Assign clusters
        clusters = new int[m_instances.numInstances()];
        for (int i = 0; i < clusterID.size(); i++) {
            for (int j = 0; j < clusterID.get(i).size(); j++) {
                clusters[clusterID.get(i).get(j)] = i;
            }
        }
    }

    @Override
    public int numberOfClusters() throws Exception {
        
        return Math.min(m_clusters, m_instances.numInstances());
    }
    
    public double findClosestDistance(ArrayList<Integer> cluster1, ArrayList<Integer> cluster2) {
        ArrayList<Double> calculatedDistances = new ArrayList<>();
        
        if ((cluster1.size() == 0) || (cluster2.size() == 0)) {
            return Double.MAX_VALUE;
        }
        
        for (Integer a : cluster1) {
            for (Integer b: cluster2) {
                //System.out.println("Dist: " + m_DistanceFunction.distance(m_instances.instance(a),m_instances.instance(b)));
                calculatedDistances.add(m_DistanceFunction.distance(m_instances.instance(a),m_instances.instance(b)));
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
    
    public double findFurthestDistance(ArrayList<Integer> cluster1, ArrayList<Integer> cluster2) {
        ArrayList<Double> calculatedDistances = new ArrayList<>();
        
        if ((cluster1.size() == 0) || (cluster2.size() == 0)) {
            return Double.MAX_VALUE;
        }
        
        for (Integer a : cluster1) {
            for (Integer b: cluster2) {
                calculatedDistances.add(m_DistanceFunction.distance(m_instances.instance(a),m_instances.instance(b)));
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
    
    public void print() {
        for (int i = 0; i < hierarchy.size(); i++) {
            for (int j = 0; j < hierarchy.get(i).size(); j++) {
                System.out.print(hierarchy.get(i).get(j));
            }
            System.out.println();
        }
        for (int i = 0; i < clusterID.size(); i++) {
            System.out.println("Cluster: " + i);
            for (int j = 0; j < clusterID.get(i).size(); j++) {
                System.out.print(clusterID.get(i).get(j) + " ");
            }
            System.out.println();
        }
        
        System.out.println();
        for (int i = 0; i < clusterID.size(); i++) {
            System.out.print(i + "     ");
            double percentage = (clusterID.get(i).size() * 100)/m_instances.numInstances();
            System.out.print(clusterID.get(i).size() + "( "+ percentage + "%)");
            System.out.println();
        }
    }
}
