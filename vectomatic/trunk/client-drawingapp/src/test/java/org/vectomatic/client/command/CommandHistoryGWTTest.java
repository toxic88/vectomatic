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
package org.vectomatic.client.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import org.vectomatic.client.GWTTestBase;
import org.vectomatic.client.rep.command.CommandHistory;
import org.vectomatic.client.rep.command.ICommand;
import org.vectomatic.client.rep.events.ICommandHistoryListener;


public class CommandHistoryGWTTest extends GWTTestBase {
	private static class CommandEvent extends EventObject {
		private static final long serialVersionUID = 1L;
		public static final int UNDO = 1;
		public static final int REDO = 2;
		private int _type;
		public CommandEvent(Object source, int type) {
			super(source);
			_type = type;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CommandEvent) {
				CommandEvent event = (CommandEvent)obj;
				return getSource().equals(event.getSource()) && _type == event._type;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return getSource().hashCode() + _type;
		}
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("[");
			if (_type == UNDO) {
				buffer.append("U ");
			} else {
				buffer.append("R ");
			}
			buffer.append(getSource().toString());
			buffer.append("]");
			return buffer.toString();
		}
		
	}
	private interface ICommandListener {
		public void onCommand(CommandEvent commandEvent);
	}
	private class CommandListener implements ICommandListener {
		private List<CommandEvent> _events;
		public CommandListener() {
			_events = new ArrayList<CommandEvent>();
		}
		public void onCommand(CommandEvent commandEvent) {
			_events.add(commandEvent);
		}
		public List<CommandEvent> getEvents() {
			return _events;
		}
		public void reset() {
			_events.clear();
		}
	}
	
	private class CommandHistoryListener implements ICommandHistoryListener {
		private boolean _hasFired;
		public void commandHistoryChange(CommandHistory commandHistory) {
			_hasFired = true;
		}
		public void reset() {
			_hasFired = false;
		}
		public boolean hasFired() {
			return _hasFired;
		}
	}
	
	private static class TestCommand implements ICommand {
		private String _description;
		private List<ICommandListener> _listeners;
		public TestCommand(String description) {
			_description = description;
		}
		public void addCommandListener(ICommandListener listener) {
			if (_listeners == null) {
				_listeners = new ArrayList<ICommandListener>();
			}
			_listeners.add(listener);
		}
		public void removeCommandListener(ICommandListener listener) {
			if (_listeners != null) {
				_listeners.remove(listener);
			}
		}
		public void execute() {
			fireOnCommand(CommandEvent.REDO);
		}
		public void unexecute() {
			fireOnCommand(CommandEvent.UNDO);
		}
		private void fireOnCommand(int type) {
			if (_listeners != null) {
				for (int i = 0, size = _listeners.size(); i < size; i++) {
					_listeners.get(i).onCommand(new CommandEvent(this, type));
				}
			}			
		}
		public String getDescription() {
			return _description;
		}
		@Override
		public String toString() {
			return _description;
		}
	}
	
	private CommandListener clistener;
	private CommandHistoryListener hlistener;
	private TestCommand ca;
	private TestCommand cb;
	private TestCommand cc;
	private TestCommand cd;
	private TestCommand ce;
	private TestCommand cf;
	private TestCommand cg;
	private TestCommand ch;
	private TestCommand ci;
	private TestCommand cj;
	private TestCommand ck;

	private CommandEvent uca;
	private CommandEvent ucb;
	private CommandEvent ucc;
	private CommandEvent ucd;
	private CommandEvent uce;
	private CommandEvent ucf;
	private CommandEvent ucg;
	private CommandEvent uch;

	private CommandEvent rca;
	private CommandEvent rcc;
	private CommandEvent rcf;

	@Override
	protected void gwtSetUp() throws Exception {
		clistener = new CommandListener();
		ca = new TestCommand("A");
		ca.addCommandListener(clistener);
		cb = new TestCommand("B");
		cb.addCommandListener(clistener);
		cc = new TestCommand("C");
		cc.addCommandListener(clistener);
		cd = new TestCommand("D");
		cd.addCommandListener(clistener);
		ce = new TestCommand("E");
		ce.addCommandListener(clistener);
		cf = new TestCommand("F");
		cf.addCommandListener(clistener);
		cg = new TestCommand("G");
		cg.addCommandListener(clistener);
		ch = new TestCommand("H");
		ch.addCommandListener(clistener);
		ci = new TestCommand("I");
		ci.addCommandListener(clistener);
		cj = new TestCommand("J");
		cj.addCommandListener(clistener);
		ck = new TestCommand("K");
		ck.addCommandListener(clistener);
		
		uca = new CommandEvent(ca, CommandEvent.UNDO);
		ucb = new CommandEvent(cb, CommandEvent.UNDO);
		ucc = new CommandEvent(cc, CommandEvent.UNDO);
		ucd = new CommandEvent(cd, CommandEvent.UNDO);
		uce = new CommandEvent(ce, CommandEvent.UNDO);
		ucf = new CommandEvent(cf, CommandEvent.UNDO);
		ucg = new CommandEvent(cg, CommandEvent.UNDO);
		uch = new CommandEvent(ch, CommandEvent.UNDO);

		rca = new CommandEvent(ca, CommandEvent.REDO);
		rcc = new CommandEvent(cc, CommandEvent.REDO);
		rcf = new CommandEvent(cf, CommandEvent.REDO);
		
		hlistener = new CommandHistoryListener();
	}
	
	public void testConstructor0() {
		try {
			new CommandHistory(0);
			fail();
		} catch(IllegalArgumentException e) {
		}
	}		
	public void testConstructor1() {
		// stack of size 1
		CommandHistory history = new CommandHistory(1);
		assertEquals(1, history.getCapacity());
		assertFalse(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(0, history.getCurrentCommand());
		assertFalse(history.needsSaving());
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		assertNull(history.getUndoCommand());
		assertNull(history.getRedoCommand());
		ICommand[] commands = history.getCommands();
		assertNotNull(commands);
		assertEquals(0, commands.length);
	}
	public void testConstructor2() {
		// stack of size 2
		CommandHistory history = new CommandHistory(2);
		assertEquals(2, history.getCapacity());
		assertFalse(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(0, history.getCurrentCommand());
		assertFalse(history.needsSaving());
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		assertNull(history.getUndoCommand());
		assertNull(history.getRedoCommand());
		ICommand[] commands = history.getCommands();
		assertNotNull(commands);
		assertEquals(0, commands.length);
	}		
	public void testConstructor3() {
		// stack of size 3
		CommandHistory history = new CommandHistory(3);
		assertEquals(3, history.getCapacity());
		assertFalse(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(0, history.getCurrentCommand());
		assertFalse(history.needsSaving());
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		assertNull(history.getUndoCommand());
		assertNull(history.getRedoCommand());
		ICommand[] commands = history.getCommands();
		assertNotNull(commands);
		assertEquals(0, commands.length);
	}
	
	public void testUndoRedo() {
		// A |
		CommandHistory history = new CommandHistory(100);
		history.addCommand(ca);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(ca, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}

		// A B |
		history.addCommand(cb);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cb, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(2, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}

		// A B C |
		history.addCommand(cc);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cc, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(3, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, cc}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// A B | C
		history.undo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(cb, history.getUndoCommand());
		assertEquals(cc, history.getRedoCommand());
		assertEquals(2, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, cc}), Arrays.asList(history.getCommands()));
		
		// A B C |
		history.redo();
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cc, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(3, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, cc}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// A | B C
		history.undo();
		history.undo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(ca, history.getUndoCommand());
		assertEquals(cb, history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, cc}), Arrays.asList(history.getCommands()));

		// | A B C
		history.undo();
		assertFalse(history.canUndo());
		assertTrue(history.canRedo());
		assertNull(history.getUndoCommand());
		assertEquals(ca, history.getRedoCommand());
		assertEquals(0, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, cc}), Arrays.asList(history.getCommands()));
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// A | B C
		history.redo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(ca, history.getUndoCommand());
		assertEquals(cb, history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, cc}), Arrays.asList(history.getCommands()));

		// Validate events
		assertEquals(Arrays.asList(new CommandEvent[]{ucc, rcc, ucc, ucb, uca, rca}), clistener.getEvents());
	}
	
	public void testAddCommand() {
		// A B | C
		CommandHistory history = new CommandHistory(100);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		history.undo();

		// A B D |
		history.addCommand(cd);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cd, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(3, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, cd}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// A B E |
		history.undo();
		history.addCommand(ce);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(ce, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(3, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cb, ce}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}

		// A F G |
		history.undo();
		history.undo();
		history.addCommand(cf);
		history.addCommand(cg);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cg, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(3, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ca, cf, cg}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}

		// H |
		history.undo();
		history.undo();
		history.undo();
		history.addCommand(ch);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(ch, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ch}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// | H
		history.undo();
		assertFalse(history.canUndo());
		assertTrue(history.canRedo());
		assertNull(history.getUndoCommand());
		assertEquals(ch, history.getRedoCommand());
		assertEquals(0, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ch}), Arrays.asList(history.getCommands()));
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		// Validate events
		assertEquals(Arrays.asList(new CommandEvent[]{ucc, ucd, uce, ucb, ucg, ucf, uca, uch}), clistener.getEvents());
	}
	
	public void testExhaustCapacity3() {
		// stack of size 3
		// [A] B C D |
		CommandHistory history = new CommandHistory(3);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		history.addCommand(cd);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cd, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(3, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb, cc, cd}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// B | C D
		history.undo();
		history.undo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(cb, history.getUndoCommand());
		assertEquals(cc, history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb, cc, cd}), Arrays.asList(history.getCommands()));
		
		// | B C D
		history.undo();
		assertFalse(history.canUndo());
		assertTrue(history.canRedo());
		assertNull(history.getUndoCommand());
		assertEquals(cb, history.getRedoCommand());
		assertEquals(0, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb, cc, cd}), Arrays.asList(history.getCommands()));
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// E |
		history.addCommand(ce);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(ce, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ce}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}

		// Validate events
		assertEquals(Arrays.asList(new CommandEvent[]{ucd, ucc, ucb}), clistener.getEvents());
	}	
	
	public void testExhaustCapacity2() {
		// stack of size 2
		// [A] B C |
		CommandHistory history = new CommandHistory(2);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cc, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(2, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb, cc}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// B | C
		history.undo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(cb, history.getUndoCommand());
		assertEquals(cc, history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb, cc}), Arrays.asList(history.getCommands()));
		
		// | B C D
		history.undo();
		assertFalse(history.canUndo());
		assertTrue(history.canRedo());
		assertNull(history.getUndoCommand());
		assertEquals(cb, history.getRedoCommand());
		assertEquals(0, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb, cc}), Arrays.asList(history.getCommands()));
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// D |
		history.addCommand(cd);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cd, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cd}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}

		// Validate events
		assertEquals(Arrays.asList(new CommandEvent[]{ucc, ucb}), clistener.getEvents());
	}
	
	public void testExhaustCapacity1() {
		// stack of size 1
		// [A] B |
		CommandHistory history = new CommandHistory(1);
		history.addCommand(ca);
		history.addCommand(cb);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cb, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
				
		// | B 
		history.undo();
		assertFalse(history.canUndo());
		assertTrue(history.canRedo());
		assertNull(history.getUndoCommand());
		assertEquals(cb, history.getRedoCommand());
		assertEquals(0, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb}), Arrays.asList(history.getCommands()));
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// D |
		history.addCommand(cd);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(cd, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(1, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cd}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}

		// Validate events
		assertEquals(Arrays.asList(new CommandEvent[]{ucb}), clistener.getEvents());

	}

	public void testExhaustCapacity5() {
		// stack of size 5
		// [A] [B] [C] D E F G H |
		CommandHistory history = new CommandHistory(5);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		history.addCommand(cd);
		history.addCommand(ce);
		history.addCommand(cf);
		history.addCommand(cg);
		history.addCommand(ch);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(ch, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(5, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cd, ce, cf, cg, ch}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		
		// D E | F G H
		history.undo();
		history.undo();
		history.undo();
		// D E F | G H
		history.redo();
		// D E F I |
		history.addCommand(ci);
		assertTrue(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(ci, history.getUndoCommand());
		assertNull(history.getRedoCommand());
		assertEquals(4, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cd, ce, cf, ci}), Arrays.asList(history.getCommands()));
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		assertEquals(Arrays.asList(new CommandEvent[]{uch, ucg, ucf, rcf}), clistener.getEvents());
	}
	
	public void testPurge() {
		CommandHistory history = new CommandHistory(3);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		history.addCommand(cd);
		history.undo();
		history.undo();
		history.redo();
		history.purge();
		assertEquals(3, history.getCapacity());
		assertFalse(history.canUndo());
		assertFalse(history.canRedo());
		assertEquals(0, history.getCurrentCommand());
		assertFalse(history.needsSaving());
		try {
			history.undo();
			fail();
		} catch(IllegalStateException e) {
		}
		try {
			history.redo();
			fail();
		} catch(IllegalStateException e) {
		}
		assertNull(history.getUndoCommand());
		assertNull(history.getRedoCommand());
		ICommand[] commands = history.getCommands();
		assertNotNull(commands);
		assertEquals(0, commands.length);
		
		// E F | G
		history.addCommand(ce);
		history.addCommand(cf);
		history.addCommand(cg);
		history.undo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(cf, history.getUndoCommand());
		assertEquals(cg, history.getRedoCommand());
		assertEquals(2, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{ce, cf, cg}), Arrays.asList(history.getCommands()));
		assertEquals(Arrays.asList(new CommandEvent[]{ucd, ucc, rcc, ucg}), clistener.getEvents());
	}
	
	public void testSetCapacityGreater() {
		// [A] B C | D
		CommandHistory history = new CommandHistory(3);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		history.addCommand(cd);
		history.undo();
		history.undo();
		history.redo();
		
		// B C E |
		history.setCapacity(5);
		assertEquals(5, history.getCapacity());
		history.addCommand(ce);
		assertEquals(3, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cb, cc, ce}), Arrays.asList(history.getCommands()));
		
		// [B] C E F G | H
		history.addCommand(cf);		
		history.addCommand(cg);		
		history.addCommand(ch);
		history.undo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(cg, history.getUndoCommand());
		assertEquals(ch, history.getRedoCommand());
		assertEquals(4, history.getCurrentCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cc, ce, cf, cg, ch}), Arrays.asList(history.getCommands()));
		
		assertEquals(Arrays.asList(new CommandEvent[]{ucd, ucc, rcc, uch}), clistener.getEvents());

	}
	
	public void testSetCapacityLower() {
		// stack of size 5
		// [A] [B] [C] D E F | G H
		CommandHistory history = new CommandHistory(5);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		history.addCommand(cd);
		history.addCommand(ce);
		history.addCommand(cf);
		history.addCommand(cg);
		history.addCommand(ch);
		history.undo();
		history.undo();
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());
		assertEquals(cf, history.getUndoCommand());
		assertEquals(cg, history.getRedoCommand());
		
		// F | G H
		history.setCapacity(3);
		assertEquals(3, history.getCapacity());
		assertTrue(history.canUndo());
		assertTrue(history.canRedo());

		assertEquals(cf, history.getUndoCommand());
		assertEquals(cg, history.getRedoCommand());
		assertEquals(Arrays.asList(new TestCommand[]{cf, cg, ch}), Arrays.asList(history.getCommands()));
		assertEquals(1, history.getCurrentCommand());
	}
	
	public void testSetSavePoint() {
		// A B (C) | D E
		CommandHistory history = new CommandHistory(5);
		history.addCommand(ca);
		history.addCommand(cb);
		history.addCommand(cc);
		history.addCommand(cd);
		history.addCommand(ce);
		history.undo();
		history.undo();
		history.setSavePoint();
		assertFalse(history.needsSaving());
		history.undo();
		assertTrue(history.needsSaving());
		history.redo();
		assertFalse(history.needsSaving());
		history.redo();
		assertTrue(history.needsSaving());
		history.setSavePoint();
		assertFalse(history.needsSaving());
		history.undo();
		assertTrue(history.needsSaving());
		history.redo();
		assertFalse(history.needsSaving());
	}
	
	public void testEvents() {
		CommandHistory history = new CommandHistory(5);
		history.addCommandHistoryListener(hlistener);
		assertFalse(hlistener.hasFired());
		
		history.addCommand(ca);
		assertTrue(hlistener.hasFired());
		
		hlistener.reset();
		assertFalse(hlistener.hasFired());
		history.undo();
		assertTrue(hlistener.hasFired());

		hlistener.reset();
		history.redo();
		assertTrue(hlistener.hasFired());

		hlistener.reset();
		history.purge();
		assertTrue(hlistener.hasFired());

		hlistener.reset();
		history.setCapacity(10);
		assertTrue(hlistener.hasFired());
	}
}
