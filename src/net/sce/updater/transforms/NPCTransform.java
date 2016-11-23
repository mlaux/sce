package net.sce.updater.transforms;

import net.sce.bytecode.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@Dependent({ PlayerTransform.class })
public class NPCTransform extends BytecodeTask {
	private static final String pattern = "getstatic aload getfield aaload";
	
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		SCEClassNode sce = getIdentifiedClass("NPC");
		if(sce != null)
			result.add(sce);
	}

	public boolean transform(SCEClassNode scn, List<Hook> results) {
		/**for (FieldNode f : scn.fields) {
			if ((f.access & Opcodes.ACC_STATIC) == 0 && !f.desc.equals("I") && !f.desc.contains("/")) {
				results.add(new Hook("npc.definition", scn, f));
				String name = f.desc.substring(1, f.desc.length() - 1);
				SCEClassNode npcDef = classes.get(name);
				identifyClass("NPCDef", npcDef);
				for (FieldNode field : npcDef.fields) {
					if (field.desc.equals("Ljava/lang/String;"))
						results.add(new Hook("npcDef.name", npcDef, field));
					if (field.desc.equals("[Ljava/lang/String;"))
						results.add(new Hook("npcDef.actions", npcDef, field));
				}
				for(MethodNode mn : npcDef.methods) {
					InstructionSearcher is = new InstructionSearcher(mn);
					if(is.getNextPush(Opcodes.SIPUSH, 1024) != null) {
						is.getNext(Opcodes.GETFIELD);
						FieldInsnNode fn = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
						results.add(new Hook("npcDef.id", fn));
					}
				}
			}
		}   */
		for(MethodNode mn : scn.methods) {
			if(mn.desc.equals("(I)I")) {
				InstructionSearcher is = new InstructionSearcher(mn);
				FieldInsnNode f = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
				results.add(new Hook("npc.npcDef", f));
				String name = f.desc.substring(1, f.desc.length() - 1);
				SCEClassNode npcDef = classes.get(name);
				identifyClass("NPCDef", npcDef);
				for (FieldNode field : npcDef.fields) {
					if (field.desc.equals("Ljava/lang/String;"))
						results.add(new Hook("npcDef.name", npcDef, field));
					if (field.desc.equals("[Ljava/lang/String;"))
						results.add(new Hook("npcDef.actions", npcDef, field));
				}
				for(MethodNode nmn : npcDef.methods) {
					InstructionSearcher nis = new InstructionSearcher(nmn);
					if(nis.getNextPush(Opcodes.SIPUSH, 1024) != null) {
						nis.getNext(Opcodes.GETFIELD);
						FieldInsnNode fn = (FieldInsnNode) nis.getNext(Opcodes.GETFIELD);
						results.add(new Hook("npcDef.id", fn));
					}
				}
			}
			RegexSearcher rs = new RegexSearcher(mn);
			AbstractInsnNode[] insns = rs.nextMatch(pattern);
			if(insns != null)
				results.add(new Hook("client.groundTiles", (FieldInsnNode) insns[0]));
		}
		return true;
	}
}
