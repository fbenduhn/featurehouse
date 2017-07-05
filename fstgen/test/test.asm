asm LGS_var
import ../../STDL/StandardLibrary
import ../../STDL/CTLlibrary
import ../../STDL/LTLlibrary

signature:
	enum domain HandleStatus = {UP | DOWN}
	enum domain DoorStatus = {CLOSED | OPENING | OPEN | CLOSING}
	enum domain GearStatus = {RETRACTED | EXTENDING | EXTENDED | RETRACTING}
	dynamic monitored handle: HandleStatus
	dynamic controlled doors: DoorStatus
	dynamic controlled gears: GearStatus
	dynamic monitored gm: Boolean
	derived valid: Boolean
	
definitions:
function valid = gm

	rule r_closeDoor_gm =
		switch doors
			case OPEN:
				doors := CLOSING
			case CLOSING:
				doors := CLOSED
			case OPENING:
				doors := CLOSING
		endswitch
	rule r_closeDoor = r_closeDoor_gm[]
		
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

	rule r_retractionSequence = r_retractionSequence_gm[]
	CTLSPEC ag((doors = CLOSED and gears = EXTENDED and ag(handle = DOWN)) implies ag(doors = CLOSED and gears = EXTENDED))
	CTLSPEC ag((doors = CLOSED and gears = RETRACTED and ag(handle = UP)) implies ag(doors = CLOSED and gears = RETRACTED))
	CTLSPEC ag((gears = EXTENDING and handle = DOWN) implies ax(gears = EXTENDED))
	CTLSPEC ag((gears = EXTENDING and handle = UP) implies ax(gears = RETRACTING))
	CTLSPEC ag((gears = RETRACTING and handle = UP) implies ax(gears = RETRACTED))
	CTLSPEC ag((gears = RETRACTING and handle = DOWN) implies ax(gears = EXTENDING))
	CTLSPEC ag((gears = EXTENDED and doors = CLOSING and handle = DOWN) implies ax(doors = CLOSED))
	CTLSPEC ag((gears = EXTENDED and doors = CLOSING and handle = UP) implies ax(doors = OPENING))
	CTLSPEC ag((gears = RETRACTED and doors = OPENING and handle = DOWN) implies ax(doors = OPEN))
	CTLSPEC ag((gears = RETRACTED and doors = OPENING and handle = UP) implies ax(doors = CLOSING))
	LTLSPEC g(g(handle = DOWN) implies f(gears = EXTENDED and doors = CLOSED))
	LTLSPEC g(g(handle = UP) implies f(gears = RETRACTED and doors = CLOSED))
	LTLSPEC g(g(handle = DOWN) implies x(g(gears != RETRACTING)))
	LTLSPEC g(g(handle = UP) implies x(g(gears != EXTENDING)))
	CTLSPEC ag(ef(handle = UP)) //the handle can always become UP
	CTLSPEC ag(ef(handle = DOWN)) //the handle can always become DOWN
	CTLSPEC (forall $s in DoorStatus with ag(ef(doors = $s)))
	CTLSPEC (forall $s in GearStatus with ag(ef(gears = $s)))
		invariant over valid: valid
	invariant over gears, doors: (gears = EXTENDING or gears = RETRACTING) implies doors = OPEN
	invariant over gears, doors: doors = CLOSED implies (gears = EXTENDED or gears = RETRACTED)
	 
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
		
	rule r_outgoingSequence = r_outgoingSequence_gm[]		
	
	


	main rule r_Main =
		if handle = UP then
			r_retractionSequence[]
		else
			r_outgoingSequence[]
		endif

default init s0:
	function doors = CLOSED
	function gears = EXTENDED