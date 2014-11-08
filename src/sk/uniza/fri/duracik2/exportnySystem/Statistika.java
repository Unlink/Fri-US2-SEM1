/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.exportnySystem;

import java.awt.Color;
import sk.uniza.fri.duracik2.gui.IGuiPrint;
import sk.uniza.fri.duracik2.gui.IGuiPrintListHead;
import sk.uniza.fri.duracik2.gui.JColorTextPane;

/**
 *
 * @author Unlink
 */
public class Statistika implements IGuiPrint, IGuiPrintListHead {
	private String aEan;
	private int aCount;
	private int aSum;

	public Statistika(String aEan) {
		this.aEan = aEan;
		this.aCount = 0;
		this.aSum = 0;
	}

	public String getEan() {
		return aEan;
	}

	public int getCount() {
		return aCount;
	}

	public int getSum() {
		return aSum;
	}

	public void addTovar(Tovar paTovar) {
		aCount++;
		aSum += paTovar.getCena();
	}

	@Override
	public String toString() {
		return "{" + "aEan=" + aEan + ", aCount=" + aCount + ", aSum=" + aSum + '}';
	}

	@Override
	public void print(JColorTextPane pane) {
		pane.append(aEan + "\t");
		pane.append(Color.BLUE.darker(), aCount + "\t");
		pane.append(Color.GREEN.darker().darker(), aSum + "");
	}

	@Override
	public void printListHead(JColorTextPane pane) {
		pane.append("Ean\t");
		pane.append(Color.BLUE.darker(), "Počet\t");
		pane.append(Color.GREEN.darker().darker(), "Cena");
		pane.append("\n=========================");
	}

}
