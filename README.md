# IST-ObjectOrientedPrograming
Practical component of university subject. Program written in Java to manage train itineraries based on the available portuguese stations and services. It includes a Main Menu that conects to the secondary Menus, allowing someone to simulate a journey and choose amongst several possible itineraries. each passenger can  have their records in the application, and also save and load records. This application uses several java design patterns such as: Command, Visitor, Strategy. 

Example of usage: 

java -Dimport=A-08-06-M-ok.import -Din=A-08-06-M-ok.in -Dout=test86.outhyp app.App

diff test86.outhyp expected/A-08-06-M-ok.out
