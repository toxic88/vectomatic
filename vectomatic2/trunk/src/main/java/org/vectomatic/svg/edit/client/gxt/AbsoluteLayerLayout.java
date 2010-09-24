/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.gxt;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * GXT layout class to implement a multilayer layout based
 * on several CSS absolutely positionned divs with different
 * z-indices.
 * @author laaglu
 */
public class AbsoluteLayerLayout extends FitLayout {
	protected void onLayout(Container<?> container, El target) {
		super.onLayout(container, target);
		// Retrieve the first parent with absolute layout (it
		// ought to be the Window)
		Widget w = container;
		while ((w = w.getParent()) != null && !(w instanceof Window))  {
		}
		Window window = (Window)w;
		Point p = target.getOffsetsTo(window.getElement());
		GWT.log("p = " + p.toString());
		Size windowSize = window.el().getStyleSize();
		for (int i = 0, count = container.getItemCount(); i < count; i++) {
			Component c = container.getItem(i);
			Size containerSize = target.getStyleSize();
			containerSize.width -= getSideMargins(c);
			containerSize.height -= c.el().getMargins("tb");
			c.getElement().getStyle().setPosition(Position.ABSOLUTE);
			AbsoluteLayerLayoutData data = (AbsoluteLayerLayoutData) c.getData("layoutData");
			int horizontalOffset = data.getHorizontalOffset();
			if (data.isAttachedLeft()) {
				c.getElement().getStyle().setLeft(p.x + horizontalOffset, Unit.PX);
			} else {
				c.getElement().getStyle().setRight(windowSize.width - containerSize.width - p.x + horizontalOffset, Unit.PX);
			}
			int verticalOffset = data.getVerticalOffset();
			if (data.isAttachedTop()) {
				c.getElement().getStyle().setTop(p.y + verticalOffset, Unit.PX);
			} else {
				c.getElement().getStyle().setBottom(windowSize.height - containerSize.height - p.y + verticalOffset, Unit.PX);
			}
			if (c.isRendered()) {
				setSize(c, 
						data.getWidth() > 0 ? data.getWidth() : containerSize.width, 
						data.getHeight() > 0 ? data.getHeight() : containerSize.height);
			}
			c.getElement().getStyle().setZIndex(data.getZIndex());
		}
	}
	/*@Override
	protected void onLayout(Container<?> container, El target) {
		// GWT.log("target:" + target.toString());
		// GWT.log("target.getTop:" + target.getTop());
		// GWT.log("target.getLeft:" + target.getLeft());
		// GWT.log("target.getTop(true):" + target.getTop(true));
		// GWT.log("target.getLeft(true):" + target.getLeft(true));
		// GWT.log("target.getScrollLeft:" + target.getScrollLeft());
		// GWT.log("target.getScrollTop:" + target.getScrollTop());
		// GWT.log("target.getX:" + target.getX());
		// GWT.log("target.getY:" + target.getY());
		// GWT.log("target.getWidth:" + target.getWidth());
		// GWT.log("target.getHeight:" + target.getHeight());
		// GWT.log("target.getComputedWidth:" + target.getComputedWidth());
		// GWT.log("target.getClientHeight:" + target.getClientHeight());
		// GWT.log("target.getStyleSize:" + target.getStyleSize().toString());
		// GWT.log("target.getBounds:" + target.getBounds().toString());
		// GWT.log("target.getBounds(true):" +
		// target.getBounds(true).toString());
		// GWT.log("target.getOffsetsTo(true):" +
		// target.getOffsetsTo(window.getElement()));
		super.onLayout(container, target);
		// Retrieve the first parent with absolute layout (it
		// ought to be the Window)
		Widget w = container;
		while ((w = w.getParent()) != null && !(w instanceof Window))  {
		}
		Window window = (Window)w;
		Point p = target.getOffsetsTo(window.getElement());
		GWT.log("p = " + p.toString());
		Size windowSize = window.el().getStyleSize();
		for (int i = 0, count = container.getItemCount(); i < count; i++) {
			Size containerSize = target.getStyleSize();
			Component c = container.getItem(i);
			c.getElement().getStyle().setPosition(Position.ABSOLUTE);
			AbsoluteLayerLayoutData data = (AbsoluteLayerLayoutData) c.getData("layoutData");
			int horizontalOffset = data.getHorizontalOffset();
			if (data.isAttachedLeft()) {
				c.getElement().getStyle().setLeft(p.x + horizontalOffset, Unit.PX);
			} else {
				c.getElement().getStyle().setRight(windowSize.width - containerSize.width - p.x + horizontalOffset, Unit.PX);
			}
			int verticalOffset = data.getVerticalOffset();
			if (data.isAttachedTop()) {
				c.getElement().getStyle().setTop(p.y + verticalOffset, Unit.PX);
			} else {
				c.getElement().getStyle().setBottom(windowSize.height - containerSize.height - p.y + verticalOffset, Unit.PX);
			}
			if (c.isRendered()) {
				containerSize.width -= getSideMargins(c);
				containerSize.height -= c.el().getMargins("tb");
				setSize(c, 
						data.getWidth() > 0 ? data.getWidth() : containerSize.width, 
						data.getHeight() > 0 ? data.getHeight() : containerSize.height);
			}
			c.getElement().getStyle().setZIndex(data.getZIndex());
		}
	}*/
}
