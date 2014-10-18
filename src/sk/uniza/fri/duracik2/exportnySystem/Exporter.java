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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
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
    private static final String COMMA = ",";

    private File aOutputDirectory;
    private RBTree<Tovar> aTovary;
    private RBTree<Velkosklad> aSklady;
    private RBTree<Odberatel> aOdberatelia;
    private RBTree<Expedicia> aExpedicie;
    
    private LinkedList<IToCSV> fronta;

    public Exporter(File aOutputDirectory) {
        this.aOutputDirectory = aOutputDirectory;
        aTovary = new RBTree<>();
        aOdberatelia = new RBTree<>();
        aSklady = new RBTree<>();
        aExpedicie = new RBTree<>();
        fronta = new LinkedList<>();
    }

    public File getOutputDirectory() {
        return aOutputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.aOutputDirectory = outputDirectory;
    }

    public void exportuj(ExportnySystem paSystem) throws IOException { 
        //Exportujeme sklady
        Iterator<Velkosklad> it = paSystem.dajZoznamSkladov();
        while (it.hasNext()) {
            Velkosklad sklad = it.next();
            pridajNaExport(sklad);
            Iterator<Odberatel> it2 = sklad.dajOdberatelov();
            while (it2.hasNext())
                pridajNaExport(it2.next());
        }
        
        Iterator<Tovar> it3 = paSystem.dajZoznamTovatov();
        while (it3.hasNext()) {
            while (it3.hasNext())
                pridajNaExport(it3.next());
        }
        doExport();
    }

    private void pridajNaExport(IToCSV obj) {
        if (obj instanceof Tovar) {
            if (!aTovary.contains((Tovar)obj)) {
                aTovary.insert((Tovar)obj);
                fronta.add(obj);
            }
        }
        else if (obj instanceof Expedicia) {
           if (!aExpedicie.contains((Expedicia)obj)) {
                aExpedicie.insert((Expedicia)obj);
                fronta.add(obj);
            }
        }
        else if (obj instanceof Velkosklad) {
            if (!aSklady.contains((Velkosklad)obj)) {
                aSklady.insert((Velkosklad)obj);
                fronta.add(obj);
            }
        }
        else if (obj instanceof Odberatel) {
            if (!aOdberatelia.contains((Odberatel)obj)) {
                aOdberatelia.insert((Odberatel)obj);
                fronta.add(obj);
            }
        }
    }
    
    private String objToString(Object attr) {
        if (attr instanceof Tovar) {
            pridajNaExport((IToCSV) attr);
            return ((Tovar) attr).getVyrobneCislo() + "";
        }
        else if (attr instanceof Expedicia) {
            pridajNaExport((IToCSV) attr);
            return ((Expedicia) attr).getId() + "";
        }
        else if (attr instanceof Velkosklad) {
            pridajNaExport((IToCSV) attr);
            return "s_" + ((Velkosklad) attr).getId();
        }
        else if (attr instanceof Odberatel) {
            pridajNaExport((IToCSV) attr);
            return "o_" + ((Odberatel) attr).getId();
        }
        else if (attr instanceof Date) {
            return ((Date) attr).getTime() + "";
        }
        else if (attr == null) {
            return "";
        }
        else {
            return attr.toString();
        }
    }

    private void doExport() throws IOException {
        try (
            BufferedWriter brSklady = new BufferedWriter(new FileWriter(new File(aOutputDirectory, SKLADYCSV)));
            BufferedWriter brOdberatelia = new BufferedWriter(new FileWriter(new File(aOutputDirectory, ODBERATELIACSV)));
            BufferedWriter brTovary = new BufferedWriter(new FileWriter(new File(aOutputDirectory, TOVARYCSV)));
            BufferedWriter brExpedice = new BufferedWriter(new FileWriter(new File(aOutputDirectory, EXPEDICIECSV)))
        ) {
            while (!fronta.isEmpty()) {
                IToCSV obj = fronta.removeFirst();
                if (obj instanceof Tovar)
                    writeLine(brTovary, obj);
                else if (obj instanceof Velkosklad)
                    writeLine(brSklady, obj);
                else if (obj instanceof Odberatel)
                    writeLine(brOdberatelia, obj);
                else if (obj instanceof Expedicia)
                    writeLine(brExpedice, obj);
            }
        }
    }

    private void writeLine(BufferedWriter paBw, IToCSV paObj) throws IOException {
        Object[] data = paObj.toCsvData();
        for (int i = 0; i < data.length; i++) {
            paBw.write(objToString(data[i]));
            if (i < (data.length-1)) {
                paBw.write(COMMA);
            }
        }
        paBw.newLine();
    }

}
