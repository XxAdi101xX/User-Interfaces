# CS349 A2
Student: a32venka
Marker: Bahareh Sarrafzadeh

103 / 100 (103.00%)

Code: 
(CO: won’t compile, CR: crashes, FR: UI freezes/unresponsive, NS: not submitted)

$$$ Notes must be between the backticks but can contain newlines. $$$
Notes: ```  ```


## FUNCTIONAL REQUIREMENTS (60)

1. [3/5] Saving and loading data. File-Save can be used to save the model and File-Load can be used to load and restore the shapes that were saved. The user should be promoted for a file-save location using a JDialog.

-2 user is prompted with the location to save the file while the file is saved in the assignment folder. 

2. [2/2] File-New can be used to discard the current document and create a new blank document.

3. [3/3] Selection tool. The user can select this tool in the toolbar then click on a shape to select it. There should be some visual indication which shape is selected. Pressing ESC on the keyboard will clear shape selection.

4. [2/2] Erase tool. The user can select this tool in the toolbar then click on a shape to delete it.

5. [4/4] Line drawing tool. The user can select this tool in the toolbar and then draw lines in the canvas by holding and dragging the mouse.

6. [4/4] Rectangle drawing tool. The user can select this tool in the toolbar and then draw rectangles in the canvas by holding and dragging the mouse.

7. [4/4] Circle drawing tool. The user can select this tool in the toolbar and then draw circles or ellipses in the canvas by holding and dragging the mouse.

8. [3/3] Fill tool: The user can select this tool in the toolbar and then click on a shape (rectangle or circle/ellipse) to fill it with the current colour.

9. [7/7] Colour palette. Displays at least 6 colors in a graphical view, which the user can select to set the property for drawing a new shape.

Your program was tested on different Mac OS systems with different java versions installed and the color palette consistently didn't show up.
It ran fine on a Linux machine.


10. [3/3] Colour picker. The user can click on the "Chooser" button to bring up a colour chooser dialog to select a colour.

11. [7/7] Line thickness palette. Displays at least 3 line widths, graphically, and allows the user to select line width for new shapes.

12. [2/2] Selecting a shape will cause the color palette and line thickness palette to update their state to reflect the currently selected shape.

13. [10/10] Moving shapes. Users can move shapes around the screen by selecting, then dragging them. There should be an indication which shape is selected. 

I tested it again and this time there was no problem with moving. Not sure what went wrong the first time I tested your program.

14. [2/2] The interface should clearly indicate which tool, color and line thickness are selected at any time.

15. [2/2] The interface should enable/disable controls if appropriate.


## LAYOUT (15)

1. [1/1] The starting appication window is no larger than 1600x1200.

2. [4/4] Swing widgets are used appropriately to implement the window layout.

3. [5/5] The application should support dynamic resizing of the window. The tool palettes should expand and contract based upon available space, and they should remain usable at all times.

4. [5/5] If the user resizes the window smaller than the canvas, scrollbars should appear.

## TECHNICAL REQUIREMENTS AND MVC (15)

1. [5/5] Makefile exists, `make` and `make run` works successfully with default arguments.

2. [10/10] Model-View-Controller (MVC) used effectively (i.e. shape model contained in a model class, at least two views used to represent the canvas and/or toolbars).

## ADDITIONAL/BONUS FEATURES (10 + 10 optional bonus)

1. [5/10] Application incorporates one or more enhancements totaling 10%, as described in the requirements.

+5 Customizable color palette: you can wholly or partially customize color buttons in the palette (e.g. right-click a button and choose a new color for that button from a color-chooser dialog).


2. [10/10] Bonus feature: Resizing. The user can toggle between scaling the canvas full-size, or scaling the canvas and its contents to the window. The "Full size" mode is like the default (the canvas and its contents remain the same size as you resize the window, and scrollbars appear to let you scroll the image). In the "Fit to Window" mode, the canvas and its contents resize to fit the window as the window is resized.
