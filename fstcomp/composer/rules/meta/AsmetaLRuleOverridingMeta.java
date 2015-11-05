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
		
		
		if(terminalA.getBody().contains("@original")){
			//terminalA.getFeatureName();
			int indexEqualA = terminalA.getBody().indexOf("=");
			
			StringBuilder newBody = new StringBuilder(terminalA.getBody());
			
			//TODO: replace "true" with FeatureVar name
			newBody.insert(indexEqualA + 1, "\n\t if true then");
			newBody.append("\n\t else \n\t\t @original[]\n\t endif");
			
			terminalA.setBody(newBody.toString());
		} else {
			//terminalA.getFeatureName();
			int indexEqualA = terminalA.getBody().indexOf("=");
			int indexEqualB = terminalB.getBody().indexOf("=");
			String ruleBodyB = terminalB.getBody().substring(indexEqualB + 1);
			
			StringBuilder newBody = new StringBuilder(terminalA.getBody());
			
			//TODO: replace "true" with FeatureVar name
			newBody.insert(indexEqualA + 1, "\n\t if true then");
			newBody.append("\n\t else \n\t\t " + ruleBodyB +"\n\t endif");
			
			terminalComp.setBody(newBody.toString());
		}
		System.out.println("terminalA: " + terminalA.getBody());
		System.out.println("terminalB: " + terminalB.getBody());
		System.out.println("terminalComp: " + terminalComp.getBody());
		super.compose(terminalA, terminalB, terminalComp, nonterminalParent);		

		
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
	
//	/**
//	 * @param terminalA
//	 * @return
//	 */
//	protected boolean replaceOriginal(FSTTerminal terminalA) {
//		return terminalA.getBody().matches("(?s).*original.*");
//	}
}
