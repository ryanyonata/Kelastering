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
    int[] clusters;
    ArrayList<ArrayList<Integer>> clusterID = new ArrayList<>();
    EuclideanDistance m_DistanceFunction;
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
        
        for (int i = 0; i < nInstances; i++) {
            clusterID.add(i, new ArrayList<Integer>());
        }

        int idi, idj;
        double min, temp;
        while (nInstances > m_clusters) {
            System.out.println("Iterasi: " + nInstances);
            min = 0;
            idi = -1;
            idj = -1;
            for (int i = 0; i < clusterID.size()-1; i++) {
                for (int j = i+1; j < clusterID.size(); j++) {
                    //TODO: find closest clusters
//                    for (int k = 0; k < clusterID.get(i).size(); k++) {
//                        for (int l = 0; l < clusterID.get(j).size(); l++) {
//                            temp = m_DistanceFunction.distance(m_instances.instance(clusterID.get(i).get(k)), m_instances.instance(clusterID.get(j).get(l)));
//                            if (temp < min) {
//                                min = temp;
//                                idi = i;
//                                idj = j;
//                            }
//                        }
//                    }
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
            //TODO: combine closest pair
            if (idi > -1) {
                for (int i = 0; i < clusterID.get(idj).size(); i++) {
                    clusterID.get(idi).add(clusterID.get(idj).get(i));
                }
                clusterID.remove(idj);
            }
            nInstances--;
        }
        
        //Assign clusters
        clusters = new int[m_instances.numInstances()];
        for (int i = 0; i < clusterID.size(); i++) {
            for (int j = 0; j < clusterID.get(i).size(); j++) {
                System.out.println(clusterID.get(i).get(j));
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
            return 0;
        }
        
        for (Integer a : cluster1) {
            for (Integer b: cluster2) {
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
    
    public void updateDistanceMatrix(ArrayList<ArrayList<Integer>> clusters) {
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
    
    public void print() {
        for (int i = 0; i < clusterID.size(); i++) {
            System.out.println("Cluster: " + i);
            for (int j = 0; j < clusterID.get(i).size(); j++) {
                System.out.print(clusterID.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
    
}
