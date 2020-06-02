/*
 * This file is part of Flow NBT, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2011 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jungletree.api.nbt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ByteTag extends NamedTag<Byte> {

    private Byte value;

    public ByteTag(String name, boolean value) {
        this(name, (byte) (value ? 1 : 0));
    }

    public ByteTag(String name, byte value) {
        super(name);
        this.value = value;
    }

    public boolean getBooleanValue() {
        return value != 0;
    }

    @Override
    public TagType getType() {
        return TagType.TAG_BYTE;
    }

    @Override
    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Byte" + append + ": " + value;
    }

    public static Boolean getBooleanValue(Tag<?> t) {
        if (t == null) {
            return null;
        }
        try {
            ByteTag byteTag = (ByteTag) t;
            return byteTag.getBooleanValue();
        } catch (ClassCastException e) {
            return null;
        }
    }
}
