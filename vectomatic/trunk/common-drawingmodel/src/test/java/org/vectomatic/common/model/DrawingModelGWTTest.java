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
package org.vectomatic.common.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.vectomatic.common.GWTTestBase;
import org.vectomatic.common.model.DrawingModel;
import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.Shape;


public class DrawingModelGWTTest extends GWTTestBase {
	public class TestShape extends Shape {
		public String name;
		public TestShape(String name) {
			super();
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
		@Override
		public void acceptVisitor(IShapeVisitor visitor) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public boolean isSame(Shape shape) {
			return false;
		}
	}
	
	public class TestDrawingModel extends DrawingModel {
		public String toString(Node node) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("(");
			if (node.prev != null) {
				if (node.prev.shape == null) {
					buffer.append("Head");
				} else {
					buffer.append(node.prev.shape.toString());
				}
				buffer.append(" <= ");
			}
			buffer.append("[");
			if (node.shape != null) {
				buffer.append(node.shape.toString());
				buffer.append("/");
			}
			buffer.append(node.z);
			buffer.append("]");
			if (node.next != null) {
				buffer.append(" => ");
				if (node.next.shape == null) {
					buffer.append("Tail");
				} else {
					buffer.append(node.next.shape.toString());
				}
			}
			buffer.append(")");
			return buffer.toString();
		}
		
		private String toString(Node[] nodes) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < nodes.length; i++) {
				buffer.append(toString(nodes[i]));
				buffer.append("\n");
			}
			return buffer.toString();
		}
		
		private String toString(TestDrawingModel model) {
			StringBuffer buffer = new StringBuffer();
			Node node = model._head;
			do {
				buffer.append(toString(node));
				node = node.next;
			} while(node != null);
			return buffer.toString();
		}

		private String toShortString(TestDrawingModel model) {
			StringBuffer buffer = new StringBuffer();
			Node node = model._head.next;
			while (node != model._tail) {
				buffer.append(node.shape.toString());
				node = node.next;
			};
			return buffer.toString();
		}

		public void testModel() {
			System.out.println("=========== Begin testModel ===========");
			assertNotNull(_ascIterator);
			assertNotNull(_descIterator);
			assertNotNull(_head);
			assertNotNull(_tail);
			assertNotNull(_shapeSet);
			assertNotNull(_shapeToNode);
			System.out.println(toString(this));
			System.out.println("=========== Begin testModel ===========");
		}
		
		public void testAdd() {
			System.out.println("=========== Begin testAdd ===========");
			model.addShape(A);
			Node nodeA = _shapeToNode.get(A);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(A, nodeA.shape);
			assertEquals(this, A.getModel());
			assertEquals(null, _head.prev);
			assertEquals(nodeA, _head.next);
			assertEquals(_head, nodeA.prev);
			assertEquals(_tail, nodeA.next);
			assertEquals(nodeA, _tail.prev);
			assertEquals(null, _tail.next);
			System.out.println(toString(this));
			
			model.addShape(B);
			Node nodeB = _shapeToNode.get(B);
			assertEquals(2f, nodeB.z, 0f);
			assertEquals(B, nodeB.shape);
			assertEquals(null, _head.prev);
			assertEquals(nodeA, _head.next);
			assertEquals(_head, nodeA.prev);
			assertEquals(nodeB, nodeA.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(_tail, nodeB.next);
			assertEquals(nodeB, _tail.prev);
			assertEquals(null, _tail.next);
			System.out.println(toString(this));
			
			model.addShape(C);
			Node nodeC = _shapeToNode.get(C);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(C, nodeC.shape);
			System.out.println(toString(this));
			assertEquals(null, _head.prev);
			assertEquals(nodeA, _head.next);
			assertEquals(_head, nodeA.prev);
			assertEquals(nodeB, nodeA.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(nodeC, nodeB.next);
			assertEquals(nodeB, nodeC.prev);
			assertEquals(_tail, nodeC.next);
			assertEquals(nodeC, _tail.prev);
			assertEquals(null, _tail.next);
			
			try {
				model.addShape(null);
				fail();
			} catch(NullPointerException e) {
			}
			System.out.println("============ End testAdd ============");
		}
		
		public void testAscendingNodeComparator() {
			System.out.println("=========== Begin testAscendingNodeComparator ===========");
			model.addShape(A);
			Node nodeA = _shapeToNode.get(A);
			model.addShape(B);
			Node nodeB = _shapeToNode.get(B);
			model.addShape(C);
			Node nodeC = _shapeToNode.get(C);
			Node[] nodes = new Node[] {nodeB, nodeA, nodeC};
			System.out.println(toString(nodes));
			Arrays.sort(nodes, ORDER_ASCENDING);
			System.out.println(toString(nodes));
			assertEquals(nodeA, nodes[0]);
			assertEquals(nodeB, nodes[1]);
			assertEquals(nodeC, nodes[2]);
			System.out.println("============ End testAscendingNodeComparator ============");
		}
		
		public void testDescendingNodeComparator() {
			System.out.println("=========== Begin testDescendingNodeComparator ===========");
			model.addShape(A);
			Node nodeA = _shapeToNode.get(A);
			model.addShape(B);
			Node nodeB = _shapeToNode.get(B);
			model.addShape(C);
			Node nodeC = _shapeToNode.get(C);
			Node[] nodes = new Node[] {nodeB, nodeA, nodeC};
			System.out.println(toString(nodes));
			Arrays.sort(nodes, ORDER_DESCENDING);
			System.out.println(toString(nodes));
			assertEquals(nodeC, nodes[0]);
			assertEquals(nodeB, nodes[1]);
			assertEquals(nodeA, nodes[2]);
			System.out.println("============ End testDescendingNodeComparator ============");
		}
		
		public void testRemove() {
			System.out.println("=========== Begin testRemove ===========");
			System.out.println("Remove A");
			model.addShape(A);
			Node nodeA = model._shapeToNode.get(A);
			model.addShape(B);
			Node nodeB = model._shapeToNode.get(B);
			model.addShape(C);
			Node nodeC = model._shapeToNode.get(C);
			System.out.println(toString(model));
			model.removeShape(A);
			System.out.println(toString(model));
			assertNull(A.getModel());
			assertEquals(null, model._head.prev);
			assertEquals(nodeB, model._head.next);
			assertEquals(model._head, nodeB.prev);
			assertEquals(nodeC, nodeB.next);
			assertEquals(nodeB, nodeC.prev);
			assertEquals(model._tail, nodeC.next);
			assertEquals(nodeC, model._tail.prev);
			assertEquals(null, model._tail.next);

			System.out.println("Remove B");
			model = new TestDrawingModel();
			model.addShape(A);
			nodeA = model._shapeToNode.get(A);
			model.addShape(B);
			nodeB = model._shapeToNode.get(B);
			model.addShape(C);
			nodeC = model._shapeToNode.get(C);
			System.out.println(toString(model));
			model.removeShape(B);
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(nodeC, nodeA.next);
			assertEquals(nodeA, nodeC.prev);
			assertEquals(model._tail, nodeC.next);
			assertEquals(nodeC, model._tail.prev);
			assertEquals(null, model._tail.next);

			System.out.println("Remove C");
			model = new TestDrawingModel();
			model.addShape(A);
			nodeA = model._shapeToNode.get(A);
			model.addShape(B);
			nodeB = model._shapeToNode.get(B);
			model.addShape(C);
			nodeC = model._shapeToNode.get(C);
			System.out.println(toString(model));
			model.removeShape(C);
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(nodeB, nodeA.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(model._tail, nodeB.next);
			assertEquals(nodeB, model._tail.prev);
			assertEquals(null, model._tail.next);

			System.out.println("Remove A");
			model.removeShape(A);
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeB, model._head.next);
			assertEquals(model._head, nodeB.prev);
			assertEquals(model._tail, nodeB.next);
			assertEquals(nodeB, model._tail.prev);
			assertEquals(null, model._tail.next);
			
			System.out.println("Remove B");
			model.removeShape(B);
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(model._tail, model._head.next);
			assertEquals(model._head, model._tail.prev);
			assertEquals(null, model._tail.next);

			try {
				model.removeShape(null);
				fail();
			} catch(NullPointerException e) {
			}

			try {
				model.removeShape(D);
				fail();
			} catch(IllegalArgumentException e) {
			}

			System.out.println("============ End testRemove ============");		
		}
		
		public void testIterator() {
			System.out.println("=========== Begin testIterator ===========");
			Iterator<Shape> iterator = model.iterator();
			assertFalse(iterator.hasNext());
			try {
				iterator.next();
				fail();
			} catch(NoSuchElementException e) {
			}
			try {
				iterator.remove();
				fail();
			} catch(IllegalStateException e) {
			}
			
			model.addShape(A);
			model.addShape(B);
			Node nodeB = model._shapeToNode.get(B);
			model.addShape(C);
			Node nodeC = model._shapeToNode.get(C);
			System.out.println(toString(model));
			iterator = model.iterator();
			assertTrue(iterator.hasNext());
			assertEquals(A, iterator.next());
			assertTrue(iterator.hasNext());
			assertEquals(B, iterator.next());
			assertTrue(iterator.hasNext());
			assertEquals(C, iterator.next());
			assertFalse(iterator.hasNext());
			try {
				iterator.next();
				fail();
			} catch(NoSuchElementException e) {
			}

			iterator = model.iterator();
			assertEquals(A, iterator.next());
			iterator.remove();
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeB, model._head.next);
			assertEquals(model._head, nodeB.prev);
			assertEquals(nodeC, nodeB.next);
			assertEquals(nodeB, nodeC.prev);
			assertEquals(model._tail, nodeC.next);
			assertEquals(nodeC, model._tail.prev);
			assertEquals(null, model._tail.next);
			
			
			assertEquals(B, iterator.next());
			iterator.remove();
			System.out.println(toString(model));
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeC, model._head.next);
			assertEquals(model._head, nodeC.prev);
			assertEquals(model._tail, nodeC.next);
			assertEquals(nodeC, model._tail.prev);
			assertEquals(null, model._tail.next);

			
			assertEquals(C, iterator.next());
			iterator.remove();
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(model._tail, model._head.next);
			assertEquals(model._head, model._tail.prev);
			assertEquals(null, model._tail.next);
			
			try {
				iterator.next();
				fail();
			} catch(NoSuchElementException e) {
			}
			try {
				iterator.remove();
				fail();
			} catch(IllegalStateException e) {
			}			
			System.out.println("============ End testIterator ============");		
		}

		public void testReverseIterator() {
			System.out.println("=========== Begin testReverseIterator ===========");
			Iterator<Shape> iterator = model.reverseIterator();
			assertFalse(iterator.hasNext());
			try {
				iterator.next();
				fail();
			} catch(NoSuchElementException e) {
			}
			try {
				iterator.remove();
				fail();
			} catch(IllegalStateException e) {
			}
			
			model.addShape(A);
			Node nodeA = model._shapeToNode.get(A);
			model.addShape(B);
			Node nodeB = model._shapeToNode.get(B);
			model.addShape(C);
			System.out.println(toString(model));
			iterator = model.reverseIterator();
			assertTrue(iterator.hasNext());
			assertEquals(C, iterator.next());
			assertTrue(iterator.hasNext());
			assertEquals(B, iterator.next());
			assertTrue(iterator.hasNext());
			assertEquals(A, iterator.next());
			assertFalse(iterator.hasNext());
			try {
				iterator.next();
				fail();
			} catch(NoSuchElementException e) {
			}

			iterator = model.reverseIterator();
			assertEquals(C, iterator.next());
			iterator.remove();
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(nodeB, nodeA.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(model._tail, nodeB.next);
			assertEquals(nodeB, model._tail.prev);
			assertEquals(null, model._tail.next);
			
			
			assertEquals(B, iterator.next());
			iterator.remove();
			System.out.println(toString(model));
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(model._tail, nodeA.next);
			assertEquals(nodeA, model._tail.prev);
			assertEquals(null, model._tail.next);

			
			assertEquals(A, iterator.next());
			iterator.remove();
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(model._tail, model._head.next);
			assertEquals(model._head, model._tail.prev);
			assertEquals(null, model._tail.next);
			
			try {
				iterator.next();
				fail();
			} catch(NoSuchElementException e) {
			}
			try {
				iterator.remove();
				fail();
			} catch(IllegalStateException e) {
			}			
			System.out.println("============ End testReverseIterator ============");		
		}

		public void testContains() {
			System.out.println("=========== Begin testContains ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			assertTrue(model.contains(A));
			assertTrue(model.contains(B));
			assertTrue(model.contains(C));
			assertFalse(model.contains(D));
			System.out.println("============ End testContains ============");				
		}
		
		public void testCount() {
			System.out.println("=========== Begin testCount ===========");
			assertEquals(0, model.count());
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			assertEquals(3, model.count());
			System.out.println("============ End testCount ============");				
		}


		public void testBringForward() {
			System.out.println("=========== Begin testBringForward ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			Node nodeA = model._shapeToNode.get(A);
			Node nodeB = model._shapeToNode.get(B);
			Node nodeC = model._shapeToNode.get(C);
			Node nodeD = model._shapeToNode.get(D);
			Node nodeE = model._shapeToNode.get(E);
			Node nodeF = model._shapeToNode.get(F);
			Node nodeG = model._shapeToNode.get(G);
			Node nodeH = model._shapeToNode.get(H);
			System.out.println("Forward: H, E, B, D");
			System.out.println(toShortString(model));
			List<Float> orders = model.bringForward(Arrays.asList(new Shape[]{H, E, B, D}));
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(Arrays.asList(new Float[]{ new Float(8), new Float(5), new Float(2), new Float(4)}), orders);
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeC, nodeA.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeB, nodeC.next);
			assertEquals(nodeA, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeF, nodeB.next);
			assertEquals(nodeC, nodeB.prev);
			assertEquals(4.5f, nodeB.z, 0f);
			assertEquals(nodeD, nodeF.next);
			assertEquals(nodeB, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeF, nodeD.prev);
			assertEquals(6.25f, nodeD.z, 0f);
			assertEquals(nodeG, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(6.5f, nodeE.z, 0f);
			assertEquals(nodeH, nodeG.next);
			assertEquals(nodeE, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(model._tail, nodeH.next);
			assertEquals(nodeG, nodeH.prev);
			assertEquals(nodeH, model._tail.prev);
			assertEquals(8f, nodeH.z, 0f);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);

			System.out.println("============ End testBringForward ============");				
		}

		public void testSendBackward() {
			System.out.println("=========== Begin testSendBackward ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			Node nodeA = model._shapeToNode.get(A);
			Node nodeB = model._shapeToNode.get(B);
			Node nodeC = model._shapeToNode.get(C);
			Node nodeD = model._shapeToNode.get(D);
			Node nodeE = model._shapeToNode.get(E);
			Node nodeF = model._shapeToNode.get(F);
			Node nodeG = model._shapeToNode.get(G);
			Node nodeH = model._shapeToNode.get(H);
			System.out.println("Backward: H, E, B, D");
			System.out.println(toShortString(model));
			List<Float> orders = model.sendBackward(Arrays.asList(new Shape[]{H, E, B, D}));
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(Arrays.asList(new Float[]{ new Float(8), new Float(5), new Float(2), new Float(4)}), orders);
			assertEquals(null, model._head.prev);
			assertEquals(nodeB, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeA, nodeB.next);
			assertEquals(model._head, nodeB.prev);
			assertEquals(0.5f, nodeB.z, 0f);
			assertEquals(nodeD, nodeA.next);
			assertEquals(nodeB, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeA, nodeD.prev);
			assertEquals(2f, nodeD.z, 0f);
			assertEquals(nodeC, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(2.5f, nodeE.z, 0f);
			assertEquals(nodeF, nodeC.next);
			assertEquals(nodeE, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeH, nodeF.next);
			assertEquals(nodeC, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(nodeG, nodeH.next);
			assertEquals(nodeF, nodeH.prev);
			assertEquals(6.5f, nodeH.z, 0f);
			assertEquals(model._tail, nodeG.next);
			assertEquals(nodeH, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(nodeG, model._tail.prev);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);

			System.out.println("============ End testSendBackward ============");				
		}

		public void testBringToFront() {
			System.out.println("=========== Begin testBringToFront ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			Node nodeA = model._shapeToNode.get(A);
			Node nodeB = model._shapeToNode.get(B);
			Node nodeC = model._shapeToNode.get(C);
			Node nodeD = model._shapeToNode.get(D);
			Node nodeE = model._shapeToNode.get(E);
			Node nodeF = model._shapeToNode.get(F);
			Node nodeG = model._shapeToNode.get(G);
			Node nodeH = model._shapeToNode.get(H);
			System.out.println("Front: H, E, B, D");
			System.out.println(toShortString(model));
			List<Float> orders = model.bringToFront(Arrays.asList(new Shape[]{H, E, B, D}));
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(Arrays.asList(new Float[]{ new Float(8), new Float(5), new Float(2), new Float(4)}), orders);
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeC, nodeA.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeF, nodeC.next);
			assertEquals(nodeA, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeG, nodeF.next);
			assertEquals(nodeC, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(nodeB, nodeG.next);
			assertEquals(nodeF, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(nodeD, nodeB.next);
			assertEquals(nodeG, nodeB.prev);
			assertEquals(7.125f, nodeB.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeB, nodeD.prev);
			assertEquals(7.25f, nodeD.z, 0f);
			assertEquals(nodeH, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(7.5f, nodeE.z, 0f);
			assertEquals(model._tail, nodeH.next);
			assertEquals(nodeE, nodeH.prev);
			assertEquals(nodeH, model._tail.prev);
			assertEquals(8f, nodeH.z, 0f);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);

			System.out.println("============ End testBringToFront ============");				
		}

		public void testSendToBack() {
			System.out.println("=========== Begin testSendToBack ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			Node nodeA = model._shapeToNode.get(A);
			Node nodeB = model._shapeToNode.get(B);
			Node nodeC = model._shapeToNode.get(C);
			Node nodeD = model._shapeToNode.get(D);
			Node nodeE = model._shapeToNode.get(E);
			Node nodeF = model._shapeToNode.get(F);
			Node nodeG = model._shapeToNode.get(G);
			Node nodeH = model._shapeToNode.get(H);
			System.out.println("Back: H, E, B, D");
			System.out.println(toShortString(model));
			List<Float> orders = model.sendToBack(Arrays.asList(new Shape[]{H, E, B, D}));
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(Arrays.asList(new Float[]{ new Float(8), new Float(5), new Float(2), new Float(4)}), orders);
			assertEquals(null, model._head.prev);
			assertEquals(nodeB, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeD, nodeB.next);
			assertEquals(model._head, nodeB.prev);
			assertEquals(0.5f, nodeB.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeB, nodeD.prev);
			assertEquals(0.75f, nodeD.z, 0f);
			assertEquals(nodeH, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(0.875f, nodeE.z, 0f);
			assertEquals(nodeA, nodeH.next);
			assertEquals(nodeE, nodeH.prev);
			assertEquals(0.9375f, nodeH.z, 0f);
			assertEquals(nodeC, nodeA.next);
			assertEquals(nodeH, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeF, nodeC.next);
			assertEquals(nodeA, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeG, nodeF.next);
			assertEquals(nodeC, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(model._tail, nodeG.next);
			assertEquals(nodeF, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(nodeG, model._tail.prev);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);
			System.out.println("============ End testSendToBack ============");				
		}

		public void testCanBringForward() {
			System.out.println("=========== Begin testCanBringForward ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			List<Shape> shapes = Arrays.asList(new Shape[]{H, E, B, D});
			System.out.println(toShortString(model));
			System.out.println("canBringForward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringForward(shapes));
			
			shapes = Arrays.asList(new Shape[]{H});
			System.out.println(toShortString(model));
			System.out.println("canBringForward" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canBringForward(shapes));

			shapes = Arrays.asList(new Shape[]{H, G});
			System.out.println(toShortString(model));
			System.out.println("canBringForward" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canBringForward(shapes));

			shapes = Arrays.asList(new Shape[]{H, F});
			System.out.println(toShortString(model));
			System.out.println("canBringForward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringForward(shapes));
	
			shapes = Arrays.asList(new Shape[]{});
			System.out.println(toShortString(model));
			System.out.println("canBringForward" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canBringForward(shapes));

			shapes = Arrays.asList(new Shape[]{A});
			System.out.println(toShortString(model));
			System.out.println("canBringForward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringForward(shapes));

			shapes = Arrays.asList(new Shape[]{B});
			System.out.println(toShortString(model));
			System.out.println("canBringForward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringForward(shapes));
			
			System.out.println("============ End testCanBringForward ============");				
		}

		public void testCanSendBackward() {
			System.out.println("=========== Begin testCanSendBackward ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			List<Shape> shapes = Arrays.asList(new Shape[]{H, E, B, D});
			System.out.println(toShortString(model));
			System.out.println("canSendBackward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendBackward(shapes));
			
			shapes = Arrays.asList(new Shape[]{A});
			System.out.println(toShortString(model));
			System.out.println("canSendBackward" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canSendBackward(shapes));

			shapes = Arrays.asList(new Shape[]{A, B});
			System.out.println(toShortString(model));
			System.out.println("canSendBackward" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canSendBackward(shapes));

			shapes = Arrays.asList(new Shape[]{A, C});
			System.out.println(toShortString(model));
			System.out.println("canSendBackward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendBackward(shapes));

			shapes = Arrays.asList(new Shape[]{H});
			System.out.println(toShortString(model));
			System.out.println("canSendBackward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendBackward(shapes));

			shapes = Arrays.asList(new Shape[]{});
			System.out.println(toShortString(model));
			System.out.println("canSendBackward" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canSendBackward(shapes));

			shapes = Arrays.asList(new Shape[]{E, C});
			System.out.println(toShortString(model));
			System.out.println("canSendBackward" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendBackward(shapes));
			System.out.println("============ End testCanSendBackward ============");				
		}

		public void testCanBringToFront() {
			System.out.println("=========== Begin testCanBringToFront ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			List<Shape> shapes = Arrays.asList(new Shape[]{H, E, B, D});
			System.out.println(toShortString(model));
			System.out.println("canBringToFront" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringToFront(shapes));
			
			shapes = Arrays.asList(new Shape[]{H});
			System.out.println(toShortString(model));
			System.out.println("canBringToFront" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canBringToFront(shapes));

			shapes = Arrays.asList(new Shape[]{H, G});
			System.out.println(toShortString(model));
			System.out.println("canBringToFront" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canBringToFront(shapes));

			shapes = Arrays.asList(new Shape[]{H, G, E});
			System.out.println(toShortString(model));
			System.out.println("canBringToFront" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringToFront(shapes));

			shapes = Arrays.asList(new Shape[]{A});
			System.out.println(toShortString(model));
			System.out.println("canBringToFront" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringToFront(shapes));

			shapes = Arrays.asList(new Shape[]{B, C});
			System.out.println(toShortString(model));
			System.out.println("canBringToFront" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canBringToFront(shapes));

			shapes = Arrays.asList(new Shape[]{});
			System.out.println(toShortString(model));
			System.out.println("canBringToFront" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canBringToFront(shapes));
			System.out.println("============ End testCanBringToFront ============");				
		}

		public void testCanSendToBack() {
			System.out.println("=========== Begin testCanSendToBack ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			List<Shape> shapes = Arrays.asList(new Shape[]{H, E, B, D});
			System.out.println(toShortString(model));
			System.out.println("canSendToBack" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendToBack(shapes));
			
			shapes = Arrays.asList(new Shape[]{A});
			System.out.println(toShortString(model));
			System.out.println("canSendToBack" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canSendToBack(shapes));

			shapes = Arrays.asList(new Shape[]{B, A});
			System.out.println(toShortString(model));
			System.out.println("canSendToBack" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canSendToBack(shapes));

			shapes = Arrays.asList(new Shape[]{B, A, H});
			System.out.println(toShortString(model));
			System.out.println("canSendToBack" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendToBack(shapes));

			shapes = Arrays.asList(new Shape[]{H});
			System.out.println(toShortString(model));
			System.out.println("canSendToBack" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendToBack(shapes));

			shapes = Arrays.asList(new Shape[]{C, D});
			System.out.println(toShortString(model));
			System.out.println("canSendToBack" + shapes + " ->" + model.canBringForward(shapes));
			assertTrue(model.canSendToBack(shapes));

			shapes = Arrays.asList(new Shape[]{});
			System.out.println(toShortString(model));
			System.out.println("canSendToBack" + shapes + " ->" + model.canBringForward(shapes));
			assertFalse(model.canSendToBack(shapes));
			System.out.println("============ End testCanSendToBack ============");				
		}

		public void testReorder() {
			System.out.println("=========== Begin testReorder ===========");
			model.addShape(A);
			model.addShape(B);
			model.addShape(C);
			model.addShape(D);
			model.addShape(E);
			model.addShape(F);
			model.addShape(G);
			model.addShape(H);
			Node nodeA = model._shapeToNode.get(A);
			Node nodeB = model._shapeToNode.get(B);
			Node nodeC = model._shapeToNode.get(C);
			Node nodeD = model._shapeToNode.get(D);
			Node nodeE = model._shapeToNode.get(E);
			Node nodeF = model._shapeToNode.get(F);
			Node nodeG = model._shapeToNode.get(G);
			Node nodeH = model._shapeToNode.get(H);
			System.out.println("Forward: H, D, E, B");
			System.out.println(toShortString(model));
			List<Shape> shapes = Arrays.asList(new Shape[]{H, D, E, B});
			List<Float> orders = model.bringForward(shapes);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			System.out.println("Reorder");
			model.reorder(shapes, orders);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeB, nodeA.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeC, nodeB.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(2f, nodeB.z, 0f);
			assertEquals(nodeD, nodeC.next);
			assertEquals(nodeB, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeC, nodeD.prev);
			assertEquals(4f, nodeD.z, 0f);
			assertEquals(nodeF, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(5f, nodeE.z, 0f);
			assertEquals(nodeG, nodeF.next);
			assertEquals(nodeE, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(nodeH, nodeG.next);
			assertEquals(nodeF, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(model._tail, nodeH.next);
			assertEquals(nodeG, nodeH.prev);
			assertEquals(nodeH, model._tail.prev);
			assertEquals(8f, nodeH.z, 0f);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);
		
			System.out.println("Front: H, D, E, B");
			orders = model.bringToFront(shapes);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			System.out.println("Reorder");
			model.reorder(shapes, orders);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeB, nodeA.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeC, nodeB.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(2f, nodeB.z, 0f);
			assertEquals(nodeD, nodeC.next);
			assertEquals(nodeB, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeC, nodeD.prev);
			assertEquals(4f, nodeD.z, 0f);
			assertEquals(nodeF, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(5f, nodeE.z, 0f);
			assertEquals(nodeG, nodeF.next);
			assertEquals(nodeE, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(nodeH, nodeG.next);
			assertEquals(nodeF, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(model._tail, nodeH.next);
			assertEquals(nodeG, nodeH.prev);
			assertEquals(nodeH, model._tail.prev);
			assertEquals(8f, nodeH.z, 0f);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);
			
			System.out.println("Backward: H, E, B, D");
			orders = model.sendBackward(shapes);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			System.out.println("Reorder");
			model.reorder(shapes, orders);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeB, nodeA.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeC, nodeB.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(2f, nodeB.z, 0f);
			assertEquals(nodeD, nodeC.next);
			assertEquals(nodeB, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeC, nodeD.prev);
			assertEquals(4f, nodeD.z, 0f);
			assertEquals(nodeF, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(5f, nodeE.z, 0f);
			assertEquals(nodeG, nodeF.next);
			assertEquals(nodeE, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(nodeH, nodeG.next);
			assertEquals(nodeF, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(model._tail, nodeH.next);
			assertEquals(nodeG, nodeH.prev);
			assertEquals(nodeH, model._tail.prev);
			assertEquals(8f, nodeH.z, 0f);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);


			System.out.println("Back: H, E, B, D");
			orders = model.sendToBack(shapes);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			System.out.println("Reorder");
			model.reorder(shapes, orders);
			System.out.println(toShortString(model));
			System.out.println(toString(model));
			assertEquals(null, model._head.prev);
			assertEquals(nodeA, model._head.next);
			assertEquals(0f, model._head.z, 0f);
			assertEquals(nodeB, nodeA.next);
			assertEquals(model._head, nodeA.prev);
			assertEquals(1f, nodeA.z, 0f);
			assertEquals(nodeC, nodeB.next);
			assertEquals(nodeA, nodeB.prev);
			assertEquals(2f, nodeB.z, 0f);
			assertEquals(nodeD, nodeC.next);
			assertEquals(nodeB, nodeC.prev);
			assertEquals(3f, nodeC.z, 0f);
			assertEquals(nodeE, nodeD.next);
			assertEquals(nodeC, nodeD.prev);
			assertEquals(4f, nodeD.z, 0f);
			assertEquals(nodeF, nodeE.next);
			assertEquals(nodeD, nodeE.prev);
			assertEquals(5f, nodeE.z, 0f);
			assertEquals(nodeG, nodeF.next);
			assertEquals(nodeE, nodeF.prev);
			assertEquals(6f, nodeF.z, 0f);
			assertEquals(nodeH, nodeG.next);
			assertEquals(nodeF, nodeG.prev);
			assertEquals(7f, nodeG.z, 0f);
			assertEquals(model._tail, nodeH.next);
			assertEquals(nodeG, nodeH.prev);
			assertEquals(nodeH, model._tail.prev);
			assertEquals(8f, nodeH.z, 0f);
			assertEquals(null, model._tail.next);
			assertEquals(0f, model._tail.z, 0f);

			System.out.println("============ End testReorder ============");				
		}

	}
	
	private TestShape A, B, C, D, E, F, G, H;
	private TestDrawingModel model;
	
	@Override
	public void gwtSetUp() {
		A = new TestShape("A");
		B = new TestShape("B");
		C = new TestShape("C");
		D = new TestShape("D");
		E = new TestShape("E");
		F = new TestShape("F");
		G = new TestShape("G");
		H = new TestShape("H");
		model = new TestDrawingModel();
	}
	public void testModel() {
		model.testModel();
	}
	public void testAscendingNodeComparator() {
		model.testAscendingNodeComparator();
	}
	public void testDescendingNodeComparator() {
		model.testDescendingNodeComparator();
	}
	public void testAdd() {
		model.testAdd();
	}
	public void testRemove() {
		model.testRemove();
	}
	public void testIterator() {
		model.testIterator();
	}
	public void testReverseIterator() {
		model.testReverseIterator();
	}
	public void testContains() {
		model.testContains();
	}
	public void testCount() {
		model.testCount();
	}
	public void testBringForward() {
		model.testBringForward();
	}
	public void testSendBackward() {
		model.testSendBackward();
	}
	public void testBringToFront() {
		model.testBringToFront();
	}
	public void testSendToBack() {
		model.testSendToBack();
	}
	public void testCanBringForward() {
		model.testCanBringForward();
	}
	public void testCanSendBackward() {
		model.testCanSendBackward();
	}
	public void testCanBringToFront() {
		model.testCanBringToFront();
	}
	public void testCanSendToBack() {
		model.testCanSendToBack();
	}
	
	public void testReorder() {
		model.testReorder();
	}
}

