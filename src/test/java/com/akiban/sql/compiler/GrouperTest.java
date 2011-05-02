/* Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.akiban.sql.compiler;

import com.akiban.sql.parser.StatementNode;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Collection;

@RunWith(Parameterized.class)
public class GrouperTest extends ASTTransformTestBase
{
  public static final File RESOURCE_DIR = 
    new File(ASTTransformTestBase.RESOURCE_DIR, "group");

  @Parameters
  public static Collection<Object[]> statements() throws Exception {
    return sqlAndExpected(RESOURCE_DIR);
  }

  public GrouperTest(String sql, String expected) {
    super(sql, expected);
  }

  @Before
  public void loadDDL() throws Exception {
    loadSchema(new File(RESOURCE_DIR, "schema.ddl"));
    loadView(new File(RESOURCE_DIR, "view-1.ddl"));
  }

  @Test
  public void testGroup() throws Exception {
    StatementNode stmt = parser.parseStatement(sql);
    binder.bind(stmt);
    stmt = booleanNormalizer.normalize(stmt);
    typeComputer.compute(stmt);
    stmt = subqueryFlattener.flatten(stmt);
    grouper.group(stmt);
    grouper.rewrite(stmt);
    assertEqualsWithoutPattern(expected.trim(), unparser.toString(stmt), "_G_\\d+");
  }

}