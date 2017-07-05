asm LGS
import ../../STDL/StandardLibrary
import ../../STDL/CTLlibrary
import ../../STDL/LTLlibrary


signature:


	//EV
	enum domain CylinderStatus = {CYLINDER_RETRACTED | CYLINDER_EXTENDING | CYLINDER_EXTENDED | CYLINDER_RETRACTING}
	

	dynamic controlled ev : Boolean
	dynamic monitored evInit : Boolean



definitions:
	//Feature Model
	function valid = gmInit and (not evInit or gmInit) 
		
	function cylindersDoors =
		switch doors
			case OPEN:CYLINDER_EXTENDED
			case OPENING:CYLINDER_EXTENDING
			case CLOSING:CYLINDER_RETRACTING
			case CLOSED:CYLINDER_RETRACTED
		endswitch

	function cylindersGears =
		switch gears
			case RETRACTED:	CYLINDER_RETRACTED
			case EXTENDING:	CYLINDER_EXTENDING
			case EXTENDED:	CYLINDER_EXTENDED
			case RETRACTING: CYLINDER_RETRACTING
		endswitch
		
			
	rule r_closeDoor_gm =
		switch doors
			case OPEN:
				doors := CLOSING
			case CLOSING:
				doors := CLOSED
			case OPENING:
				doors := CLOSING
		endswitch

rule r_closeDoor_ev =
	switch doors
			case OPEN:
			par
				doors := CLOSING//original
				closeDoorsElectroValve := true
			endpar
			case CLOSING:
			par
				doors := CLOSED//original
				generalElectroValve := false
				closeDoorsElectroValve := false
			endpar
			case OPENING:
			par
				doors := CLOSING//original
				closeDoorsElectroValve := true
				openDoorsElectroValve := false
			endpar
		endswitch

	rule r_closeDoor =
		if(not ev)then
	r_closeDoor_gm[]
		else
	r_closeDoor_ev[]
	endif
		
	rule r_retractionSequence_gm =
		if gears != RETRACTED then
			switch doors
				case CLOSED:
					doors := OPENING
				case CLOSING:
					doors := OPENING
				case OPENING:
					doors := OPEN
				case OPEN:
					switch gears
						case EXTENDED:
							gears := RETRACTING
						case RETRACTING:
							gears := RETRACTED
						case EXTENDING:
							gears := RETRACTING
					endswitch
			endswitch
		else
			r_closeDoor[]
		endif
		
	rule r_retractionSequence_ev =

		if gears != RETRACTED then
				switch doors
				case CLOSED:
					par
						generalElectroValve := true
						openDoorsElectroValve := true
						doors := OPENING//original
					endpar
				case CLOSING:
					par
						closeDoorsElectroValve := false
						openDoorsElectroValve := true
						doors := OPENING//original
					endpar
				case OPENING:
					par
						openDoorsElectroValve := false
						doors := OPEN//original
					endpar
				case OPEN:
					 switch gears
						case EXTENDED:
							par
								retractGearsElectroValve := true
								gears := RETRACTING//original
							endpar
						case RETRACTING:
							par
								retractGearsElectroValve := false
								gears := RETRACTED//original
							endpar
						case EXTENDING:
							par
								extendGearsElectroValve := false
								retractGearsElectroValve := true
								gears := RETRACTING//original
							endpar
					endswitch
			endswitch
		else
			r_closeDoor[]
		endif

	
	rule r_retractionSequence =
	if(not ev)then
	r_retractionSequence_gm[]
	else
	r_retractionSequence_ev[]	
	endif
		
	rule r_outgoingSequence_gm =
		if gears != EXTENDED then
			switch doors
				case CLOSED:
					doors := OPENING
				case OPENING:
					doors := OPEN
				case CLOSING:
					doors := OPENING
				case OPEN:
					switch gears
						case RETRACTED:
							gears := EXTENDING
						case EXTENDING:
							gears := EXTENDED
						case RETRACTING:
							gears := EXTENDING
					endswitch
			endswitch
		else
			r_closeDoor[]
		endif

	rule r_outgoingSequence_ev =
		if gears != EXTENDED then
			switch doors
				case CLOSED:
				par
					generalElectroValve := true 
					openDoorsElectroValve := true
					doors := OPENING
				endpar
				

				case OPENING:
				par
					openDoorsElectroValve := false
					doors := OPEN
				endpar
					
				case CLOSING:
					par
						closeDoorsElectroValve := false
						openDoorsElectroValve := true
						doors := OPENING
					endpar	
				case OPEN:
					switch gears
						case RETRACTED:
							par
								extendGearsElectroValve := true
								gears := EXTENDING
							endpar
						case EXTENDING:
							par
								extendGearsElectroValve := false
								gears := EXTENDED
							endpar
						case RETRACTING:
							par
								extendGearsElectroValve := true
								retractGearsElectroValve := false
								gears := EXTENDING
							endpar
							
					endswitch
			endswitch
		else
			r_closeDoor[]
		endif


	rule r_outgoingSequence =
	if(not ev)then
	r_outgoingSequence_gm[]
	else
	r_outgoingSequence_ev[]
	endif
	

		


	CTLSPEC ev implies (ag((extendGearsElectroValve or retractGearsElectroValve) implies doors = OPEN))
	LTLSPEC ev implies (g((extendGearsElectroValve or retractGearsElectroValve) implies doors = OPEN))
	//R32
	CTLSPEC  ev implies (ag((openDoorsElectroValve or closeDoorsElectroValve) implies (gears = RETRACTED or gears = EXTENDED)))
	LTLSPEC  ev implies (g((openDoorsElectroValve or closeDoorsElectroValve) implies (gears = RETRACTED or gears = EXTENDED)))
	//R41
	CTLSPEC  ev implies (ag(not(openDoorsElectroValve and closeDoorsElectroValve)))
	LTLSPEC  ev implies (g(not(openDoorsElectroValve and closeDoorsElectroValve)))
	//R42
	CTLSPEC  ev implies (ag(not(extendGearsElectroValve and retractGearsElectroValve)))
	LTLSPEC  ev implies (g(not(extendGearsElectroValve and retractGearsElectroValve)))
	//R51
	CTLSPEC  ev implies (ag((openDoorsElectroValve or closeDoorsElectroValve or extendGearsElectroValve or retractGearsElectroValve) implies generalElectroValve))
	LTLSPEC  ev implies (g((openDoorsElectroValve or closeDoorsElectroValve or extendGearsElectroValve or retractGearsElectroValve) implies generalElectroValve))

	
	
	
default init s0:

	function generalElectroValve = false
	function extendGearsElectroValve = false
	function retractGearsElectroValve = false
	function openDoorsElectroValve = false
	function closeDoorsElectroValve = false
	function gm = gmInit
	function ev = evInit