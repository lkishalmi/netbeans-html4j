/**
 * HTML via Java(tm) Language Bindings
 * Copyright (C) 2013 Jaroslav Tulach <jaroslav.tulach@apidesign.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. apidesign.org
 * designates this particular file as subject to the
 * "Classpath" exception as provided by apidesign.org
 * in the License file that accompanied this code.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://wiki.apidesign.org/wiki/GPLwithClassPathException
 */
package org.apidesign.html.kofx;

import java.util.concurrent.Callable;
import java.util.logging.Logger;
import net.java.html.json.Context;
import netscape.javascript.JSObject;
import org.apidesign.html.json.spi.ContextBuilder;
import org.apidesign.html.json.spi.FunctionBinding;
import org.apidesign.html.json.spi.JSONCall;
import org.apidesign.html.json.spi.PropertyBinding;
import org.apidesign.html.json.spi.Technology;
import org.apidesign.html.json.spi.Transfer;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public final class FXContext implements Callable<Context>, Technology<JSObject>, Transfer {
    static final Logger LOG = Logger.getLogger(FXContext.class.getName());
    
    
    @Override
    public Context call() throws Exception {
        return ContextBuilder.create().withTechnology(this).
            withTransfer(this).build();
    }

    @Override
    public JSObject wrapModel(Object model) {
        return (JSObject) Knockout.createBinding(model).koData();
    }

    @Override
    public void bind(PropertyBinding b, Object model, JSObject data) {
        final boolean isList = false;
        final boolean isPrimitive = false;
        Knockout.bind(data, model, b, isPrimitive, isList);
    }

    @Override
    public void valueHasMutated(JSObject data, String propertyName) {
        Knockout.valueHasMutated(data, propertyName);
    }

    @Override
    public void expose(FunctionBinding fb, Object model, JSObject d) {
        Knockout.expose(d, fb);
    }

    @Override
    public void applyBindings(JSObject data) {
        Knockout.applyBindings(data);
    }

    @Override
    public Object wrapArray(Object[] arr) {
        return Knockout.toArray(arr);
    }

    @Override
    public void extract(Object obj, String[] props, Object[] values) {
        LoadJSON.extractJSON(obj, props, values);
    }

    @Override
    public void loadJSON(final JSONCall call) {
        LoadJSON.loadJSON(call);
    }
}
