package org.vectomatic.svg.edu.client.dots;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface DotsConstants extends Constants {
	public static final DotsConstants INSTANCE =  GWT.create(DotsConstants.class);
	public String add();
	public String remove();
	public String save();
	public String test();
	public String showLine();
	public String pictureAlpha();
	public String dot();
}
