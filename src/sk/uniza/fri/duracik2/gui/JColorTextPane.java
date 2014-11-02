/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Unlink
 */
public class JColorTextPane extends JTextPane {

	public static final int TYPE_BOLD = 1;
	public static final int TYPE_UNDERLINE = 2;
	public static final int TYPE_ITALICS = 4;
	
	public void append(Color c, String s) {
		append(c, s, 0);
	}

	public void append(Color c, String s, int paStyle) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
			StyleConstants.Foreground, c);
		
			aset = sc.addAttribute(aset, StyleConstants.Bold, ((paStyle & TYPE_BOLD) != 0));
			aset = sc.addAttribute(aset, StyleConstants.Italic, ((paStyle & TYPE_ITALICS) != 0));
			aset = sc.addAttribute(aset, StyleConstants.Underline, ((paStyle & TYPE_UNDERLINE) != 0));

		int len = getDocument().getLength(); // same value as getText().length();
		setCaretPosition(len);  // place caret at the end (with no selection)
		setCharacterAttributes(aset, false);
		replaceSelection(s); // there is no selection, so inserts at caret
	}
	
	public void append(String s) {
		append(getForeground(), s);
	}

	public void clear() {
		try {
			getDocument().remove(0, getDocument().getLength());
		}
		catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

}
