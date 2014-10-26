/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Unlink
 */
public class ReflectionalJTextField extends JTextField
{
	private Object aObjekt;
	private Class<?> aTyp;
	private JDialog aDialog;
	private Constructor<?> aConstructor;
	private FormGenerator aFormGenerator;

	public ReflectionalJTextField(Field paField)
	{
		this.aTyp = paField.getType();
		FunkcnyKonstruktor anotacia = null;
		for (Constructor<?> c : aTyp.getConstructors())
		{
			if ((anotacia = c.getAnnotation(FunkcnyKonstruktor.class)) != null)
			{
				aConstructor = c;
			}
		}
		String label = anotacia.popis();
		if (label.endsWith(""))
		{
			label = aTyp.getName();
		}
		setText(label);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!aDialog.isVisible())
				{
					aDialog.setVisible(true);
				}
			}

		});

		ArrayList<Field> fields = new ArrayList<>();
		int i = 0;
		for (Class<?> parameterType : aConstructor.getParameterTypes())
		{
			String labelTitle = parameterType.toString();
			if (anotacia.parametre().length > 1)
			{
				labelTitle = anotacia.parametre()[i];
			}
			if (aConstructor.getAnnotation(FunkciaParametre.class) != null) {
				HashMap<String, FunkciaParameter> x = new HashMap<>();
				for (FunkciaParameter p : aConstructor.getAnnotation(FunkciaParametre.class).parametre())
				{
					if (p.param() == i)
						x.put(p.key(), p);
				}
				fields.add(new Field(labelTitle, parameterType, x));
			}
			else {
				fields.add(new Field(labelTitle, parameterType));
			}
			i++;
		}
		aFormGenerator = new FormGenerator(fields);
		aDialog = new KonstruktorDialog(aFormGenerator.getForm(), "Vytvor nový objekt " + aTyp.getSimpleName(), this);
	}

	public void formSubmited()
	{
		try
		{
			aObjekt = aConstructor.newInstance(aFormGenerator.getFormValues());
			setText(aObjekt.toString());
			getCaret().moveDot(0);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new ReflectorException("Nepodarilo sa vytvoriť pod objekt", ex);
		}
	}

	public Object getObject()
	{
		return aObjekt;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Icon icon = new javax.swing.ImageIcon(getClass().getResource("/sk/uniza/fri/duracik2/gui/reflection/3d_objects.png"));
		int iconWidth = icon.getIconWidth();
		int iconHeight = icon.getIconHeight();
		int y = (this.getHeight() - iconHeight) / 2;
		icon.paintIcon(this, g, 5, y);
		setMargin(new Insets(2, 10+iconWidth, 2, 2));
	}

}
