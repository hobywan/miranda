package miranda;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

public class MainTest {

  @Before
  public void setUp() throws Exception { }

  @After
  public void tearDown() throws Exception { }

  @Test
  public void testCalculSolutionOptimale() throws Exception {

    String sequence = "GGGAAAUCCAAUGGUACGGGGAAAUCCAAUGGUACGGGGAAAUCCAAUGG";
    sequence += "UACGGGGAAAUCCAAUGGUACGGGGAAAUCCAAUGGUACGGGGAAAUCCAAUGGUACG";

    MiRNA structure = new MiRNA(sequence);
    structure.setCriterion(Matching.Rule.REAL);
    structure.computeSecondaryStructure();
    System.out.println(structure);
  }
}
