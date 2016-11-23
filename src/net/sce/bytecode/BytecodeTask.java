package net.sce.bytecode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sce.updater.Updater;

public abstract class BytecodeTask {
	public Map<String, SCEClassNode> classes = new HashMap<String, SCEClassNode>();
	
	public void setClasses(Map<String, SCEClassNode> classes) {
		this.classes = classes;
	}
	public abstract void getPossibleClasses(List<SCEClassNode> all, List<SCEClassNode> result);

	public abstract boolean transform(SCEClassNode scn, List<Hook> results);

	public void identifyClass(String descr, SCEClassNode scn) {
		System.out.println(" * " + descr + " identified as " + scn.name);
		descr = descr.toLowerCase();
		Updater.identifiedClasses.put(descr, scn);
	}

	public SCEClassNode getIdentifiedClass(String descr) {
		descr = descr.toLowerCase();
		return Updater.identifiedClasses.get(descr);
	}
}
