/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Unlink
 */
public class JColorTextPane extends JTextPane {

	public void append(Color c, String s) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
			StyleConstants.Foreground, c);

		int len = getDocument().getLength(); // same value as getText().length();
		setCaretPosition(len);  // place caret at the end (with no selection)
		setCharacterAttributes(aset, false);
		replaceSelection(s); // there is no selection, so inserts at caret
	}

	public void append(String s) {
		append(getForeground(), s);
	}

}
