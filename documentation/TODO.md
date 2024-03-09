App Features Backlog:
	- Checkmate / End of game
	- En pasante
	- Username at top of board
	- Turn indicator
    - Remove 'Wait Menu' after player accepts invitation
	

Server Backlog:
	- Graceful failures
	- User timeout
    - Log active users once every x seconds
    - Match declining.
    - Add server documentation. (Default Port, IP Address, etc)


SQL Server:
    - Create/Use a SQL Server to store/validate user information and to store previous games.
    -- NOTE: I would like to get the app to a working player stage before adding this feature.


BOTH:
    - When client declines invitation, alert user that the invitation was declined. (Currently starts a game on invitor's side)
    - When a repeat packet is received and not acknowledged, an endless loop of "Repeat packet number. Ignoring. Opcode: x" will display.


