/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vectomatic.dom.svg.impl;

import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.ExternalSVGResource;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;

/**
 * Implementation of ExternalSVGResource derived from Google's original
 * ExternalTextResourcePrototype implementation
 * @author laaglu
 */
public class ExternalSVGResourcePrototype implements ExternalSVGResource {

	/**
	 * Maps the HTTP callback onto the ResourceCallback.
	 */
	private class ESRCallback implements RequestCallback {
		final ResourceCallback<SVGResource> callback;

		public ESRCallback(ResourceCallback<SVGResource> callback) {
			this.callback = callback;
		}

		public void onError(Request request, Throwable exception) {
			callback.onError(new ResourceException(
					ExternalSVGResourcePrototype.this,
					"Unable to retrieve external resource", exception));
		}

		public void onResponseReceived(Request request, final Response response) {
			// Get the contents of the JSON bundle
			String responseText = response.getText();

			// Call eval() on the object.
			JavaScriptObject jso = evalObject(responseText);
			if (jso == null) {
				callback.onError(new ResourceException(
						ExternalSVGResourcePrototype.this,
						"eval() returned null"));
				return;
			}

			// Populate the TextResponse cache array
			for (int i = 0; i < cache.length; i++) {
				final String resourceText = extractString(jso, i);
				cache[i] = new SVGResource() {

					public String getName() {
						return name;
					}

					public OMSVGSVGElement getSvg() {
						return OMSVGParser.parse(resourceText);
					}

				};
			}

			// Finish by invoking the callback
			callback.onSuccess(cache[index]);
		}
	}

	/**
	 * Evaluate the JSON payload. The regular expression to validate the safety
	 * of the payload is taken from RFC 4627 (D. Crockford).
	 * 
	 * @param data
	 *            the raw JSON-encapsulated string bundle
	 * @return the evaluated JSON object, or <code>null</code> if there is an
	 *         error.
	 */
	private static native JavaScriptObject evalObject(String data) /*-{
	    var safe = !(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(
	      data.replace(/"(\\.|[^"\\])*"/g, '')));

	    if (!safe) {
	      return null;
	    }

	    return eval('(' + data + ')') || null;
	  }-*/;

	/**
	 * Extract the specified String from a JavaScriptObject that is array-like.
	 * @param jso
	 * the JavaScriptObject returned from {@link #evalObject(String)}
	 * @param index
	 * the index of the string to extract
	 * @return the requested string, or <code>null</code> if it does not exist.
	 */
	private static native String extractString(JavaScriptObject jso, int index) /*-{
	    return (jso.length > index) && jso[index] || null;
	  }-*/;

	/**
	 * This is a reference to an array nominally created in the IRB that
	 * contains the ExternalSVGResource. It is intended to be shared between
	 * all instances of the ETR that have a common parent IRB.
	 */
	private final SVGResource[] cache;
	private final int index;
	private final String name;
	private final String url;

	/**
	 * Constructor
	 * @param name The resource name
	 * @param url The resource URL
	 * @param cache A cache of loaded resource
	 * @param index The index for this resource in the cache
	 */
	public ExternalSVGResourcePrototype(String name, String url,
			SVGResource[] cache, int index) {
		this.name = name;
		this.url = url;
		this.cache = cache;
		this.index = index;
	}

	/**
	 * Returns the SVG resource name
	 * @return the SVG resource name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Possibly fire off an HTTPRequest for the SVG resource.
	 * @param callback The request callback
	 */
	public void getSvg(ResourceCallback<SVGResource> callback)
			throws ResourceException {

		// If we've already parsed the JSON bundle, short-circuit.
		if (cache[index] != null) {
			callback.onSuccess(cache[index]);
			return;
		}

		// Otherwise, fire an HTTP request.
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		try {
			rb.sendRequest("", new ESRCallback(callback));
		} catch (RequestException e) {
			throw new ResourceException(this,
					"Unable to initiate request for external resource", e);
		}
	}
}
