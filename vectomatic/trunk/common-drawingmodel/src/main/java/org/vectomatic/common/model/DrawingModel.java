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
package org.vectomatic.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.vectomatic.common.events.IDrawingModelListener;

/**
 * Class to represent a drawing. Contains the collection
 * of shapes in the drawing stored in a linked list.
 */
public class DrawingModel {
	protected class Node {
		public Node prev;
		public Node next;
		public float z;
		public Shape shape;
	}
	
	protected static class AscendingNodeComparator implements Comparator<Node> {
		public int compare(Node o1, Node o2) {
			return Float.compare(o1.z, o2.z);
		}
	}
	
	protected static class DescendingNodeComparator implements Comparator<Node> {
		public int compare(Node o1, Node o2) {
			return Float.compare(o1.z, o2.z) * -1;
		}
	}
	
	protected class AscendingIterator implements Iterator<Shape> {
		Node _current;
		
		public boolean hasNext() {
			return (_current.next != _tail);
		}
		
		public Shape next() {
			if (_current.next == _tail) {
				throw new NoSuchElementException();
			}
			_current = _current.next;
			return _current.shape;
		}
		
		public void remove() {
			if (_current == _head) {
				throw new IllegalStateException();
			}
			Node prev = _current.prev;
			Node next = _current.next;
			prev.next = next;
			next.prev = prev;
			_current = prev;
		}		
	}

	protected class DescendingIterator implements Iterator<Shape> {
		Node _current;
		
		public boolean hasNext() {
			return (_current.prev != _head);
		}
		
		public Shape next() {
			if (_current.prev == _head) {
				throw new NoSuchElementException();
			}
			_current = _current.prev;
			return _current.shape;
		}
		
		public void remove() {
			if (_current == _tail) {
				throw new IllegalStateException();
			}
			Node prev = _current.prev;
			Node next = _current.next;
			prev.next = next;
			next.prev = prev;
			_current = next;
		}		
	}

	protected List<IDrawingModelListener> _drawingModelListeners;
	/**
	 * Shape linked list head node
	 */
	protected Node _head;
	/**
	 * Shape linked list tail node
	 */
	protected Node _tail;
	/**
	 * A Map<Shape,Node> to retrieve the linked list node which contains
	 * a shape.
	 */
	protected Map<Shape,Node> _shapeToNode;
	/**
	 * Linked list forward traversal (back to front)
	 */
	protected AscendingIterator _ascIterator;
	/**
	 * Linked list reverse traversal (front to back)
	 */
	protected DescendingIterator _descIterator;
	/**
	 * To sort Node based on their depth in ascending order
	 */
	protected static final Comparator<Node> ORDER_ASCENDING = new AscendingNodeComparator();
	/**
	 * To sort Node based on their depth in descending order
	 */
	protected static final Comparator<Node> ORDER_DESCENDING = new DescendingNodeComparator();
	/**
	 * Temporary set used for ordering computations
	 */
	protected Set<Shape> _shapeSet;
	
	public DrawingModel() {
		_shapeToNode = new HashMap<Shape,Node>();
		_head = new Node();
		_tail = new Node();
		connect(_head, _tail);
		_shapeSet = new HashSet<Shape>();
		_ascIterator = new AscendingIterator();
		_descIterator = new DescendingIterator();
	}
	
	public void addDrawingModelListener(IDrawingModelListener listener) {
		if (_drawingModelListeners == null) {
			_drawingModelListeners = new ArrayList<IDrawingModelListener>();
		}
		_drawingModelListeners.add(listener);
	}
	
	public void removeDrawingModelListener(IDrawingModelListener listener) {
		if (_drawingModelListeners != null) {
			_drawingModelListeners.remove(listener);
		}
	}
	
	public void fireModelHasChanged() {
		if (_drawingModelListeners != null) {
			for (int i = 0, size = _drawingModelListeners.size(); i < size; i++) {
				IDrawingModelListener listener = _drawingModelListeners.get(i);
				listener.modelHasChanged(this);
			}
		}
	}
	
	public void addShape(Shape shape) {
		if (shape == null) {
			throw new NullPointerException();
		}
		shape.setModel(this);
		Node prevNode = _tail.prev;
		Node node = new Node();
		node.shape = shape;
		node.z = 1f + prevNode.z;
		connect(prevNode, node);
		connect(node, _tail);
		_shapeToNode.put(shape, node);
	}
	
	public void removeShape(Shape shape) {
		if (shape == null) {
			throw new NullPointerException();
		}
		Node node = _shapeToNode.get(shape);
		if (node == null) {
			throw new IllegalArgumentException();
		}
		Node prevNode = node.prev;
		Node nextNode = node.next;
		connect(prevNode, nextNode);
		_shapeToNode.remove(shape);
		shape.setModel(null);
	}
	
	public void clear() {
		connect(_head, _tail);
		_shapeToNode.clear();
	}
	
	public void reorder(List<Shape> shapes, List<Float> orders) {
		Node[] nodes = toNodeArray(shapes);
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].z = orders.get(i).floatValue();
			removeShape(nodes[i].shape);
		}
		Arrays.sort(nodes, ORDER_ASCENDING);
		Node node = _head;
		int index = 0, count = shapes.size();
		while (index < count) {
			if ((node == _tail) || (node.z > nodes[index].z)) {
				Node prevNode = node.prev;
				connect(prevNode, nodes[index]);
				connect(nodes[index], node);
				_shapeToNode.put(nodes[index].shape, nodes[index]);
				index++;
			} else {
				node = node.next;
			}
		}
	}
	
	public int count() {
		return _shapeToNode.size();
	}
	
	public Iterator<Shape> iterator() {
		_ascIterator._current = _head;
		return _ascIterator;
	}

	public Iterator<Shape> reverseIterator() {
		_descIterator._current = _tail;
		return _descIterator;
	}

	protected Node[] toNodeArray(List<Shape> shapes) {
		int size = shapes.size();
		Node[] nodes = new Node[size];
		for (int i = 0; i < size; i++) {
			nodes[i] = _shapeToNode.get(shapes.get(i));
		}
		return nodes;
	}
	
	public boolean canBringToFront(List<Shape> shapes) {
		boolean canBringToFront = false;
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Node nextNode = _shapeToNode.get(shapes.get(i)).next;
			if (nextNode != _tail && (!_shapeSet.contains(nextNode.shape))) {
				canBringToFront = true;
				break;
			}
		}
		return canBringToFront;
	}
	
	public boolean canSendToBack(List<Shape> shapes) {
		boolean canSendToBack = false;
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Node prevNode = _shapeToNode.get(shapes.get(i)).prev;
			if (prevNode != _head && (!_shapeSet.contains(prevNode.shape))) {
				canSendToBack = true;
				break;
			}
		}
		return canSendToBack;
	}
	
	public boolean canBringForward(List<Shape> shapes) {
		boolean canBringForward = false;
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Node nextNode = _shapeToNode.get(shapes.get(i)).next;
			if (nextNode != _tail && (!_shapeSet.contains(nextNode.shape))) {
				canBringForward = true;
				break;
			}
		}
		return canBringForward;
	}
	
	public boolean canSendBackward(List<Shape> shapes) {
		boolean canSendBackward = false;
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Node prevNode = _shapeToNode.get(shapes.get(i)).prev;
			if (prevNode != _head && (!_shapeSet.contains(prevNode.shape))) {
				canSendBackward = true;
				break;
			}
		}
		return canSendBackward;
	}
	
	public List<Float> bringToFront(List<Shape> shapes) {
		List<Float> orders = new ArrayList<Float>(shapes.size());
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		Node[] nodes = toNodeArray(shapes);
		for (int i = 0; i < nodes.length; i++) {
			orders.add(new Float(nodes[i].z));
		}
		Arrays.sort(nodes, ORDER_DESCENDING);
		Node tailNode = _tail;
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Node nextNode = node.next;
			if (nextNode != tailNode) {
				Node prevNode = node.prev;
				Node prevTailNode = tailNode.prev;
				connect(prevNode, nextNode);
				connect(prevTailNode, node);
				connect(node, tailNode);
				float nextz = (tailNode != _tail) ? tailNode.z : prevTailNode.z + 1f;
				node.z = (prevTailNode.z + nextz) * 0.5f;
			}
			tailNode = node;
		}
		return orders;
	}
	
	public List<Float> sendToBack(List<Shape> shapes) {
		List<Float> orders = new ArrayList<Float>(shapes.size());
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		Node[] nodes = toNodeArray(shapes);
		for (int i = 0; i < nodes.length; i++) {
			orders.add(new Float(nodes[i].z));
		}
		Arrays.sort(nodes, ORDER_ASCENDING);
		Node headNode = _head;
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Node prevNode = node.prev;
			if (prevNode != headNode) {
				Node nextNode = node.next;
				Node nextHeadNode = headNode.next;
				connect(prevNode, nextNode);
				connect(node, nextHeadNode);
				connect(headNode, node);
				float prevz = (headNode != _head) ? headNode.z : nextHeadNode.z - 1f;
				node.z = (nextHeadNode.z + prevz) * 0.5f;
			}
			headNode = node;
		}
		return orders;
	}
	
	public List<Float> bringForward(List<Shape> shapes) {
		List<Float> orders = new ArrayList<Float>(shapes.size());
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		Node[] nodes = toNodeArray(shapes);
		for (int i = 0; i < nodes.length; i++) {
			orders.add(new Float(nodes[i].z));
		}
		Arrays.sort(nodes, ORDER_DESCENDING);
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Node nextNode = node.next;
			if (nextNode != _tail && !_shapeSet.contains(nextNode)) {
				Node prevNode = node.prev;
				Node nextNextNode = nextNode.next;
				connect(prevNode, nextNode);
				connect(nextNode, node);
				connect(node, nextNextNode);
				float nextNextz = (nextNextNode != _tail) ? nextNextNode.z : nextNode.z + 1f;
				node.z = (nextNode.z + nextNextz) * 0.5f;
			}
		}
		return orders;
	}
	
	public List<Float> sendBackward(List<Shape> shapes) {
		List<Float> orders = new ArrayList<Float>(shapes.size());
		_shapeSet.clear();
		_shapeSet.addAll(shapes);
		Node[] nodes = toNodeArray(shapes);
		for (int i = 0; i < nodes.length; i++) {
			orders.add(new Float(nodes[i].z));
		}
		Arrays.sort(nodes, ORDER_ASCENDING);
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Node prevNode = node.prev;
			if (prevNode != _head && !_shapeSet.contains(prevNode)) {
				Node nextNode = node.next;
				Node prevPrevNode = prevNode.prev;
				connect(prevPrevNode, node);
				connect(node, prevNode);
				connect(prevNode, nextNode);
				float prevPrevz = (prevPrevNode != _head) ? prevPrevNode.z : prevNode.z - 1f;
				node.z = (prevNode.z + prevPrevz) * 0.5f;
			}
		}
		return orders;
	}
	
	private void connect(Node a, Node b) {
		a.next = b;
		b.prev = a;
	}
	
	public boolean contains(Shape shape) {
		return _shapeToNode.containsKey(shape);
	}
	
	public Shape[] toShapeArray() {
		Shape[] shapeArray = new Shape[count()];
		Iterator<Shape> iterator = iterator();
		int index = 0;
		while(iterator.hasNext()) {
			shapeArray[index++] = iterator.next();
		}
		return shapeArray;
	}
	
	public void fromShapeArray(Shape[] shapeArray) {
		clear();
		if (shapeArray != null) {
			for (int i = 0; i < shapeArray.length; i++) {
				addShape(shapeArray[i]);
			}
		}
	}
}
