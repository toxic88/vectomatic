package org.vectomatic.svg.edu.client.dots;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface DotsResources extends ClientBundle {
	public static final DotsResources INSTANCE =  GWT.create(DotsResources.class);

	@Source("tasto_5_architetto_franc_01.png")
	public ImageResource rightArrow();
	
	@Source("tasto_6_architetto_franc_01.png")
	public ImageResource leftArrow();

	@Source("pictures.txt")
	public TextResource pictureList();

}
