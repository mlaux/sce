package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Dependent;
import net.sce.bytecode.Hook;
import net.sce.bytecode.InstructionSearcher;
import net.sce.bytecode.RegexSearcher;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

@Dependent(SDModelTransform.class)
public class ObjectTransform extends BytecodeTask {
	// Some subclasses have 'ldc aload getfield', others have 'aload getfield ldc'
	// doesn't matter really because we only need one
	private static final String directPattern = "aload getfield ldc iand ireturn";
	
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for(SCEClassNode scn : all) {
			for(MethodNode mn : scn.methods) {
				if(mn.desc.contains("[[[L") && mn.desc.length() < 12)
					result.add(scn);
			}
		}
	}
	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		for(MethodNode mn : scn.methods) {
			if(!mn.desc.contains("[[[L") || mn.desc.length() > 11)
				continue;
			InstructionSearcher is = new InstructionSearcher(mn);
			
			identifyType("FloorDecoration", "groundTile.floorDecoration", is, results);
			
			identifyType("WallDecoration", "groundTile.wallDecoration1", is, results);
			identifyType("WallDecoration", "groundTile.wallDecoration2", is, results);
			
			identifyType("WallObject", "groundTile.wallObject1", is, results);
			identifyType("WallObject", "groundTile.wallObject2", is, results);
			
			FieldInsnNode fin = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
			results.add(new Hook("groundTile.animableNode", fin));
			String type = fin.desc.substring(fin.desc.indexOf('L') + 1, fin.desc.length() - 1);
			identifyClass("AnimableNode", classes.get(type));
			
			fin = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
			results.add(new Hook("animableNode.entity", fin));
			fin = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
			results.add(new Hook("animableNode.next", fin));
		}
		findID(results);
		return true;
	}
	
	private void findID(List<Hook> results) {
		// Find identified WorldObject interface.
		SCEClassNode obj = getIdentifiedClass("WorldObject");
		if(obj == null) return;
		
		for(SCEClassNode scn : classes.values()) {
			// If this class does not implement it, discard.
			if(!scn.interfaces.contains(obj.name)) continue;
			
			for(MethodNode mn : scn.methods) {
				// If the method does not return int, discard.
				if(!mn.desc.endsWith("I")) continue;
				
				// If this is a method which is not present in the Object
				// interface, discard it.
				MethodNode realmth = null;
				for(MethodNode objm : obj.methods) {
					if(objm.name.equals(mn.name) && objm.desc.equals(mn.desc))
						realmth = objm;
				}
				if(realmth == null) continue;
				
				// See if this method contains "return this.id & 0xffff";
				InstructionSearcher is = new InstructionSearcher(mn);
				RegexSearcher rs = new RegexSearcher(mn);
				AbstractInsnNode[] insns = rs.nextMatch(directPattern);
				if(insns == null) continue;
				
				// Check if there is a dummy parameter.
				IntInsnNode iin = ((IntInsnNode) is.getNext(Opcodes.BIPUSH));
				if(iin == null) {
					// We want the method from the top-level Object interface,
					// not the one from this particular subclass.
					System.out.println(" * No dummy parameter to object.getID");
					results.add(new Hook("worldObject.getID", obj, realmth));
					return;
				} else {
					System.out.println(" * Dummy parameter to object.getID: " + iin.operand);
					results.add(new Hook("worldObject.getID", obj, realmth, iin.operand));
					return;
				}
				
			}
		}
	}

	private void identifyType(String className, String hookName, InstructionSearcher is, List<Hook> results) {
		TypeInsnNode in = (TypeInsnNode) is.getNext(Opcodes.INSTANCEOF);
		if(getIdentifiedClass("WorldObject") == null) {
			identifyClass("WorldObject", classes.get(in.desc));
		}
		
		FieldInsnNode fin = (FieldInsnNode) is.getNext(Opcodes.GETFIELD);
		if(getIdentifiedClass("GroundTile") == null)
			identifyClass("GroundTile", classes.get(fin.owner));
		
		String type = fin.desc.substring(fin.desc.indexOf('L') + 1, fin.desc.length() - 1);
		String lowertype = Character.toLowerCase(className.charAt(0)) + className.substring(1);
		if(getIdentifiedClass(className) == null) {
			SCEClassNode scn = classes.get(type);
			identifyClass(className, scn);
			for(SCEClassNode all : classes.values()) {
				if(!all.superName.equals(scn.name))
					continue;
				for(FieldNode fn : all.fields) {
					if(fn.desc.equals("L" + getIdentifiedClass("Model").name + ";"))
						results.add(new Hook(lowertype + ".model", all, fn));
				}
			}
		}
		results.add(new Hook(hookName, fin));
	}
}
