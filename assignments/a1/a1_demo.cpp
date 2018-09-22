// CS 349 Fall 2018
// A1: Breakout code sample
// You may use any or all of this code in your assignment!
// See makefile for compiling instructions

#include <cstdlib>
#include <iostream>
#include <unistd.h>
#include <sys/time.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>

using namespace std;

class Displayable {
	public:
		virtual void paint() = 0;
};

class Block: public Displayable {
	int x;
	int y;
public:
	Block(int x, int y) : x(x), y(y) {}
	virtual void paint() {}

};

class Paddle: public Displayable {
	int x;
	int y;
public:
	Paddle(int x, int y): x(x), y(y){}
};

// X11 structures
Display* display;
Window window;
Pixmap buffer;

// fixed frames per second animation
int FPS = 60;

// window size configuration
int windowWidth = 1280;
int windowHeight = 800;

// get current time
unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_usec;
}

// entry point
int main( int argc, char *argv[] ) {

	// create window
	display = XOpenDisplay("");
	if (display == NULL) exit (-1);
	int screennum = DefaultScreen(display);
	long background = WhitePixel(display, screennum);
	long foreground = BlackPixel(display, screennum);
	window = XCreateSimpleWindow(display, DefaultRootWindow(display),
                            10, 10, windowWidth, windowHeight, 2, foreground, background);

	// make window unresizable
	/*XSizeHints *hints = XAllocSizeHints();
	hints->min_width = windowWidth;
	hints->min_height = windowHeight;
	hints->max_width = windowWidth;
	hints->max_height = windowHeight;
	hints->flags = PAllHints;	
	XSetWMNormalHints(display, window, hints);*/

	// set events to monitor and display window
	XSelectInput(display, window, ButtonPressMask | KeyPressMask);
	XMapRaised(display, window);
	XFlush(display);

	// ball postition, size, and velocity
	XPoint ballPos;
	ballPos.x = 50;
	ballPos.y = 50;
	int ballSize = 50;

	XPoint ballDir;
	ballDir.x = 3;
	ballDir.y = 3;

	// block position, size
	XPoint rectPos;
	rectPos.x = 225;
	rectPos.y = 400;

	// create gc for drawing
	GC gc = XCreateGC(display, window, 0, 0);
	XWindowAttributes w;
	XGetWindowAttributes(display, window, &w);

	// create buffer for double buffering
  int depth = DefaultDepth(display, DefaultScreen(display));
  buffer = XCreatePixmap(display, window, w.width, w.height, depth);

	// save time of last window paint
	unsigned long lastRepaint = 0;

	// event handle for current event
	XEvent event;

	// event loop
	while ( true ) {

		// process if we have any events
		if (XPending(display) > 0) { 
			XNextEvent( display, &event ); 

			switch ( event.type ) {

				// mouse button press
				case ButtonPress:
					cout << "CLICK" << endl;
					break;

				case KeyPress: // any keypress
					KeySym key;
					char text[10];
					int i = XLookupString( (XKeyEvent*)&event, text, 10, &key, 0 );

					// move right
					if ( i == 1 && text[0] == 'd' ) {
						rectPos.x += 10;
					}

					// move left
					if ( i == 1 && text[0] == 'a' ) {
						rectPos.x -= 10;
					}

					// quit game
					if ( i == 1 && text[0] == 'q' ) {
						XCloseDisplay(display);
						exit(0);
					}
					break;
				}
		}

		unsigned long end = now();	// get current time in microsecond

		if (end - lastRepaint > 1000000 / FPS) {
			// draw to buffer to take advantage of double buffering

			// clear background
      XSetForeground(display, gc, WhitePixel(display, DefaultScreen(display)));
      XFillRectangle(display, buffer, gc,
                     0, 0, w.width, w.height);

			// draw rectangle
			XSetForeground(display, gc, BlackPixel(display, DefaultScreen(display)));
			XDrawRectangle(display, buffer, gc, rectPos.x, rectPos.y, 50, 50);

			// draw ball from centre
			XFillArc(display, buffer, gc, 
				ballPos.x - ballSize/2, 
				ballPos.y - ballSize/2, 
				ballSize, ballSize,
				0, 360*64);

			// update ball position
			ballPos.x += ballDir.x;
			ballPos.y += ballDir.y;

			// bounce ball
			if (ballPos.x + ballSize/2 > w.width ||
				ballPos.x - ballSize/2 < 0)
				ballDir.x = -ballDir.x;
			if (ballPos.y + ballSize/2 > w.height ||
				ballPos.y - ballSize/2 < 0)
				ballDir.y = -ballDir.y;

			// copy buffer to window
      XCopyArea(display, buffer, window, gc,
                0, 0, w.width, w.height,  // region of pixmap to copy
                0, 0); // position to put top left corner of pixmap in window

			XFlush( display );

			lastRepaint = now(); // remember when the paint happened
		}

		// IMPORTANT: sleep for a bit to let other processes work
		if (XPending(display) == 0) {
			usleep(1000000 / FPS - (now() - lastRepaint));
		}
	}
	XCloseDisplay(display);
}
