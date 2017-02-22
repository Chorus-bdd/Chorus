/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.tools.xml.adapter.ResultsSummaryAdapter;
import org.chorusbdd.chorus.tools.xml.beans.ResultSummaryBean;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 10/01/13
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class ResultSummaryXmlMarshaller extends AbstractXmlMarshaller<ResultsSummary> {

    public ResultSummaryXmlMarshaller() {
        super(ResultSummaryBean.class);
    }

    @Override
    public void write(XMLStreamWriter writer, ResultsSummary summary) throws Exception {
        ResultSummaryBean resultSummaryBean = new ResultsSummaryAdapter().marshal(summary);
        Marshaller marshaller = getMarshaller();
        marshaller.marshal(resultSummaryBean, writer);
    }

    @Override
    public void write(Writer writer, ResultsSummary summary) throws Exception {
        ResultSummaryBean resultSummaryBean = new ResultsSummaryAdapter().marshal(summary);
        Marshaller marshaller = getMarshaller();
        marshaller.marshal(resultSummaryBean, writer);
    }

}
