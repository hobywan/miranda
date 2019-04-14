/*
 * Copyright 2014, Hoby Rakotoarivelo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package miranda.util;

public class Case {

  public double val;
  private int lig, col;

  public Case(double val, int lig, int col) {
    this.val = val;
    this.lig = lig;
    this.col = col;
  }

  public Case(Case current) {
    this(current.val, current.lig, current.col);
  }

  public boolean equals(Case c) {
    return (this.val == c.val && this.lig == c.lig && this.col == c.col);
  }
}
