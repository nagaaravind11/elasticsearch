/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.gradle.doc


import org.elasticsearch.gradle.GradleUnitTestCase
import org.gradle.api.InvalidUserDataException
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.elasticsearch.gradle.doc.RestTestsFromSnippetsTask.shouldAddShardFailureCheck
import static org.elasticsearch.gradle.doc.RestTestsFromSnippetsTask.replaceBlockQuote
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class RestTestFromSnippetsTaskTests extends GradleUnitTestCase {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none()

    @Test
    void testInvalidBlockQuote() {
        String input = "\"foo\": \"\"\"bar\""
        expectedEx.expect(InvalidUserDataException.class)
        expectedEx.expectMessage("Invalid block quote starting at 7 in:\n$input")
        replaceBlockQuote(input)
    }

    @Test
    void testSimpleBlockQuote() {
        assertEquals("\"foo\": \"bort baz\"",
            replaceBlockQuote("\"foo\": \"\"\"bort baz\"\"\""))
    }

    @Test
    void testMultipleBlockQuotes() {
        assertEquals("\"foo\": \"bort baz\", \"bar\": \"other\"",
            replaceBlockQuote("\"foo\": \"\"\"bort baz\"\"\", \"bar\": \"\"\"other\"\"\""))
    }

    @Test
    void testEscapingInBlockQuote() {
        assertEquals("\"foo\": \"bort\\\" baz\"",
            replaceBlockQuote("\"foo\": \"\"\"bort\" baz\"\"\""))
        assertEquals("\"foo\": \"bort\\n baz\"",
            replaceBlockQuote("\"foo\": \"\"\"bort\n baz\"\"\""))
    }

    void testIsDocWriteRequest() {
        assertTrue(shouldAddShardFailureCheck("doc-index/_search"));
        assertFalse(shouldAddShardFailureCheck("_cat"))
        assertFalse(shouldAddShardFailureCheck("_xpack/ml/datafeeds/datafeed-id/_preview"));
    }
}
