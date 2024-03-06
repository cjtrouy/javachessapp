This is the client/user side of a chess desktop application that is created using Java. It connects to a remote server running off of my network. 
To use the application, you must either build a .jar file OR run the 'startgame.java' code. Java 8 or newer must be installed on your system and viewable by
the application.

The server code is currently *a tad* wonky and will not always work properly. Work is currently being done to the server to make it more reliable.
If the server is not running correctly, feel free to SSH into it and restart it. *Add SSH URL* *Migrate server from Windows based to Linux. Should NOT need to change source code as it is JAVA*

To import this code on your own machine, please see [Importing Steps](https://github.com/cjtrouy/javachessapp/blob/master/documentation/ImportingCode.txt).

*Add this to its own document*
If running on a windows machine, please open inbound access for UDP packets over all ports. To do this, access 'Windows Defender Firewall > Advanced Settings > Inbound Rules' and click 'New Rule' on the right hand side.
Select 'Port', 'UDP', and 'All local ports'. Next select 'Allow the connection' and then check the boxes that you are comfortable with for when this rule is active (Domain, Private, and/or Public networks).
Lastly, name the rule however you would like, and optionally add a description. By pressing 'Finish', the rule will become active.
