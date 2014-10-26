/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.main;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import sk.uniza.fri.duracik2.exportnySystem.ExportnySystem;
import sk.uniza.fri.duracik2.exportnySystem.Tovar;
import sk.uniza.fri.duracik2.tree.RBTree;

/**
 *
 * @author Unlink
 */
public class Main {
    
    public static Date STD(String str) {
        try {
            return new SimpleDateFormat("dd.mm.yyyy").parse(str);
        } catch (ParseException ex) {
            return null;
        }
    }
    
    public static void main(String[] args) {
        
		System.out.println("Bla ble blix");
		
        ExportnySystem s = new ExportnySystem();
        s.pridajVelkosklad(1, "Názov skladu 1", "Adresa skladu 1");
        s.pridajVelkosklad(2, "Názov skladu 2", "Adresa skladu 4");
        s.pridajVelkosklad(3, "Názov skladu 3", "Adresa skladu 4");
        s.pridajVelkosklad(4, "Názov skladu 4", "Adresa skladu 4");
        
        s.pridajOdberatela(1, "1", "1. Odberatel", "Adresa1");
        s.pridajOdberatela(1, "2", "2. Odberatel", "Adresa2");
        s.pridajOdberatela(2, "3", "3. Odberatel", "Adresa3");
        s.pridajOdberatela(3, "4", "4. Odberatel", "Adresa4");
        
        s.naskladniTovar(new Tovar(1L, "AA", STD("18.10.2014"), STD("18.12.2014"), 100), 1);
        s.naskladniTovar(new Tovar(2L, "AA", STD("18.10.2014"), STD("18.12.2014"), 100), 1);
        s.naskladniTovar(new Tovar(3L, "BB", STD("18.10.2014"), STD("18.12.2014"), 100), 1);
        s.naskladniTovar(new Tovar(4L, "BB", STD("18.10.2014"), STD("18.12.2014"), 100), 1);
        s.naskladniTovar(new Tovar(5L, "BB", STD("18.10.2014"), STD("18.12.2014"), 100), 4);
        s.naskladniTovar(new Tovar(6L, "CC", STD("18.10.2014"), STD("18.12.2014"), 100), 4);
        
        s.expedujTovarDoVelkoskladu(1, 1, 3, STD("20.10.2014"), "EVC1");
        s.vylozTovar(1);
        s.expedujTovarDoVelkoskladu(3, 1, 2, STD("15.10.2014"), "EVC2");
        s.vylozTovar(1);
        s.expedujTovarKOdberatelovi(2, 1, "3", STD("15.10.2014"), "EVC3");
        s.vylozTovar(1);
        s.zrusOdberatela(2, "3");
        s.zrusVelkosklad(3, 4);
        
        
        s.exportujData(new File("C:\\Users\\Unlink\\Desktop\\ExportedData"));
        ExportnySystem s2 = new ExportnySystem();
        s2.importujData(new File("C:\\Users\\Unlink\\Desktop\\ExportedData"));
        s2.exportujData(new File("C:\\Users\\Unlink\\Desktop\\ExportedData2"));
        
        /*Exporter export = new Exporter(new File("C:\\Users\\Unlink\\Desktop\\ExportedData"));
        try {
            export.exportuj(s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        Importer importer = new Importer(new File("C:\\Users\\Unlink\\Desktop\\ExportedData"));
        System.out.println(importer.importuj());
        
        System.out.println("Importované dáta");
        for (Velkosklad importovaneSklady : importer.getImportovaneSklady()) {
            System.out.println(importovaneSklady);
        }
        
        for (Odberatel importovanyOdberatelia : importer.getImportovanyOdberatelia()) {
            System.out.println(importovanyOdberatelia);
        }
        
        for (Tovar importovaneTovary : importer.getImportovaneTovary()) {
            System.out.println(importovaneTovary);
        }
        
        for (Expedicia importovaneExpedicie : importer.getImportovaneExpedicie()) {
            System.out.println(importovaneExpedicie);
        }*/
        /*TreeIndexer porovnavacPodlaMena = new TreeIndexer() {

            @Override
            public int compare(Comparable e1, Object... params) {
                Osoba osoba1 = (Osoba) e1;
                Osoba osoba2 = (Osoba) params[0];
                int cmp = osoba1.meno.compareTo(osoba2.meno);
                //Maju rovnake meno
                if (cmp == 0)
                    return osoba1.priezvisko.compareTo(osoba2.priezvisko);
                else return cmp;
            }
        };
        RBTree<Osoba> osoby = new RBTree<>(porovnavacPodlaMena);*/
        
        RBTree<Integer> t = new RBTree<>();
        t.insert(4);
        t.insert(7);
        t.insert(12);
        t.insert(15);
        t.insert(3);
        t.insert(5);
        t.insert(14);
        t.insert(18);
        t.insert(16);
        t.insert(17);
        
        //t.delete(17);
        t.printTree();
        
        Iterator<Integer> it = t.inOrderIterator();
        System.out.print("In order: ");
        while (it.hasNext()) {
            System.out.print(it.next()+", ");
        }
        System.out.println("");
        
        it = t.levelOrderIterator();
        System.out.print("Level order: ");
        while (it.hasNext()) {
            System.out.print(it.next()+", ");
        }
        System.out.println("");
        
        t.testTreeRules();
        
        it = t.findRange(6, 15);
        System.out.print("6 až 15: ");
        while (it.hasNext()) {
            System.out.print(it.next()+", ");
        }
        System.out.println("");
		
		System.out.println("text");
        
        /*int pocet = 1000000;
        TreeSet<Integer> hs = new TreeSet<>();
        RBTree<Integer> t = new RBTree<>();
        Random r = new Random();
        while (hs.size() < pocet) {
            int x = r.nextInt();
            if (!hs.contains(x)) {
                hs.add(x);
                t.insert(x);
            }
        }
        
        for (int i:hs) {
            if (t.find(i) != null) {
                t.delete(i);
                if (t.find(i) != null)
                    System.out.println(i+" Sa nepodarilo zmazat");
                    
            }
            else 
                System.out.println(i+" Sa nenašlo");
        }
        
        System.out.println(t.getSize());
        t.printTree();
        t.insert(18);
        t.insert(10);
        t.insert(29);
        t.insert(1);
        t.printTree();*/
        
        /*long start, end;
        long t1 = 0, t2 = 0;
        for (int j = 0; j < 25; j++) {
            start = System.currentTimeMillis();
            RBTree<Integer> t = new RBTree<>();
            for (int i = 0; i < pocet; i++) {
                t.insert(i);
            }
            end = System.currentTimeMillis();
            t1 += (end - start);
            System.out.println("My RB: "+t.getSize() + "-> "+(end - start)+"ms");
            
            start = System.currentTimeMillis();
            TreeSet<Integer> s = new TreeSet<>();
            for (int i = 0; i < pocet; i++) {
                s.add(i);
            }
            end = System.currentTimeMillis();
            t2 += (end - start);
            System.out.println("Treeset: "+s.size()+ "-> "+(end - start)+"ms");
            
        }
        
        System.out.println("Priemer1: "+(t1/25));
        System.out.println("Priemer2: "+(t2/25));
        */
        /*for (int i : hs) {
            if (t.find(i) == null) {
                System.out.println("Nenašiel som "+i+" :(");
            }
        }*/
    }
}
