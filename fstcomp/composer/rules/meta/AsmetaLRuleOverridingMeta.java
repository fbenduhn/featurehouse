package composer.rules.meta;


import composer.rules.AsmetaLRuleOverriding;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class AsmetaLRuleOverridingMeta extends AsmetaLRuleOverriding{

	
	@Override
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent){
		

		//Get names of both rules, by getting a substring of the rule body up to the first equality sign
		int indexEqualA = terminalA.getBody().indexOf("=");
		int indexEqualB = terminalB.getBody().indexOf("=");
		String ruleNameA = terminalA.getBody().substring(0, indexEqualA);
		String ruleNameB = terminalB.getBody().substring(0, indexEqualB);
		String bodyTempB = "";
		
		/**if the rule name already contains the string "__wrappee__", that means, it was composed before
		and now two rules are in this terminal(the ...__wrappee__ and the original.
		To change that, save the whole string temporally and cut the wrapppee out.*/
		if(ruleNameB.contains("__wrapee__")){
			int indexOriginalRuleB = terminalB.getBody().lastIndexOf(ruleNameA);
			String newBodyB = terminalB.getBody().substring(indexOriginalRuleB);
			bodyTempB = terminalB.getBody().substring(0, indexOriginalRuleB);
			terminalB.setBody(newBodyB);	
		}
		
		//if it contains @original --> refinement; else replacement
		if(terminalA.getBody().contains("@original")){
			StringBuilder newBody = new StringBuilder(terminalA.getBody());
			
			//since we don't know the name of the feature, we have refined(which will be in the name of the wrappee rule)
			//we have to insert another "@original[]"-string, which will be replaced with the rule call later.
			String featureName = terminalA.getFeatureName().toLowerCase() + "__refinementVar__";
			newBody.insert(indexEqualA + 1, "\n\t if " + featureName + " then");
			newBody.append("\n\t else \n\t\t @original[]\n\t endif");
			
			terminalA.setBody(newBody.toString());
		} else {
			String ruleBodyB = terminalB.getBody().substring(indexEqualB + 1);
			
			StringBuilder newBody = new StringBuilder(terminalA.getBody());
			
			//when we replace the original rule, no code repetition can occur, so we can just insert the whole body in the else tree 
			String featureName = terminalA.getFeatureName().toLowerCase() + "__refinementVar__";
			newBody.insert(indexEqualA + 1, "\n\t if " + featureName + " then");
			newBody.append("\n\t else \n\t\t " + ruleBodyB +"\n\t endif");
			
			terminalComp.setBody(newBody.toString());
		}
		//now we call the compose method of AsmetaLRuleOverriding to replace all @originals
		super.compose(terminalA, terminalB, terminalComp, nonterminalParent);		
		
		//finally we have to change the body of terminalB back to what it was before the cutting-out
		if(ruleNameB.contains("__wrapee__")){
			terminalB.setBody(bodyTempB + terminalB.getBody());
			terminalComp.setBody(bodyTempB + terminalComp.getBody());	
		}
	}
	
	@Override
	public void postCompose(FSTTerminal child) {
		if (child.getBody().trim().startsWith("main rule")){
			int indexEqualChild = child.getBody().indexOf("=");
			
			StringBuilder newBody = new StringBuilder(child.getBody());
			
			//TODO: replace "valid" with unique name
			newBody.insert(indexEqualChild + 1, "\n\t if valid then");
			newBody.append("\n\t endif");
			child.setBody(newBody.toString());
		}
	}
	
}
