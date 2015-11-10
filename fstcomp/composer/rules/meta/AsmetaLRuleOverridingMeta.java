package composer.rules.meta;


import composer.rules.AsmetaLRuleOverriding;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class AsmetaLRuleOverridingMeta extends AsmetaLRuleOverriding{

	
	@Override
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent){
		
		
		if(terminalA.getBody().contains("@original")){
			//terminalA.getFeatureName();
			int indexEqualA = terminalA.getBody().indexOf("=");
			
			StringBuilder newBody = new StringBuilder(terminalA.getBody());
			
			//TODO: replace "true" with FeatureVar name
			String featureName = terminalA.getFeatureName();
			newBody.insert(indexEqualA + 1, "\n\t if " + featureName + " then");
			newBody.append("\n\t else \n\t\t @original[]\n\t endif");
			
			terminalA.setBody(newBody.toString());
		} else {
			//terminalA.getFeatureName();
			int indexEqualA = terminalA.getBody().indexOf("=");
			int indexEqualB = terminalB.getBody().indexOf("=");
			String ruleBodyB = terminalB.getBody().substring(indexEqualB + 1);
			
			StringBuilder newBody = new StringBuilder(terminalA.getBody());
			
			//TODO: replace "true" with FeatureVar name
			String featureName = terminalA.getFeatureName();
			newBody.insert(indexEqualA + 1, "\n\t if " + featureName + " then");
			newBody.append("\n\t else \n\t\t " + ruleBodyB +"\n\t endif");
			
			terminalComp.setBody(newBody.toString());
		}
		
		super.compose(terminalA, terminalB, terminalComp, nonterminalParent);		
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
			System.out.println(child.getBody());
		}
	}
	
}
