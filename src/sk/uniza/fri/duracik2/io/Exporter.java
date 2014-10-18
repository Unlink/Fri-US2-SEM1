/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.uniza.fri.duracik2.exportnySystem.Expedicia;
import sk.uniza.fri.duracik2.exportnySystem.ExportnySystem;
import sk.uniza.fri.duracik2.exportnySystem.Odberatel;
import sk.uniza.fri.duracik2.exportnySystem.Tovar;
import sk.uniza.fri.duracik2.exportnySystem.Velkosklad;
import sk.uniza.fri.duracik2.tree.RBTree;

/**
 *
 * @author Unlink
 */
public class Exporter {

    private static final String COMMA = ",";

    private File aOutputDirectory;
    
    private final RBTree<Integer> aSpracovaneObj;
    
    private final LinkedList<IToCSV> fronta;

    public Exporter(File aOutputDirectory) {
        
        aSpracovaneObj = new RBTree<>();
        
        this.aOutputDirectory = aOutputDirectory;
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
        /**
         * http://stackoverflow.com/questions/909843/java-how-to-get-the-unique-id-of-an-object-which-overrides-hashcode
         * Ale nieje zaručené že bude vždy fungovať
         * System.identityHashCode() Vráti hash code Triedy object, 
         * ktorá ho ráta za adresy v pamäti => malo by to byť unikátne
         */
        if (!aSpracovaneObj.contains(System.identityHashCode(obj))) {
            aSpracovaneObj.insert(System.identityHashCode(obj));
            fronta.add(obj);
        }
    }
    
    private String objToString(Object attr) {
        if (attr instanceof IToCSV) {
            pridajNaExport((IToCSV) attr);
            return ((IToCSV) attr).getObjectKey();
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
        BufferedWriter[] writre = new BufferedWriter[EObjectType.values().length];
        try {
            for (int i = 0; i < writre.length; i++) {
                writre[i] = new BufferedWriter(new FileWriter(new File(aOutputDirectory, EObjectType.values()[i].getFilename())));
            }
            while (!fronta.isEmpty()) {
                IToCSV obj = fronta.removeFirst();
                writeLine(writre[obj.getTyp().ordinal()], obj);
            }
        }
        finally {
            for (int i = 0; i < writre.length; i++) {
                if (writre[i] != null)
                    writre[i].close();
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
