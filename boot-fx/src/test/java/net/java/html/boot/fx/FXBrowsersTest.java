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
package net.java.html.boot.fx;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.java.html.js.JavaScriptBody;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Jaroslav Tulach <jaroslav.tulach@apidesign.org>
 */
public class FXBrowsersTest {
    
    public FXBrowsersTest() {
    }
    
    @BeforeClass public void initFX() throws Throwable {
        new Thread("initFX") {
            @Override
            public void run() {
                App.launch(App.class);
            }
        }.start();
        App.CDL.await();
    }

    @Test
    public void behaviorOfTwoWebViewsAtOnce() throws Throwable {
        class R implements Runnable {
            CountDownLatch DONE = new CountDownLatch(1);
            Throwable t;

            @Override
            public void run() {
                try {
                    doTest();
                } catch (Throwable ex) {
                    t = ex;
                } finally {
                    DONE.countDown();
                }
            }
            
            private void doTest() throws Throwable {
                URL u = FXBrowsersTest.class.getResource("/org/apidesign/html/boot/fx/empty.html");
                assertNotNull(u, "URL found");
                FXBrowsers.load(App.getV1(), u, OnPages.class, "first");
                
            }
        }
        R run = new R();
        Platform.runLater(run);
        run.DONE.await();
        for (int i = 0; i < 100; i++) {
            if (run.t != null) {
                throw run.t;
            }
            if (System.getProperty("finalSecond") == null) {
                Thread.sleep(100);
            }
        }
        
        
        
        assertEquals(Integer.getInteger("finalFirst"), Integer.valueOf(3), "Three times in view one");
        assertEquals(Integer.getInteger("finalSecond"), Integer.valueOf(2), "Two times in view one");
    }
    
    public static class OnPages {
        static Class<?> first;
        static Object firstWindow;
        
        public static void first() {
            first = OnPages.class;
            firstWindow = window();
            assertNotNull(firstWindow, "First window found");
            
            assertEquals(increment(), 1, "Now it is one");
            
            URL u = FXBrowsersTest.class.getResource("/org/apidesign/html/boot/fx/empty.html");
            assertNotNull(u, "URL found");
            FXBrowsers.load(App.getV2(), u, OnPages.class, "second", "Hello");
            
            assertEquals(increment(), 2, "Now it is two and not influenced by second view");
            System.setProperty("finalFirst", "" + increment());
        }
        
        public static void second(String... args) {
            assertEquals(args.length, 1, "One string argument");
            assertEquals(args[0], "Hello", "It is hello");
            assertEquals(first, OnPages.class, "Both views share the same classloader");
            
            Object window = window();
            assertNotNull(window, "Some window found");
            assertNotNull(firstWindow, "First window is known");
            assertNotSame(firstWindow, window, "The window objects should be different");
            
            assertEquals(increment(), 1, "Counting starts from zero");
            System.setProperty("finalSecond", "" + increment());
        }
        
        @JavaScriptBody(args = {}, body = "return window;")
        private static native Object window();
        
        @JavaScriptBody(args = {}, body = ""
            + "if (window.cnt) return ++window.cnt;"
            + "return window.cnt = 1;"
        )
        private static native int increment();
    }
    
    public static class App extends Application {
        static final CountDownLatch CDL = new CountDownLatch(1);
        private static BorderPane pane;

        /**
         * @return the v1
         */
        static WebView getV1() {
            return (WebView)System.getProperties().get("v1");
        }

        /**
         * @return the v2
         */
        static WebView getV2() {
            return (WebView)System.getProperties().get("v2");
        }

        @Override
        public void start(Stage stage) throws Exception {
            pane= new BorderPane();
            Scene scene = new Scene(pane, 800, 600);
            stage.setScene(scene);
            
            System.getProperties().put("v1", new WebView());
            System.getProperties().put("v2", new WebView());

            pane.setCenter(getV1());
            pane.setBottom(getV2());

            stage.show();
            CDL.countDown();
        }
        
        
    }
}