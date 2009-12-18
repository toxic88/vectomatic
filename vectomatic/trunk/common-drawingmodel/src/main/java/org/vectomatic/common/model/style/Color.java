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
package org.vectomatic.common.model.style;

import org.vectomatic.common.model.IStyleVisitor;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a Color
 */
public class Color implements IStyle, IsSerializable {
	public static final int HSV = 0;
	public static final int RGB = 1;
	public static final int R = 0;
	public static final int G = 1;
	public static final int B = 2;
	public static final int H = 0;
	public static final int S = 1;
	public static final int V = 2;
	public static final Color BLACK = fromRGB(0, 0, 0);
	public static final Color WHITE = fromRGB(255, 255, 255);
	public static final Color GRAY = fromRGB(127, 127, 127);
	public static final Color DARK_GRAY = fromRGB(88, 88, 88);
	public static final Color LIGHT_GRAY = fromRGB(176, 176, 176);
	public static final Color RED = fromRGB(255, 0, 0);
	public static final Color GREEN = fromRGB(0, 255, 0);
	public static final Color BLUE = fromRGB(0, 0, 255);
	private static final int[][] _channelMaxs = {{359, 100, 100}, {255, 255, 255}};
	private static final String[][] _channelNames = {{"H", "S", "V"}, {"R", "G", "B"}};
	private int _channels[];
	private int _space;
	
	public static Color fromRGB(int r, int g, int b) {
		Color color = new Color();
		color._channels[R] = r;
		color._channels[G] = g;
		color._channels[B] = b;
		color._space = RGB;
		return color;
	}
	
	public static Color fromHSV(int h, int s, int v) {
		Color color = new Color();
		color._channels[H] = h;
		color._channels[S] = s;
		color._channels[V] = v;
		color._space = HSV;
		return color;
	}
	
	public Color() {
		_channels = new int[4];
	}
	
	public Color(Color c) {
		this();
		_channels[0] = c._channels[0];
		_channels[1] = c._channels[1];
		_channels[2] = c._channels[2];
		_space = c._space;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Color) {
			Color c = (Color)o;
			return (_channels[0] == c._channels[0]) && (_channels[1] == c._channels[1]) && (_channels[2] == c._channels[2]) && (_space == c._space);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return _channels[0] + 256 * _channels[1] + 65536 * _channels[2] + _space * 16777216;
	}
	
	public int getChannel(int index) {
		return _channels[index];
	}
	
	public void setChannel(int index, int value) {	
		_channels[index] = value;
	}
	
	public static int getChannelMax(int space, int index) {
		return _channelMaxs[space][index];
	}
	
	public static String getChannelName(int space, int index) {
		return _channelNames[space][index];
	}

	public int getSpace() {
		return _space;
	}

	public void setSpace(int space) {
		_space = space;
	}
	
	public Color copyTo(Color c) {
		c._channels[0] = _channels[0];
		c._channels[1] = _channels[1];
		c._channels[2] = _channels[2];
		c._space = _space;
		return c;
	}
	
	public Color convertToColorSpace(int space) {
		return convertToColorSpace(space, this);
	}
	
	public Color convertToColorSpace(int space, Color c) {
		if (this._space == space) {
			copyTo(c);
		} else if (space == HSV) {
			int max = (_channels[R] > _channels[G]) ? (_channels[R] > _channels[B] ? _channels[R] : _channels[B])  : (_channels[G] > _channels[B] ? _channels[G] : _channels[B]);
			int min = (_channels[R] < _channels[G]) ? (_channels[R] < _channels[B] ? _channels[R] : _channels[B])  : (_channels[G] < _channels[B] ? _channels[G] : _channels[B]);
			if (max != min) {
				if (max == _channels[R]) {
					if (_channels[1] >= _channels[2]) {
						c._channels[H] = 60 * (_channels[G] - _channels[B]) / (max - min);
					} else {
						c._channels[H] = 60 * (_channels[G] - _channels[B]) / (max - min) + 360;
					}
				} else if (max == _channels[1]) {
					c._channels[H] = 60 * (_channels[B] - _channels[R]) / (max - min) + 120;
				} else {
					c._channels[H] = 60 * (_channels[R] - _channels[G]) / (max - min) + 240;
				}	
			} else {
				c._channels[H] = 0;
			}
			c._channels[S] = (max == 0) ? 0 : (100 - 100 * min / max);
			c._channels[V] = max * 100 / 255;
			c._space = space;
		} else if (space == RGB) {
			int h = (_channels[H] / 60);
			int p = (255 * _channels[V] * (100 - _channels[S])) / 10000;
			int q = (255 * _channels[V] * (6000 - _channels[S] * (_channels[H] - 60 * h))) / 600000;
			int t = (255 * _channels[V] * (6000 - _channels[S] * (60 - (_channels[H] - 60 * h)))) / 600000;
			switch(h) {
				case 0:
					c._channels[R] = _channels[V] * 255 / 100;
					c._channels[G]  = t;
					c._channels[B]  = p;
					break;
				case 1:
					c._channels[R] = q;
					c._channels[G]  = _channels[V] * 255 / 100;
					c._channels[B]  = p;
					break;
				case 2:
					c._channels[R] = p;
					c._channels[G]  = _channels[V] * 255 / 100;
					c._channels[B]  = t;
					break;
				case 3:
					c._channels[R] = p;
					c._channels[G]  = q;
					c._channels[B]  = _channels[V] * 255 / 100;
					break;
				case 4:
					c._channels[R] = t;
					c._channels[G]  = p;
					c._channels[B]  = _channels[V] * 255 / 100;
					break;
				case 5:
					c._channels[R] = _channels[V] * 255 / 100;
					c._channels[G] = p;
					c._channels[B]  = q;
					break;
			}
			c._space = space;
		} else {
			throw new IllegalArgumentException();
		}
		return c;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (_space == RGB) {
			buffer.append("rgb(");
			buffer.append(_channels[R]);
			buffer.append(",");
			buffer.append(_channels[G]);
			buffer.append(",");
			buffer.append(_channels[B]);
			buffer.append(")");
		} else {
			buffer.append("hsv(");
			buffer.append(_channels[H]);
			buffer.append(",");
			buffer.append(_channels[S]);
			buffer.append(",");
			buffer.append(_channels[V]);
			buffer.append(")");
		}
		return buffer.toString();
	}

	public void acceptVisitor(IStyleVisitor visitor) {
		visitor.visitColor(this);
	};


	public static Color parse(String attributeValue) throws IllegalArgumentException {
		Color color = null;
		if (attributeValue.startsWith("rgb(")) {
			try {
				int start = 4;
				int end = attributeValue.indexOf(',', start);
				int r = Integer.parseInt(attributeValue.substring(start, end));
				start = end + 1;
				end = attributeValue.indexOf(',', start);
				int g = Integer.parseInt(attributeValue.substring(start, end));
				start = end + 1;
				end = attributeValue.indexOf(')', start);
				int b = Integer.parseInt(attributeValue.substring(start, end));
				color = Color.fromRGB(r, g, b);
			} catch(Throwable t) {
				throw new IllegalArgumentException(t.getMessage());
			}
		} else if (attributeValue.startsWith("hsv(")) {
			try {
				int start = 4;
				int end = attributeValue.indexOf(',', start);
				int h = Integer.parseInt(attributeValue.substring(start, end));
				start = end + 1;
				end = attributeValue.indexOf(',', start);
				int s = Integer.parseInt(attributeValue.substring(start, end));
				start = end + 1;
				end = attributeValue.indexOf(')', start);
				int v = Integer.parseInt(attributeValue.substring(start, end));
				color = Color.fromHSV(h, s, v);
			} catch(Throwable t) {
				throw new IllegalArgumentException(t.getMessage());
			}
		} else {
			throw new IllegalArgumentException();
		}
		return color;
	}

	public Integer getKind() {
		return IStyle.PLAIN;
	}
}
