// CS 349 Fall 2018

#include <cstdlib>
#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
#include <unistd.h>
#include <sys/time.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>

using namespace std;

class Displayable {
	public:
		virtual void paint(Display* display, Pixmap buffer) const = 0;
};

class Ball: public Displayable {
	const int startingXPosition;
	const int startingYPosition;
	int xPosition;
	int yPosition;
	int xDirection;
	int yDirection;
	const int ballSize;
	GC gc;
public:
	Ball(int xPos, int yPos, GC gc, int ballSize, int ballSpeed): 
				xPosition(xPos), yPosition(yPos), gc(gc), ballSize(ballSize),
				xDirection(ballSpeed), yDirection(-ballSpeed),
				startingXPosition(xPos), startingYPosition(yPos) {}
	virtual void paint(Display* display, Pixmap buffer) const {
		XFillArc( display, buffer, gc,
        			xPosition - ballSize/2,
        			yPosition - ballSize/2,
        			ballSize, ballSize,
        			0, 360*64);
	}
	int xPos() const {
		return xPosition;
	}
	int yPos() const {
		return yPosition;
	}
	int xDir() const {
		return xDirection;
	}
	int yDir() const {
		return yDirection;	
	}
	void updatePos() {
		xPosition += xDirection;
		yPosition += yDirection;
	}
	void invertXDir() {
		xDirection = -xDirection;
	}
	void invertYDir() {
		yDirection = -yDirection;
	}
	int size() const {
		return ballSize;
	}
	int reset() {
		xPosition = startingXPosition;
		yPosition = startingYPosition;
		xDirection = abs(xDirection);
		yDirection = yDirection > 0 ? -yDirection : yDirection;
	}
};

class Block: public Displayable {
	int x;
	int y;
	GC gc;
	int currentHealth;
	const int maxHealth;
	const int widthDimension = 105;
	const int heightDimension = 50;
public:
	Block(int x, int y, GC gc, int health = 1): x(x), y(y), gc(gc),
																							currentHealth(health), maxHealth(health) {}
	virtual void paint(Display* display, Pixmap buffer) const {
	/*XColor red, brown, blue, yellow, green;
 	XSetFillStyle(display, gc, FillSolid);
  XSetForeground(display, gc, red.pixel);*/
		XFillRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
	}
	int xPos() const {
		return x;
	}
	int yPos() const {
		return y;
	}
	int width() const {
		return widthDimension;
	}
	int height() const {
		return heightDimension;
	}
	void onHit() {
		--currentHealth;
	}
	bool isDestroyed() const {
		return currentHealth == 0;
	}
	void reset() {
		currentHealth = maxHealth;
	}

};

class Paddle: public Displayable {
  const int startingXPosition;
  const int startingYPosition;
	int x;
	int y;
	GC gc;
	const int widthDimension = 150;
	const int heightDimension = 30;
public:
	Paddle(int x, int y, GC gc): x(x), y(y), startingXPosition(x), startingYPosition(y), gc(gc) {}
	virtual void paint(Display* display, Pixmap buffer) const {
		XFillRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
	}
	void moveXPos(int offset) {
		x += offset;
	}
	int xPos() const {
		return x;
	}
	int yPos() const {
		return y;
	}
	int width() const {
		return widthDimension;
	}
	int height() const {
		return heightDimension;
	}
	void reset() {
		x = startingXPosition;
		y = startingYPosition;
	}
};

class Text: public Displayable {
	int x;
	int y;
	GC gc;
	string text;
public:
	Text(int x, int y, GC gc, string text): x(x), y(y), gc(gc), text(text) {}
	virtual void paint(Display* display, Pixmap buffer) const {
		XDrawString(display, buffer, gc, x, y, text.c_str(), text.length());
	}
	void update(string newText) {
		text = newText;
	}
	string getValue() const {
		return text;
	}
};

typedef struct {
  Block block;
  bool ballLeftOfBlock;
  bool ballRightOfBlock;
  bool ballAboveBlock;
  bool ballBelowBlock;
} BlockInfo;

// X11 structures
Display* display;
Window window;
Pixmap buffer;

// fixed frames per second animation
int FPS = 60;

// ball speed
int ballSpeed = 4;

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
	cerr << "Invalid Arguements!" << endl
			 <<	"Usage: './breakout [FPS] [BallSpeed]' where " 
			 << "10 <= FPS <= 60 and 1 <= BallSpeed <= 10" << endl;
	exit( EXIT_FAILURE );
}

void resetGameState(vector<BlockInfo> &blocks, Ball &ball, Paddle &paddle, Text &currentScore) {
	for (auto &blockinfo : blocks) {
  	blockinfo.block.reset();
  }
  ball.reset();
	paddle.reset();
  currentScore.update("0");
}

GC createGC(XColor colour) {
	GC gc = XCreateGC(display, window, 0, 0);

  XSetFillStyle(display, gc, FillSolid);
  XSetForeground(display, gc, colour.pixel);
	return gc;
}

// entry point
int main( int argc, char *argv[] ) {
	if (argc == 3) {
		try {
			int inputFPS = stoi(argv[1]);
			int inputBallSpeed = stoi(argv[2]);
			int scalingRatio = 60 / inputFPS;

			if (inputFPS > 60 || inputFPS < 10 || inputBallSpeed > 10 || inputBallSpeed < 1) {
				throw 1;
			}

			// maintain ball speed regardless of FPS
			inputBallSpeed = inputBallSpeed * scalingRatio;

			FPS = inputFPS;
			ballSpeed = max(0.5 * inputBallSpeed, 1.0);
		} catch (...) {
			handleInvalidCmdArgs();		
		}
	} else if (argc == 1) {
		cout << "Your game will start with a preset FPS of 60 and regular ballspeed. Have fun!!!" << endl;
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

	// Allocate colours
	Colormap screen_colormap = DefaultColormap(display, DefaultScreen(display));
  XColor red, brown, blue, yellow, green, black, white;

  XAllocNamedColor(display, screen_colormap, "red", &red, &red);
  XAllocNamedColor(display, screen_colormap, "brown", &brown, &brown);
  XAllocNamedColor(display, screen_colormap, "blue", &blue, &blue);
  XAllocNamedColor(display, screen_colormap, "yellow", &yellow, &yellow);
  XAllocNamedColor(display, screen_colormap, "green", &green, &green);
	XAllocNamedColor(display, screen_colormap, "black", &black, &black);
	XAllocNamedColor(display, screen_colormap, "white", &white, &white);

	// create gc for drawing
  GC gcBlack = createGC(black);
	GC gcWhite = createGC(white);
	GC gcRed = createGC(red);
	GC gcBrown = createGC(brown);
	GC gcBlue = createGC(blue);
	GC gcYellow = createGC(yellow);
	GC gcGreen = createGC(green);	

	// initialize ball
	Ball ball(windowWidth / 2, windowHeight * 0.75, gcBlack, 35, ballSpeed);

	// initialize blocks
	vector<GC> gcs {gcRed, gcBrown, gcBlue, gcYellow, gcGreen};
	vector<BlockInfo> blocks;
	for (int i = 0; i < 5; ++i) {
		for (int j = 1; j <= 10; ++j) {
			Block block(j * 110, i * 55, gcs[i], 1);
			BlockInfo info{block, false, false, false, false};
			blocks.emplace_back(info);
		}
	}

	// initialize	paddle
	Paddle paddle((windowWidth / 2) - 75, windowHeight - 100, gcBlack);
	bool ballLeftOfPaddle = false;
  bool ballRightOfPaddle = false;
  bool ballAbovePaddle = false;
  bool ballBelowPaddle = false;

	// get window attributes
	XWindowAttributes w;
	XGetWindowAttributes(display, window, &w);

	// create buffer for double buffering
  int depth = DefaultDepth(display, DefaultScreen(display));
  buffer = XCreatePixmap(display, window, w.width, w.height, depth);

	// save time of last window paint
	unsigned long lastRepaint = 0;

	// event handle for current event
	XEvent event;

	// track hit score
	Text currentScore(1000, 500, gcBlack, "0");

eventLoop:
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
						paddle.moveXPos(15);
					}

					// move left
					if ( i == 1 && text[0] == 'a' ) {
						paddle.moveXPos(-15);
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
      XFillRectangle(display, buffer, gcWhite,
                     0, 0, w.width, w.height);

			// draw paddle
			paddle.paint(display, buffer);

			// draw blocks
			for (auto &blockinfo : blocks) {
				if (!blockinfo.block.isDestroyed()) {
					blockinfo.block.paint(display, buffer);
				}
			}

			// draw ball from centre
			ball.paint(display, buffer);

			// update ball position
			ball.updatePos();

			// check ball collision with wall
			if (ball.xPos() + ball.size()/2 > w.width ||
					ball.xPos() - ball.size()/2 < 0) {
				ball.invertXDir();
			} else if (ball.yPos() - ball.size()/2 < 0) {
				ball.invertYDir();
			} else if (ball.yPos() + ball.size()/2 > w.height) {
				// quit game
				goto endGame;
			}
			
			// check ball collision with paddle
			bool ballLeftOfPaddleNow = ball.xPos() - ball.size()/2 < paddle.xPos() + paddle.width();
      bool ballRightOfPaddleNow = ball.xPos() + ball.size()/2 > paddle.xPos();
      bool ballAbovePaddleNow = ball.yPos() - ball.size()/2 < paddle.yPos() + paddle.height();
      bool ballBelowPaddleNow = ball.yPos() + ball.size()/2 > paddle.yPos();

      if (ballLeftOfPaddleNow && ballRightOfPaddleNow && ballAbovePaddleNow && ballBelowPaddleNow) {
        if (!ballLeftOfPaddle || !ballRightOfPaddle) {
     	  	ball.invertXDir();
        } else if (!ballBelowPaddle) { // don't interact with hit detection from below paddle
          ball.invertYDir();
					if (ball.xPos() > paddle.xPos() + paddle.width()/2) {
						if (ball.xDir() < 0) ball.invertXDir();
					} else {
						if (ball.xDir() > 0) ball.invertXDir();
					}
        }
      }
      ballLeftOfPaddle = ballLeftOfPaddleNow;
      ballRightOfPaddle = ballRightOfPaddleNow;
      ballAbovePaddle = ballAbovePaddleNow;
      ballBelowPaddle = ballBelowPaddleNow;
			
			// check ball collision with with blocks
			for (auto &blockinfo: blocks) {
				Block *block = &(blockinfo.block);

				if (block->isDestroyed()) continue;

				bool ballLeftOfBlockNow = ball.xPos() - ball.size()/2 < block->xPos() + block->width();
				bool ballRightOfBlockNow = ball.xPos() + ball.size()/2 > block->xPos();
				bool ballAboveBlockNow = ball.yPos() - ball.size()/2 < block->yPos() + block->height();
				bool ballBelowBlockNow = ball.yPos() + ball.size()/2 > block->yPos();

				if (ballLeftOfBlockNow && ballRightOfBlockNow && ballAboveBlockNow && ballBelowBlockNow) {
					if (!blockinfo.ballLeftOfBlock || !blockinfo.ballRightOfBlock) {
          	ball.invertXDir();
					} else {
          	ball.invertYDir();
					}
					block->onHit();
					currentScore.update(to_string(stoi(currentScore.getValue()) + 1));
					break;
				}
				blockinfo.ballLeftOfBlock = ballLeftOfBlockNow;
				blockinfo.ballRightOfBlock = ballRightOfBlockNow;
				blockinfo.ballAboveBlock = ballAboveBlockNow;
				blockinfo.ballBelowBlock = ballBelowBlockNow;
			}

			// show score
			currentScore.paint(display, buffer);

			// reset game state
			if (stoi(currentScore.getValue()) == 50) {
        resetGameState(blocks, ball, paddle, currentScore);
      }

			// copy buffer to window
      XCopyArea(display, buffer, window, gcBlack,
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
endGame:
	while (true) {
		//if (XPending(display) > 0) {
      XNextEvent( display, &event );
      if (event.type == KeyPress) {
        KeySym key;
        char text[10];
        int i = XLookupString( (XKeyEvent*)&event, text, 10, &key, 0 );

        // move left
        if ( i == 1 && text[0] == 'r' ) {
          resetGameState(blocks, ball, paddle, currentScore);
					goto eventLoop;
        }
        // quit game
        if ( i == 1 && text[0] == 'q' ) {
          XCloseDisplay(display);
          exit(0);
        }
			}
		//}
	}
}
