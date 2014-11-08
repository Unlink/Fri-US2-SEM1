/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

/**
 *
 * @author Unlink
 */
public class ReflectorException extends RuntimeException {

	public ReflectorException(String paMessage) {
		super(paMessage);
	}

	public ReflectorException(String paMessage, Throwable paCause) {
		super(paMessage, paCause);
	}

}
