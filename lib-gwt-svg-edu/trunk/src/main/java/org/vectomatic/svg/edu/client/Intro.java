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
package org.vectomatic.svg.edu.client;

import org.apache.bcel.generic.GETSTATIC;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGTSpanElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.utils.OMSVGParser;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
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
public class Intro implements EntryPoint {
	public static final String ID_UIROOT = "uiRoot";
	
	private static abstract class GameCallback implements RunAsyncCallback {
		 public void onFailure(Throwable reason) {
              Window.alert(EduConstants.INSTANCE.loadError());
		  }
		  public abstract void onSuccess();
	}
	
	RunAsyncCallback connectDotsCallback = new GameCallback() {
        public void onSuccess() {
        	DotsMain main = new DotsMain();
    		main.onModuleLoad2();
        }
	};
	RunAsyncCallback mazeCallback = new GameCallback() {
        public void onSuccess() {
        	MazeMain main = new MazeMain();
    		main.onModuleLoad2();
        }
	};
	RunAsyncCallback pushCallback = new GameCallback() {
        public void onSuccess() {
        	PushMain main = new PushMain();
    		main.onModuleLoad2();
        }
	};
	RunAsyncCallback puzzleCallback = new GameCallback() {
        public void onSuccess() {
        	PuzzleMain main = new PuzzleMain();
    		main.onModuleLoad2();
        }
	};

	void createGame(FlexTable table,
			int row, int col,
			final RunAsyncCallback callback, 
			SVGResource gameLogo,
			String gameTitleId,
			String gameTitleValue,
			String gameRule) {
		OMSVGDocument document = OMSVGParser.currentDocument();
		final SVGImage svgImage = new SVGImage(gameLogo);
		svgImage.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				svgImage.getElement().setClassName(CommonBundle.INSTANCE.css().gameLogoSelected());
			}
		});
		svgImage.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				svgImage.getElement().setClassName(CommonBundle.INSTANCE.css().gameLogo());
			}
		});
		svgImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GWT.log("click");
				RootPanel root = RootPanel.get(ID_UIROOT);
				root.remove(root.getWidget(0));
		        GWT.runAsync(callback);
			}
			
		});
		table.setWidget(row * 2, col, svgImage);
		
		Label ruleLabel = new Label(gameRule);
		ruleLabel.setStyleName(CommonBundle.INSTANCE.css().gameRule());
		
		table.setWidget(1 + row * 2, col, ruleLabel);
		
		table.getCellFormatter().setWidth(row * 2, col, "50%");
		table.getCellFormatter().setWidth(1 + row * 2, col, "50%");

		table.getCellFormatter().setAlignment(row * 2, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setAlignment(1 + row * 2, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

		OMSVGTSpanElement tspan = document.getElementById(gameTitleId);
		tspan.appendChild(document.createTextNode(gameTitleValue));
	}
	
	@Override
    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {                                                                              
                public void onUncaughtException(Throwable throwable) {   
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
		CommonBundle.INSTANCE.css().ensureInjected();
		FlexTable table = new FlexTable() {
			@Override
			protected void onLoad() {
				super.onLoad();
				createGame(
						this, 
						0, 0, 
						connectDotsCallback, 
						CommonBundle.INSTANCE.connectdots(),
						"connectDotsTitle", 
						EduConstants.INSTANCE.connectDotsTitle(), 
						EduConstants.INSTANCE.connectDotsRule());
				createGame(
						this, 
						0, 1, 
						mazeCallback, 
						CommonBundle.INSTANCE.maze(),
						"mazeTitle", 
						EduConstants.INSTANCE.mazeTitle(), 
						EduConstants.INSTANCE.mazeRule());
				createGame(
						this, 
						1, 0, 
						pushCallback, 
						CommonBundle.INSTANCE.push(),
						"pushTitle", 
						EduConstants.INSTANCE.pushTitle(), 
						EduConstants.INSTANCE.pushRule());
				createGame(
						this, 
						1, 1, 
						puzzleCallback, 
						CommonBundle.INSTANCE.puzzle(),
						"puzzleTitle", 
						EduConstants.INSTANCE.puzzleTitle(), 
						EduConstants.INSTANCE.puzzleRule());

				final LicenseBox licenseBox = new LicenseBox();
				Anchor licenseAnchor = new Anchor();
				licenseAnchor.setText(EduConstants.INSTANCE.license());
				licenseAnchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						licenseBox.box.center();
						licenseBox.box.show();
					}
					
				});
				int row = getRowCount();
				setWidget(row, 0, licenseAnchor);
				getFlexCellFormatter().setColSpan(row, 0, 2);
				getCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
			}
		};
		table.setBorderWidth(0);
		table.setCellSpacing(5);
		table.setWidth("70%");
		table.setHeight("100%");
		FlexTable table2 = new FlexTable();
		table2.setWidth("100%");
		table2.setHeight("100%");
		table2.setWidget(0, 0, table);
		table2.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		RootPanel.get("uiRoot").add(table2);
	}

}
