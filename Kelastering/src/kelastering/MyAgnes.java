/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelastering;

import java.util.ArrayList;
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
    ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<>();
    EuclideanDistance distance;

    public MyAgnes (int clusters, int linkType, Instances data) throws Exception {
        m_clusters = clusters;
        m_linkType = linkType;
        buildClusterer(data);
    }
    
    @Override
    public void buildClusterer(Instances i) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int numberOfClusters() throws Exception {
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
