/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import sk.uniza.fri.duracik2.tree.RBTree;

/**
 *
 * @author Unlink
 */
public class Test {

    static RBTree<Integer> tree;
    static TreeSet<Integer> controlTree;
    static List<Integer> controlList;
    static Random r;

    public static void inicializuj() {
        tree = new RBTree<>();
        controlTree = new TreeSet<>();
        controlList = new ArrayList<>();
        r = new Random();
    }

    public static void napln(int mnozstvo) {
        if (tree == null) {
            inicializuj();
        }
        while (controlTree.size() != mnozstvo) {
            insertNumber();
        }
    }

    public static void testuj() {
        if (tree == null) {
            inicializuj();
        }
        long op = 1;
        while (true) {
            //System.out.print(op+" -> ");
            switch (r.nextInt(3)) {
                case 0:
                    insertNumber();
                    break;
                case 1:
                    if (controlTree.size() > 1) {
                        //Citam random hodnotu
                        if (r.nextDouble() < 0.1) {
                            int num = r.nextInt();
                            //System.out.println("Hladam nahodne:" + num);
                            if (controlTree.contains(num) != (tree.find(num) != null)) {
                                throw new RuntimeException("Nezrovnalosť v hladaní náhodneho čísla - " + num);
                            }
                        }
                        else {
                            int index = r.nextInt(controlTree.size() - 1);
                            Integer num = controlList.get(index);
                            //System.out.println("Hladam:" + num);
                            if (tree.find(num) == null) {
                                throw new RuntimeException("Nepodarilo sa najsť prvok v strome - " + num);
                            }
                        }
                    }
                    break;
                case 2:
                //case 3:
                    if (controlTree.size() > 1) {
                        int index = r.nextInt(controlTree.size() - 1);
                        Integer num = controlList.remove(index);
                        controlTree.remove(num);
                        tree.delete(num);
                        //System.out.println("Mazem:" + num);
                        if (tree.size() != controlTree.size() || tree.find(num) != null) {
                            throw new RuntimeException("Nepodarilo sa vymazať prvok - " + num);
                        }
                    }
                    break;
            }
            
            //Kontrola velkosti a prvkov
            if (op++%10000 == 0) {
                int size = sizeAndCheckByInOrder();
                if (size!=tree.size() || size != controlTree.size()) {
                    throw new RuntimeException("Pri kontrole velkosti na nasli nezrovnalosti " + size + " :: " + tree.size() + " :: " + controlTree.size());
                }
                else {
                    System.out.println(op+"^Velkost stromu "+size);
                    //Kontrola stavu RB
                    if (!tree.testTreeRules()) {
                        throw new RuntimeException("");
                    }
                }
            }
        }
    }

    public static void insertNumber() {
        int num = r.nextInt();
        if (!controlTree.contains(num)) {
            controlTree.add(num);
            controlList.add(num);
            tree.insert(num);
            //System.out.println("Vkladam:" + num);
            if (controlTree.size() != tree.size()) {
                throw new RuntimeException("Nesedia veľkosti");
            }
        }
    }
    
    public static int sizeAndCheckByInOrder() {
        if (tree != null) {
            int x = 0;
            
            for (int i:tree) {
                if (!controlTree.contains(i)) {
                    throw new RuntimeException("Pri kontrole pomocou inorder sa v strome našiel prvok ktorý tam byt nemal: "+i);
                }
                x++;
            }
            return x;
        }
        return 0;
    }

    public static void main(String[] args) {
        napln(100);
        testuj();
    }
}
