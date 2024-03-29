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
package org.vectomatic.dev.svg.impl.gen;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.vectomatic.dom.svg.impl.ExternalSVGResourcePrototype;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.ui.ExternalSVGResource.Validated;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.util.Util;
import com.google.gwt.resources.ext.AbstractResourceGenerator;
import com.google.gwt.resources.ext.ClientBundleFields;
import com.google.gwt.resources.ext.ClientBundleRequirements;
import com.google.gwt.resources.ext.ResourceContext;
import com.google.gwt.resources.ext.ResourceGeneratorUtil;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.StringSourceWriter;

/**
 * Implementation of ExternalSVGResource derived from Google's original
 * ExternalSVGResourceGenerator implementation
 * @author laaglu
 */
public class ExternalSVGResourceGenerator extends AbstractResourceGenerator {
	private StringBuffer data;
	private boolean first;
	private String urlExpression;
	private Map<String, Integer> hashes;
	private Map<String, Integer> offsets;
	private int currentIndex;

	private String externalSVGUrlIdent;

	private String externalSVGCacheIdent;

	@Override
	public String createAssignment(TreeLogger logger, ResourceContext context,
			JMethod method) throws UnableToCompleteException {
		String name = method.getName();

		SourceWriter sw = new StringSourceWriter();
		sw
				.println("new " + ExternalSVGResourcePrototype.class.getName()
						+ "(");
		sw.indent();
		sw.println('"' + name + "\",");
		// These are field names
		sw.println(externalSVGUrlIdent + ", " + externalSVGCacheIdent + ", ");
		sw.println(offsets.get(method.getName()).toString());
		sw.outdent();
		sw.print(")");

		return sw.toString();
	}

	@Override
	public void createFields(TreeLogger logger, ResourceContext context,
			ClientBundleFields fields) throws UnableToCompleteException {
		data.append(']');

		urlExpression = context.deploy(context.getClientBundleType()
				.getQualifiedSourceName().replace('.', '_')
				+ "_jsonbundle.txt", "text/plain", data.toString().getBytes(),
				true);

		TypeOracle typeOracle = context.getGeneratorContext().getTypeOracle();
		JClassType stringType = typeOracle.findType(String.class.getName());
		assert stringType != null;

		externalSVGUrlIdent = fields.define(stringType, "externalSVGUrl",
				urlExpression, true, true);

		JClassType textResourceType = typeOracle.findType(SVGResource.class
				.getName());
		assert textResourceType != null;
		JType textResourceArrayType = typeOracle.getArrayType(textResourceType);

		externalSVGCacheIdent = fields.define(textResourceArrayType,
				"externalSVGCache", "new " + SVGResource.class.getName()
						+ "[" + currentIndex + "]", true, true);
	}

	@Override
	public void init(TreeLogger logger, ResourceContext context)
			throws UnableToCompleteException {
		data = new StringBuffer("[\n");
		first = true;
		urlExpression = null;
		hashes = new HashMap<String, Integer>();
		offsets = new HashMap<String, Integer>();
		currentIndex = 0;
	}

	@Override
	public void prepare(TreeLogger logger, ResourceContext context,
			ClientBundleRequirements requirements, JMethod method)
			throws UnableToCompleteException {

		URL[] urls = ResourceGeneratorUtil.findResources(logger, context,
				method);

		if (urls.length != 1) {
			logger.log(TreeLogger.ERROR,
					"Exactly one resource must be specified", null);
			throw new UnableToCompleteException();
		}

		URL resource = urls[0];

		String toWrite = Util.readURLAsString(resource);
		if (getValidated(method)) {
			SVGValidator.validate(toWrite, resource.toExternalForm(), logger, null);
		}

		// This de-duplicates strings in the bundle.
		if (!hashes.containsKey(toWrite)) {
			hashes.put(toWrite, currentIndex++);

			if (!first) {
				data.append(",\n");
			} else {
				first = false;
			}

			data.append('"');
			data.append(Generator.escape(toWrite));
			data.append('"');
		}

		// Store the (possibly n:1) mapping of resource function to bundle
		// index.
		offsets.put(method.getName(), hashes.get(toWrite));
	}

	private boolean getValidated(JMethod method) {
		Validated validated = method.getAnnotation(Validated.class);
		if (validated == null) {
			return true;
		} else {
			return validated.validated();
		}
	}
}
