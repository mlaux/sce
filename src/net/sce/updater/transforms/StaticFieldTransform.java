package net.sce.updater.transforms;

import net.sce.bytecode.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Set;

@Dependent({ WorldTransform.class })
public class StaticFieldTransform extends BytecodeTask {

	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		System.out.println(" * Searching for static fields.");
		result.addAll(all);
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		for(FieldNode f : scn.fields) {
			SCEClassNode npcnode = getIdentifiedClass("NPCNode");
			if(npcnode != null)
				if(f.desc.equals("[L" + npcnode.name + ";"))
					results.add(new Hook("client.localNpcs", scn, f));
		}
		for(MethodNode mn : scn.methods) {
			InstructionSearcher is = new InstructionSearcher(mn);
			if(is.getNext(Opcodes.ACONST_NULL) != null && is.getNext(Opcodes.PUTSTATIC) != null && is.getNext(Opcodes.ACONST_NULL) != null && is.getNext(Opcodes.PUTSTATIC) != null && is.getNext(Opcodes.ACONST_NULL) == null) {
				is.reset();
				is.getNext(Opcodes.ACONST_NULL);
				FieldInsnNode field1 = (FieldInsnNode) is.getNext(Opcodes.PUTSTATIC);
				FieldInsnNode field2 = (FieldInsnNode) is.getNext(Opcodes.PUTSTATIC);
				if(field1.desc.equals("L"+getIdentifiedClass("World").name+";"))
					results.add(new Hook("client.world", field1));
				if(field2.desc.equals("L"+getIdentifiedClass("World").name+";"))
					results.add(new Hook("client.world", field2));
			}
		}
		Set<SCEMethodNode> methodNodes = scn.constants.get("js5connect_outofdate");
		if (methodNodes != null) {
			for (SCEMethodNode mn : methodNodes) {
				InstructionSearcher is = new InstructionSearcher(mn);
				AbstractInsnNode node = is.getNextLDC("js5connect_outofdate");
				if (node != null) {
					FieldInsnNode fieldInsn = (FieldInsnNode) is.getNext(Opcodes.PUTSTATIC);
					results.add(new Hook("client.gameState", fieldInsn));
				}
			}
		}
		methodNodes = scn.constants.get("getcgcoord");
		if (methodNodes != null) {
			for (SCEMethodNode mn : methodNodes) {
				InstructionSearcher is = new InstructionSearcher(mn);
				AbstractInsnNode node = is.getNextLDC("getcgcoord");
				if (node != null) {
					FieldInsnNode fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					results.add(new Hook("client.currentPlayer", fieldInsn));
					fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
					results.add(new Hook("entity.localX", fieldInsn));
					fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
					results.add(new Hook("entity.localY", fieldInsn));
				}
			}
		}
		methodNodes = scn.constants.get("getheight");
		if (methodNodes != null) {
			for (SCEMethodNode mn : methodNodes) {
				InstructionSearcher is = new InstructionSearcher(mn);
				AbstractInsnNode node = is.getNextLDC("getheight");
				if (node != null) {
					FieldInsnNode fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					// just to be safe
					if(fieldInsn.desc.contains("[L")) {
						results.add(new Hook("client.planes", fieldInsn));
						SCEClassNode plane = classes.get(fieldInsn.desc.substring(2, fieldInsn.desc.length() - 1));
						identifyClass("Plane", plane);
						for(FieldNode fn : plane.fields) {
							if((fn.access & Opcodes.ACC_STATIC) == 0 && fn.desc.equals("[[I"))
								results.add(new Hook("plane.heights", plane, fn));
						}
					}
					fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
					results.add(new Hook("entity.plane", fieldInsn));
				}
			}
		}
		/**methodNodes = scn.constants.get("getcamerapos");
		if (methodNodes != null) {
			for (SCEMethodNode mn : methodNodes) {
				InstructionSearcher is = new InstructionSearcher(mn);
				AbstractInsnNode node = is.getNextLDC("getcamerapos");
				if (node != null) {
					is.getNext(Opcodes.GETSTATIC);
					FieldInsnNode fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					results.add(new Hook("client.baseX", fieldInsn));
					is.getNext(Opcodes.GETSTATIC);
					fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					results.add(new Hook("client.baseY", fieldInsn));
				}
			}
		}*/
		methodNodes = scn.constants.get("loggedin");
		if (methodNodes != null) {
			for (SCEMethodNode mn : methodNodes) {
				InstructionSearcher is = new InstructionSearcher(mn);
				AbstractInsnNode node = is.getNextLDC(9.9999999E7);
				if (node != null) {
					is.getNext(Opcodes.GETSTATIC);
					FieldInsnNode fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					results.add(new Hook("client.password", fieldInsn));
					fieldInsn = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC);
					results.add(new Hook("client.username", fieldInsn));
				}
			}
		}
		methodNodes = scn.constants.get(" ->");
		if(methodNodes != null) {
			for(SCEMethodNode mn : methodNodes) {
				InstructionSearcher is = new InstructionSearcher(mn);
				FieldInsnNode fin;
				while((fin = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC)) != null) {
					if(fin.desc.equals("[[[B"))
						results.add(new Hook("client.heightData", fin));
				}
			}
		}
		
		methodNodes = scn.constants.get("tk1");
		if(methodNodes != null) {
			for(SCEMethodNode mn : methodNodes) {
				InstructionSearcher is = new InstructionSearcher(mn);
				AbstractInsnNode node = is.getNextLDC("tk1");
				if (node != null) {
					results.add(new Hook("client.runConsoleCommand", scn, mn));
				}
			}
		}
		return true;
	}
}
