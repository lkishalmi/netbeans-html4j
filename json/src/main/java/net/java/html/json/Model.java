/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013-2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Oracle. Portions Copyright 2013-2014 Oracle. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package net.java.html.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.List;
import org.netbeans.html.json.spi.Technology;

/** Defines a model class that represents a single 
 * <a target="_blank" href="http://en.wikipedia.org/wiki/JSON">JSON</a>-like object
 * named {@link #className()}. The generated class contains
 * getters and setters for properties defined via {@link #properties()} and
 * getters for other, derived properties defined by annotating methods
 * of this class by {@link ComputedProperty}. Each property
 * can be of primitive type, an {@link Enum enum type} or (in order to create 
 * nested <a target="_blank" href="http://en.wikipedia.org/wiki/JSON">JSON</a> structure)
 * of another {@link Model class generated by @Model} annotation. Each property
 * can either represent a single value or be an array of its values.
 * <p>
 * The {@link #className() generated class}'s <code>toString</code> method
 * converts the state of the object into 
 * <a target="_blank" href="http://en.wikipedia.org/wiki/JSON">JSON</a> format. One can
 * use {@link Models#parse(net.java.html.BrwsrCtx, java.lang.Class, java.io.InputStream)}
 * method to read the JSON text stored in a file or other stream back into the Java model. 
 * One can also use {@link OnReceive @OnReceive} annotation to read the model
 * asynchronously from a {@link URL}.
 * <p>
 * An example where one defines class <code>Person</code> with four
 * properties (<code>firstName</code>, <code>lastName</code>, array of <code>addresses</code> and
 * <code>fullName</code>) follows:
 * <pre>
 * {@link Model @Model}(className="Person", properties={
 *   {@link Property @Property}(name = "firstName", type=String.<b>class</b>),
 *   {@link Property @Property}(name = "lastName", type=String.<b>class</b>)
 *   {@link Property @Property}(name = "addresses", type=Address.<b>class</b>, array = <b>true</b>)
 * })
 * <b>static class</b> PersonModel {
 *   {@link ComputedProperty @ComputedProperty}
 *   <b>static</b> String fullName(String firstName, String lastName) {
 *     <b>return</b> firstName + " " + lastName;
 *   }
 * 
 *   {@link ComputedProperty @ComputedProperty}
 *   <b>static</b> String mainAddress({@link List List&lt;Address&gt;} addresses) {
 *     <b>for</b> (Address a : addresses) {
 *       <b>return</b> a.getStreet() + " " + a.getTown();
 *     }
 *     <b>return</b> "No address";
 *   }
 * 
 *   {@link Model @Model}(className="Address", properties={
 *     {@link Property @Property}(name = "street", type=String.<b>class</b>),
 *     {@link Property @Property}(name = "town", type=String.<b>class</b>)
 *   })
 *   <b>static class</b> AddressModel {
 *   }
 * }
 * </pre>
 * The generated model class has a default constructor, and also <em>quick
 * instantiation</em> constructor that accepts all non-array properties 
 * (in the order used in the {@link #properties()} attribute) and vararg list
 * for the first array property (if any). One can thus use following code
 * to create an instance of the Person and Address classes:
 * <pre>
 * Person p = <b>new</b> Person("Jaroslav", "Tulach",
 *   <b>new</b> Address("Markoušovice", "Úpice"),
 *   <b>new</b> Address("V Parku", "Praha")
 * );
 * // p.toString() then returns equivalent of following <a target="_blank" href="http://en.wikipedia.org/wiki/JSON">JSON</a> object
 * {
 *   "firstName" : "Jaroslav",
 *   "lastName" : "Tulach",
 *   "addresses" : [
 *     { "street" : "Markoušovice", "town" : "Úpice" },
 *     { "street" : "V Parku", "town" : "Praha" },
 *   ]
 * }
 * </pre>
 * <p>
 * In case you are using <a target="_blank" href="http://knockoutjs.com/">Knockout technology</a>
 * for Java then you can associate such model object with surrounding HTML page by
 * calling: <code>p.applyBindings();</code> (in case you specify {@link #targetId()}. 
 * The page can then use regular
 * <a target="_blank" href="http://knockoutjs.com/">Knockout</a> bindings to reference your
 * model and create dynamic connection between your model in Java and 
 * live DOM structure in the browser:
 * </p>
 * <pre>
 * Name: &lt;span data-bind="text: fullName"&gt;
 * &lt;div data-bind="foreach: addresses"&gt;
 *   Lives in &lt;span data-bind="text: town"/&gt;
 * &lt;/div&gt;
 * </pre>
 * 
 * <h3>Access Raw <a target="_blank" href="http://knockoutjs.com/">Knockout</a> Observables</h3>
 * 
 * One can obtain <em>raw</em> JavaScript object representing the 
 * instance of {@link Model model class} (with appropriate
 * <a target="_blank" href="http://knockoutjs.com/">Knockout</a> <b>observable</b> properties)
 * by calling {@link Models#toRaw(java.lang.Object) Models.toRaw(p)}. For 
 * example here is a way to obtain the value of <code>fullName</code> property
 * (inefficient as it switches between Java and JavaScript back and forth, 
 * but functional and instructive) via a JavaScript call:
 * <pre>
 * {@link net.java.html.js.JavaScriptBody @JavaScriptBody}(args = "raw", javacall = true, body =
 *   "return raw.fullName();" // yes, the <a target="_blank" href="http://knockoutjs.com/">Knockout</a> property is a function
 * )
 * static native String jsFullName(Object raw);
 * // and later
 * Person p = ...;
 * String fullName = jsFullName({@link Models#toRaw(java.lang.Object) Models.toRaw(p)});
 * </pre>
 * The above shows how to read a value from <a target="_blank" href="http://knockoutjs.com/">Knockout</a>
 * observable. There is a way to change the value too:
 * One can pass a parameter to the property-function and then
 * it acts like a setter (of course not in the case of read only <code>fullName</code> property,
 * but for <code>firstName</code> or <code>lastName</code> the setter is
 * available). Everything mentioned in this paragraph applies only when 
 * <a target="_blank" href="http://knockoutjs.com/">Knockout</a> technology is active
 * other technologies may behave differently.
 * 
 * <h4>Copy to Plain JSON</h4>
 * There is a way to pass a value of a Java {@link Model model class} instance 
 * by copy and convert 
 * the {@link Model the whole object} into plain 
 * <a target="_blank" href="http://en.wikipedia.org/wiki/JSON">JSON</a>. Just
 * print it as a string and parse it in JavaScript:
 * <pre>
 * {@link net.java.html.js.JavaScriptBody @JavaScriptBody}(args = { "txt" }, body =
 *   "return JSON.parse(txt);"
 * )
 * private static native Object parseJSON(String txt);
 * 
 * public static Object toPlainJSON(Object model) {
 *   return parseJSON(model.toString());
 * }
 * </pre>
 * The newly returned instance is a one time copy of the original model and is no longer
 * connected to it. The copy based behavior is independent on any 
 * particular technology and should work
 * in <a target="_blank" href="http://knockoutjs.com/">Knockout</a> as well as other
 * technology implementations.
 * 
 * <h4>References</h4>
 * 
 * Visit an <a target="_blank" href="http://dew.apidesign.org/dew/#7510833">on-line demo</a>
 * to see a histogram driven by the {@link Model} annotation or try 
 * a little <a target="_blank" href="http://dew.apidesign.org/dew/#7263102">math test</a>.
 *
 * @author Jaroslav Tulach
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Model {
    /** Name of the model class.
     * @return valid Java identifier to use as a name of the model class
     */
    String className();
    /** List of properties in the model.
     * @return array of property definitions
     */
    Property[] properties();
    
    /** The id of an element to bind this model too. If this
     * property is specified an <code>applyBindings</code> method
     * in the model class is going to be generated which later calls
     * {@link Models#applyBindings(java.lang.Object, java.lang.String)}
     * with appropriate <code>targetId</code>. If the <code>targetId</code>
     * is specified as empty string, <code>null</code> value is passed
     * to {@link Models#applyBindings(java.lang.Object, java.lang.String)} method.
     * If the <code>targetId</code> is not specified at all, no public
     * <code>applyBindings</code> method is generated at all (a change compared
     * to previous versions of this API).
     * 
     * @return an empty string (means apply globally), or ID of a (usually DOM)
     *    element to apply this model after calling its generated
     *    <code>applyBindings()</code> method to
     * @since 1.1
     */
    String targetId() default "";
}
