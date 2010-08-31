package org.vectomatic.svg.edu.client.commons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;

public class Utils {
	public static boolean isDebug() {
		String debugParam = Window.Location.getParameter("debug");
		return (!GWT.isScript() || debugParam != null);
	}
	public static void handleFatalError(Throwable throwable) {
		String text = "Uncaught exception: ";
		GWT.log(text, throwable);
		while (throwable != null) {
			StackTraceElement[] stackTraceElements = throwable
					.getStackTrace();
			text += throwable.toString() + "\n";
			for (int i = 0; i < stackTraceElements.length; i++) {
				text += "    at " + stackTraceElements[i] + "\n";
			}
			throwable = throwable.getCause();
			if (throwable != null) {
				text += "Caused by: ";
			}
		}
		DialogBox dialogBox = new DialogBox(true);
		DOM.setStyleAttribute(dialogBox.getElement(),
				"backgroundColor", "#ABCDEF");
		System.err.print(text);
		text = text.replaceAll(" ", "&nbsp;");
		dialogBox.setHTML("<pre>" + text + "</pre>");
		dialogBox.center();
	}

}
