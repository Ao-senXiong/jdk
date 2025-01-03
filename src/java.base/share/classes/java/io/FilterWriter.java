/*
 * Copyright (c) 1996, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.io;

import org.checkerframework.checker.index.qual.IndexOrHigh;
import org.checkerframework.checker.index.qual.LTLengthOf;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.framework.qual.AnnotatedFor;


/**
 * Abstract class for writing filtered character streams.
 * The abstract class {@code FilterWriter} itself
 * provides default methods that pass all requests to the
 * contained stream. Subclasses of {@code FilterWriter}
 * should override some of these methods and may also
 * provide additional methods and fields.
 *
 * @author      Mark Reinhold
 * @since       1.1
 */

@AnnotatedFor({"index", "initialization", "nullness"})
public abstract class FilterWriter extends Writer {

    /**
     * The underlying character-output stream.
     */
    protected Writer out;

    /**
     * Create a new filtered writer.
     *
     * @param out  a Writer object to provide the underlying stream.
     * @throws NullPointerException if {@code out} is {@code null}
     */
    protected FilterWriter(Writer out) {
        super(out);
        this.out = out;
    }

    /**
     * Writes a single character.
     *
     * @throws     IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
        out.write(c);
    }

    /**
     * Writes a portion of an array of characters.
     *
     * @param  cbuf  Buffer of characters to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @throws  IndexOutOfBoundsException
     *          If the values of the {@code off} and {@code len} parameters
     *          cause the corresponding method of the underlying {@code Writer}
     *          to throw an {@code IndexOutOfBoundsException}
     *
     * @throws  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], @IndexOrHigh({"#1"}) int off, @LTLengthOf(value={"#1"}, offset={"#2 - 1"}) @NonNegative int len) throws IOException {
        out.write(cbuf, off, len);
    }

    /**
     * Writes a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     *
     * @throws  IndexOutOfBoundsException
     *          If the values of the {@code off} and {@code len} parameters
     *          cause the corresponding method of the underlying {@code Writer}
     *          to throw an {@code IndexOutOfBoundsException}
     *
     * @throws  IOException  If an I/O error occurs
     */
    public void write(String str, @IndexOrHigh({"#1"}) int off, @LTLengthOf(value={"#1"}, offset={"#2 - 1"}) @NonNegative int len) throws IOException {
        out.write(str, off, len);
    }

    /**
     * Flushes the stream.
     *
     * @throws     IOException  If an I/O error occurs
     */
    public void flush() throws IOException {
        out.flush();
    }

    public void close() throws IOException {
        out.close();
    }

}
