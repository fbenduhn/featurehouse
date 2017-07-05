package composer.rules;

import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class AsmetaLFunctionConjunction extends AbstractCompositionRule 
{
	
	private static final String ASMETAL_ASSIGNMENT_OPERATOR = "=";
    private static final String FUNCTION = "function";
	public final static String COMPOSITION_RULE_NAME = "AsmetaLFunctionConjunction";
	private static final String ASMETAL_CONJUNCTION = " and ";
    

    public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
	    FSTTerminal terminalComp, FSTNonTerminal nonterminalParent) 
    {    	
    	String termA = getFunctionTerm(terminalA);   
    	String termB = getFunctionTerm(terminalB);    	
    	
    	StringBuffer newBody = new StringBuffer();
    	newBody.append(FUNCTION);
    	newBody.append(terminalA.getName());
    	newBody.append(ASMETAL_ASSIGNMENT_OPERATOR);
    	newBody.append(termA);
    	newBody.append(ASMETAL_CONJUNCTION);
    	newBody.append(termB);
    }

	private String getFunctionTerm(FSTTerminal terminal) {
		return terminal.getBody().substring(terminal.getBody().indexOf("="), terminal.getBody().length());
	}
    
	@Override
	public String getRuleName() {
		return COMPOSITION_RULE_NAME;
	}
}
