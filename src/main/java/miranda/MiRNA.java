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

import org.apache.commons.lang3.ArrayUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import miranda.util.Case;
import miranda.util.Index;

public class MiRNA {

  // initial nucleotide sequence
  private Character[] sequence;
  // resulting conformation pattern
  private Character[] pattern;
  // sequence size
  private int size;
  // computed number of nucleotide pairs
  private int nbPairs;
  // released energy after conformation
  private double score;
  // energy matrix for each pair
  private double[][] energy;
  // index matrix for matched pairs
  private Index[][] matched;
  // matching criterion used
  private Matching.Rule mode;

  MiRNA() {
    this.size = 0;
    this.nbPairs = 0;
    this.score = 0;
    this.mode = Matching.Rule.REAL;
  }

  MiRNA(String sequence) throws Exception {
    setSequence(sequence);
  }

  void setCriterion(Matching.Rule mode) {
    this.mode = mode;
  }

  void setSequence(String sequence) throws Exception {
    this.sequence = ArrayUtils.toObject(sequence.toCharArray());
    if (this.isValid()) {
      this.size = this.sequence.length;
      this.score = 0;
      this.nbPairs = 0;

      // pattern array initialization
      this.pattern = new Character[size];
      for (int i = 0; i < size; i++) {
        this.pattern[i] = '.';
      }

      // matrices initializations
      this.energy = new double[size][size];
      this.matched = new Index[size][size];

      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          this.matched[i][j] = new Index(0, 0);
        }
      }
    } else {
      throw new Exception("Invalid sequence");
    }
  }

  boolean isValid() {
    for (char current : this.sequence) {
      if (current != 'A' && current != 'U' &&
          current != 'C' && current != 'G') {
        System.err.println("invalid character " + current);
        return false;
      }
    }
    return true;
  }

  // Retrieve the max energy value regardless of the comparison order in case of
  // multiple identical values. It enables to retrieve multiple solutions,
  // so use a Case instance to retrieve the index of the item to be updated.
  private Case maxAlea(Case a, Case b, Case c, int lig, int col) {

    Case optimal = new Case(-1, -1, -1);

    ArrayList<Case> copy = new ArrayList<Case>();
    copy.add(a);
    copy.add(b);
    copy.add(c);

    Collections.shuffle(copy);

    for (int i = 0; i < 3; i++) {
      Case current = copy.get(i);
      if (optimal.val < current.val)
        optimal = new Case(current);
    }

    if (optimal.equals(a)) {
      this.matched[lig][col] = new Index(lig + 1, col - 1);
    } else if (optimal.equals(b)) {
      this.matched[lig][col] = new Index(lig + 1, col);
    } else if (optimal.equals(c)) {
      this.matched[lig][col] = new Index(lig, col - 1);
    }
    return optimal;
  }


  // compute the secondary structure related to the nucleotide
  // sequence. It fills the released energy matrix.
  void computeSecondaryStructure() {

    int col;
    double S1, S2, S3;

    // 1. Initialization
    // A basis cannot be matched neither with itself nor with a direct neighbor.
    // Fill related entries with 0.
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        this.energy[i][i] = 0;

        if (i > 0 && i == j)
          this.energy[i - 1][i] = 0;

        else {
          this.energy[i][j] = 0;
        }
      }
    }

    // 2. compute induced energy.
    // fill values diagonal by diagonal
    double current;

    for (int diag = 1; diag < size; diag++) {
      // line traversal
      for (int lig = 0; lig < size - diag; lig++) {
        // retrieve column index
        col = lig + diag;
        // retrieve energy(i+1, j-1), energy(i+1, j), et energy(i, j-1)
        current = Matching.getPairEnergy(sequence[lig], sequence[col], mode);
        S1 = energy[lig + 1][col - 1] + current;
        S2 = energy[lig + 1][col];
        S3 = energy[lig][col - 1];
        // take the max
        Case a, b, c, max;
        a = new Case(S1, lig + 1, col - 1);
        b = new Case(S2, lig + 1, col);
        c = new Case(S3, lig, col - 1);
        max = this.maxAlea(a, b, c, lig, col);
        // update energy[lig][col]
        energy[lig][col] = max.val;
      }
    }
    // mark pairs
    this.tagMatched();
    // retrieve final released energy
    this.score = this.energy[0][size - 1];
  }

  private void tagMatched() {

    Index previous;
    Index current = this.matched[0][size - 1];

    ArrayList<Index> solut = new ArrayList<Index>();
    solut.add(new Index(0, size - 1));

    while (
        this.matched[current.x][current.y].x != 0 ||
            this.matched[current.x][current.y].y != 0
    ) {
      solut.add(current);
      current = this.matched[current.x][current.y];
    }
    // search for diagonals
    for (int i = 1; i < solut.size(); i++) {
      previous = solut.get(i - 1);
      current = solut.get(i);
      // if we found one
      if (current.x == previous.x + 1 && current.y == previous.y - 1) {
        this.pattern[previous.x] = '(';
        this.pattern[previous.y] = ')';
        this.nbPairs++;
      }
    }

  }

  String printEnergyMatrix() {
    StringBuilder s = new StringBuilder();
    Locale.setDefault(new Locale("en", "US", "MAC"));
    DecimalFormat formatter = new DecimalFormat("#0.0");

    // horizontal header
    for (int i = 0; i < size; i++) {
      if (i == 0) {
        s.append("\t");
      }
      s.append(this.sequence[i]).append("\t");
    }
    s.append("\n");

    for (int i = 0; i < size; i++) {
      // vertical header
      s.append(this.sequence[i]).append("\t");
      for (int j = 0; j < size; j++) {
        if (j == size - 1) {
          s.append(formatter.format(energy[i][j]));
          s.append('\n');
        } else {
          s.append(formatter.format(energy[i][j]));
          s.append("\t");
        }
      }
    }
    return s.toString();
  }

  String printMatchingMatrix() {
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < size; i++) {
      if (i == 0) {
        s.append("\t");
      }
      s.append(this.sequence[i]).append("\t");
    }
    s.append("\n");

    for (int i = 0; i < size; i++) {
      s.append(this.sequence[i]).append("\t");
      for (int j = 0; j < size; j++) {
        if (j == size - 1) {
          s.append(this.matched[i][j]).append("\n");
        } else {
          s.append(this.matched[i][j]).append("\t");
        }
      }
    }
    return s.toString();
  }

  String getPattern() {
    StringBuilder s = new StringBuilder();
    for (char current : this.pattern) {
      s.append(current); //.append(" ");
    }
    s.append("\n");
    return s.toString();
  }

  int getLength() {
    return this.sequence.length;
  }

  int getNumberPairs() {
    return this.nbPairs;
  }

  double getEnergy() {
    return this.score;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    Locale.setDefault(new Locale("en", "US", "MAC"));
    DecimalFormat formatter = new DecimalFormat("#0.00");

    if (sequence.length > 0 && pattern.length > 0) {
      s.append("- sequence:    ");
      Arrays.stream(this.sequence, 0, size).forEach(s::append);
      s.append("\n");
      s.append("- pattern:     ");
      Arrays.stream(this.pattern, 0, size).forEach(s::append);
      s.append("\n");
      s.append("- length:      ").append(sequence.length).append("\n");
      s.append("- found pairs: ").append(nbPairs).append("\n");
      s.append("- energy:      ").append(formatter.format(score)).append("\n");
    } else {
      s.append("Empty sequence\n");
    }
    return s.toString();
  }
}