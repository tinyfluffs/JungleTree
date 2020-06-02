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

import java.util.*;

@Getter
@Setter
public final class CompoundTag implements Tag<SortedMap<String, Tag<?>>> {

    private SortedMap<String, Tag<?>> value;

    public CompoundTag() {
        this.value = new TreeMap<>();
    }

    public CompoundTag(Map<String, Tag<?>> value) {
        this.value = new TreeMap<>(value);
    }

    @Override
    public TagType getType() {
        return TagType.TAG_COMPOUND;
    }

    @Override
    public String toString() {
        String append = "";
        StringBuilder b = new StringBuilder();
        b.append("TAG_Compound").append(append).append(": ").append(value.size()).append(" entries\r\n{\r\n");
        for (Tag entry : value.values()) {
            b.append("   ").append(entry.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        b.append("}");
        return b.toString();
    }
}
