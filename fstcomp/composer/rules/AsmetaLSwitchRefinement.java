package composer.rules;

import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class AsmetaLSwitchRefinement extends AbstractCompositionRule 
{
    public final static String COMPOSITION_RULE_NAME = "AsmetaLSwitchRefinement";
    

    public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
	    FSTTerminal terminalComp, FSTNonTerminal nonterminalParent) 
    {    	
    	System.out.println("Switch Refinement applied!!!");
 	terminalComp.setBody("Switch Refinement applied!");
    }
    
	@Override
	public String getRuleName() {
		return COMPOSITION_RULE_NAME;
	}
}
