/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg-edu.
 * 
 * libgwtsvg-edu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg-edu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with libgwtsvg-edu.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edu.client.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGTSpanElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGPushButton;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.ui.SVGButtonBase.SVGFace;
import org.vectomatic.dom.svg.ui.SVGButtonBase.SVGFaceName;
import org.vectomatic.dom.svg.ui.SVGButtonBase.SVGStyleChange;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.dom.svg.utils.SVGPrefixResolver;
import org.vectomatic.svg.edu.client.commons.CommonBundle;
import org.vectomatic.svg.edu.client.commons.CommonConstants;
import org.vectomatic.svg.edu.client.commons.ConfirmBox;
import org.vectomatic.svg.edu.client.commons.LicenseBox;
import org.vectomatic.svg.edu.client.dots.DotsMain;
import org.vectomatic.svg.edu.client.maze.MazeMain;
import org.vectomatic.svg.edu.client.push.PushMain;
import org.vectomatic.svg.edu.client.puzzle.PuzzleMain;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main game menu
 * @author laaglu
 */
public class Menu implements EntryPoint {
	
	/**
	 * Utility class to make use of code splitting less verbose
	 */
	private static abstract class GameCallback implements RunAsyncCallback {
		 public void onFailure(Throwable reason) {
			final String debugParam = Window.Location.getParameter("debug");
			if (!GWT.isScript() || debugParam != null) {
				handleFatalError(reason);
			} else {
				Window.alert(CommonConstants.INSTANCE.loadError());
			}
		  }
		  public abstract void onSuccess();
	}
	
	/**
	 * Prefix resolver class. The prefix resolver is used by the XPath
	 * expression which injects the game title in the SVG images
	 */
	private static class GamePrefixResolver extends SVGPrefixResolver {
		public GamePrefixResolver() {
			prefixToUri.put("v", VECTOMATIC_NS_URI);
		}
	}
	private static final String VECTOMATIC_NS_URI = "http://www.vectomatic.org";
	private static GamePrefixResolver resolver = new GamePrefixResolver();
	private static MenuBundle bundle = MenuBundle.INSTANCE;
	private static MenuCss css = bundle.css();
	private static MenuConstants menuConstants = MenuConstants.INSTANCE;
	/**
	 * A dialog box to ask for confirmation before leaving the game
	 */
	private static DialogBox confirmBox;

	/**
	 * Enum to represent the available games and their characteristics
	 */
	enum Game {
		DOTS {
			@Override
			protected RunAsyncCallback getCallback() {
				return new GameCallback() {
			        public void onSuccess() {
						RootPanel root = RootPanel.get(CommonConstants.ID_UIROOT);
						root.remove(root.getWidget(0));
						DotsMain main = new DotsMain(Game.createHomeButton());
		    			main.onModuleLoad();
			        }
				};
			}
			@Override
			public SVGImage getImage() {
				return createImage(
						bundle.connectdots(), 
						menuConstants.connectDotsTitle(), 
						getCallback());
			}
			@Override
			public Label getRule() {
				return createLabel(menuConstants.connectDotsRule());
			}
		},
		MAZE {
			@Override
			protected RunAsyncCallback getCallback() {
				return new GameCallback() {
			        public void onSuccess() {
						RootPanel root = RootPanel.get(CommonConstants.ID_UIROOT);
						root.remove(root.getWidget(0));
			        	MazeMain main = new MazeMain(Game.createHomeButton());
			    		main.onModuleLoad();
			        }
				};
			}
			@Override
			public SVGImage getImage() {
				return createImage(
						bundle.maze(), 
						menuConstants.mazeTitle(), 
						getCallback());
			}
			@Override
			public Label getRule() {
				return createLabel(menuConstants.mazeRule());
			}
		},
		PUSH {
			@Override
			protected RunAsyncCallback getCallback() {
				return new GameCallback() {
			        public void onSuccess() {
						RootPanel root = RootPanel.get(CommonConstants.ID_UIROOT);
						root.remove(root.getWidget(0));
			        	PushMain main = new PushMain(Game.createHomeButton());
			    		main.onModuleLoad();
			        }
				};
			}
			@Override
			public SVGImage getImage() {
				return createImage(
						bundle.push(), 
						menuConstants.pushTitle(), 
						getCallback());
			}
			@Override
			public Label getRule() {
				return createLabel(menuConstants.pushRule());
			}
		},
		PUZZLE {
			@Override
			protected RunAsyncCallback getCallback() {
				return new GameCallback() {
			        public void onSuccess() {
						RootPanel root = RootPanel.get(CommonConstants.ID_UIROOT);
						root.remove(root.getWidget(0));
			        	PuzzleMain main = new PuzzleMain(Game.createHomeButton());
			    		main.onModuleLoad();
			        }
				};
			}
			@Override
			public SVGImage getImage() {
				return createImage(
						bundle.puzzle(), 
						menuConstants.puzzleTitle(), 
						getCallback());
			}
			@Override
			public Label getRule() {
				return createLabel(menuConstants.puzzleRule());
			}
		};
		public abstract SVGImage getImage();
		public abstract Label getRule();
		protected abstract RunAsyncCallback getCallback();
		private static SVGImage createImage(
				SVGResource gameLogo,
				String gameTitle,
				final RunAsyncCallback callback) {
			OMSVGDocument document = OMSVGParser.currentDocument();
			final SVGImage svgImage = new SVGImage(gameLogo);
			svgImage.setStyleName(css.gameLogo());
			svgImage.getElement().getStyle().setBorderColor(SVGConstants.CSS_WHITE_VALUE);
			svgImage.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					svgImage.getElement().getStyle().setBorderColor(SVGConstants.CSS_BLUE_VALUE);
				}
			});
			svgImage.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					svgImage.getElement().getStyle().setBorderColor(SVGConstants.CSS_WHITE_VALUE);
				}
			});
			svgImage.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					svgImage.getElement().getStyle().setBorderColor(SVGConstants.CSS_WHITE_VALUE);
			        GWT.runAsync(callback);
				}
				
			});
			Iterator<OMSVGTSpanElement> iterator = DOMHelper.evaluateXPath(svgImage.getSvgElement(), ".//svg:tspan[@v:title]", resolver);
			iterator.next().appendChild(document.createTextNode(gameTitle));
			return svgImage;
		}
		private static Label createLabel(String gameRule) {
			Label ruleLabel = new Label(gameRule);
			ruleLabel.setStyleName(css.gameRule());
			return ruleLabel;
		}
		private static SVGPushButton createHomeButton() {
			Map<SVGFaceName, SVGFace> faces = new HashMap<SVGFaceName, SVGFace>();
			faces.put(SVGFaceName.UP, new SVGFace(new SVGStyleChange[] { new SVGStyleChange( new String[] { CommonBundle.INSTANCE.css().navigationUp()} )}));
			faces.put(SVGFaceName.UP_HOVERING, new SVGFace(new SVGStyleChange[] { new SVGStyleChange( new String[] { CommonBundle.INSTANCE.css().navigationUpHovering()} )}));
			SVGPushButton homeButton = new SVGPushButton(bundle.home().getSvg(), faces);
			homeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
			        confirmBox.center();
			        confirmBox.show();
				}
			});
			return homeButton;
		}
	};
	
	private static void handleFatalError(Throwable throwable) {
		final String debugParam = Window.Location.getParameter("debug");
		if (!GWT.isScript() || debugParam != null) {
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
		} else {
			GWT.log("Fatal error:", throwable);
		}
	}
		
	@Override
    public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable throwable) {
				handleFatalError(throwable);
			}
		});                                                              

        // use a deferred command so that the handler catches onModuleLoad2()                                                                             
        // exceptions                                                    
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				onModuleLoad2();
			}
		});                                                              
	} 
	
	public void onModuleLoad2() {
		MenuBundle.INSTANCE.css().ensureInjected();
		
		final FlexTable table = new FlexTable();
		table.setBorderWidth(0);
		table.setCellSpacing(5);
		table.setStyleName(css.gameTable());
		
		table.setWidget(0, 0, Game.DOTS.getImage());
		table.setWidget(1, 0, Game.DOTS.getRule());
		table.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

		table.setWidget(0, 1, Game.MAZE.getImage());
		table.setWidget(1, 1, Game.MAZE.getRule());
		table.getCellFormatter().setAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

		table.setWidget(2, 0, Game.PUSH.getImage());
		table.setWidget(3, 0, Game.PUSH.getRule());
		table.getCellFormatter().setAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

		table.setWidget(2, 1, Game.PUZZLE.getImage());
		table.setWidget(3, 1, Game.PUZZLE.getRule());
		table.getCellFormatter().setAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setAlignment(3, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

		final LicenseBox licenseBox = new LicenseBox();
		Anchor licenseAnchor = new Anchor();
		licenseAnchor.setText(CommonConstants.INSTANCE.license());
		licenseAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				licenseBox.getBox().center();
				licenseBox.getBox().show();
			}
			
		});
		table.setWidget(4, 0, licenseAnchor);
		table.getFlexCellFormatter().setColSpan(4, 0, 2);
		table.getCellFormatter().setAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		ResizeHandler resizeHandler = new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				float w = Window.getClientWidth() * 0.45f;
				float h = Window.getClientHeight() * 0.3f;
				for (int i = 0; i < 4; i += 2) {
					for (int j = 0; j < 2; j++) {
						SVGImage svgImage = (SVGImage) table.getWidget(i, j);
						svgImage.getSvgElement().getStyle().setSVGProperty("width", Float.toString(w));
						svgImage.getSvgElement().getStyle().setSVGProperty("height", Float.toString(h));
					}
				}
				GWT.log(w + "x" + h);
			}
			
		};
		Window.addResizeHandler(resizeHandler);
		resizeHandler.onResize(null);
		RootPanel.get(CommonConstants.ID_UIROOT).add(table);
		confirmBox = ConfirmBox.createConfirmBox(table);
		
		String gameParam = Window.Location.getParameter("game");
		Game game = null;
		if (gameParam != null) {
			try {
				game = Game.valueOf(gameParam.toUpperCase());
			} catch (IllegalArgumentException e) {
			}
		}
		if (game != null) {
	        GWT.runAsync(game.getCallback());
		}
	}
}
