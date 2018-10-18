This application was created on a windows 10 machine by Adithya Venkatarao (a32venka).

All icons were found on www.shareicon.net.

The fill icon is protected by a Freeware Non-Comerical License.
The circle icon is protected by a CC 3.0 BY (creative commons) License.
The line icon is protected by a CC 3.0 BY (creative commons) License.
The rectangle icon is protected by a Freeware License.
The eraser icon is protected by a Freeware License.
The cursor icon is protected by a Creative Commons Attribution Non-commercial (by-nc) License.

For the extra feature, I chose to implement the customizable colour pallet which can change colour by right clicking 
and selecting a colour. I didn't implement a second extra feature for the other 5%.

I have also implemented the bonus feature in full. It is defaulted to be set to full view which will add scrollbars as the window increases 
in size. Hence if the window increases, the canvas will always increase but if the window size decreases, it will use the largest
canvas size so far as reference and add scrollbars appropriately. If the scaling option is chosen, it will scale down the shapes and the canvas according to the window size. It should work as intended.

You can load in the jPanelDrawing.txt to test out the load function and then also just save your drawing to another file. Ensure that
the file ends with the .txt postfix so that it's not filtered out by the file filter. When creating a new file, it will also prompt you to save 
the file before exiting. Trying to create a new canvas will prompt you to first save the existing canvas and then create a new canvas with the new window size configurations. Just a note, shortcuts to these menu items have been added for convenience.

The colour palette, colour picker and line width bar are disabled if some incompatible tool types are selected.

The only bugs that are still unresolved are some bugs regarding the custom colour picker in regards to changing some properties of shapes. I could probably fix it but I've spent too long on this assingment and I have other stuff to do unfortunately :(
When testing, please test everything out with just the default colour pallet to ensure that everything else is working and then test out 
all the functionality using the custom colours. Everything else should be fine.

As mentioned in the code, I've used the template from the HelloMVC example in the class examples to structure our the code architecture. Please run
"make run" to run the application. Thanks!