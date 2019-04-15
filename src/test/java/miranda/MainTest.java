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

package miranda;

import org.junit.Assert;
import org.junit.Test;

public class MainTest {

  @Test public void testComputeSecondaryStructure() throws Exception {

    String sequence = "GGGAAAUCCAAUGGUACGGGGAAAUCCAAUGGUACGGGGAAAUCCAAUGG";
    sequence += "UACGGGGAAAUCCAAUGGUACGGGGAAAUCCAAUGGUACGGGGAAAUCCAAUGGUACG";

    MiRNA structure = new MiRNA(sequence);
    int length = structure.getLength();
    Assert.assertTrue(length > 0);

    structure.setCriterion(Matching.Rule.REAL);
    structure.computeSecondaryStructure();
    System.out.println(structure);

    double energy = structure.getEnergy();
    int nbPairs   = structure.getNumberPairs();
    Assert.assertTrue(energy > 0);
    Assert.assertTrue(nbPairs > 0);
  }
}
