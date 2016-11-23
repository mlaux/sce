package net.sce.updater.transforms;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.InstructionSearcher;
import net.sce.bytecode.SCEClassNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class InterfaceTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for (SCEClassNode scn : all) {
			for (MethodNode mn : scn.methods) {
				InstructionSearcher is = new InstructionSearcher(mn);
				if (is.getNextLDC(-65536) != null && is.getNextPush(Opcodes.SIPUSH, 4095) != null) {
					result.add(scn);
				}
			}
		}
	}

	
	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("Interface", scn);
		for (FieldNode fn : scn.fields) {
			if (fn.desc.equals("[L" + scn.name + ";")) {
				results.add(new Hook("interface.children", scn, fn));
			}
		}
		for (MethodNode mn : scn.methods) {
			if (mn.access != Opcodes.ACC_ABSTRACT && mn.access != Opcodes.ACC_NATIVE && mn.access == Opcodes.ACC_FINAL) {
				InstructionSearcher is = new InstructionSearcher(mn);
				if (is.getNextLDC(-65536) != null && is.getNextPush(Opcodes.SIPUSH, 4095) != null) {
					List<AbstractInsnNode> getFields = is.analyze(Opcodes.GETFIELD);
					List<AbstractInsnNode> putFields = is.analyze(Opcodes.PUTFIELD);
                    results.add(new Hook("interface.getType", (FieldInsnNode) putFields.get(0)));
                    results.add(new Hook("interface.getContentType", (FieldInsnNode)  putFields.get(3)));
                    results.add(new Hook("interface.getX", (FieldInsnNode)  putFields.get(4)));
                    results.add(new Hook("interface.getY", (FieldInsnNode)  putFields.get(5)));
                    results.add(new Hook("interface.getWidth", (FieldInsnNode)  putFields.get(6)));
                    results.add(new Hook("interface.getHeight", (FieldInsnNode)  putFields.get(7)));
                    results.add(new Hook("interface.getId", (FieldInsnNode)  getFields.get(3)));
                    results.add(new Hook("interface.getParentId", (FieldInsnNode)  putFields.get(12)));
                    results.add(new Hook("interface.isHidden", (FieldInsnNode)  putFields.get(15)));
                    results.add(new Hook("interface.getMaxHorizontalScroll", (FieldInsnNode)  putFields.get(17)));
                    results.add(new Hook("interface.getMaxVerticalScroll", (FieldInsnNode)  putFields.get(18)));
                    results.add(new Hook("interface.getTextureId", (FieldInsnNode)  putFields.get(20)));
                    results.add(new Hook("interface.getBorderThickness", (FieldInsnNode)  putFields.get(25)));
                    results.add(new Hook("interface.getShadowColor", (FieldInsnNode)  putFields.get(26)));
                    results.add(new Hook("interface.isFlippedVertically", (FieldInsnNode)  putFields.get(27)));
                    results.add(new Hook("interface.isFlippedHorizontally", (FieldInsnNode)  putFields.get(28)));
                    results.add(new Hook("interface.getTextColor", (FieldInsnNode)  putFields.get(29)));
                    results.add(new Hook("interface.getModelType", (FieldInsnNode)  putFields.get(30)));
                    results.add(new Hook("interface.getModelId", (FieldInsnNode)  putFields.get(31)));
                    results.add(new Hook("interface.getModelRotationX", (FieldInsnNode)  putFields.get(35)));
                    results.add(new Hook("interface.getModelRotationY", (FieldInsnNode)  putFields.get(36)));
                    results.add(new Hook("interface.getModelRotationZ", (FieldInsnNode)  putFields.get(37)));
                    results.add(new Hook("interface.getModelZoom", (FieldInsnNode)  putFields.get(38)));
                    results.add(new Hook("interface.getFontId", (FieldInsnNode)  putFields.get(47)));
                    results.add(new Hook("interface.getText", (FieldInsnNode)  putFields.get(49)));
                    results.add(new Hook("interface.isInventory", (FieldInsnNode)  putFields.get(53)));
                    results.add(new Hook("interface.isFilled", (FieldInsnNode) putFields.get(58)));
                    results.add(new Hook("interface.getComponentName", (FieldInsnNode)  putFields.get(67)));
                    results.add(new Hook("interface.getActions", (FieldInsnNode)  putFields.get(68)));
                    results.add(new Hook("interface.getSelectedAction", (FieldInsnNode) putFields.get(70)));
				}
			}
		}
		return true;
	}
}
