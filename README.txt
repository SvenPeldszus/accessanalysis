SOURCECODE of AccessAnalysis - Readme
=====================================

Version 1.2

Eclipse-Plug-In that determines the minimal access modifiers of Java types and 
methods and computes the metrics Inappropriate Generosity with Accessibility of 
Types (IGAT) and Inappropriate Generosity with Accessibility of Methods (IGAM).

For more information see: http://accessanalysis.sourceforge.net


Copyright
---------

Copyright (C) 2010-2012
    Christian Zoller <chrzoller@users.sourceforge.net>


License
-------

This program is open-source software and licensed under the terms of the Eclipse 
Public License (EPL). You find the license text in the file LICENSE.txt.
For more information see: http://www.eclipse.org/legal/eplfaq.php

Binaries of this programm ready to install can be found under: 
https://sourceforge.net/projects/accessanalysis/files


Release Notes
-------------

Changes in Version 1.2:
* Changed table column headers in AccessAnalysis View from "Minimal Access"/"Actual
Access" to "Minimal Access Modifier"/"Actual Access Modifier".

Bug fixed in Version 1.1:
* Enum constructors without explicit access modifier are now correctly classified 
as private according to JLS § 8.8.3.

New features added in Version 1.0:
* Sourcefolders are included in the Results View.
* JUnit test classes and methods are considered to be public.
* Exceptional rules via annotations can be configured. 