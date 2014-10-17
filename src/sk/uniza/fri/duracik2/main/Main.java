/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import sk.uniza.fri.duracik2.exportnySystem.Tovar;
import sk.uniza.fri.duracik2.tree.RBTree;
import sk.uniza.fri.duracik2.tree.TreeIndexer;

/**
 *
 * @author Unlink
 */
public class Main {
    public static void main(String[] args) {
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
        
        System.out.println(null instanceof Object);
        
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
