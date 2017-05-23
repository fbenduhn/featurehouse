asm test

signature: 
enum domain Sign = {BAR | CHERRY | DOLLAR | SMILEY | DYNAMITE } 

	
	domain Money subsetof Integer 
	domain BetDomain subsetof Integer 
	domain MultDomain subsetof Integer 
	dynamic controlled playerBudget: Money 
	dynamic controlled slotMachineBudget: Money 
	derived insertedMoney: Money 
	dynamic monitored insertedMoney: BetDomain 
	derived multFactor: Sign -> MultDomain


	
definitions: 
	domain Money = {1..100} 

domain BetDomain = {1..5} 
domain MultDomain = {1..5} 
	function insertedMoney = 1 

	function multFactor($s in Sign) =
		switch($s)
			case BAR: 1
			case CHERRY: 2
			case DOLLAR: 3
			case SMILEY: 4
			case DYNAMITE: 5
		endswitch 

	rule r_win($x in Sign) =
				par
					playerBudget := playerBudget + multFactor($x)*insertedMoney
					slotMachineBudget := slotMachineBudget - multFactor($x)*insertedMoney 
				endpar 
				
	rule r_lose =
				par
					playerBudget := playerBudget - insertedMoney
					slotMachineBudget := slotMachineBudget + insertedMoney 
				endpar 
		
rule r_game =
		choose $x in Sign, $y in Sign, $z in Sign with true do
			if ($x = $y and $y = $z) then
			r_win[$x]
			else				
			r_lose[]	
			endif 
		

	CTLSPEC ag(playerBudget + slotMachineBudget = 100) 
	CTLSPEC ef(playerBudget = 23 and ex(playerBudget = 32)) 
	CTLSPEC eg(playerBudget >= 40 and playerBudget <= 60)


	main rule r_Main =
		if(playerBudget >= 3 and slotMachineBudget >= 9) then
			r_game[]
		endif



default  init s0:
	function playerBudget = 50
	function slotMachineBudget = 50
