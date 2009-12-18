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
package org.vectomatic.client.rep.view;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.controller.ColorEditor;
import org.vectomatic.client.rep.controller.PickColorController;
import org.vectomatic.client.rep.controller.StyleController;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.style.IStyle;
import org.vectomatic.common.model.style.Palette;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.HSliderBar;
import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.widgetideas.client.SliderListenerAdapter;

/**
 * IStyleMenu implementation to let the end-user pick
 * a color when the selected style is plain color  
 */
public class PlainColorMenu extends PopupPanel implements IStyleMenu {
	private PaletteEditor _paletteEditor;
	private PaletteWidget _paletteWidget;
	private TextBox _alphaTextBox;
	private HSliderBar _alphaSlider;
	private boolean _eventsDisabled;
	private StyleController _styleController;
	private PickColorController _pickColorController;
	private RepApplication _app;
	
	public PlainColorMenu(RepApplication app, StyleController styleController) {
		super(true, true);
		_app = app;
		_pickColorController = new PickColorController(app, styleController);
		setStyleName("plainColorMenu");
	    ColorEditor colorEditor = new ColorEditor(_app);
		_paletteEditor = new PaletteEditor(_app, colorEditor, this);
		_styleController = styleController;
		_paletteWidget = new PaletteWidget(_paletteEditor.getSelectedPalette(), null);
		_paletteWidget.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				_styleController.setStyle(_paletteWidget.getSelectedColor());
				hide();
			}		
		});
		
		Label alphaLabel = new Label(_app.getConstants().transparencyLabel());
		_alphaTextBox = new TextBox();
		_alphaTextBox.setMaxLength(3);
		_alphaTextBox.setWidth("50px");
		_alphaTextBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				if (!_eventsDisabled) {
					_eventsDisabled = true;
					try {
						double value = Double.parseDouble(_alphaTextBox.getText());
						if (value >= _alphaSlider.getMinValue() && value <= _alphaSlider.getMaxValue()) {
							_alphaSlider.setCurrentValue(value);
							_styleController.setOpacity(new FloatAttributeValue((float)(_alphaSlider.getCurrentValue() / 100d)));
						} else {
							_alphaTextBox.setText(Integer.toString((int)_alphaSlider.getCurrentValue()));
						}
					} catch(NumberFormatException e) {
					}
					_eventsDisabled = false;
				}
			}		
		});
		
		_alphaSlider = new HSliderBar(0d, 100d, null, new AbstractImagePrototype[] {_app.getIcons().chslider(), _app.getIcons().chsliderSliding()}, "plainColorMenu-HSliderBar");
		_alphaSlider.setStepSize(1d);
		_alphaSlider.addSliderListener(new SliderListenerAdapter() {
			@Override
			public void onValueChanged(SliderBar slider, double curValue) {
				if (!_eventsDisabled) {
					_eventsDisabled = true;
					_alphaTextBox.setText(Integer.toString((int)curValue));
					_eventsDisabled = false;
				}
			}

			@Override
			public void onStopSliding(SliderBar slider) {
				_styleController.setOpacity(new FloatAttributeValue((float)(slider.getCurrentValue() / 100d)));
			}
		});
		_alphaSlider.setCurrentValue(0d);

		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.add(alphaLabel);
		hpanel.add(_alphaTextBox);
		hpanel.add(_alphaSlider);

		Label editPaletteLabel = new Label(_app.getConstants().editPaletteButton());
		editPaletteLabel.setStyleName("plainColorMenuItem");
		editPaletteLabel.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				editPalette();
			}			
		});
		Label pickColorLabel = new Label(_app.getConstants().pickColorButton());
		pickColorLabel.setStyleName("plainColorMenuItem");
		pickColorLabel.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				pickColor();
			}			
		});
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(_paletteWidget);
		vpanel.add(hpanel);
		vpanel.add(editPaletteLabel);
		vpanel.add(pickColorLabel);
		setWidget(vpanel);
	}
	
	public void editPalette() {
		hide();
		_paletteEditor.show();
	}
	
	public void pickColor() {
		hide();
		_pickColorController.activate(_app.getView());
	}
	
	public IStyle getSelectedStyle() {
		return _paletteWidget.getSelectedColor();
	}
	
	public void setSelectedStyle(IStyle style) {
	}

	public void setOpacity(FloatAttributeValue opacity) {
		int intOpacity = (int)(opacity.getValue() * 100f);
		_alphaSlider.setCurrentValue(intOpacity);
	}

	public FloatAttributeValue getOpacity() {
		return new FloatAttributeValue((float)_alphaSlider.getCurrentValue());
	}
	
	public void setPalette(Palette palette) {
		if (_paletteWidget.getPalette() != palette) {
			_paletteWidget.setPalette(palette);
			_styleController.setStyle(_paletteWidget.getSelectedColor());
		}
	}
}
