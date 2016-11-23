package net.sce.bytecode;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Hook {
	private String id;
	private String className, itemName, itemType;
	private int dummyParam = -1;

	public Hook(String id, SCEClassNode scn, FieldNode field) {
		this(id, scn.name, field.name, field.desc);
	}
	
	public Hook(String id, SCEClassNode scn, MethodNode method) {
		this(id, scn.name, method.name, method.desc);
	}
	
	public Hook(String id, SCEClassNode scn, MethodNode method, int dummy) {
		this(id, scn.name, method.name, method.desc);
		dummyParam = dummy;
	}
	
	public Hook(String id, FieldInsnNode fin) {
		this(id, fin.owner, fin.name, fin.desc);
	}
	
	public Hook(String id, MethodInsnNode iin) {
		this(id, iin.owner, iin.name, iin.desc);
	}
	
	public Hook(String id, String cN, String fN, String fT) {
		this.id = id;
		this.className = cN;
		this.itemName = fN;
		this.itemType = fT;
		System.out.println("   + " + id + " identified as " + cN + "." + fN + " (" + fT + ")");
	}

	public String getId() {
		return id;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getItemName() { 
		return itemName;
	}

	public String toString() {
		String s = id + ":" + className + ":" + itemName + ":" + itemType + (dummyParam != -1 ? ":" + dummyParam : "");
		return s;
	}
}