/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package uk.co.smartkey.chorus.annotations;

/**
 * The scopes determine when the interpreter creates a new handler instance.
 * <p/>
 * The default is SCENARIO, which ensures that each scenario tested will have a new instance of the
 * handler to work with.
 * <p/>
 * In some special cases (e.g. the handler is stateless) the UNMANAGED scope may be more appropriate as
 * there is no advantage to creating new instances for each scenario run. This may improve performance of the
 * interpreter in cases where creating a new handler instance is an expensive operation. Use UNMANAGED with caution as
 * it as this may affect the commutativiy of the scenarios if the handlers are not stateless. Also consider the effects
 * of using an unmanaged handler in combination with SCENARIO scoped handlers.
 * <p/>
 * Created by: Steve Neal
 * Date: 27/10/11
 */
public enum HandlerScope {
    SCENARIO,//A new instance of the handler will be created for each scenario
    UNMANAGED//A single handler will be created and used for all scenarios
}
