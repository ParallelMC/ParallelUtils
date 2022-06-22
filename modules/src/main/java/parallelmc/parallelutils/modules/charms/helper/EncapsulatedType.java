package parallelmc.parallelutils.modules.charms.helper;


public class EncapsulatedType {

	// TODO: Figure if there's a nicer way to do this with generics or something
	// There is no type checking here and it makes me sad
	Types t;
	Object val;

	public EncapsulatedType(Types t, Object val) {
		this.t = t;
		this.val = val;
	}

	public Types getType() {
		return t;
	}

	public Object getVal() {
		return val;
	}
}
