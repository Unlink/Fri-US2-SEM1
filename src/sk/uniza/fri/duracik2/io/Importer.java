/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import sk.uniza.fri.duracik2.exportnySystem.Expedicia;
import sk.uniza.fri.duracik2.exportnySystem.Odberatel;
import sk.uniza.fri.duracik2.exportnySystem.Tovar;
import sk.uniza.fri.duracik2.exportnySystem.Velkosklad;
import sk.uniza.fri.duracik2.tree.RBTree;

/**
 *
 * @author Unlink
 */
public class Importer {
    
    private File aInputDirectory;
    private final RBTree<Velkosklad> aImportovaneSklady;
    private final RBTree<Odberatel> aImportovanyOdberatelia;
    private final RBTree<Tovar> aImportovaneTovary;
    private final RBTree<Expedicia> aImportovaneExpedicie;

    public Importer(File paInputDirectory) {
        this.aInputDirectory = paInputDirectory;
        aImportovaneExpedicie = new RBTree<>(Expedicia.INDEXER);
        aImportovaneSklady = new RBTree<>(Velkosklad.INDEXER);
        aImportovaneTovary = new RBTree<>(Tovar.INDEXER);
        aImportovanyOdberatelia = new RBTree<>(Odberatel.INDEXER);
    }

    public File getInputDirectory() {
        return aInputDirectory;
    }

    public void setInputDirectory(File inputDirectory) {
        this.aInputDirectory = inputDirectory;
    }

    public RBTree<Velkosklad> getImportovaneSklady() {
        return aImportovaneSklady;
    }

    public RBTree<Odberatel> getImportovanyOdberatelia() {
        return aImportovanyOdberatelia;
    }

    public RBTree<Tovar> getImportovaneTovary() {
        return aImportovaneTovary;
    }

    public RBTree<Expedicia> getImportovaneExpedicie() {
        return aImportovaneExpedicie;
    }
    
    public boolean importuj() {
        BufferedReader br = null;
        String line;
        try {
            //Import Velkoskladov
            br = new BufferedReader(new FileReader(new File(aInputDirectory, EObjectType.SKLAD.getFilename())));
            while ((line = br.readLine()) != null) {
                String[] atrrs = line.split(Exporter.COMMA);
                Integer key = Velkosklad.getKey(atrrs);
                Velkosklad sklad = getVelkosklad(key);
                sklad.fromCSV(this, atrrs);
            }
            br.close();
            //Import Odberatelov
            br = new BufferedReader(new FileReader(new File(aInputDirectory, EObjectType.ODBERATEL.getFilename())));
            while ((line = br.readLine()) != null) {
                String[] atrrs = line.split(Exporter.COMMA);
                String key = Odberatel.getKey(atrrs);
                Odberatel odberatel = getOrberatel(key);
                odberatel.fromCSV(this, atrrs);
            }
            br.close();
            //Import Tovarov
            br = new BufferedReader(new FileReader(new File(aInputDirectory, EObjectType.TOVAR.getFilename())));
            while ((line = br.readLine()) != null) {
                String[] atrrs = line.split(Exporter.COMMA);
                Long key = Tovar.getKey(atrrs);
                Tovar tovar = getTovar(key);
                tovar.fromCSV(this, atrrs);
            }
            br.close();
            //Import Tovarov
            br = new BufferedReader(new FileReader(new File(aInputDirectory, EObjectType.EXPEDICIA.getFilename())));
            while ((line = br.readLine()) != null) {
                String[] atrrs = line.split(Exporter.COMMA);
                Long key = Expedicia.getKey(atrrs);
                Expedicia expedicia = getExpedicia(key);
                expedicia.fromCSV(this, atrrs);
            }
        }
        catch (IOException ex) {
            return false;
        } 
        finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException ex) {
                    return false;
                }
        }
        return true;
    }
    
    public Velkosklad getVelkosklad(int paIdSkladu) {
        Velkosklad sklad = aImportovaneSklady.findByParams(paIdSkladu);
        if (sklad == null) {
            sklad = new Velkosklad(paIdSkladu, null, null);
            aImportovaneSklady.insert(sklad);
        }
        return sklad;
    }
    
    public Tovar getTovar(long paId) {
        Tovar tovar = aImportovaneTovary.findByParams(paId);
        if (tovar == null) {
            tovar = new Tovar(paId, null, null, null, 0);
            aImportovaneTovary.insert(tovar);
        }
        return tovar;
    }
    
    public Odberatel getOrberatel(String paId) {
        Odberatel odberatel = aImportovanyOdberatelia.findByParams(paId);
        if (odberatel == null) {
            odberatel = new Odberatel(paId, null, null, null);
            aImportovanyOdberatelia.insert(odberatel);
        }
        return odberatel;
    }
    
    public Expedicia getExpedicia(long paId) {
        Expedicia expedicia = aImportovaneExpedicie.findByParams(paId);
        if (expedicia == null) {
            expedicia = new Expedicia(paId);
            aImportovaneExpedicie.insert(expedicia);
        }
        return expedicia;
    }
    
    
}
