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
		virtual void paint(Display* display, Pixmap buffer, GC gc) const = 0;
};

class Ball: public Displayable {
	const int startingXPosition;
	const int startingYPosition;
	int xPosition;
	int yPosition;
	int xDirection;
	int yDirection;
	const int ballSize;
public:
	Ball(int xPos, int yPos, int ballSize, int ballSpeed): 
				xPosition(xPos), yPosition(yPos), ballSize(ballSize),
				xDirection(ballSpeed), yDirection(-ballSpeed),
				startingXPosition(xPos), startingYPosition(yPos) {}
	virtual void paint(Display* display, Pixmap buffer, GC gc) const {
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
	}
};

class Block: public Displayable {
	int x;
	int y;
	int currentHealth;
	const int maxHealth;
	const int widthDimension = 105;
	const int heightDimension = 50;
public:
	Block(int x, int y, int health = 1): x(x), y(y), currentHealth(health), maxHealth(health) {}
	virtual void paint(Display* display, Pixmap buffer, GC gc) const {
		XDrawRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
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
	int x;
	int y;
	const int widthDimension = 150;
	const int heightDimension = 30;
public:
	Paddle(int x, int y): x(x), y(y) {}
	virtual void paint(Display* display, Pixmap buffer, GC gc) const {
		XDrawRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
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
};

class Text: public Displayable {
	int x;
	int y;
	string text;
public:
	Text(int x, int y, string text): x(x), y(y), text(text) {}
	virtual void paint(Display* display, Pixmap buffer, GC gc) const {
		XDrawString(display, buffer, gc, x, y, text.c_str(), text.length());
	}
	void update(string newText) {
		text = newText;
	}
	string getValue() const {
		return text;
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
	cerr << "Invalid Arguements!" << endl
			 <<	"Usage: './breakout [FPS] [BallSpeed]' where " 
			 << "10 <= FPS <= 60 and 1 <= BallSpeed <= 10" << endl;
	exit( EXIT_FAILURE );
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
			ballSpeed = max(0.7 * inputBallSpeed, 1.0);
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

	// initialize ball
	Ball ball(windowWidth / 2, windowHeight * 0.75, 35, ballSpeed);

	// initialize blocks
	typedef struct {
		Block block;
		bool ballLeftOfEntity;
		bool ballRightOfEntity;
		bool ballAboveEntity;
		bool ballBelowEntity;
	} BlockInfo;
	vector<BlockInfo> blocks;
	for (int i = 0; i < 5; ++i) {
		for (int j = 1; j <= 10; ++j) {
			Block block(j * 110, i * 55);
			BlockInfo info{block, false, false, false, false};
			blocks.emplace_back(info);
		}
	}

	
	Paddle paddle((windowWidth / 2) - 75, windowHeight - 100);

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

	// track hit score
	Text currentScore(1000, 500, "0");

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
						paddle.moveXPos(10);
					}

					// move left
					if ( i == 1 && text[0] == 'a' ) {
						paddle.moveXPos(-10);
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
			for (auto &blockinfo : blocks) {
				if (!blockinfo.block.isDestroyed()) {
					blockinfo.block.paint(display, buffer, gc);
				}
			}

			// draw ball from centre
			ball.paint(display, buffer, gc);

			// update ball position
			ball.updatePos();

			// check ball collision with wall
			if (ball.xPos() + ball.size()/2 > w.width ||
					ball.xPos() - ball.size()/2 < 0) {
				ball.invertXDir();
			}
			
			if (ball.yPos() + ball.size()/2 > w.height ||
					ball.yPos() - ball.size()/2 < 0) {
				ball.invertYDir();
			}
			
			// check ball collision with paddle
      bool paddleHit = ((ball.yPos() + ball.size()/2 > paddle.yPos()) &&
                        ((ball.xPos() - ball.size()/2 >= paddle.xPos()) &&
                         (ball.xPos() + ball.size()/2 <= paddle.xPos() + paddle.width())));			

			if (paddleHit) { // FIX THISSSSSSSSSSSSSSSSSSSSS
				if (ball.xPos() <= paddle.xPos() + paddle.width()/2) {
					if (ball.xDir() > 0) ball.invertXDir();
				} else {
					if (ball.xDir() < 0) ball.invertXDir();
				}
				ball.invertYDir();
			}

			// check ball collision with with blocks
			for (auto &blockinfo: blocks) {
				Block *block = &(blockinfo.block);

				if (block->isDestroyed()) continue;

				bool ballLeftOfBlock = ball.xPos() - ball.size()/2 < block->xPos() + block->width();
				bool ballRightOfBlock = ball.xPos() + ball.size()/2 > block->xPos();
				bool ballAboveBlock = ball.yPos() - ball.size()/2 < block->yPos() + block->height();
				bool ballBelowBlock = ball.yPos() + ball.size()/2 > block->yPos();

				if (ballLeftOfBlock && ballRightOfBlock && ballAboveBlock && ballBelowBlock) {
					if (!blockinfo.ballLeftOfEntity || !blockinfo.ballRightOfEntity) {
          	ball.invertXDir();
					} else {
          	ball.invertYDir();
					}
					block->onHit();
					currentScore.update(to_string(stoi(currentScore.getValue()) + 1));
					break;
				}
				blockinfo.ballLeftOfEntity = ballLeftOfBlock;
				blockinfo.ballRightOfEntity = ballRightOfBlock;
				blockinfo.ballAboveEntity = ballAboveBlock;
				blockinfo.ballBelowEntity = ballBelowBlock;
			}

			// show score
			currentScore.paint(display, buffer, gc);

			// reset game state
			if (stoi(currentScore.getValue()) == 50) {
        for (auto &blockinfo : blocks) {
          blockinfo.block.reset();
        }
				ball.reset();
        currentScore.update("0");
      }


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
