package extension.model.program;

import extension.model.elements.Box;

public class Connection
{
	private Box src, dst;

	public Connection(Box src, Box dst) {
		this.src = src;
		this.dst = dst;
	}

	public Box getSrc() { return src; }
	public Box getDst() { return dst; }
}
