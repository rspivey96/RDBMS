# CSCE-315 Group Project 1 - RDBMS

## Project Summary

**RDBMS** is the second programming assignment for CSCE 315 at Texas A&M, compromising of 3 parts: an engine, a parser, and an interactive system. Check out our design documents detailing the entire program in the design documents [directory](https://github.tamu.edu/reedspivey/ComputersAreHardP1D4/tree/master/design_documents). Compare the development log [below](https://github.tamu.edu/reedspivey/ComputersAreHardP1D4#development-log) with the commit history for a detailed list of our meetings and productivity. If you want to see the guidelines for the project, look [here](http://ecologylab.net/courses/studio/assignments/team1.html).

## Members
Cory Avra<br>
Jason Alonzo<br>
Reed Spivey

## Aggie Code of Honor
An Aggie does not lie, cheat or steal or tolerate those who do.<br>
This project represents many hours of hard work. Plagiarizing my project for your own grade not only goes against the Aggie Code of Honor, but it is detrimental to your education. Respect yourself, the University, and my work by following the Code.

## Build Instructions
#### TAMU Server: 
compute.cs.tamu.edu

#### Parser Execution Instructions: 
From the RDBMS directory, compile with the makefile, and then execute it with:

$ java Parser TEST_xxxxx.txt 

We have several TEST_xxxxx.txt files, each to test a certain aspect of our program. These test files correlate with our resubmission changelog for the Parser + Engine. Feel free to edit 
these text files. They all begin with "test" are are ready to be given to the Parser. 

#### Server Execution Instructions: 
From the RDBMS directory, compile with the makefile, and then, with two separate command line windows, start the Server in one with:

$ java Server

And start the Client in the other with:

$ java Client

## Development Log

#### Week 1: Design Documents

**9/12/16**

Met at annex<br/>
Developed understanding of project and discussed ideas

**9/14/16**

Met at annex<br/>
Collaborated together on how to approach project

**9/18/16**

Met at annex<br/>
Collaborated together on ALL ideas + intro of Design Document<br/>
Reed Spivey - Engine/Parser section of Design Document<br/>
Jason Alonzo - Engine/Parser section of Design Document<br/>
Cory Avra - Interactive System section of Design Document

#### Week 2: Engine

**9/19/16**

Met at annex<br/>
Creating Sport, Team, Player classes<br/>
Create a Array of Strings for each class

**9/21/16**

Met at annex<br/>
Created table class<br/>
Completed create table function<br/>
Removed Player, Team, and Sport Class<br/>
DBMS now represented as HashMap hashmap storing String Vectors, keyed with Strings<br/>
@TODO ID-Table class constructor<br/>
@TODO Work on insert function<br/>
@TODO Reed Spivey - Natural Join, Projection<br/>
      Jason Alonzo - Set Union, Set Difference<br/>
      Cory Avra - Write, Open, Close

**9/22/16**

Met at annex<br/>
Reed Spivey - Finished Natural Join function

**9/24/16**

Met at annex<br/>
Reed Spivey - Worked on Projection<br/>
Jason Alonzo - Worked on Set Union<br/>
Cory Avra - Write and read functions

**9/25/16**

Met at annex<br/>
Reed Spivey - Projection/cleaned code<br/>
Jason Alonzo - Set Union & Set difference/wrote print statements for successes<br/>
Cory Avra - Cross product, Testing, Re-wrote functions

#### Week 3: Parser

**9/26/16**

Met at annex<br/>
Reed Spivey - Worked on revising Design Documents<br/>
Jason Alonzo - Worked on testing framework<br/>
Cory Avra - Worked on cleaning code

**9/27/16**

Met at annex<br/>
Reed Spivey - Revised engine section of Design Documents, worked on Change Log<br/>
Jason Alonzo - Worked Change Log framework<br/>
Cory Avra - Revised Interactive System and Parser sections of Design Documents, worked on Change Log

**9/28/16**

Met at annex<br/>
Reed Spivey - Worked on recursive data structure design<br/>
Jason Alonzo - Worked on recursive data structure design<br/>
Cory Avra - Worked on regex for parser

**9/29/16**

Met at annex<br/>
Reed Spivey - Continued work on recursive data structure design<br/>
Jason Alonzo - Continued work on recursive data structure design<br/>
Cory Avra - Continued Worked on regex for parser

**10/02/16**

Met at annex<br/>
Reed Spivey - Continued work on recursive data structure design<br/>
Jason Alonzo - Continued work on recursive data structure design<br/>
Cory Avra - Revised regex designs, worked on a better implementation

#### Week 4: Interactive System

**10/03/16**

Met at annex<br/>
Reed Spivey - Worked on various functions for parser<br/>
Jason Alonzo - Worked on various functions for parser<br/>
Cory Avra - Scrapped regex, now using tokenizer, worked on various functions for parser

**10/04/16**

Met at annex<br/>
@Completed<br/>
Reed Spivey - Worked on Attribtue.java<br/>
Jason Alonzo - Worked on test functions and changelog for engine resubmission<br/>
Cory Avra - Finished basic parser functionality, improved engine<br/>
@TODO<br/>
Reed Spivey - Finish Attribute class<br/>
Jason Alonzo - Begin work on the Interactive System<br/>
Cory Avra - Begin work on the Interactive System, finish parser and engine

**10/06/16**

Met at annex from 1 - 6<br/>
@Completed<br/>
Reed Spivey - Worked on cases for user input, commits were overwritten<br/>
Jason Alonzo - Worked on cases for user input. Worked 2 hours longer than Reed, commits were overwritten<br/>
Cory Avra - Created Client.java and Server.java, basic functionality, worked on parser and engine (specifically, recursive expressions and queries)<br/>
@TODO<br/>
Reed Spivey - Continue working on cases for user input<br/>
Jason Alonzo - Continue working on cases for user input<br/>
Cory Avra - Finish engine and parser, begin working on cases for user input

**10/09/16**

Met at annex from 6 - 12<br/>
@Completed<br/>
Reed Spivey - Worked on Interactive System<br/>
Jason Alonzo - Worked on Interactive System<br/>
Cory Avra - Worked on fixing engine and parser<br/>
@TODO<br/>
Reed Spivey - Finish Interactive System<br/>
Jason Alonzo - Finish Interactive System<br/>
Cory Avra - Finish engine and parser, Finish Interactive System

**10/10/16**

Met at annex from 1 - 8<br/>
@Completed<br/>
Reed Spivey - Worked on Interactive System<br/>
Jason Alonzo - Worked on Interactive System<br/>
Cory Avra - Fixed parser and engine (almost 100% functionality)<br/>
@TODO<br/>
Reed Spivey - Finish Interactive System<br/>
Jason Alonzo - Finish Interactive System<br/>
Cory Avra - Finish engine and parser, Finish Interactive System

**10/12/16**

Met at lab from 11:30 - 12:20<br/>
@TODO<br/>
All - Move Server code to Client, make sure engine/parser are 100% functional<br/>

**10/17/16**

Met at lab from 11:30 - 12:20<br/>
Met at Annex from 1-3<br>
@Completed<br/>
Reed Spivey - Worked on Remove/Update functions<br/>
Cory Avra - Finished functionality and cleaned code after pulling an all nighter, started Final Report<br/>
@TODO<br/>
Reed Spivey - Finish the final report<br/>
Jason Alonzo - Finish the final report<br/>
Cory Avra - Finish the final report

**10/18/16**

@Completed<br/>
Cory Avra - Finished Final Report
