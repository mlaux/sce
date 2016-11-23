package net.sce.updater.transforms;

import java.util.List;
import java.util.Set;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;
import net.sce.bytecode.SCEMethodNode;

public class PlayerTransform extends BytecodeTask {
	public void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result) {
		for (SCEClassNode scn : all) {
			Set<SCEMethodNode> set = scn.constants.get("gdn1");
			if (set != null) result.add(scn);
		}
	}

	public boolean transform(SCEClassNode scn, List<Hook> results) {
		identifyClass("Player", scn);
		SCEClassNode character = classes.get(scn.superName);
		if (character != null) {
			identifyClass("Character", character);
			for(SCEClassNode cn : classes.values()) {
				if(cn.superName.equals(character.name) && !cn.equals(scn)) {
					identifyClass("NPC", cn);
				}
			}
			SCEClassNode anim = classes.get(character.superName);
			if (anim != null) {
				identifyClass("AnimableEntity", anim);
				SCEClassNode entity = classes.get(anim.superName);
				if(entity != null) {
					identifyClass("Entity", entity);
					SCEClassNode interactable = classes.get(entity.superName);
					if(interactable != null) {
						identifyClass("SceneNode", interactable);
					}
				}
			}
		}
		return true;
	}
}
