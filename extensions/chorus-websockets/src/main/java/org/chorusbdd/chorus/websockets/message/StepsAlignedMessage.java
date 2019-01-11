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
package org.chorusbdd.chorus.websockets.message;

/**
 * Created by nick on 09/12/2016.
 */
public class StepsAlignedMessage extends AbstractTypedMessage {

    private String chorusClientId;

    /**
     * Nullary constructor required for deserialization
     */
    public StepsAlignedMessage() {
        super(MessageType.STEPS_ALIGNED.name());
    }

    public StepsAlignedMessage(String chorusClientId) {
        this();
        this.chorusClientId = chorusClientId;
    }

    public String getChorusClientId() {
        return chorusClientId;
    }

    public void setChorusClientId(String chorusClientId) {
        this.chorusClientId = chorusClientId;
    }


    @Override
    public String toString() {
        return "StepsAligned{" +
            "type='" + type + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepsAlignedMessage that = (StepsAlignedMessage) o;

        return chorusClientId.equals(that.chorusClientId);

    }

    @Override
    public int hashCode() {
        return chorusClientId.hashCode();
    }
}
