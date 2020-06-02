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

import java.util.Arrays;

@Getter
@Setter
public final class LongArrayTag extends NamedTag<long[]> {

    private long[] value;

    public LongArrayTag(String name, long[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public TagType getType() {
        return TagType.TAG_LONG_ARRAY;
    }

    @Override
    public String toString() {
        StringBuilder hex = new StringBuilder();
        for (long s : value) {
            String hexDigits = Long.toHexString(s).toUpperCase();
            if (hexDigits.length() == 1) {
                hex.append("0");
            }
            hex.append(hexDigits).append(" ");
        }

        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Long_Array" + append + ": " + hex.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LongArrayTag)) {
            return false;
        }
        LongArrayTag tag = (LongArrayTag) other;
        return Arrays.equals(value, tag.value) && getName().equals(tag.getName());
    }
}
