package composer.rules;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cide.gparser.OffsetCharStream;
import cide.gparser.ParseException;
import composer.CompositionException;
import composer.FSTGenComposer;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import de.ovgu.cide.fstgen.parsers.generated_AsmetaL_rules.AsmetaLParser_rules;

public class AsmetaLRuleRefinement extends AbstractCompositionRule {
	private static final String KEYWORD_EXTEND_ORIGINAL = "/*@extend_original#";
	public final static String COMPOSITION_RULE_NAME = "AsmetaLRuleRefinement";
	List<FSTNode> refList, baseList;

	// terminalA = Refinement, terminalB = Base
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB, FSTTerminal terminalComp,
			FSTNonTerminal nonterminalParent) throws CompositionException {
		this.refList = new ArrayList<FSTNode>();
		this.baseList = new ArrayList<FSTNode>();

		if (isFinalRule(terminalB))
			throw new CompositionException(null, terminalA,
					"Previously you used the keyword \\final_rule. Thus you can't refine rule!");

		System.out.println("metaTerminalComp " + terminalComp.getBody());

		AsmetaLParser_rules base = new AsmetaLParser_rules(new OffsetCharStream(new StringReader(terminalB.getBody())));
		try {
			base.RuleDeclaration(false);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AsmetaLParser_rules ref = new AsmetaLParser_rules(new OffsetCharStream(new StringReader(terminalB.getBody())));
		try {
			ref.RuleDeclaration(false);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getSwitchRules(base.getRoot(), baseList);
		getSwitchRules(ref.getRoot(), refList);
		ArrayList<FSTNode> mergedList = new ArrayList<FSTNode>();
		
		for (FSTNode baseSwitch : baseList) {
			for (FSTNode refSwitch : refList) {
				if (baseSwitch.getName().equals(refSwitch.getName())) {
					FSTNode composedSwitch = composeSwitchStatement(refSwitch,baseSwitch);
					mergedList.add(composedSwitch);
					baseList.remove(baseSwitch);
					baseList.remove(refSwitch);
				}
				
			}

		}
		mergedList.addAll(baseList);
		mergedList.addAll(refList);
		for(FSTNode n:mergedList){
			((FSTTerminal) n).getBody();
		}
	}

	private FSTNode composeSwitchStatement(FSTNode refSwitch, FSTNode baseSwitch) {
		return new FSTGenComposer().compose(refSwitch, baseSwitch);
	}

	private void getSwitchRules(FSTNode node, List<FSTNode> list) {

		FSTNode current = node;

		if (current instanceof FSTNonTerminal) {
			for (FSTNode child : ((FSTNonTerminal) current).getChildren()) {
				getSwitchRules(child, list);
			}
			return;
		}

		FSTTerminal caseRule = (FSTTerminal) current;

		if (caseRule.getType().equals("CaseRule")) {
			list.add((FSTTerminal) node);
		}

		return;

	}

	private boolean isFinalRule(FSTTerminal terminalB) {

		return false;
	}

	private int getKeywordPos(String ruleRef) {
		return ruleRef.indexOf(KEYWORD_EXTEND_ORIGINAL);
	}

	@Override
	public String getRuleName() {
		return COMPOSITION_RULE_NAME;
	}
}
