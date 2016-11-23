package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.InstructionSearcher;
import net.sce.bytecode.RegexSearcher;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SoftwareToolkitTransform extends BytecodeTask {
	private static final String minmaxPattern = "iload aload getfield if_icmplt iload aload getfield "
			+ "if_icmpgt iload aload getfield if_icmplt iload aload getfield "
			+ "if_icmpgt";
	private static final String zClipPattern = "fload aload getfield i2f fcmpg iflt fload aload getfield i2f fcmpl ifle";
	private static final String scalePattern = "aload getfield i2f aload getfield getfield";
	
	private static final String matrixPattern = "aload getfield getfield aload getfield getfield iload " +
			"i2f fmul aload getfield getfield iload i2f fmul fadd aload getfield getfield iload i2f fmul fadd fadd";
	
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for (SCEClassNode scn : all) {
			if (scn.constants.containsKey("Pure Java")) result.add(scn);
		}
	}

	public boolean transform(SCEClassNode scn, List<Hook> results) {
		// super of SoftwareToolkit is Toolkit
		// class hierarchy:
		// Toolkit -> NativeToolkit? -> DirectX, OpenGL toolkits
		// Toolkit -> SoftwareToolkit
		SCEClassNode tk = classes.get(scn.superName);
		
		// Hook lonely index field from toolkit class
		identifyClass("Toolkit", tk);
		for(FieldNode f : tk.fields) {
			if((f.access & Opcodes.ACC_STATIC) == 0 && f.desc.equals("I"))
				results.add(new Hook("toolkit.index", tk, f));
		}
		
		identifyClass("SoftwareToolkit", scn);
		
		for(MethodNode mn : scn.methods) {
			if((mn.access & Opcodes.ACC_ABSTRACT) != 0 || !mn.desc.contains("[")) continue;
			RegexSearcher rs = new RegexSearcher(mn);
			
			AbstractInsnNode[] ins = rs.nextMatch(minmaxPattern);
			if(ins == null) continue;
			results.add(new Hook("softwareToolkit.minX", (FieldInsnNode) ins[2]));
			results.add(new Hook("softwareToolkit.maxX", (FieldInsnNode) ins[6]));
			results.add(new Hook("softwareToolkit.minY", (FieldInsnNode) ins[10]));
			results.add(new Hook("softwareToolkit.maxY", (FieldInsnNode) ins[14]));
			
			ins = rs.nextMatch(zClipPattern);
			results.add(new Hook("softwareToolkit.nearZ", (FieldInsnNode) ins[2]));
			results.add(new Hook("softwareToolkit.farZ", (FieldInsnNode) ins[8]));
			
			ins = rs.nextMatch(scalePattern);
			results.add(new Hook("softwareToolkit.scaleX", (FieldInsnNode) ins[1]));
			ins = rs.nextMatch(scalePattern);
			results.add(new Hook("softwareToolkit.scaleY", (FieldInsnNode) ins[1]));
			
			ins = rs.nextMatch(matrixPattern);
			results.add(new Hook("softwareToolkit.viewport", (FieldInsnNode) ins[1]));
			String cn = ((FieldInsnNode) ins[1]).desc;
			cn = cn.substring(1, cn.length() - 1);
			identifyClass("Viewport", classes.get(cn));
			results.add(new Hook("viewport.offsetZ", (FieldInsnNode) ins[2]));
			results.add(new Hook("viewport.z1", (FieldInsnNode) ins[5]));
			results.add(new Hook("viewport.z2", (FieldInsnNode) ins[11]));
			results.add(new Hook("viewport.z3", (FieldInsnNode) ins[18]));
			ins = rs.nextMatch(matrixPattern);
			results.add(new Hook("viewport.offsetX", (FieldInsnNode) ins[2]));
			results.add(new Hook("viewport.x1", (FieldInsnNode) ins[5]));
			results.add(new Hook("viewport.x2", (FieldInsnNode) ins[11]));
			results.add(new Hook("viewport.x3", (FieldInsnNode) ins[18]));
			ins = rs.nextMatch(matrixPattern);
			results.add(new Hook("viewport.offsetY", (FieldInsnNode) ins[2]));
			results.add(new Hook("viewport.y1", (FieldInsnNode) ins[5]));
			results.add(new Hook("viewport.y2", (FieldInsnNode) ins[11]));
			results.add(new Hook("viewport.y3", (FieldInsnNode) ins[18]));
			
			break;
		}
		
		for(SCEClassNode cn : classes.values()) {
			if(!cn.constants.containsKey("<br>(100%)")) continue;
			for(MethodNode mn : cn.methods) {
				if((mn.access & Opcodes.ACC_STATIC) == 0) continue;
				InstructionSearcher is = new InstructionSearcher(mn);
				LdcInsnNode ldc = is.getNextLDC("<br>(100%)");
				if(ldc == null) continue;
				FieldInsnNode fin;
				while((fin = (FieldInsnNode) is.getNext(Opcodes.GETSTATIC)) != null) {
					if(fin.desc.equals("L" + getIdentifiedClass("Toolkit").name + ";")) {
						results.add(new Hook("client.toolkit", fin));
						return true;
					}
				}
			}
		}
		
		return false;
	}
}