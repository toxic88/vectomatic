/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of Vectomatic.
 * 
 * Vectomatic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vectomatic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Vectomatic.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.client.rep.controller;

import org.vectomatic.client.rep.view.DrawingView;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;
import com.gwt.components.client.TooltipListener;

/**
 * Customized PushButton class with a tooltip.
 * @author Lukas Laag
 */
public class ControllerPushButton extends PushButton implements SourcesMouseEvents, ClickListener {
    private MouseListenerCollection mouseListeners;
    private IController _controller;
    private DrawingView _view;
    
	ControllerPushButton(DrawingView view, Image icon, Image disabledIcon, String tooltip, IController controller) {
		super(icon);
		_view = view;
		getUpDisabledFace().setImage(disabledIcon);
		_controller = controller;
	    // Unneeded since this is done at the CustomButton level
		// sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS);
		addMouseListener(new TooltipListener(tooltip, 3000));
		addClickListener(this);
	}
	
	public void addMouseListener(MouseListener listener) {
		if (mouseListeners == null) {
			mouseListeners = new MouseListenerCollection();
		}
		mouseListeners.add(listener);
	}
	
	public void removeMouseListener(MouseListener listener) {
		if (mouseListeners != null) {
			mouseListeners.remove(listener);
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEDOWN:
			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONMOUSEOVER:
			case Event.ONMOUSEOUT: {
				if (mouseListeners != null) {
					mouseListeners.fireMouseEvent(this, event);
				}
				break;
			}
		}
		super.onBrowserEvent(event);
	}

	public void onClick(Widget sender) {
		_controller.activate(_view);
	}
}

