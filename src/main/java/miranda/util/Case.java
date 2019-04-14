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

  public boolean equals(Case c){
		return (this.val == c.val && this.lig == c.lig && this.col == c.col);
	}
}
