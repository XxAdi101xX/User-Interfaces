This application was created on a windows 10 machine using Java 10.0.2. Note that the source code builds on top of the demo sprites application build by Michael Terry & Jeff Avery. The application works pretty well and as intended for the most part. For the leg scaling, that seems to be fine if you scale/rotate relatively slowly but you will get wonky behaviour otherwise because the rotations are somehow screwing things up. To prevent some of the weird behaviour, I've placed some reasonable constraints on how much you can scale.

For the addional features, I implemented the addition of other figures that can be populated onto the screen. The additional figures can be found under the add section of the menu bar. Note that the scarecrow also can move its eyes! I did not implement a second feature.

Please execute "make run" to start up the application.