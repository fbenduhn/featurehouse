rule r_2 =		switch doors2 
			case OPEN:
				doors := CLOSING
			case CLOSING:
				doors := CLOSED
			case OPENING:
				doors := CLOSING
		endswitch