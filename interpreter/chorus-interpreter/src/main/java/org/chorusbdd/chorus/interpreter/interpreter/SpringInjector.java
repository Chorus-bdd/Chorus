/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.results.FeatureToken;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 17:30
 *
 * To avoid a dependency on Spring from core interpreter, we check for the existence of a SpringContextInjector
 * on the classpath, and if it exists, instantiate an instance of it which we address via the SpringInjector interface
 */
public interface SpringInjector {

    SpringInjector NULL_INJECTOR = new SpringInjector() {

        //if the null injector is in use, this means we failed to find and instantiate the SpringContextInjector from the chorus-spring module
        public void injectSpringContext(Object handler, FeatureToken featureToken, String contextFileName) {
            throw new UnsupportedOperationException("You need to add chorus-spring to your classpath to use the SpringContext annotation");
        }

        public void disposeContext(Object handler) {
        }
    };

    public void injectSpringContext(Object handler, FeatureToken featureToken, String contextFileName) throws Exception;

    void disposeContext(Object handler);
}
