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
 * along with Vectomatic.  If not, see <http://www.gnu.org/licenses/>
 **********************************************/
package org.vectomatic.client.rep.controller;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.view.StyleWell;
import org.vectomatic.common.model.style.Color;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.HSliderBar;
import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.widgetideas.client.SliderListener;
import com.google.gwt.widgetideas.client.SliderListenerAdapter;
import com.google.gwt.widgetideas.client.VSliderBar;

/**
 * Widget to implement a gimp-like color editor
 */
public class ColorEditor extends DialogBox implements ClickListener {
	
	private class Gradient2D extends Widget {
		private boolean _drag;
		private Color _tmpColor;
		private int _hcount;
		private int _vcount;
		private int _valueChannel;
		private int _gradientChannel1;
		private int _gradientChannel2;
		private int _space;
		private int _value;
		private List<Element> _subdivs;

		public Gradient2D(int space, int hcount, int vcount, int valueChannel, int gradientChannel1, int gradientChannel2) {
			super();
			_tmpColor = Color.fromRGB(0, 0, 0);
			_hcount = hcount;
			_vcount = vcount;

			Element div = DOM.createDiv();
			setElement(div);
			DOM.setStyleAttribute(div, "position", "relative");
			DOM.setStyleAttribute(div, "border", "1px solid black");
			int width = 144;
			int height = 144;
			setSize(width + "px", height + "px");
			int subWidth = width / _hcount;
			int subHeight = height / _vcount;
			_subdivs = new ArrayList<Element>();
			for (int i = 0; i < _vcount; i++) {
				for (int j = 0; j < _hcount; j++) {
					Element subdiv = DOM.createDiv();
					DOM.setStyleAttribute(subdiv, "position", "absolute");
					DOM.setStyleAttribute(subdiv, "left", j * subWidth + "px");
					DOM.setStyleAttribute(subdiv, "top", i * subHeight + "px");
					DOM.setStyleAttribute(subdiv, "width", subWidth + "px");
					DOM.setStyleAttribute(subdiv, "height", subHeight + "px");
					DOM.appendChild(div, subdiv);
					_subdivs.add(subdiv);
				}
			}
			setConfiguration(space, valueChannel, gradientChannel1, gradientChannel2);
			sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEMOVE | Event.ONMOUSEUP);
		}

		public void render() {
			int max1 = Color.getChannelMax(_space, _gradientChannel1);
			int max2 = Color.getChannelMax(_space, _gradientChannel2);
			for (int i = 0; i < _vcount; i++) {
				for (int j = 0; j < _hcount; j++) {
					_tmpColor.setSpace(_space);
					_tmpColor.setChannel(_valueChannel, _value);
					_tmpColor.setChannel(_gradientChannel1, max1 * j / (_vcount - 1));
					_tmpColor.setChannel(_gradientChannel2, max2 * i / (_hcount - 1));
					_tmpColor.convertToColorSpace(Color.RGB);
					Element subdiv =  _subdivs.get(i * _hcount + j);
					DOM.setStyleAttribute(subdiv, "backgroundColor", _tmpColor.toString());
				}
			}
		}

		public void setConfiguration(int space, int valueChannel, int gradientChannel1, int gradientChannel2) {
			_space = space;
			_valueChannel = valueChannel;
			_gradientChannel1 = gradientChannel1;
			_gradientChannel2 = gradientChannel2;
			render();
		}

		public void setValue(int value) {
			_value = value;
			render();
		}
	  
		@Override
		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);
			switch (DOM.eventGetType(event)) {
				case Event.ONMOUSEUP:
					_drag = false;
					break;
				case Event.ONMOUSEDOWN:
					_drag = true;
				case Event.ONMOUSEMOVE:
					if (_drag) {
						int width = 144;
						int height = 144;
						int x = DOM.eventGetClientX(event);
						x -= ColorEditor.getAbsoluteLeft(getElement());
						int y = DOM.eventGetClientY(event);
						y -= ColorEditor.getAbsoluteTop(getElement());
						int subWidth = width / _hcount;
						int subHeight = height / _vcount;
						int u = Math.min(x / subWidth, _hcount - 1);
						int v = Math.min(y / subHeight, _vcount - 1);
						int a = (u * Color.getChannelMax(_space, _gradientChannel1)) / (_hcount - 1);
						int b = (v * Color.getChannelMax(_space, _gradientChannel2)) / (_vcount - 1);
						if ((a != _colors[_space].getChannel(_gradientChannel1)) || (b != _colors[_space].getChannel(_gradientChannel2))) {
							_colors[_space].setChannel(_gradientChannel1, a);
							_colors[_space].setChannel(_gradientChannel2, b);
							_colors[_space].setChannel(_valueChannel, _value);
							int nspace = (_space == Color.HSV) ? Color.RGB : Color.HSV;
							_colors[_space].convertToColorSpace(nspace, _colors[nspace]);
							_colorWell.update();
						}
					}
					break;
			}
		}
	}

	private class HColorSliderBar extends HSliderBar {
		private List<Element> _subdivs;
		protected int _count;
		
		public HColorSliderBar(double minValue, double maxValue, int count) {
			super(minValue, maxValue, null, new AbstractImagePrototype[] {RepApplication.app._icons.chslider(), RepApplication.app._icons.chsliderSliding()}, "colorEditor-HSliderBar");
			_subdivs = new ArrayList<Element>();
			_count = count;
		}

		@Override
		public void onResize(int width, int height) {
			// Center the line in the shell
			int lineWidth = DOM.getElementPropertyInt(lineElement, "offsetWidth");
			lineOffset = (width / 2) - (lineWidth / 2);
			DOM.setStyleAttribute(lineElement, "left", lineOffset + "px");

			drawColors();
			drawKnob();
		}
		
		private void drawColors() {
		    // Abort if not attached
			if (!isAttached()) {
				return;
			}

			// Draw the color subdivs
			int lineWidth = DOM.getElementPropertyInt(lineElement, "offsetWidth");
			int lineTop = DOM.getElementPropertyInt(lineElement, "offsetTop") + 1;
			int subWidth = lineWidth / _count;
			int subHeight = DOM.getElementPropertyInt(lineElement, "offsetHeight") - 2;
			// Create the ticks or make them visible
			for (int i = 0; i < _count; i++) {
				Element subdiv = null;
				if (_subdivs.size() == _count) {
					subdiv = _subdivs.get(i);
				} else {
					subdiv = DOM.createDiv();
					DOM.setStyleAttribute(subdiv, "position", "absolute");
					DOM.appendChild(getElement(), subdiv);
					_subdivs.add(subdiv);
				}
				DOM.setStyleAttribute(subdiv, "left", (lineOffset + 1 + i * subWidth) + "px");
				DOM.setStyleAttribute(subdiv, "top", lineTop + "px");
				DOM.setStyleAttribute(subdiv, "width", subWidth + "px");
				DOM.setStyleAttribute(subdiv, "height", subHeight + "px");
			}
		}

		public List<Element> getSubdivs() {
			return _subdivs;
		}
	}

	private class VColorSliderBar extends VSliderBar {
		private List<Element> _subdivs;
		protected int _count;
		
		public VColorSliderBar(double minValue, double maxValue, int count) {
			super(minValue, maxValue, null, new AbstractImagePrototype[] {RepApplication.app._icons.cvslider(), RepApplication.app._icons.cvsliderSliding()}, "colorEditor-VSliderBar");
			_subdivs = new ArrayList<Element>();
			_count = count;
		}

		@Override
		public void onResize(int width, int height) {
		    // Center the line in the shell
		    int lineHeight = DOM.getElementPropertyInt(lineElement, "offsetHeight");
		    lineOffset = (height / 2) - (lineHeight / 2);
		    DOM.setStyleAttribute(lineElement, "top", lineOffset + "px");

			drawColors();
			drawKnob();
		    if (sliderListeners != null) {
		        sliderListeners.fireValueChanged(this, getCurrentValue());
		      }
		}


		private void drawColors() {
		    // Abort if not attached
			if (!isAttached()) {
				return;
			}

			// Draw the color subdivs
			int lineLeft = DOM.getElementPropertyInt(lineElement, "offsetLeft") + 1;
			int lineHeight = DOM.getElementPropertyInt(lineElement, "offsetHeight");
			int subWidth = DOM.getElementPropertyInt(lineElement, "offsetWidth") - 2;
			int subHeight = lineHeight / _count;
			// Create the ticks or make them visible
			for (int i = 0; i < _count; i++) {
				Element subdiv = null;
				if (_subdivs.size() == _count) {
					subdiv = _subdivs.get(i);
				} else {
					subdiv = DOM.createDiv();
					DOM.setStyleAttribute(subdiv, "position", "absolute");
					DOM.appendChild(getElement(), subdiv);
					_subdivs.add(subdiv);
				}
				DOM.setStyleAttribute(subdiv, "top", (lineOffset + 1 + i * subHeight) + "px");
				DOM.setStyleAttribute(subdiv, "left", lineLeft + "px");
				DOM.setStyleAttribute(subdiv, "width", subWidth + "px");
				DOM.setStyleAttribute(subdiv, "height", subHeight + "px");
			}
		}

		public List<Element> getSubdivs() {
			return _subdivs;
		}

	}
	
	private class GradientPanel extends FlexTable implements ClickListener {
		private VColorSliderBar _slider;
		private Gradient2D _gradient2D;
		private ToggleButton[] _buttons;
		protected Color _tmpColor;
		
		public GradientPanel() {
			_tmpColor = Color.fromRGB(0, 0, 0);
			_slider = new VColorSliderBar(0, Color.getChannelMax(Color.HSV, Color.H), 16);
			_slider.setStepSize(1.0);
			_slider.addSliderListener(new SliderListenerAdapter() {
				@Override
				public void onValueChanged(SliderBar slider, double curValue) {
					_gradient2D.setValue((int)curValue);
					renderSlider();
				}
			});
			_gradient2D = new Gradient2D(Color.HSV, 16, 16, Color.H, Color.S, Color.V);
			_buttons = new ToggleButton[6];
			for (int i = 0; i < 6; i++) {
				_buttons[i] = new ToggleButton(Color.getChannelName(i / 3, i % 3));
				_buttons[i].addClickListener(this);
				setWidget(i, 0, _buttons[i]);
			}
			_buttons[0].setDown(true);
			onClick(_buttons[0]);
			setWidget(0, 1, _slider);
			setWidget(0, 2, _gradient2D);
			getFlexCellFormatter().setRowSpan(0, 1, 6);
			getFlexCellFormatter().setRowSpan(0, 2, 6);
			getColumnFormatter().setWidth(1, "25px");
		}
		
		public void onClick(Widget sender) {
			for (int i = 0, isize = _buttons.length; i < isize; i ++) {
				if (_buttons[i] == sender) {
					if (_buttons[i].isDown()) {
						int space = i / 3;
						int sliderChannel = i % 3;
						int gradientChannel1 = (i + 1) % 3;
						int gradientChannel2 = (i + 2) % 3;
						_gradient2D.setConfiguration(space, sliderChannel, gradientChannel1, gradientChannel2);
					} else {
						 // The button is already selected
						_buttons[i].setDown(true);
					}
				} else if (_buttons[i].isDown()) {
					// Deselect the previously selected button
					_buttons[i].setDown(false);
				}
			}
			renderSlider();
		}
		
		private void renderSlider() {
			for (int i = 0, isize = _buttons.length; i < isize; i ++) {
				if (_buttons[i].isDown()) {
					// Select the new button
					int space = i / 3;
					int sliderChannel = i % 3;
					int gradientChannel1 = (i + 1) % 3;
					int gradientChannel2 = (i + 2) % 3;
					int max = Color.getChannelMax(space, sliderChannel);
					
					List<Element> subdivs = _slider.getSubdivs();
					for (int j = 0, jsize = subdivs.size(); j < jsize; j++) {
						_tmpColor.setSpace(i / 3);
						if (i < 2) {
							_tmpColor.setChannel(gradientChannel1, Color.getChannelMax(space, gradientChannel1));
							_tmpColor.setChannel(gradientChannel2, Color.getChannelMax(space, gradientChannel2));
						} else {
							_tmpColor.setChannel(gradientChannel1, 0);
							_tmpColor.setChannel(gradientChannel2, 0);
						}
						_tmpColor.setChannel(sliderChannel, max * j / (subdivs.size() - 1));
						_tmpColor.convertToColorSpace(Color.RGB);
						Element subdiv = subdivs.get(j);
						DOM.setStyleAttribute(subdiv, "backgroundColor", _tmpColor.toString());
					}
				}
			}
		}
	}
	
	private class ValuePanel extends Grid implements SliderListener, ChangeListener, TabListener {
		private HColorSliderBar[] _sliders;
		private TextBox[] _textBoxes;
		protected Color _tmpColor;
		
		public ValuePanel() {
			super(6, 3);
			_tmpColor = Color.fromRGB(0, 0, 0);
			_sliders = new HColorSliderBar[6];
			_textBoxes = new TextBox[6];
			for (int i = 0; i < 6; i++) {
				_sliders[i] = new HColorSliderBar(0, Color.getChannelMax(i / 3, i % 3), 16);
				_sliders[i].setStepSize(1.0);
				_sliders[i].setCurrentValue(0.0);
				_sliders[i].addSliderListener(this);
				_sliders[i].setTabIndex(2 * i + 2);
				_textBoxes[i] = new TextBox();
				_textBoxes[i].setMaxLength(3);
				_textBoxes[i].setVisibleLength(3);
				_textBoxes[i].setWidth("40px");
				_textBoxes[i].addChangeListener(this);
				_textBoxes[i].setTabIndex(2 * i + 1);
				setWidget(i, 0, new Label(Color.getChannelName(i / 3, i % 3)));
				setWidget(i, 1, _textBoxes[i]);
				setWidget(i, 2, _sliders[i]);
			}
			ColumnFormatter columnFormatter = getColumnFormatter();
			columnFormatter.setWidth(2, "160px");
			render();
		}

		public void onStartSliding(SliderBar slider) {
		}
		public void onStopSliding(SliderBar slider) {
		}

		public void onValueChanged(SliderBar slider, double curValue) {
			if (!_eventsDisabled) {
				_eventsDisabled = true;
				for (int i = 0; i < 6; i++) {
					if (slider == _sliders[i]) {
						int space = i / 3;
						int nspace = (space == Color.HSV) ? Color.RGB : Color.HSV;
						_colors[space].setChannel(i % 3, (int)curValue);
						_colors[space].convertToColorSpace(nspace, _colors[nspace]);
						break;
					}
				}				
				render();
				_eventsDisabled = false;
			}
		}
		
		public void onChange(Widget sender) {
			if (!_eventsDisabled) {
				_eventsDisabled = true;
				try {
					int value = Integer.parseInt(((TextBox)sender).getText());
					for (int i = 0; i < 6; i++) {
						if (sender == _textBoxes[i]) {
							if ((0 <= value) && (value <= Color.getChannelMax(i / 3, i % 3))) {
								int space = i / 3;
								int nspace = (space == Color.HSV) ? Color.RGB : Color.HSV;
								_colors[space].setChannel(i % 3, value);
								_colors[space].convertToColorSpace(nspace, _colors[nspace]);
							}
							break;
						}
					}
				} catch(NumberFormatException e) {
				}
				render();
				_eventsDisabled = false;
			}
		}
		
		public void render() {
			_eventsDisabled = true;
			for (int i = 0; i < 6; i++) {
				_sliders[i].setCurrentValue(_colors[i / 3].getChannel(i % 3));
				_textBoxes[i].setText(Integer.toString(_colors[i / 3].getChannel(i % 3)));

				// Update the slider bar colors
				List<Element> subdivs = _sliders[i].getSubdivs();
				int max = Color.getChannelMax(i / 3, i % 3);
				for (int j = 0, size = subdivs.size(); j < size; j++) {
					_tmpColor.setSpace(i / 3);
					_tmpColor.setChannel((i + 1) % 3, _colors[i / 3].getChannel((i + 1) % 3));
					_tmpColor.setChannel((i + 2) % 3, _colors[i / 3].getChannel((i + 2) % 3));
					_tmpColor.setChannel(i % 3, max * j / (subdivs.size() - 1));
					_tmpColor.convertToColorSpace(Color.RGB);
					Element subdiv = subdivs.get(j);
					DOM.setStyleAttribute(subdiv, "backgroundColor", _tmpColor.toString());
				}
			}
			_colorWell.update();
			_eventsDisabled = false;
		}
		public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
			return true;
		}

		public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
			if (_tabPanel.getWidget(tabIndex) == this) {
				render();
			}
		}
	}
	
	private boolean _eventsDisabled;
	private Color[] _colors;
	private StyleWell _colorWell;
	private TabPanel _tabPanel;
	private Button _okButton;
	private Button _cancelButton;
	private ChangeListener _listener;
	
	public ColorEditor(RepApplication app)  {
		super();
		setText(app.getConstants().colorEditorPanel());
			
		_colors = new Color[2];
		_colors[Color.HSV] = Color.fromHSV(0, 0, 0);
		_colors[Color.RGB] = Color.fromRGB(0, 0, 0);

		_colorWell = new StyleWell();
		
		GradientPanel gradientPanel = new GradientPanel();
		ValuePanel valuePanel = new ValuePanel();

		_tabPanel = new TabPanel();
		_tabPanel.add(gradientPanel, app.getConstants().gradientsTab());
		_tabPanel.add(valuePanel, app.getConstants().valuesTab());
		_tabPanel.addTabListener(valuePanel);
		
		_okButton = new Button(app.getConstants().okButton());
		_okButton.addClickListener(this);
		_cancelButton = new Button(app.getConstants().cancelButton());
		_cancelButton.addClickListener(this);

		FlexTable table = new FlexTable();
		table.setWidget(0, 0, _tabPanel);
		table.setWidget(1, 0, _colorWell);
		table.setWidget(1, 1, _okButton);
		table.setWidget(1, 2, _cancelButton);
		table.getFlexCellFormatter().setColSpan(0, 0, 3);
		table.getColumnFormatter().setWidth(0, "120px");
		DOM.setStyleAttribute(_colorWell.getElement(), "height", "20px");
		setWidget(table);
	}

	public void onClick(Widget sender) {
		if (sender == _okButton) {
			_listener.onChange(this);
		}
		hide();
	}

	public void setColor(Color color, ChangeListener listener) {
		color.copyTo(_colors[Color.RGB]);
		_colorWell.setStyle(_colors[Color.RGB], null);
		_listener = listener;
		_tabPanel.selectTab(1);
	}
	
	public void getColor(Color color) {
		_colors[Color.RGB].copyTo(color);
	}
	

	public static native int getAbsoluteTop(Element elem) /*-{
	    var top = 0;
	    var curr = elem;
	    // This intentionally excludes body which has a null offsetParent.    
	    while (curr) {
	      top -= curr.scrollTop;
	      curr = curr.offsetParent;
	    }
	    while (elem) {
	      top += elem.offsetTop;
	      elem = elem.offsetParent;
	    }
	    return top;
	}-*/;
	  
	public static native int getAbsoluteLeft(Element elem) /*-{
	    var left = 0;
	    var curr = elem;
	    // This intentionally excludes body which has a null offsetParent.    
	    while (curr) {
	      left -= curr.scrollLeft;
	      curr = curr.offsetParent;
	    }
	    while (elem) {
	      left += elem.offsetLeft;
	      elem = elem.offsetParent;
	    }
	    return left;
	}-*/;
}
