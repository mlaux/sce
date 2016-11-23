package net.sce.updater.transforms;

import java.util.List;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.tree.FieldNode;

public class SignlinkTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for (SCEClassNode scn : all) {
			for (FieldNode fieldNode : scn.fields) {
				if (fieldNode.desc.equalsIgnoreCase("Ljava/awt/EventQueue;"))
					result.add(scn);
			}
		}
	}

	public boolean transform(SCEClassNode scn, List<Hook> results) {
		System.out.println(" * Signlink identified as " + scn.name);
		for (FieldNode field : scn.fields) {
			if (field.desc.equals("Ljava/awt/EventQueue;")) {
				Hook hook = new Hook("signlink.eventQueue", scn, field);
				results.add(hook);
			}
		}
		return true;
	}
}
