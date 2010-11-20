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
package org.vectomatic.svg.edit.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.engine.SVGModel;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Application main class.
 * @author laaglu
 */
public class VectomaticApp2 implements EntryPoint {
	static VectomaticApp2 APP;
	private List<SVGWindow> windows;
	private SVGWindow activeWindow;
	private int windowX, windowY;
	private Viewport viewPort;
	private MenuBar menuBar;
	private Menu recentDocsMenu;
	private MenuItem resetViewItem;
	private MenuItem tileWindowsItem;
	private MenuItem stackWindowsItem;
	private MenuItem closeWindowItem;
	private SelectionListener<MenuEvent> dispatcher;
	private MessageBox openUrlBox;
	private RSSReader rssReader;
	private AboutDialog aboutDialog;
	
	public void onModuleLoad() {
		APP = this;
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable throwable) {
				GWT.log("Uncaught exception", throwable);
				if (!GWT.isScript()) {
					String text = "Uncaught exception: ";
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
		});
		AppBundle.INSTANCE.css().ensureInjected();
		windows = new ArrayList<SVGWindow>();
		viewPort = new Viewport();
		
		Menu fileMenu = new Menu();
		final MenuItem openUrlItem = new MenuItem(AppConstants.INSTANCE.openUrlMenuItem());
		fileMenu.add(openUrlItem);
		final MenuItem openRssFeedItem = new MenuItem(AppConstants.INSTANCE.openRssFeedMenuItem());
		fileMenu.add(openRssFeedItem);
		MenuItem recentDocumentsItem = new MenuItem(AppConstants.INSTANCE.recentDocumentsMenuItem());
		recentDocsMenu = new Menu();
		recentDocumentsItem.setSubMenu(recentDocsMenu);
		fileMenu.add(recentDocumentsItem);

		Menu windowMenu = new Menu();
		resetViewItem = new MenuItem(AppConstants.INSTANCE.resetViewMenuItem());
		windowMenu.add(resetViewItem);
		windowMenu.add(new SeparatorMenuItem());
		tileWindowsItem = new MenuItem(AppConstants.INSTANCE.tileWindowsMenuItem());
		windowMenu.add(tileWindowsItem);
		stackWindowsItem = new MenuItem(AppConstants.INSTANCE.stackWindowsMenuItem());
		windowMenu.add(stackWindowsItem);
		windowMenu.add(new SeparatorMenuItem());
		closeWindowItem = new MenuItem(AppConstants.INSTANCE.closeWindowMenuItem());
		windowMenu.add(closeWindowItem);
		
		Menu aboutMenu = new Menu();
		final MenuItem aboutItem = new MenuItem(AppConstants.INSTANCE.aboutMenuItem());
		aboutMenu.add(aboutItem);

		menuBar = new MenuBar();
		menuBar.setBorders(true);  
		menuBar.setStyleAttribute("borderTop", "none");
		menuBar.add(new MenuBarItem(AppConstants.INSTANCE.fileMenu(), fileMenu));
		menuBar.add(new MenuBarItem(AppConstants.INSTANCE.windowMenu(), windowMenu));
		menuBar.add(new MenuBarItem(AppConstants.INSTANCE.aboutMenu(), aboutMenu));
		
		dispatcher = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				MenuItem item = (MenuItem) me.getItem();
				if (item.getParentMenu() == recentDocsMenu) {
					openRecent(item.getText());
				} else if (item == openUrlItem) {
					openUrl();
				} else if (item == openRssFeedItem) {
					openRssFeed();
				} else if (item == resetViewItem) {
					resetView();
				} else if (item == tileWindowsItem) {
					tileWindows();
				} else if (item == stackWindowsItem) {
					stackWindows();
				} else if (item == closeWindowItem) {
					closeWindow(activeWindow);
				} else if (item == aboutItem) {
					about();
				}
			}
		};
		openUrlItem.addSelectionListener(dispatcher);
		openRssFeedItem.addSelectionListener(dispatcher);
		resetViewItem.addSelectionListener(dispatcher);
		tileWindowsItem.addSelectionListener(dispatcher);
		stackWindowsItem.addSelectionListener(dispatcher);
		closeWindowItem.addSelectionListener(dispatcher);
		aboutItem.addSelectionListener(dispatcher);
		
		viewPort.add(menuBar);
		addWindow(AppBundle.INSTANCE.fish().getSvg(), "fish.svg");
		addWindow(AppBundle.INSTANCE.fries().getSvg(), "fries.svg");
		viewPort.setStyleAttribute("background-color", SVGConstants.CSS_BEIGE_VALUE);

		update();
		RootPanel.get().add(viewPort);
	}
	
	public SVGWindow addWindow(OMSVGSVGElement svg, String title) {
		SVGModel model = GWT.create(SVGModel.class);;
		model.setSvgElement(svg);
		SVGWindow window = new SVGWindow(model);
		window.setHeading(title);
		windows.add(window);
		// To be notified when a window is activated in order to
		// keep track of the active window 
		window.addListener(Events.Activate, new Listener<WindowEvent>() {

			@Override
			public void handleEvent(WindowEvent we) {
				activeWindow = (SVGWindow) we.getWindow();
			}
		});
		window.addListener(Events.BeforeHide, new Listener<WindowEvent>() {

			@Override
			public void handleEvent(WindowEvent we) {
				closeWindow((SVGWindow) we.getWindow());
			}
		});

		// Update the recent docs menu
		List<Component> recentDocs = recentDocsMenu.getItems();
		boolean alreadyInRecentDocs = false;
		for (Component item : recentDocs) {
			MenuItem menuItem = (MenuItem)item;
			if (title.equals(menuItem.getText())) {
				alreadyInRecentDocs = true;
				break;
			}
		}
		if (!alreadyInRecentDocs) {
			if (recentDocs.size() >= 8) {
				Component oldestItem = recentDocs.get(0);
				oldestItem.removeAllListeners();
				recentDocsMenu.remove(oldestItem);
			}
			MenuItem windowItem = new MenuItem(title);
			windowItem.addSelectionListener(dispatcher);
			recentDocsMenu.add(windowItem);
		}

		
		int windowBarHeight = getWindowBarHeight();
		windowY += windowBarHeight;
		window.setPagePosition(windowX, windowY);
		windowX += windowBarHeight;
//		window.show();
		viewPort.add(window);
		window.setVisible(true);
		update();
		return window;
	}

	public void openUrl() {
		GWT.log("openUrl()");
		openUrlBox = MessageBox.prompt(AppConstants.INSTANCE.openUrlMenuItem(), AppConstants.INSTANCE.openUrlText());
		openUrlBox.addCallback(new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent be) {
				load(be.getValue());
			}  
		});  
	}
	
	public void info(String command, String message) {
		Info.display(command, message);  
	}
	
	public void load(final String url) {
		OMSVGSVGElement svg = null;
		if ("fish.svg".equals(url)) {
			svg = AppBundle.INSTANCE.fish().getSvg();
			addWindow(svg, url);
		} else if ("fries.svg".equals(url)) {
			svg = AppBundle.INSTANCE.fries().getSvg();
			addWindow(svg, url);
		} else {
			String resourceUrl = GWT.getHostPageBaseURL() + "fetch?url=" + url + "&type=text/xml";
			RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, resourceUrl);
			requestBuilder.setCallback(new RequestCallback() {
				public void onError(Request request, Throwable e) {
					GWT.log("Cannot fetch " + url, e);
					info(AppConstants.INSTANCE.openUrlMenuItem(), AppMessages.INSTANCE.loadErrorMessage(url, e.getMessage()));
				}

				private void onSuccess(Request request, Response response) {
					OMSVGSVGElement svg = OMSVGParser.parse(response.getText());
					addWindow(svg, url);
				}
				
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == Response.SC_OK) {
						onSuccess(request, response);
					} else {
						onError(request, new IOException(AppMessages.INSTANCE.httpErrorMessage(Integer.toString(response.getStatusCode()))));
					}
				}
			});
			try {
				requestBuilder.send();
			} catch (RequestException e) {
				GWT.log("Cannot fetch " + url, e);
				info(AppConstants.INSTANCE.openUrlMenuItem(), AppMessages.INSTANCE.loadErrorMessage(url, e.getMessage()));
			}
		}
	}
	
	public void openRecent(String url) {
		GWT.log("openRecent(" + url + ")");
		load(url);
	}
	
	public void openRssFeed() {
		GWT.log("openRssFeed()");
		if (rssReader == null) {
			rssReader = new RSSReader();
		}
		rssReader.show();
	}
	
	public void resetView() {
		GWT.log("resetView()");
		activeWindow.setRotationCompass(0);
		activeWindow.setScaleSlider(50);
	}
	public void tileWindows() {
		GWT.log("tileWindows()");
		Rectangle rect = getRectangle();
		int count = windows.size();
		int cols = (int)Math.ceil(Math.sqrt(count));
		int rows = (int)Math.ceil((double)count/cols);
		GWT.log("cols=" + cols + "; rows=" + rows);
		Size windowSize = new Size(rect.width / cols, rect.height / rows);
		int index = 0;
		for (SVGWindow window : windows) {
			window.setSize(windowSize.width, windowSize.height);
			int x = index % cols;
			int y = index / cols;
			window.setPagePosition(rect.x + x * windowSize.width, rect.y + y * windowSize.height);
			index++;
		}
	}
	public void stackWindows() {
		GWT.log("stackWindows()");
		Rectangle rect = getRectangle();
		Size size = viewPort.getSize();
		Size windowSize = new Size((int)(size.width * 0.75f), (int)(size.height * 0.75f));
		int windowBarHeight = getWindowBarHeight();
		int index = 0;
		for (SVGWindow window : windows) {
			window.setSize(windowSize.width, windowSize.height);
			int x = rect.x + index * windowBarHeight;
			int y = rect.y + index * windowBarHeight;
			window.setPagePosition(x, y);
			index++;
			if (y + windowSize.height > size.height) {
				x = rect.x;
				y = rect.y;
				index = 0;
			}
		}
	}
	public void closeWindow(SVGWindow window) {
		GWT.log("closeWindow()");
		if (window != null) {
			window.removeAllListeners();
			windows.remove(window);
			activeWindow = null;
			window.hide();
			update();
		}
	}
	public void about() {
		GWT.log("about()");
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog();
		}
		aboutDialog.show();
	}
	public Rectangle getRectangle() {
		int windowBarHeight = getWindowBarHeight();
		Size viewPortSize = viewPort.getSize();
		Rectangle rect = new Rectangle(0, windowBarHeight, viewPortSize.width, viewPortSize.height - windowBarHeight);
		return rect;
	}
	public static final int getWindowBarHeight() {
		return 22;
	}
	
	private void update() {
		closeWindowItem.setEnabled(windows.size() > 0);
		tileWindowsItem.setEnabled(windows.size() > 0);
		stackWindowsItem.setEnabled(windows.size() > 0);
		resetViewItem.setEnabled(windows.size() > 0);
	}
	
    public static final native void log(String msg) /*-{
	    console.log(msg);
	}-*/;

}
