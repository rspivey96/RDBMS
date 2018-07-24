# Final Report

## Updated Design Documents

We have updated our Design Documents to reflect what we have actually built. They can be found in PDF form [here](https://github.tamu.edu/reedspivey/ComputersAreHardP1D4/blob/master/design_documents/DesignDocumentsFinal.pdf) and in [Google Docs](https://docs.google.com/document/d/1WJr-slwylkafvPg8UwRK5ph_V0THIVFP7vxWbtluEeQ/edit?usp=sharing).

## Post-Production Notes

We had to make many changes to our original design, as well as one complete overhaul of the entire engine and parser system after it was already graded. In the beginning, our design documents were incredibly weak. We were vague and had no real idea how to go about mapping our our plans. In reality, we ended up just solving each problem as we came across it, instead of anticipating them beforehand in our design documents. This slowed us down tremendously. 

For example, we originally stored the key of each row in the first element of the row vector, and all its values would just follow afterwards. We also didn't have any way of checking an attribute against its given domain, so when it was time to plug in the Interactive System, we had to go back and completely revamp everything. An Attribute class was implemented, as well as a Row class, which allowed for much easier methods of storing keys and values. In the process of doing this, lost several days when we could have been working on the Client Application. Lesson learned: anticipate future hurdles and write clean code to handle them. 

Another time where we faced difficulty was with the implementation of the recursive expressions. Once implemented, it made the rest of the code super easy. But before we implemented it, we had hardcoded a lot of the cases into each and every query function, which cost us hours of work. Once we actually thought about it instead of just jumping into coding head-first, we were able to finish our parser much sooner than we anticipated. 

Overall, we made a lot of mistakes, but we were able to recover from all of them once we learned where we went wrong. There were also several cases where we actually had *good* code, which enabled us to easily tackle future obstacles that could have been much more foreboding. 

## Representation of Interactive System

Our example session representation can be found in PDF form here [here](https://github.tamu.edu/reedspivey/ComputersAreHardP1D4/blob/master/ISRepresentation.pdf) and in [google docs](https://docs.google.com/document/d/1Fek1xbwdeCJ0VJMu4jNZfZbaK8SrUSPaCjpYSXI9TXM/edit?usp=sharing).

## Development Log

Our Development Log can be found [here](https://github.tamu.edu/reedspivey/ComputersAreHardP1D4/blob/master/README.md#development-log).