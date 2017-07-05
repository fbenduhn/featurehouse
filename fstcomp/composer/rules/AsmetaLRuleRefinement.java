package composer.rules;

import java.util.List;

import composer.CompositionException;
import composer.FSTGenComposer;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import metadata.CompositionMetadataStore;

public class AsmetaLRuleRefinement extends AbstractCompositionRule {
	private static final String KEYWORD_EXTEND_ORIGINAL = "/*@extend_original#";
	public final static String COMPOSITION_RULE_NAME = "AsmetaLRuleRefinement";
	List<FSTNode> refList, baseList;

	// terminalA = Refinement, terminalB = Base
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB, FSTTerminal terminalComp,
			FSTNonTerminal nonterminalParent) throws CompositionException {
		
		CompositionMetadataStore meta = CompositionMetadataStore.getInstance();

		if (terminalA.getBody().contains("@original")) {
			if (terminalA.getBody().startsWith("main")) {
				String newBodyB = terminalB.getBody().substring(terminalB.getBody().indexOf("=") + 1);
				String newBodyA = terminalA.getBody().replace("@original[]", newBodyB).replace("@original()", newBodyB);
				terminalComp.setBody(newBodyA);
			} else {
				String newBodyA = terminalA.getBody().replace("@original",
						terminalA.getName() + "_" + terminalA.getFeatureName() + "__wrapee__");
				String newBodyB = terminalB.getBody();
				// Delete main token
				newBodyB = newBodyB.substring(newBodyB.indexOf("rule"));
				int posEqual = newBodyB.indexOf("=") == -1 ? Integer.MAX_VALUE : newBodyB.indexOf("=");
				int posBracket = newBodyB.indexOf("(") == -1 ? Integer.MAX_VALUE : newBodyB.indexOf("(");
				String ruleName = newBodyB.substring(newBodyB.indexOf("r_"), Math.min(posEqual, posBracket));
				newBodyB = newBodyB.replace(ruleName,
						terminalA.getName() + "_" + terminalA.getFeatureName() + "__wrapee__");
				newBodyB += "\n";
				terminalComp.setBody(newBodyB + newBodyA);

			}
		} else {
			terminalComp.setBody(terminalA.getBody());
			String funcName = meta.getMethodName(terminalA);
			meta.putMapping(funcName, (terminalA.getOriginalFeatureName()), funcName);

		}
	}

	protected String getNewBody(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, String oldMethodName) {
		return terminalComp.getBody();
	}
	
	private FSTTerminal composeSwitchStatement(FSTNode refSwitch, FSTNode baseSwitch, FSTNonTerminal parent) {
		refSwitch.setParent(parent);
		baseSwitch.setParent(parent);
		parent.addChild(refSwitch);
		parent.addChild(baseSwitch);
		System.out.println("ref:" + refSwitch);
		System.out.println("base:" + baseSwitch);
		System.out.println("comp" + parent);

		if (!refSwitch.compatibleWith(baseSwitch))
			return null;
		System.out.println("abv " + baseSwitch.getParent().printFST(1));
		FSTNode node = new FSTGenComposer().compose(refSwitch, baseSwitch, parent);
		if (node instanceof FSTNonTerminal) {
			return null;
		}
		return (FSTTerminal) node;
	}

	@Override
	public String getRuleName() {
		return COMPOSITION_RULE_NAME;
	}
}
