/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.text.JTextComponent;
import layout.SpringUtilities;

/**
 *
 * @author Unlink
 */
public class FormGenerator
{
	private List<JComponent> aComponents;
	private List<Field> aFields;

	public FormGenerator(List<Field> paFields)
	{
		aComponents = new ArrayList<>();
		aFields = paFields;
	}
	
	public JPanel getForm() {
		JPanel jp = new JPanel(new SpringLayout());
		for (Field field : aFields)
		{
			JLabel l = new JLabel(field.getName(), JLabel.TRAILING);
			jp.add(l);
			JComponent jc = BasicFormFields.getInstance().getRenderedComponent(field.getType());
			jp.add(jc);
			aComponents.add(jc);
		}
		SpringUtilities.makeCompactGrid(jp, aFields.size(), 2, 3, 3, 3, 3);
		return jp;
	}
	
	public Object[] getFormValues() {
		Object[] params = new Object[aFields.size()];
		for (int i = 0; i < params.length; i++)
		{
			params[i] = BasicFormFields.getInstance().getData(aComponents.get(i), aFields.get(i).getType());
		}
		return params;
	}
}
