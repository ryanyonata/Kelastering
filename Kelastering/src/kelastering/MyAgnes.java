/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelastering;

import weka.clusterers.AbstractClusterer;
import weka.core.Instances;

/**
 *
 * @author ryanyonata
 */
public class MyAgnes extends AbstractClusterer {
    
    final static int SINGLE = 0;
    final static int COMPLETE = 0;
    
    Instances m_instances;
    int m_clusters = 2;
    int m_linkType = SINGLE;

    public void AbstractClusterer (int clusters, int linkType) {
        m_clusters = clusters;
        m_linkType = linkType;
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
