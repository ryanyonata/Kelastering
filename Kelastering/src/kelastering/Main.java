/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kelastering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
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
    final static int SINGLE = 0;
    final static int COMPLETE = 1;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        Instances trainset;
        ClusterEvaluation eval;
        String path = "resources/weather.nominal.arff";
        
        System.out.println("Selamat datang di program Kelastering!");
        int pilihan;
        int linktype;
        int jumlahCluster;
        int initSeed;
        //String path;
        
        do {
            
            System.out.println("Menu:");
            System.out.println("1. WEKA K-Means Clustering");
            System.out.println("2. WEKA Hierarchical Clustering");
            System.out.println("3. MyKMeans");
            System.out.println("4. MyAgnes");
            System.out.println("5. Keluar");
            System.out.print("Masukkan pilihan: ");
            Scanner sc = new Scanner(System.in);
            pilihan = sc.nextInt();
            if (pilihan == 1) {
                System.out.println();
                System.out.println("===== WEKA Simple KMeans =====");
                System.out.print("Masukkan lokasi file: ");
                path = sc.next();
                System.out.print("Masukkan jumlah cluster: ");
                jumlahCluster = sc.nextInt();
                System.out.print("Masukkan jumlah seed awal: ");
                initSeed = sc.nextInt();
                SimpleKMeans wekaKmeans = new SimpleKMeans();
                wekaKmeans.setSeed(initSeed);
                wekaKmeans.setPreserveInstancesOrder(true);
                wekaKmeans.setNumClusters(jumlahCluster);
                BufferedReader data = new BufferedReader(new FileReader(path));
                trainset = new Instances(data);
                wekaKmeans.buildClusterer(trainset);
                eval = new ClusterEvaluation();
                eval.setClusterer(wekaKmeans);
                eval.evaluateClusterer(trainset);
                System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());
                System.out.println();
            } else if (pilihan == 2) {
                System.out.println();
                System.out.println("===== WEKA Hierarchical Clustering =====");
                System.out.print("Masukkan lokasi file: ");
                path = sc.next();
                System.out.print("Masukkan jumlah cluster: ");
                jumlahCluster = sc.nextInt();
                System.out.println();
                /*System.out.println("Jenis linktype");
                System.out.println("1. SINGLE");
                System.out.println("2. COMPLETE");
                System.out.print("Masukkan jenis linktype: ");
                linktype = sc.nextInt();*/
                BufferedReader data = new BufferedReader(new FileReader(path));
                trainset = new Instances(data);
                HierarchicalClusterer wekaAgnes = new HierarchicalClusterer();
                //wekaAgnes.setLinkType(SINGLE);
                wekaAgnes.setNumClusters(jumlahCluster);
                wekaAgnes.buildClusterer(trainset);
                eval = new ClusterEvaluation();
                eval.setClusterer(wekaAgnes);
                eval.evaluateClusterer(trainset);
                System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());
                System.out.println();
            } else if (pilihan == 3) {
                System.out.println();
                System.out.println("===== MyKMeans =====");
                System.out.print("Masukkan lokasi file: ");
                path = sc.next();
                System.out.print("Masukkan jumlah cluster: ");
                jumlahCluster = sc.nextInt();
                System.out.print("Masukkan jumlah seed awal: ");
                initSeed = sc.nextInt();
                MyKMeans myKmeans = new MyKMeans();
                myKmeans.setSeed(initSeed);
                myKmeans.setNumClusters(jumlahCluster);
                BufferedReader data = new BufferedReader(new FileReader(path));
                trainset = new Instances(data);
                myKmeans.buildClusterer(trainset);
                eval = new ClusterEvaluation();
                eval.setClusterer(myKmeans);
                eval.evaluateClusterer(trainset);
                System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());
                System.out.println();
            } else if (pilihan == 4) {
                System.out.println();
                System.out.println("===== WEKA Hierarchical Clustering =====");
                System.out.print("Masukkan lokasi file: ");
                path = sc.next();
                System.out.print("Masukkan jumlah cluster: ");
                jumlahCluster = sc.nextInt();
                System.out.println();
                System.out.println("Jenis linktype");
                System.out.println("1. SINGLE");
                System.out.println("2. COMPLETE");
                System.out.print("Masukkan jenis linktype: ");
                linktype = sc.nextInt();
                BufferedReader data = new BufferedReader(new FileReader(path));
                trainset = new Instances(data);
                MyAgnes myAgnes = new MyAgnes();
                myAgnes.setLinkType(SINGLE);
                myAgnes.setNumClusters(jumlahCluster);
                myAgnes.buildClusterer(trainset);
                myAgnes.print();
                System.out.println();
            } else if ((pilihan < 1) && (pilihan > 5)){
                System.out.println("Masukan salah. Coba lagi!");
                System.out.print("Masukkan pilihan: ");
                pilihan = sc.nextInt();
            }
        } while (pilihan != 5);
        
        
        //String path;
        /*BufferedReader data = new BufferedReader(new FileReader(path));
        trainset = new Instances(data);
        MyAgnes wekaKmeans = new MyAgnes();
        wekaKmeans.setSeed(3);
        wekaKmeans.setPreserveInstancesOrder(true);
        wekaKmeans.setNumClusters(5);
        wekaKmeans.setLinkType(COMPLETE);
        wekaKmeans.buildClusterer(trainset);
        wekaKmeans.print();*/
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
