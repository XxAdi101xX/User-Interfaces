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
	const double startingXPosition;
	const double startingYPosition;
	double xPosition;
	double yPosition;
	double xDirection;
	double yDirection;
	const int ballSize;
	GC gc;
public:
	Ball(double xPos, double yPos, GC gc, int ballSize, double ballSpeed): 
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
	double xPos() const {
		return xPosition;
	}
	double yPos() const {
		return yPosition;
	}
	double xDir() const {
		return xDirection;
	}
	double yDir() const {
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
	void reset() {
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
	string getText() const {
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
double ballSpeed = 4.0;

// window size configuration
int windowWidth = 1280;
int windowHeight = 800;

// get current time
unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_usec;
}

// handle bad command line args
void handleInvalidCmdArgs() {
	cerr << "Invalid Arguements!" << endl
			 <<	"Usage: './breakout [FPS] [BallSpeed]' where " 
			 << "10 <= FPS <= 60 and 1 <= BallSpeed <= 10" << endl;
	exit( EXIT_FAILURE );
}

// reset the game state
void resetGameState(vector<BlockInfo> &blocks, Ball &ball, Paddle &paddle, Text &currentScore) {
	for (auto &blockinfo : blocks) {
  	blockinfo.block.reset();
  }
  ball.reset();
	paddle.reset();
  currentScore.update("0");
}

// create gcs with some preset properties
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
			double inputBallSpeed = stoi(argv[2]);
			double scalingRatio = 60.0 / inputFPS;

			if (inputFPS > 60 || inputFPS < 10 || inputBallSpeed > 10 || inputBallSpeed < 1) {
				throw 1;
			}

			// scale ballspeed according to inputFPS
			inputBallSpeed = inputBallSpeed * scalingRatio;

			FPS = inputFPS;
			ballSpeed = max(0.6 * inputBallSpeed, 1.0);
			cout << ballSpeed << endl;
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

	// allocate colours
	Colormap screen_colormap = DefaultColormap(display, DefaultScreen(display));
  XColor red, brown, blue, yellow, green, black, white;

  XAllocNamedColor(display, screen_colormap, "red", &red, &red);
  XAllocNamedColor(display, screen_colormap, "brown", &brown, &brown);
  XAllocNamedColor(display, screen_colormap, "blue", &blue, &blue);
  XAllocNamedColor(display, screen_colormap, "yellow", &yellow, &yellow);
  XAllocNamedColor(display, screen_colormap, "green", &green, &green);
	XAllocNamedColor(display, screen_colormap, "black", &black, &black);
	XAllocNamedColor(display, screen_colormap, "white", &white, &white);

	// create gcs for images
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
	int offSet = 35;
	for (int i = 0; i < 5; ++i) {
		for (int j = 0; j < 11; ++j) {
			Block block(j * 110 + offSet, i * 55, gcs[i], 1);
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

	// create buffer for double buffering
  int depth = DefaultDepth(display, DefaultScreen(display));
  buffer = XCreatePixmap(display, window, windowWidth, windowHeight, depth);

	// save time of last window paint
	unsigned long lastRepaint = 0;

	// event handle for current event
	XEvent event;

	// track hit score
	Text scoreTitle(1150, 775, gcRed, "Current Score: ");
	Text currentScore(1250, 775, gcRed, "0");

	// intro text
	string msg0 = "Welcome to Breakout!";
	string msg1 = "This game is created by Adithya Venkatarao (ID: a32venka && Student#: 20606942)";
	string msg2 = "To play this game, use the 'a' and 'd' buttons to move the paddle left and right respectively.";
	string msg3 = "You can press 'q' at anytime to quit the game. Press 'r' to start playing!!!";
	
	Text introMessage0(windowWidth / 2 - 120, 250, gcWhite, msg0);
	Text introMessage1(windowWidth / 4, 300, gcWhite, msg1);
	Text introMessage2(windowWidth / 4, 350, gcWhite, msg2);
	Text introMessage3(windowWidth / 4, 400, gcWhite, msg3);

	// end game text
	Text endGameMessage(windowWidth / 3, 525, gcRed, "");

	//bool firstEntry = true;
	bool intro = true;
	bool gameInPlay = false;

	// event loop
	while ( true ) {
		// process if we have any events
		if (XPending(display) > 0) { 
			XNextEvent( display, &event ); 

			switch ( event.type ) {
				case KeyPress: // any keypress
					KeySym key;
					char text[10];
					int i = XLookupString( (XKeyEvent*)&event, text, 10, &key, 0 );

					// move right
					if ( i == 1 && text[0] == 'd' && gameInPlay ) {
						paddle.moveXPos(15);
					}

					// move left
					if ( i == 1 && text[0] == 'a' && gameInPlay ) {
						paddle.moveXPos(-15);
					}

					// quit game
					if ( i == 1 && text[0] == 'q' ) {
						XCloseDisplay(display);
						exit(0);
					}

					// reset game
          if ( i == 1 && text[0] == 'r' && !gameInPlay ) {
						resetGameState(blocks, ball, paddle, currentScore);
						gameInPlay = true;
						intro = false;
          }
					break;
				}
		}

		unsigned long end = now();	// get current time in microsecond

		if (end - lastRepaint > 1000000 / FPS) {
			if (intro) {
				introMessage0.paint(display, buffer);
				introMessage1.paint(display, buffer);
				introMessage2.paint(display, buffer);
				introMessage3.paint(display, buffer);
			}
			// draw to buffer to take advantage of double buffering
			if (!gameInPlay) {
				endGameMessage.paint(display, buffer);
			}

			if (gameInPlay) {
				// clear background
      	XFillRectangle(display, buffer, gcWhite,
                     	0, 0, windowWidth, windowHeight);

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

				// show score
				scoreTitle.paint(display, buffer);
      	currentScore.paint(display, buffer);

				// update ball position
				ball.updatePos();
			} // drawing block

			// check ball collision with wall
			if (ball.xPos() + ball.size()/2 > windowWidth ||
					ball.xPos() - ball.size()/2 < 0) {
				ball.invertXDir();
			} else if (ball.yPos() - ball.size()/2 < 0) {
				ball.invertYDir();
			} else if (ball.yPos() + ball.size()/2 > windowHeight) {
				// quit game
				string endGameText = "You have lost with a total score of " + currentScore.getText() +
        				             "! Press 'r' to play again or 'q' to quit.";
				endGameMessage.update(endGameText);
				gameInPlay = false;
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
					currentScore.update(to_string(stoi(currentScore.getText()) + 1));
					break;
				}
				blockinfo.ballLeftOfBlock = ballLeftOfBlockNow;
				blockinfo.ballRightOfBlock = ballRightOfBlockNow;
				blockinfo.ballAboveBlock = ballAboveBlockNow;
				blockinfo.ballBelowBlock = ballBelowBlockNow;
			}

			// reset game state
			int score = stoi(currentScore.getText());
			if (score > 0 && score % 50 == 0) {
        resetGameState(blocks, ball, paddle, currentScore);
      }

			// copy buffer to window
      XCopyArea(display, buffer, window, gcBlack,
                0, 0, windowWidth, windowHeight,  // region of pixmap to copy
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
