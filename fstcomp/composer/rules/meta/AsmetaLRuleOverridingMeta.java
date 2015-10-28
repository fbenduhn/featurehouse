package composer.rules.meta;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import composer.CompositionException;
import composer.rules.AsmetaLRuleOverriding;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class AsmetaLRuleOverridingMeta extends AsmetaLRuleOverriding{

	
	@Override
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent){
		System.out.println("in asm meta compose");
		super.compose(terminalA, terminalB, terminalComp, nonterminalParent);
		
		if(replaceOriginal(terminalA)){
			String ruleName = terminalA.getName();
			System.out.println("TerminalComp Name: " + terminalComp.getName());
			//terminalA.getFeatureName();
			//StringBuilder newBody = new StringBuilder(terminalComp.getBody());
			//newBody.insert(offset, str)
		}
		
		System.out.println("metaTerminalComp " + terminalComp.getBody());
		
//		if (!super.replaceOriginal(terminalA)) {
//			// all sub methods will not exist when this feature is selected
//			// TODO experimental
//			// dont do this if generating runtime assertions  
//			List<FSTNode> children = ((FSTNonTerminal)nonterminalParent.getParent()).getChildren();
//			for (FSTNode child : children) {
//				if (child.getType().equals("MethodDeclarationWithSpec")) {
//					FSTTerminal terminal = (FSTTerminal)((FSTNonTerminal)child).getChildren().get(2);
//					if (terminal.getName().equals(terminalA.getName())) {
//						setNotFeature(child, getFeatureName(terminalA));
//					}
//				}
//			}
//		}
	}
	
	/**
	 * @param terminalA
	 * @return
	 */
	protected boolean replaceOriginal(FSTTerminal terminalA) {
		return terminalA.getBody().matches("(?s).*\\s*original\\s*.*");
	}
}
