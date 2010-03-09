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
package org.vectomatic.client.rep.command;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.events.ICommandHistoryListener;


/**
 * Class to represent an undo/redo stack of commands with a fixed depth.
 * The oldest command is automatically removed when the stack capacity
 * is exhausted.
 * @author Lukas Laag
 */
public class CommandHistory {
	/**
	 * A circular array to store the commands
	 */
	private ICommand[] _commands;
	
	/**
	 * The slot in the array occupied by the least recent command
	 * Can vary between 0 and K-1 where K is the array capacity
	 */
	private int _first;

	/**
	 * The slot in the array occupied by the most recent command
	 * Can vary between 0 and K-1 where K is the array capacity
	 */
	private int _last;

	/**
	 * The current command
	 * Can vary between 0 and N where N is the number of commands
	 * in the stack. 0 means before the least recent command (redo
	 * is possible, undo is impossible). N means after the most recent
	 * command (redo is impossible, undo is possible)
	 */
	private int _current;
	
	/**
	 * A list of CommandHistoryListener
	 */
	private List<ICommandHistoryListener> _listeners;
	
	/**
	 * The command at which the last save occurred
	 */
	private ICommand _savePoint;

	/**
	 * Constructor
	 * @param capacity
	 * The depth of the undo stack
	 */
	public CommandHistory(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException();
		}
		_commands = new ICommand[capacity];
		_first = -1;
	}
	
	/**
	 * Returns true if the command stack contains a command which can be 
	 * undone false otherwise.
	 * @return true if the command stack contains a command which can be 
	 * undone false otherwise.
	 */
	public boolean canUndo() {
		return (_current > 0);
	}
	
	/**
	 * Returns true if the command stack contains a command which can be 
	 * redone false otherwise.
	 * @return true if the command stack contains a command which can be 
	 * redone false otherwise.
	 */
	public boolean canRedo() {
		if (_first == -1) {
			// Degenerate case: no command in the stack yet
			return false;
		}
		int count = (_last >= _first) ? _last - _first + 1 : _commands.length - _first + _last + 1;
		return (_current < count);
	}
	
	/**
	 * Undoes the current command
	 */
	public void undo() {
		if (!canUndo()) {
			throw new IllegalStateException("Invalid undo");
		}
		getUndoCommand().unexecute();
		_current--;
		fireCommandHistoryChange();
	}
	
	/**
	 * Redoes the previously undone command
	 */
	public void redo() {
		if (!canRedo()) {
			throw new IllegalStateException("Invalid redo");
		}
		getRedoCommand().execute();
		_current++;
		fireCommandHistoryChange();
	}
	
	/**
	 * Adds a new command to the stack
	 * @param command the command to add
	 */
	public void addCommand(ICommand command) {
		if (_first == -1) {
			// Degenerate case: no command in the stack yet
			_first = 0;
		}
		
		// Remove references to redoable commands
		int count = (_last >= _first) ? _last - _first + 1 : _commands.length - _first + _last + 1;
		for (int i = _current + 1; i < count; i++) {
			int index = (_first + i) % _commands.length;
			_commands[index] = null;
		}
		
		// Add new command
		_last = (_current + _first) % _commands.length;
		_commands[_last] = command;
		
		// Capacity exhausted: erase oldest command
		if (_current == _commands.length) {
			_first = (_first == _commands.length - 1) ? 0 : _first + 1;
		} else {
			// Update current command
			_current++;
		}
		fireCommandHistoryChange();
	}
	
	/**
	 * Returns the command which will be undone if undo is invoked
	 * @return
	 */
	public ICommand getUndoCommand() {
		if (!canUndo()) {
			return null;
		}
		int index = (_first + _current - 1) % _commands.length;
		return _commands[index];
	}
	
	/**
	 * Returns the command which will be redone if undo is invoked
	 * @return
	 */
	public ICommand getRedoCommand() {
		if (!canRedo()) {
			return null;
		}
		int index = (_first + _current) % _commands.length;
		return _commands[index];
	}
	
	/**
	 * Gets all the commands currently in the stack
	 * @return
	 */
	public ICommand[] getCommands() {	
		if (_first == -1) {
			// Degenerate case: no command in the stack yet
			return new ICommand[0];
		}
		int count = (_last >= _first) ? _last - _first + 1 : _commands.length - _first + _last + 1;
		ICommand[] commands = new ICommand[count];
		for (int i = 0; i < count; i++) {
			int index = (_first + i) % _commands.length;
			commands[i] = _commands[index];
		}
		return commands;
	}
	
	/**
	 * Returns the index of the current command
	 * in the array returned by getCommands().
	 * It can vary between 0 and N where N is the number of commands
	 * in the stack. 0 means before the least recent command (redo
	 * is possible, undo is impossible). N means after the most recent
	 * command (redo is impossible, undo is possible)
	 * @return
	 */
	public int getCurrentCommand() {
		return _current;
	}
	
	/**
	 * Clears the stack
	 * @return
	 */
	public void purge() {
		_current = _last = 0;
		_first = -1;
		for (int i = 0; i < _commands.length; i++) {
			_commands[i] = null;
		}
		_savePoint = null;
		fireCommandHistoryChange();
	}
	
	/**
	 * Specifies a new capacity for the stack
	 * If the new capacity is smaller than the previous
	 * one, a proportional window of commands around
	 * the current command will be kept.
	 * @param capacity
	 */
	public void setCapacity(int capacity) {
		if (_first == -1) {
			// Degenerate case: no command in the stack yet
			_commands = new ICommand[capacity];
			fireCommandHistoryChange();
			return;
		}		
		int count = (_last >= _first) ? _last - _first + 1 : _commands.length - _first + _last + 1;
		if (capacity < count) {
			int current = _current * capacity / count;
			ICommand[] commands = new ICommand[capacity];
			for (int i = 0; i < capacity; i++) {
				int index = (_first + _current - current + i) % _commands.length;
				commands[i] = _commands[index];
			}
			_commands = commands;
			_first = 0;
			_current = current;
			_last = capacity - 1;
			fireCommandHistoryChange();
		} else if (capacity > count) {
			ICommand[] commands = new ICommand[capacity];
			for (int i = 0; i < count; i++) {
				int index = (_first + i) % _commands.length;
				commands[i] = _commands[index];
			}
			_commands = commands;
			_first = 0;
			_last = count - 1;
			fireCommandHistoryChange();
		}
	}
	
	/**
	 * Returns the stack capacity
	 * @return
	 */
	public int getCapacity() {
		return _commands.length;
	}

	public void addCommandHistoryListener(ICommandHistoryListener listener) {
		if (_listeners == null) {
			_listeners = new ArrayList<ICommandHistoryListener>();
		}
		_listeners.add(listener);
	}

	public void removeCommandHistoryListener(ICommandHistoryListener listener) {
		if (_listeners != null) {
			_listeners.remove(listener);
		}
	}
	
	public void fireCommandHistoryChange() {
		if (_listeners != null) {
			for (int i = 0, size = _listeners.size(); i < size; i++) {
				ICommandHistoryListener listener = _listeners.get(i);
				listener.commandHistoryChange(this);
			}
		}
	}

	public void setSavePoint() {
		_savePoint = (canUndo()) ? getUndoCommand() : null;
		
	}

	public boolean needsSaving() {
		ICommand command = (canUndo()) ? getUndoCommand() : null;
		return (command  != _savePoint);
	}

}

