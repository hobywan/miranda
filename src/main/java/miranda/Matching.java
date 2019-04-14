package miranda;

class Matching {

  // criterion to be applied
  public enum Rule { UNIFORM, REAL, LOOKAHEAD }

  // check if two basis may be matched
  private static boolean matches(char a, char b) {
    // Watson-Crick (A-U, G-C) and Wobble (G-U)
    return (a=='A' && b=='U') || (a=='U' && b=='A')
        || (a=='C' && b=='G') || (a=='G' && b=='C')
        || (a=='G' && b=='U') || (a=='U' && b=='G');
  }

  static double getPairEnergy(char a, char b, Rule mode) {
    switch (mode) {
      case UNIFORM:   return getUniformPairEnergy(a, b);
      case REAL:      return getRealPairEnergy(a, b);
      case LOOKAHEAD: return 0.; // not yet supported
    }
    return 0.;
  }

  private static int getUniformPairEnergy(char a, char b) {
		return (matches(a, b) ? 1 : 0);
	}

  // retrieve released energy according to matched basis.
  // we only consider positive values here since we'll take
  // the max within the secondary structure computation.
  private static double getRealPairEnergy(char a, char b) {

    if (matches(a,b)) {
      if (a=='A' && b=='U') return 0.4;
      if (a=='U' && b=='A') return 0.4;
      if (a=='C' && b=='G') return 0.7;
      if (a=='G' && b=='C') return 0.7;
      if (a=='G' && b=='U') return 0.4;
      if (a=='U' && b=='G') return 0.4;
    }
    return -1;
  }

  // successive pattern score
  static double getLookaheadPairEnergy(char a1, char b1, char a2, char b2) {

    if(a1=='A' && b1 =='U'){
      if(a2=='A' && b2=='U') return 0.9;
      if(a2=='C' && b2=='G') return 2.1;
      if(a2=='G' && b2=='C') return 2.4;
      if(a2=='G' && b2=='U') return 1.3;
      if(a2=='U' && b2=='A') return 1.3;
      if(a2=='U' && b2=='G') return 1;
    }
    if(a1=='C' && b1 =='G'){
      if(a2=='A' && b2=='U') return 2.2;
      if(a2=='C' && b2=='G') return 3.3;
      if(a2=='G' && b2=='C') return 3.4;
      if(a2=='G' && b2=='U') return 2.5;
      if(a2=='U' && b2=='A') return 2.4;
      if(a2=='U' && b2=='G') return 1.5;
    }
    if(a1=='G' && b1 =='C'){
      if(a2=='A' && b2=='U') return 2.1;
      if(a2=='C' && b2=='G') return 2.4;
      if(a2=='G' && b2=='C') return 3.3;
      if(a2=='G' && b2=='U') return 2.1;
      if(a2=='U' && b2=='A') return 2.1;
      if(a2=='U' && b2=='G') return 1.4;
    }
    if(a1=='G' && b1 =='U'){
      if(a2=='A' && b2=='U') return 0.6;
      if(a2=='C' && b2=='G') return 1.4;
      if(a2=='G' && b2=='C') return 1.5;
      if(a2=='G' && b2=='U') return 0.5;
      if(a2=='U' && b2=='A') return 1;
      if(a2=='U' && b2=='G') return 0.3;
    }
    if(a1=='U' && b1 =='A'){
      if(a2=='A' && b2=='U') return 1.1;
      if(a2=='C' && b2=='G') return 2.1;
      if(a2=='G' && b2=='C') return 2.2;
      if(a2=='G' && b2=='U') return 1.4;
      if(a2=='U' && b2=='A') return 0.9;
      if(a2=='U' && b2=='G') return 0.6;
    }
    if(a1=='U' && b1 =='G'){
      if(a2=='A' && b2=='U') return 1.4;
      if(a2=='C' && b2=='G') return 2.1;
      if(a2=='G' && b2=='C') return 2.5;
      if(a2=='G' && b2=='U') return 1.3;
      if(a2=='U' && b2=='A') return 1.3;
      if(a2=='U' && b2=='G') return 0.5;
    }
    return -1.0;
  }
}
