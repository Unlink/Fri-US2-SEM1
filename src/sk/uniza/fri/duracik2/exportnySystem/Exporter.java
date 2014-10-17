/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.exportnySystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.uniza.fri.duracik2.tree.RBTree;

/**
 *
 * @author Unlink
 */
public class Exporter {
    
    private static final String EXPEDICIECSV = "expedicie.csv";
    private static final String TOVARYCSV = "tovary.csv";
    private static final String ODBERATELIACSV = "odberatelia.csv";
    private static final String SKLADYCSV = "sklady.csv";
    
    private File aOutputDirectory;
    private RBTree<Tovar> aTovary;
    private RBTree<Velkosklad> aSklady;
    private RBTree<Odberatel> aOdberatelia;
    private RBTree<Expedicia> aExpedicie;

    public Exporter(File aOutputDirectory) {
        this.aOutputDirectory = aOutputDirectory;
        aTovary = new RBTree<>();
        aOdberatelia = new RBTree<>();
        aSklady = new RBTree<>();
        aExpedicie = new RBTree<>();
    }

    public File getOutputDirectory() {
        return aOutputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.aOutputDirectory = outputDirectory;
    }
    
    public void exportuj(ExportnySystem paSystem) {
        //Vytvorenie súborov
        try (
            BufferedWriter brSklady = new BufferedWriter(new FileWriter(new File(aOutputDirectory, SKLADYCSV)));
            BufferedWriter brOdberatelia = new BufferedWriter(new FileWriter(new File(aOutputDirectory, ODBERATELIACSV)));
            BufferedWriter brTovary = new BufferedWriter(new FileWriter(new File(aOutputDirectory, TOVARYCSV)));
            BufferedWriter brExpedice = new BufferedWriter(new FileWriter(new File(aOutputDirectory, EXPEDICIECSV)))
        ) {
            
        } catch (IOException ex) {
            
        }
    }

    
}
