/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.tree;

/**
 *
 * @author Unlink
 * @param <T>
 */
public interface TreeIndexer<T extends Comparable> {
	public int compare(T e1, Object... params);
}
