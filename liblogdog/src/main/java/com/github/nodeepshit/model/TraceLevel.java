/*
 * Copyright (C) 2015 Pedro Vicente Gomez Sanchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nodeepshit.model;

/**
 * Logcat trace levels used to indicate the trace importance.
 *
 * @author Pedro Vicente Gomez Sanchez.
 */
public enum TraceLevel {
    RESULT("S"),
    RESPONSE("R"),
    TIP("T"),
    VERBOSE("V"),
    DEBUG("D"),
    INFO("I"),
    WARNING("W"),
    ERROR("E"),
    ASSERT("A"),
    UPDATE("U"),
    WTF("F");

    private final String value;

    TraceLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TraceLevel getTraceLevel(char traceString) {
        TraceLevel traceLevel;
        switch (traceString) {
            case 'S':
                traceLevel = TraceLevel.RESULT;
                break;
            case 'R':
                traceLevel = TraceLevel.RESPONSE;
                break;
            case 'T':
                traceLevel = TraceLevel.TIP;
                break;
            case 'V':
                traceLevel = TraceLevel.VERBOSE;
                break;
            case 'A':
                traceLevel = TraceLevel.ASSERT;
                break;
            case 'I':
                traceLevel = TraceLevel.INFO;
                break;
            case 'W':
                traceLevel = TraceLevel.WARNING;
                break;
            case 'E':
                traceLevel = TraceLevel.ERROR;
                break;
            case 'F':
                traceLevel = TraceLevel.WTF;
                break;
            case 'U':
                traceLevel = TraceLevel.UPDATE;
                break;
            default:
                traceLevel = TraceLevel.DEBUG;
        }
        return traceLevel;
    }
}
