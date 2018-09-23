// CS 349 Fall 2018

#include <cstdlib>
#include <iostream>
#include <vector>
#include <unistd.h>
#include <sys/time.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>

using namespace std;

class Displayable {
	public:
		virtual void paint(Display* display, Pixmap buffer, GC gc) const = 0;
};

class Block: public Displayable {
	int x;
	int y;
public:
	Block(int x, int y) : x(x), y(y) {}
	virtual void paint(Display* display, Pixmap buffer, GC gc) const {
		int xOffset = -15;
		int yOffset = 20;

		XDrawRectangle(display, buffer, gc, x + xOffset, y + yOffset, 105, 50);
	}

};

class Paddle: public Displayable {
	int x;
	int y;
public:
	Paddle(int x, int y): x(x), y(y){}
	virtual void paint(Display* display, Pixmap buffer, GC gc) const {
		XDrawRectangle(display, buffer, gc, x, y, 150, 30);
	}
	void changeXPos(int offset) {
		x += offset;
	}
};

// X11 structures
Display* display;
Window window;
Pixmap buffer;

// fixed frames per second animation
int FPS = 60;

// ball speed
int ballSpeed = 3;

// window size configuration
int windowWidth = 1280;
int windowHeight = 800;

// get current time
unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_usec;
}

void handleInvalidCmdArgs() {
	cerr << "Invalid Arguements! Usage: ./breakout [FPS] [Ball Speed]" << endl;
	exit( EXIT_FAILURE );
}

// entry point
int main( int argc, char *argv[] ) {
	if (argc == 3) {
		try {
			FPS = stoi(argv[1]);
			ballSpeed = stoi(argv[2]);
		} catch (...) {
			handleInvalidCmdArgs();		
		}
	} else if (argc == 1) {
		cout << "Your game will start with a preset FPS of 60 and ball speed of 3. Have fun!!!" << endl;
	} else {
		handleInvalidCmdArgs();
	}

	// create window
	display = XOpenDisplay("");
	if (display == NULL) exit (-1);
	int screennum = DefaultScreen(display);
	long background = WhitePixel(display, screennum);
	long foreground = BlackPixel(display, screennum);
	window = XCreateSimpleWindow(display, DefaultRootWindow(display),
                            10, 10, windowWidth, windowHeight, 2, foreground, background);

	// make window unresizable
	XSizeHints *hints = XAllocSizeHints();
	hints->min_width = windowWidth;
	hints->min_height = windowHeight;
	hints->max_width = windowWidth;
	hints->max_height = windowHeight;
	hints->width_inc = 0;
	hints->height_inc = 0;
	hints->flags = PAllHints;	
	XSetWMNormalHints(display, window, hints);

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
	ballDir.x = ballSpeed;
	ballDir.y = ballSpeed;

	// block position
	vector<Block> blocks;
	for (int i = 1; i <= 5; ++i) {
		for (int j = 1; j <= 10; ++j) {
			Block block(j * 110, i * 55);
			blocks.emplace_back(block);
		}
	}

	
	Paddle paddle(windowWidth / 2, windowHeight - 100);

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
						paddle.changeXPos(10);
					}

					// move left
					if ( i == 1 && text[0] == 'a' ) {
						paddle.changeXPos(-10);
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

			// draw paddle
			XSetForeground(display, gc, BlackPixel(display, DefaultScreen(display)));
			paddle.paint(display, buffer, gc);

			// draw blocks
			for (auto &block : blocks) {
				block.paint(display, buffer, gc);
			}

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
