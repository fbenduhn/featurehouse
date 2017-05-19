package composer.rules.meta;


import composer.rules.AsmetaLInvariantConjunction;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class AsmetaLInvariantConjunctionMeta extends AsmetaLInvariantConjunction{

	
	@Override
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent){

		super.compose(terminalA, terminalB, terminalComp, nonterminalParent);

		int indexColonComp = terminalComp.getBody().indexOf(":");
		int indexColonB = terminalB.getBody().indexOf(":");
		String invariantBodyB = terminalB.getBody().substring(indexColonB + 1);
		
		StringBuilder newBody = new StringBuilder(terminalComp.getBody());
		
		String featureName = terminalA.getFeatureName().toLowerCase();
		newBody.insert(indexColonComp + 1, "\n (" + featureName + " implies (");
		newBody.insert(indexColonComp, ", " + featureName);
		newBody.append(")) and (not(" + featureName + ") implies ( " + invariantBodyB +"))");
		
		terminalComp.setBody(newBody.toString());
	}
	
}
